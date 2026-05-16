package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("Messages", new java.util.Locale("en"));
        Parent root = FXMLLoader.load(getClass().getResource("/mode_selection.fxml"), bundle);
        primaryStage.setTitle("mode_selection.fxml");
        primaryStage.setScene(new Scene(root, 500, 250));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
