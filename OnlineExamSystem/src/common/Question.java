package common;

import java.io.Serializable;

public abstract class Question implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String questionText;
    protected String correctAnswer;

    public Question() {}

    public Question(String questionText, String correctAnswer) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String t) { this.questionText = t; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String a) { this.correctAnswer = a; }

    public abstract String getType();
}
