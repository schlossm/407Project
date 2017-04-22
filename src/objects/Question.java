package objects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Question.java
 * Alex Rosenberg
 */
public class Question implements Serializable {
    private String question;
    private ArrayList<String> choices;
    private String correctChoice;
<<<<<<< Updated upstream
    private double points;

    public Question(String question, ArrayList<String> choices, String correctChoice, Double points) {
        this.question = question;
        this.choices = choices;
        this.correctChoice = correctChoice;
=======
    private String selectedChoice;
    private double points;

    public Question(String question, ArrayList<String> choices, String correctChoice, String selectedChoice, double points) {
        this.question = question;
        this.choices = choices;
        this.correctChoice = correctChoice;
        this.selectedChoice = selectedChoice;
>>>>>>> Stashed changes
        this.points = points;
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

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public String toString() {
        return "question: " + this.question
                + "\nchoices: " + this.choices
                + "\ncorrectChoice: " + this.correctChoice
<<<<<<< Updated upstream
                + "\nselectedChoice: " + this.points;
=======
                + "\nselectedChoice: " + this.selectedChoice
                + "\npoints: " + this.points;
>>>>>>> Stashed changes
    }
}
