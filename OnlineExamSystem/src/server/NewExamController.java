package server;

import common.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NewExamController {

    @FXML private TextField courseNameField;
    @FXML private ComboBox<String> yearCombo;
    @FXML private ComboBox<String> semesterCombo;
    @FXML private Label mcqLabel;
    @FXML private Label tfLabel;
    @FXML private Label errorLabel;

    private List<Question> importedQuestions = new ArrayList<>();
    private DatabaseManager db;
    private ServerController parent;

    public void initialize(DatabaseManager db, ServerController parent) {
        this.db = db;
        this.parent = parent;
        
        yearCombo.getItems().addAll("2024", "2025", "2026", "2027");
        yearCombo.setValue("2026");
        
        semesterCombo.getItems().addAll("Fall", "Spring", "Summer");
        semesterCombo.setValue("Spring");
    }

    @FXML
    private void handleImport() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = chooser.showOpenDialog(courseNameField.getScene().getWindow());
        if (file != null) {
            importedQuestions = parseQuestionFile(file);
            int mcq = 0, tf = 0;
            for (Question q : importedQuestions) {
                if (q instanceof MCQ) mcq++;
                else tf++;
            }
            mcqLabel.setText("MCQ: " + mcq);
            tfLabel.setText("TF: " + tf);
        }
    }

    @FXML
    private void handleCreateExam() {
        String courseName = courseNameField.getText().trim();
        String year = yearCombo.getValue();
        String semester = semesterCombo.getValue();

        if (courseName.isEmpty()) {
            errorLabel.setText("Please enter a course name.");
            return;
        }
        if (importedQuestions.isEmpty()) {
            errorLabel.setText("Please import exam questions.");
            return;
        }

        Exam exam = new Exam(0, courseName, year, semester, LocalDateTime.now(), importedQuestions);
        try {
            if (db == null) {
                errorLabel.setText("DB connection failed.");
                return;
            }
            int id = db.saveExam(exam);
            exam.setExamId(id);
            parent.addExam(exam);
            ((Stage) courseNameField.getScene().getWindow()).close();
        } catch (Exception e) {
            errorLabel.setText("DB Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("Group Members");
        alert.setContentText("Project developed by:\n1. Raef Alharbi\n2. Mohanad Alqarni");
        alert.showAndWait();
    }

    private List<Question> parseQuestionFile(File file) {
        List<Question> list = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            int i = 0;
            while (i < lines.size()) {
                String type = lines.get(i++).trim();
                if (type.isEmpty()) continue;
                String text = lines.get(i++).trim();
                String ans = lines.get(i++).trim();
                if (type.equalsIgnoreCase("MCQ")) {
                    String a = lines.get(i++).trim();
                    String b = lines.get(i++).trim();
                    String c = lines.get(i++).trim();
                    String d = lines.get(i++).trim();
                    list.add(new MCQ(text, ans, a, b, c, d));
                } else if (type.equalsIgnoreCase("TF")) {
                    list.add(new TF(text, ans));
                }
            }
        } catch (Exception e) {
            errorLabel.setText("Error reading file: " + e.getMessage());
        }
        return list;
    }
}
