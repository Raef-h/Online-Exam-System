package common;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        CONNECT,       // Client→Server: String studentName
        EXAM_LIST,     // Server→Client: List<ExamInfo>
        SELECT_EXAM,   // Client→Server: Integer examId
        EXAM_DATA,     // Server→Client: Exam (we can still use this for the exam details, without questions)
        NEXT_QUESTION, // Server→Client: QuestionDTO
        SUBMIT_ANSWER, // Client→Server: String answer
        SUBMIT,        // Client→Server: List<String> answers (deprecated, but keep enum index to avoid breakage or change it)
        RESULT,        // Server→Client: Result
        ERROR          // Both: String message
    }

    private final Type type;
    private final Object data;

    public Message(Type type, Object data) {
        this.type = type;
        this.data = data;
    }

    public Type getType() { return type; }
    public Object getData() { return data; }
}
