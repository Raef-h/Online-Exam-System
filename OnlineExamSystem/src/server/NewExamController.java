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
        
        int currentYear = java.time.LocalDate.now().getYear();
        yearCombo.getItems().addAll(
            String.valueOf(currentYear - 2),
            String.valueOf(currentYear - 1),
            String.valueOf(currentYear)
        );
        yearCombo.setValue(String.valueOf(currentYear));
        
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
                if (q instanceof QuestionMCQ) mcq++;
                else if (q instanceof QuestionTF) tf++;
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

        // Exam name is combined in the constructor or we can keep them separate
        Exam exam = new Exam(0, courseName, year, semester, LocalDateTime.now(), importedQuestions);
        try {
            if (db == null) {
                errorLabel.setText("DB connection failed.");
                return;
            }
            int id = db.saveExam(exam);
            exam.setExamId(id);
            parent.addExam(exam);
            LogManager.log("EXAM_CREATED: " + courseName + " (" + semester + " " + year + ") with " + importedQuestions.size() + " questions");
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
            int lineNum = 0;
            for (String line : lines) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(";");
                
                String type = parts[0].trim();
                if (type.equalsIgnoreCase("TF")) {
                    if (parts.length < 3) {
                        System.err.println("Line " + lineNum + ": Invalid TF format. Expected TF;Text;Answer");
                        continue;
                    }
                    String text = parts[1].trim();
                    String ans = parts[2].trim(); // TRUE or FALSE
                    list.add(new QuestionTF(text, ans.equalsIgnoreCase("TRUE")));
                } else if (type.equalsIgnoreCase("MCQ")) {
                    // PDF Format: MCQ;Text;C1;M1;C2;M2;C3;M3;C4;M4
                    if (parts.length < 10) {
                        System.err.println("Line " + lineNum + ": Invalid MCQ format. Expected 10 semicolon-separated parts.");
                        continue;
                    }
                    String text = parts[1].trim();
                    String c1 = parts[2].trim();
                    String m1 = parts[3].trim();
                    String c2 = parts[4].trim();
                    String m2 = parts[5].trim();
                    String c3 = parts[6].trim();
                    String m3 = parts[7].trim();
                    String c4 = parts[8].trim();
                    String m4 = parts[9].trim();

                    list.add(new QuestionMCQ(text, 
                        c1, m1.equalsIgnoreCase("CORRECT"),
                        c2, m2.equalsIgnoreCase("CORRECT"),
                        c3, m3.equalsIgnoreCase("CORRECT"),
                        c4, m4.equalsIgnoreCase("CORRECT")
                    ));
                } else {
                    System.err.println("Line " + lineNum + ": Unknown question type: " + type);
                }
            }
        } catch (Exception e) {
            errorLabel.setText("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
        if (list.isEmpty()) {
            errorLabel.setText("No valid questions found. Check console for details.");
        }
        return list;
    }
}
