package common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Exam implements Serializable {
    private static final long serialVersionUID = 1L;

    private int examId;
    private String examName;
    private String year;
    private String semester;
    private LocalDateTime startDateTime;
    private List<Question> questions;

    public Exam() {}

    public Exam(int examId, String examName, String year, String semester, LocalDateTime startDateTime, List<Question> questions) {
        this.examId = examId;
        this.examName = examName;
        this.year = year;
        this.semester = semester;
        this.startDateTime = startDateTime;
        this.questions = questions;
    }

    public int getExamId() { return examId; }
    public void setExamId(int id) { this.examId = id; }
    public String getExamName() { return examName; }
    public void setExamName(String n) { this.examName = n; }
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime t) { this.startDateTime = t; }
    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> q) { this.questions = q; }
}
