package objects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * QuizAssignment.java
 * Alex Rosenberg
 */
public class QuizAssignment extends Assignment implements Serializable {
    private ArrayList<Question> questions;

    public QuizAssignment(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public QuizAssignment() {
        // empty
    }

    public ArrayList<Question> getQuestions() {
        return this.questions;
    }

    public void setQuestions(ArrayList<Question> newQs) {
        this.questions = newQs;
    }

    public String toString() {
        return super.toString()
                + "\nquestions: " + this.questions;
    }
}
