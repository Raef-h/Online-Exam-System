package common;

public class QuestionMCQ extends Question {
    private static final long serialVersionUID = 1L;

    private String choice1;
    private boolean choice1Correct;
    private String choice2;
    private boolean choice2Correct;
    private String choice3;
    private boolean choice3Correct;
    private String choice4;
    private boolean choice4Correct;

    public QuestionMCQ() {}

    public QuestionMCQ(String questionText, String c1, boolean c1c, String c2, boolean c2c, 
                      String c3, boolean c3c, String c4, boolean c4c) {
        super(questionText, "");
        this.choice1 = c1;
        this.choice1Correct = c1c;
        this.choice2 = c2;
        this.choice2Correct = c2c;
        this.choice3 = c3;
        this.choice3Correct = c3c;
        this.choice4 = c4;
        this.choice4Correct = c4c;
        
        if (c1c) setCorrectAnswer(c1);
        else if (c2c) setCorrectAnswer(c2);
        else if (c3c) setCorrectAnswer(c3);
        else if (c4c) setCorrectAnswer(c4);
    }

    @Override public String getType() { return "MCQ"; }

    public String getChoice1() { return choice1; }
    public void setChoice1(String s) { choice1 = s; }
    public boolean isChoice1Correct() { return choice1Correct; }
    public void setChoice1Correct(boolean b) { choice1Correct = b; }

    public String getChoice2() { return choice2; }
    public void setChoice2(String s) { choice2 = s; }
    public boolean isChoice2Correct() { return choice2Correct; }
    public void setChoice2Correct(boolean b) { choice2Correct = b; }

    public String getChoice3() { return choice3; }
    public void setChoice3(String s) { choice3 = s; }
    public boolean isChoice3Correct() { return choice3Correct; }
    public void setChoice3Correct(boolean b) { choice3Correct = b; }

    public String getChoice4() { return choice4; }
    public void setChoice4(String s) { choice4 = s; }
    public boolean isChoice4Correct() { return choice4Correct; }
    public void setChoice4Correct(boolean b) { choice4Correct = b; }

    public String getChoiceA() { return choice1; }
    public String getChoiceB() { return choice2; }
    public String getChoiceC() { return choice3; }
    public String getChoiceD() { return choice4; }
}
