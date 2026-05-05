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

public class ExamListController {

    @FXML private ComboBox<String> examCombo;
    @FXML private Button takeExamBtn;
    @FXML private Label errorLabel;

    private String studentName;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private List<ExamInfo> exams;

    public void initialize(String studentName, Socket socket,
                           ObjectInputStream in, ObjectOutputStream out,
                           List<ExamInfo> exams, boolean isArabic) {
        this.studentName = studentName;
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.exams = exams;

        for (ExamInfo e : exams) {
            examCombo.getItems().add(e.getExamName());
        }
        
        if (!exams.isEmpty()) {
            examCombo.getSelectionModel().selectFirst();
        } else {
            errorLabel.setText("No active exams available.");
            takeExamBtn.setDisable(true);
        }
    }

    @FXML
    private void handleTakeExam() {
        int idx = examCombo.getSelectionModel().getSelectedIndex();
        if (idx < 0) {
            errorLabel.setText("Please select an exam.");
            return;
        }

        ExamInfo selected = exams.get(idx);
        takeExamBtn.setDisable(true);

        new Thread(() -> {
            try {
                out.writeObject(new Message(Message.Type.SELECT_EXAM, selected.getExamId()));
                out.flush();

                Message reply = (Message) in.readObject();

                if (reply.getType() == Message.Type.ERROR) {
                    Platform.runLater(() -> {
                        errorLabel.setText((String) reply.getData());
                        takeExamBtn.setDisable(false);
                    });
                    return;
                }

                Exam exam = (Exam) reply.getData();

                Platform.runLater(() -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/exam.fxml"));
                        VBox root = loader.load();
                        ExamController ctrl = loader.getController();
                        ctrl.initialize(studentName, socket, in, out, exam, false);

                        Stage stage = (Stage) takeExamBtn.getScene().getWindow();
                        Scene scene = new Scene(root, 400, 300);
                        stage.setScene(scene);
                        stage.setTitle("Exam.fxml");
                    } catch (Exception ex) {
                        errorLabel.setText("UI Error: " + ex.getMessage());
                        takeExamBtn.setDisable(false);
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    errorLabel.setText("Connection error: " + e.getMessage());
                    takeExamBtn.setDisable(false);
                });
            }
        }).start();
    }
}
