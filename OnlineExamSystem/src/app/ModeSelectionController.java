package app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ModeSelectionController {

    @FXML private Button serverBtn, clientBtn, langBtn;
    @FXML private java.util.ResourceBundle resources;

    @FXML
    private void handleToggleLang() {
        try {
            boolean wasArabic = resources.getLocale().getLanguage().equals("ar");
            java.util.Locale newLocale = wasArabic ? new java.util.Locale("en") : new java.util.Locale("ar");
            java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("Messages", newLocale);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mode_selection.fxml"), bundle);
            Parent root = loader.load();
            
            Stage stage = (Stage) langBtn.getScene().getWindow();
            stage.setScene(new Scene(root, 500, 250));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleStartServer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/server.fxml"), resources);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("server.fxml"); 
            stage.setScene(new Scene(root, 700, 400));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleJoinExam() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"), resources);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Join Exam.fxml");
            stage.setScene(new Scene(root, 400, 300));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
