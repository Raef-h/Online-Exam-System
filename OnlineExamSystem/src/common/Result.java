package common;

import java.io.Serializable;

public class Result implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentName;
    private int examId;
    private String examName;
    private int score;
    private int totalQuestions;

    public Result() {}

    public Result(String studentName, int examId, String examName, int score, int totalQuestions) {
        this.studentName = studentName;
        this.examId = examId;
        this.examName = examName;
        this.score = score;
        this.totalQuestions = totalQuestions;
    }

    public String getStudentName() { return studentName; }
    public int getExamId() { return examId; }
    public String getExamName() { return examName; }
    public int getScore() { return score; }
    public int getTotalQuestions() { return totalQuestions; }

    public void setStudentName(String s) { studentName = s; }
    public void setExamId(int i) { examId = i; }
    public void setExamName(String s) { examName = s; }
    public void setScore(int s) { score = s; }
    public void setTotalQuestions(int t) { totalQuestions = t; }
}
