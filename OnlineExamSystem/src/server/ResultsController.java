package server;

import common.Result;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class ResultsController {

    @FXML private TableView<Result> resultsTable;
    @FXML private TableColumn<Result, String> colStudent, colCourse, colYear, colSemester;
    @FXML private TableColumn<Result, Integer> colScore;

    private DatabaseManager db;

    public void initialize(DatabaseManager db) {
        this.db = db;
        
        colStudent.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colCourse.setCellValueFactory(new PropertyValueFactory<>("examName"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colSemester.setCellValueFactory(new PropertyValueFactory<>("semester"));
        colScore.setCellValueFactory(new PropertyValueFactory<>("score"));
        
        loadResults();
    }

    private void loadResults() {
        try {
            List<Result> list = db.getAllResults();
            ObservableList<Result> data = FXCollections.observableArrayList(list);
            resultsTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose() {
        ((Stage) resultsTable.getScene().getWindow()).close();
    }
}
