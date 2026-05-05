package app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ModeSelectionController {

    @FXML private Button serverBtn, clientBtn, langBtn;
    
    private boolean isArabic = false;

    @FXML
    private void handleToggleLang() {
        try {
            isArabic = !isArabic;
            java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("Messages", new java.util.Locale(isArabic ? "ar" : "en"));
            Parent root = FXMLLoader.load(getClass().getResource("/mode_selection.fxml"), bundle);
            Stage stage = (Stage) langBtn.getScene().getWindow();
            stage.setScene(new Scene(root, 500, 250));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleStartServer() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/server.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Untitled"); // As shown in the PDF image
            stage.setScene(new Scene(root, 700, 400));
            stage.show();
            // Close selection window if needed: ((Stage) serverBtn.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleJoinExam() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Join Exam.fxml");
            stage.setScene(new Scene(root, 400, 300));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
