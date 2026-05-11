package common;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ExamInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private int examId;
    private String courseName;
    private LocalDateTime startDateTime;
    private int questionCount;

    public ExamInfo() {}

    public ExamInfo(int examId, String courseName, LocalDateTime startDateTime, int questionCount) {
        this.examId = examId;
        this.courseName = courseName;
        this.startDateTime = startDateTime;
        this.questionCount = questionCount;
    }

    public int getExamId() { return examId; }
    public String getCourseName() { return courseName; }
    public String getExamName() { return courseName; }
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public int getQuestionCount() { return questionCount; }

    @Override
    public String toString() {
        return courseName + "  [" + questionCount + " questions]";
    }
}
