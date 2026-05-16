package server;

import common.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ServerController {

    @FXML private TableView<Exam> examTable;
    @FXML private TableColumn<Exam, String> colName;
    @FXML private TableColumn<Exam, String> colTime;
    @FXML private TableColumn<Exam, String> colActive;
    @FXML private TableColumn<Exam, String> colComplete;
    @FXML private TableColumn<Exam, String> colStatus;

    private ExamServer server;
    private DatabaseManager db;
    private ObservableList<Exam> activeExams = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            db = new DatabaseManager();
        } catch (Exception e) {
            showAlert("DB Error: " + e.getMessage());
        }

        colName.setCellValueFactory(data -> {
            Exam e = data.getValue();
            return new SimpleStringProperty(e.getExamName() + " " + e.getSemester() + " " + e.getYear());
        });
        colTime.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        
        colActive.setCellValueFactory(data -> {
            if (server == null) return new SimpleStringProperty("0");
            long count = server.getActiveClients().stream()
                .filter(h -> data.getValue().getExamName().equals(h.getExamName()))
                .count();
            return new SimpleStringProperty(String.valueOf(count));
        });
        
        colComplete.setCellValueFactory(data -> {
            if (db == null) return new SimpleStringProperty("0");
            try {
                int count = db.getCompletedCount(data.getValue().getExamName());
                return new SimpleStringProperty(String.valueOf(count));
            } catch (Exception e) {
                return new SimpleStringProperty("0");
            }
        });

        colStatus.setCellValueFactory(data -> {
            long minutes = java.time.Duration.between(data.getValue().getStartDateTime(), java.time.LocalDateTime.now()).toMinutes();
            return new SimpleStringProperty(minutes < 60 ? "Active" : "Ended");
        });

        examTable.setItems(activeExams);

        startServer(3000);

        javafx.animation.Timeline refreshTimer = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(2), e -> examTable.refresh())
        );
        refreshTimer.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        refreshTimer.play();
    }

    private void startServer(int port) {
        server = new ExamServer(port, db);
        new Thread(() -> {
            try { server.start(); }
            catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    @FXML
    private void handleNewExamMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/new_exam.fxml"));
            Parent root = loader.load();
            
            NewExamController ctrl = loader.getController();
            ctrl.initialize(db, this);
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("New Exam.fxml");
            stage.setScene(new Scene(root, 300, 350));
            stage.showAndWait();
        } catch (Exception e) {
            showAlert("Failed to open New Exam window: " + e.getMessage());
        }
    }

    public void addExam(Exam exam) {
        activeExams.add(exam);
        server.addExam(exam);
    }



    @FXML
    private void handleHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("Group Members");
        alert.setContentText("Project developed by:\n1. Raef Alharbi\n2. Mohanad Alqarni");
        alert.showAndWait();
    }

    private void showAlert(String msg) {
        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, msg).showAndWait());
    }
}
