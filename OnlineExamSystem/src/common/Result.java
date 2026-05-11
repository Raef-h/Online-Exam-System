package common;

import java.io.Serializable;

public class Result implements Serializable {
    private static final long serialVersionUID = 1L;

    private String studentName;
    private int examId;
    private String courseName;
    private String year;
    private String semester;
    private int score;
    private int totalQuestions;

    public Result() {}

    public Result(String studentName, int examId, String courseName, String year, String semester, int score, int totalQuestions) {
        this.studentName = studentName;
        this.examId = examId;
        this.courseName = courseName;
        this.year = year;
        this.semester = semester;
        this.score = score;
        this.totalQuestions = totalQuestions;
    }

    public String getStudentName() { return studentName; }
    public int getExamId() { return examId; }
    public String getCourseName() { return courseName; }
    public String getExamName() { return courseName; }
    public String getYear() { return year; }
    public String getSemester() { return semester; }
    public int getScore() { return score; }
    public int getTotalQuestions() { return totalQuestions; }

    public void setStudentName(String s) { studentName = s; }
    public void setExamId(int i) { examId = i; }
    public void setCourseName(String s) { courseName = s; }
    public void setExamName(String s) { courseName = s; }
    public void setYear(String y) { year = y; }
    public void setSemester(String s) { semester = s; }
    public void setScore(int s) { score = s; }
    public void setTotalQuestions(int t) { totalQuestions = t; }
}
