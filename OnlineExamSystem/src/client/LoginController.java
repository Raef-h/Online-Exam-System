package client;

import common.*;
import javafx.application.Platform;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class LoginController {

    @FXML private TextField nameField, ipField, portField;
    @FXML private Button connectBtn;
    @FXML private Label errorLabel;

    @FXML
    private void handleConnect() {
        String name = nameField.getText().trim();
        String ip   = ipField.getText().trim();
        String portTxt = portField.getText().trim();

        if (name.isEmpty() || ip.isEmpty() || portTxt.isEmpty()) {
            showError("Please fill all fields.");
            return;
        }

        int port;
        try { port = Integer.parseInt(portTxt); }
        catch (NumberFormatException e) { showError("Invalid port."); return; }

        connectBtn.setDisable(true);
        errorLabel.setText("Connecting...");

        new Thread(() -> {
            try {
                Socket socket = new Socket(ip, port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                // Send student ID
                out.writeObject(new Message(Message.Type.CONNECT, name));
                out.flush();

                // Receive exam list
                Message reply = (Message) in.readObject();
                if (reply.getType() == Message.Type.ERROR) {
                    Platform.runLater(() -> showError((String) reply.getData()));
                    socket.close();
                    return;
                }

                @SuppressWarnings("unchecked")
                List<ExamInfo> exams = (List<ExamInfo>) reply.getData();

                Platform.runLater(() -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/examlist.fxml"));
                        VBox root = loader.load();
                        ExamListController ctrl = loader.getController();
                        ctrl.initialize(name, socket, in, out, exams, false);

                        Stage stage = (Stage) connectBtn.getScene().getWindow();
                        Scene scene = new Scene(root, 400, 200);
                        stage.setScene(scene);
                        stage.setTitle("Exam Selection.fxml");
                    } catch (Exception ex) {
                        showError("UI Error: " + ex.getMessage());
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Cannot connect: " + e.getMessage());
                    connectBtn.setDisable(false);
                });
            }
        }).start();
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        connectBtn.setDisable(false);
    }
}
