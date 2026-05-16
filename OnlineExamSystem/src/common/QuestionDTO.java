package common;

import java.io.Serializable;

public class QuestionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;
    private String text;
    private String choiceA, choiceB, choiceC, choiceD;
    private int questionIndex;
    private int totalQuestions;
    
    public QuestionDTO(String type, String text, int questionIndex, int totalQuestions) {
        this.type = type;
        this.text = text;
        this.questionIndex = questionIndex;
        this.totalQuestions = totalQuestions;
    }

    public String getType() { return type; }
    public String getText() { return text; }
    public int getQuestionIndex() { return questionIndex; }
    public int getTotalQuestions() { return totalQuestions; }

    public void setChoices(String a, String b, String c, String d) {
        this.choiceA = a;
        this.choiceB = b;
        this.choiceC = c;
        this.choiceD = d;
    }

    public String getChoiceA() { return choiceA; }
    public String getChoiceB() { return choiceB; }
    public String getChoiceC() { return choiceC; }
    public String getChoiceD() { return choiceD; }
}
