package common;

import java.io.Serializable;

public class StudentProgress implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentName;
    private String examName;
    private int currentQuestion;
    private int totalQuestions;
    private int currentScore;
    private String ip;

    public StudentProgress(String studentName, String examName,
                           int currentQuestion, int totalQuestions,
                           int currentScore, String ip) {
        this.studentName = studentName;
        this.examName = examName;
        this.currentQuestion = currentQuestion;
        this.totalQuestions = totalQuestions;
        this.currentScore = currentScore;
        this.ip = ip;
    }

    public String getStudentName() { return studentName; }
    public String getExamName() { return examName; }
    public int getCurrentQuestion() { return currentQuestion; }
    public void setCurrentQuestion(int q) { currentQuestion = q; }
    public int getTotalQuestions() { return totalQuestions; }
    public int getCurrentScore() { return currentScore; }
    public void setCurrentScore(int s) { currentScore = s; }
    public String getIp() { return ip; }
}
