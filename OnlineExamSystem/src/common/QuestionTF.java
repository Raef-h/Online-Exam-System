package common;

public class QuestionTF extends Question {
    private static final long serialVersionUID = 1L;

    private boolean answer;

    public QuestionTF() {}

    public QuestionTF(String questionText, boolean answer) {
        super(questionText, answer ? "True" : "False");
        this.answer = answer;
    }

    public boolean isAnswer() { return answer; }
    public void setAnswer(boolean b) { 
        this.answer = b; 
        setCorrectAnswer(b ? "True" : "False");
    }

    @Override public String getType() { return "TF"; }
}
