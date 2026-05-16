package client;

import common.*;
import javafx.application.Platform;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ResourceBundle;

public class ExamController {

    @FXML private Label questionLabel, timerLabel, counterLabel;
    @FXML private RadioButton rbA, rbB, rbC, rbD, rbTrue, rbFalse;
    @FXML private VBox mcqBox, tfBox;
    @FXML private Button nextBtn;
    @FXML private Label errorLabel;
    @FXML private ResourceBundle resources;
    
    private javafx.animation.Timeline timerTimeline;
    private java.time.LocalDateTime examStartTime;
    
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

        this.examStartTime = exam.getStartDateTime();
        startTimer();

        new Thread(this::listenForMessages).start();
    }
    
    private void startTimer() {
        timerTimeline = new javafx.animation.Timeline(new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), e -> {
            java.time.Duration elapsed = java.time.Duration.between(examStartTime, java.time.LocalDateTime.now());
            long remaining = 3600 - elapsed.getSeconds();
            
            if (remaining <= 0) {
                timerLabel.setText("Time Left: 00:00");
                timerTimeline.stop();
                return;
            }
            
            long mins = remaining / 60;
            long secs = remaining % 60;
            timerLabel.setText(String.format("Time Left: %02d:%02d", mins, secs));
        }));
        timerTimeline.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        timerTimeline.play();
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
        } catch (Exception e) {
            Platform.runLater(() -> showError("Connection error: " + e.getMessage()));
        }
    }

    private void showQuestion(QuestionDTO dto) {
        this.currentQuestion = dto;
        nextBtn.setDisable(false);

        counterLabel.setText("Question " + (dto.getQuestionIndex() + 1) + "/" + dto.getTotalQuestions());
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
        if (resources != null && resources.getLocale().getLanguage().equals("ar")) {
            msg = "🎉 اكتمل الامتحان!\n\nالدرجة: " + result.getScore() + " / " + result.getTotalQuestions();
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setTitle("Final Result");
        alert.setHeaderText("Hello, " + studentName);
        alert.showAndWait();
        
        if (timerTimeline != null) timerTimeline.stop();
        try { socket.close(); } catch (Exception ignored) {}
        ((Stage) nextBtn.getScene().getWindow()).close();
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
    }
}
