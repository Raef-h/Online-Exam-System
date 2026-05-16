package common;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        CONNECT,
        EXAM_LIST,
        SELECT_EXAM,
        EXAM_DATA,
        NEXT_QUESTION,
        SUBMIT_ANSWER,
        SUBMIT,
        RESULT,
        ERROR
    }

    private final Type type;
    private final Object data;

    public Message(Type type, Object data) {
        this.type = type;
        this.data = data;
    }

    public Type getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}
