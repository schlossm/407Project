package objects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * QuizAssignment.java
 * Alex Rosenberg
 */
public class QuizAssignment extends Assignment implements Serializable {
    private ArrayList<Question> questions = new ArrayList<>();
    private int assignmentId = -1;

    public QuizAssignment(int assignmentId, ArrayList<Question> questions) {
        this.assignmentId = assignmentId;
        this.questions = questions;
    }

    public QuizAssignment() {
        // empty
    }

    public ArrayList<Question> getQuestions() {
        return this.questions;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public void setQuestions(ArrayList<Question> newQs) {
        this.questions = newQs;
    }

    public String toString() {
        return super.toString()
                + "\nquestions: " + this.questions;
    }

    public double getMaxPoints() {
        double points = 0;
        for(Question question: questions) {
            points += question.getPoints();
        }
        return points;
    }
}
