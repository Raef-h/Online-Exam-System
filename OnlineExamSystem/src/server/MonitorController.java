package server;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import javafx.beans.property.*;

public class MonitorController {

    @FXML private TableView<StudentRow> table;
    @FXML private TableColumn<StudentRow, String> colStudent, colExam, colProgress, colScore, colIp;
    @FXML private Label totalLabel;

    private ExamServer server;
    private ObservableList<StudentRow> rows = FXCollections.observableArrayList();
    private Timeline refreshTimer;

    @FXML
    public void initialize() {
        colStudent.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colExam.setCellValueFactory(new PropertyValueFactory<>("examName"));
        colProgress.setCellValueFactory(new PropertyValueFactory<>("progress"));
        colScore.setCellValueFactory(new PropertyValueFactory<>("score"));
        colIp.setCellValueFactory(new PropertyValueFactory<>("ip"));
        table.setItems(rows);
    }

    public void setServer(ExamServer server) {
        this.server = server;
        refreshTimer = new Timeline(new KeyFrame(Duration.seconds(2), e -> refresh()));
        refreshTimer.setCycleCount(Timeline.INDEFINITE);
        refreshTimer.play();
    }

    private void refresh() {
        if (server == null) return;
        rows.clear();
        for (ClientHandler h : server.getActiveClients()) {
            String name = h.getStudentName() != null ? h.getStudentName() : "Connecting...";
            rows.add(new StudentRow(
                name, 
                h.getExamName(), 
                h.getProgress(), 
                h.getScore(), 
                h.getIp()
            ));
        }
        totalLabel.setText("Active Students: " + rows.size());
    }

    public static class StudentRow {
        private final SimpleStringProperty studentName, examName, progress, score, ip;

        public StudentRow(String studentName, String examName, String progress, String score, String ip) {
            this.studentName = new SimpleStringProperty(studentName);
            this.examName    = new SimpleStringProperty(examName);
            this.progress    = new SimpleStringProperty(progress);
            this.score       = new SimpleStringProperty(score);
            this.ip          = new SimpleStringProperty(ip);
        }

        public String getStudentName() { return studentName.get(); }
        public String getExamName()    { return examName.get(); }
        public String getProgress()    { return progress.get(); }
        public String getScore()       { return score.get(); }
        public String getIp()          { return ip.get(); }
    }
}
