package common;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ExamInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private int examId;
    private String examName;
    private LocalDateTime startDateTime;
    private int questionCount;

    public ExamInfo() {}

    public ExamInfo(int examId, String examName, LocalDateTime startDateTime, int questionCount) {
        this.examId = examId;
        this.examName = examName;
        this.startDateTime = startDateTime;
        this.questionCount = questionCount;
    }

    public int getExamId() { return examId; }
    public String getExamName() { return examName; }
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public int getQuestionCount() { return questionCount; }

    @Override
    public String toString() {
        return examName + "  [" + questionCount + " questions]";
    }
}
