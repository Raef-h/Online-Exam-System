package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;

public class ServerApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/server.fxml"));
        BorderPane root = loader.load();

        Scene scene = new Scene(root, 950, 620);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle("Online Exam System – Server (Admin)");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(850);
        primaryStage.setMinHeight(550);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
