package common;

public class MCQ extends Question {
    private static final long serialVersionUID = 1L;

    private String choiceA, choiceB, choiceC, choiceD;

    public MCQ() {}

    public MCQ(String questionText, String choiceA, String choiceB,
               String choiceC, String choiceD, String correctAnswer) {
        super(questionText, correctAnswer);
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.choiceD = choiceD;
    }

    @Override public String getType() { return "MCQ"; }

    public String getChoiceA() { return choiceA; }
    public String getChoiceB() { return choiceB; }
    public String getChoiceC() { return choiceC; }
    public String getChoiceD() { return choiceD; }
    public void setChoiceA(String s) { choiceA = s; }
    public void setChoiceB(String s) { choiceB = s; }
    public void setChoiceC(String s) { choiceC = s; }
    public void setChoiceD(String s) { choiceD = s; }
}
