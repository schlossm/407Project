package objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringJoiner;

/**
 * Question.java
 * Alex Rosenberg
 */
public class Question implements Serializable {
    private String question;
    private ArrayList<String> choices;
    private String correctChoice;
    private String selectedChoice;

    public Question(String question, ArrayList<String> choices, String correctChoice, String selectedChoice) {
        this.question = question;
        this.choices = choices;
        this.correctChoice = correctChoice;
        this.selectedChoice = selectedChoice;
    }

    public Question() {
        // empty
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String newQ) {
        this.question = newQ;
    }

    public ArrayList<String> getChoices() {
        return this.choices;
    }

    public void setChoices(ArrayList<String> newChoices) {
        this.choices = newChoices;
    }

    public String getCorrectChoice() {
        return this.correctChoice;
    }

    public void setCorrectChoice(String newCorrect) {
        this.correctChoice = newCorrect;
    }

    public String getSelectedChoice() {
        return this.selectedChoice;
    }

    public void setSelectedChoice(String newSelected) {
        this.selectedChoice = newSelected;
    }

    public String toString() {
        return "question: " + this.question
                + "\nchoices: " + this.choices
                + "\ncorrectChoice: " + this.correctChoice
                + "\nselectedChoice: " + this.selectedChoice;
    }
}
