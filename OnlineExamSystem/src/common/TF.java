package common;

public class TF extends Question {
    private static final long serialVersionUID = 1L;

    public TF() {}

    public TF(String questionText, String correctAnswer) {
        super(questionText, correctAnswer); // correctAnswer: "True" or "False"
    }

    @Override public String getType() { return "TF"; }
}
