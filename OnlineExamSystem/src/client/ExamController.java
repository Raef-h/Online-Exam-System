package client;

import common.*;
import javafx.application.Platform;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class ExamController {

    @FXML private Label questionLabel;
    @FXML private RadioButton rbA, rbB, rbC, rbD, rbTrue, rbFalse;
    @FXML private VBox mcqBox, tfBox;
    @FXML private Button nextBtn;
    @FXML private Label errorLabel;
    
    private ToggleGroup mcqGroup, tfGroup;

    private String studentName;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    private QuestionDTO currentQuestion;

    public void initialize(String studentName, Socket socket,
                           ObjectInputStream in, ObjectOutputStream out,
                           Exam exam, boolean isArabic) {
        this.studentName = studentName;
        this.socket = socket;
        this.in = in;
        this.out = out;

        mcqGroup = new ToggleGroup();
        tfGroup  = new ToggleGroup();
        rbA.setToggleGroup(mcqGroup);
        rbB.setToggleGroup(mcqGroup);
        rbC.setToggleGroup(mcqGroup);
        rbD.setToggleGroup(mcqGroup);
        rbTrue.setToggleGroup(tfGroup);
        rbFalse.setToggleGroup(tfGroup);

        new Thread(this::listenForMessages).start();
    }

    private void listenForMessages() {
        try {
            while (true) {
                Message msg = (Message) in.readObject();
                if (msg.getType() == Message.Type.NEXT_QUESTION) {
                    QuestionDTO dto = (QuestionDTO) msg.getData();
                    Platform.runLater(() -> showQuestion(dto));
                } else if (msg.getType() == Message.Type.RESULT) {
                    Result result = (Result) msg.getData();
                    Platform.runLater(() -> showResultDialog(result));
                    break;
                } else if (msg.getType() == Message.Type.ERROR) {
                    Platform.runLater(() -> showError((String) msg.getData()));
                    break;
                }
            }
        } catch (EOFException e) {
            // Server disconnected
        } catch (Exception e) {
            Platform.runLater(() -> showError("Connection error: " + e.getMessage()));
        }
    }

    private void showQuestion(QuestionDTO dto) {
        this.currentQuestion = dto;
        nextBtn.setDisable(false);

        questionLabel.setText(dto.getText());

        if ("MCQ".equals(dto.getType())) {
            mcqBox.setVisible(true); mcqBox.setManaged(true);
            tfBox.setVisible(false); tfBox.setManaged(false);
            rbA.setText(dto.getChoiceA());
            rbB.setText(dto.getChoiceB());
            rbC.setText(dto.getChoiceC());
            rbD.setText(dto.getChoiceD());
            mcqGroup.selectToggle(null);
        } else {
            mcqBox.setVisible(false); mcqBox.setManaged(false);
            tfBox.setVisible(true); tfBox.setManaged(true);
            tfGroup.selectToggle(null);
        }
    }

    @FXML private void handleNext() {
        String answer = "";
        if ("MCQ".equals(currentQuestion.getType())) {
            Toggle sel = mcqGroup.getSelectedToggle();
            if (sel != null) answer = ((RadioButton) sel).getText();
        } else {
            Toggle sel = tfGroup.getSelectedToggle();
            if (sel != null) answer = ((RadioButton) sel).getText();
        }
        
        if (answer.isEmpty()) {
            showError("Please select an answer.");
            return;
        }

        nextBtn.setDisable(true);
        errorLabel.setText("");
        
        try {
            out.writeObject(new Message(Message.Type.SUBMIT_ANSWER, answer));
            out.flush();
        } catch (IOException e) {
            showError("Failed to send answer: " + e.getMessage());
        }
    }

    private void showResultDialog(Result result) {
        String msg = "🎉 Exam Complete!\n\nScore: " + result.getScore() + " / " + result.getTotalQuestions();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setTitle("Final Result");
        alert.setHeaderText("Hello, " + studentName);
        alert.showAndWait();
        
        try { socket.close(); } catch (Exception ignored) {}
        ((Stage) nextBtn.getScene().getWindow()).close();
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
    }
}
