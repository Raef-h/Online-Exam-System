package common;

public class QuestionTF extends Question {
    private static final long serialVersionUID = 1L;

    private boolean answer;

    public QuestionTF() {}

    public QuestionTF(String questionText, boolean answer) {
        super(questionText, answer ? "TRUE" : "FALSE");
        this.answer = answer;
    }

    public boolean isAnswer() { return answer; }
    public void setAnswer(boolean b) { 
        this.answer = b; 
        setCorrectAnswer(b ? "TRUE" : "FALSE");
    }

    @Override public String getType() { return "TF"; }
}
