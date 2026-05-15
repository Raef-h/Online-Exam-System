package common;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ExamInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private int examId;
    private String courseName;
    private String year;
    private String semester;
    private LocalDateTime startDateTime;
    private int questionCount;

    public ExamInfo() {}

    public ExamInfo(int examId, String courseName, String year, String semester, LocalDateTime startDateTime, int questionCount) {
        this.examId = examId;
        this.courseName = courseName;
        this.year = year;
        this.semester = semester;
        this.startDateTime = startDateTime;
        this.questionCount = questionCount;
    }

    public int getExamId() { return examId; }
    public String getCourseName() { return courseName; }
    public String getYear() { return year; }
    public String getSemester() { return semester; }
    public String getExamName() { return courseName; }
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public int getQuestionCount() { return questionCount; }

    @Override
    public String toString() {
        return courseName + "  [" + questionCount + " questions]";
    }
}
