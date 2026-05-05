package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        VBox root = loader.load();

        Scene scene = new Scene(root, 480, 420);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle("Online Exam System – Student Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
