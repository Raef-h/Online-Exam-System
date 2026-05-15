package common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Exam implements Serializable {
    private static final long serialVersionUID = 1L;

    private int examId;
    private String courseName;
    private String year;
    private String semester;
    private LocalDateTime startDateTime;
    private List<Question> questions = new java.util.ArrayList<>();

    public Exam() {}

    public Exam(int examId, String courseName, String year, String semester, LocalDateTime startDateTime, List<Question> questions) {
        this.examId = examId;
        this.courseName = courseName;
        this.year = year;
        this.semester = semester;
        this.startDateTime = startDateTime;
        this.questions = questions;
    }

    public int getExamId() { return examId; }
    public void setExamId(int id) { this.examId = id; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String n) { this.courseName = n; }
    public String getExamName() { return courseName; } // Keep for backward compatibility if needed
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime t) { this.startDateTime = t; }
    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> q) { this.questions = q; }
}
