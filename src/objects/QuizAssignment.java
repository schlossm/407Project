package objects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * QuizAssignment.java
 * Alex Rosenberg
 */
public class QuizAssignment extends Assignment implements Serializable {
    protected ArrayList<Question> questions;
    protected int assignmentId;

    public QuizAssignment(int assignmentId, ArrayList<Question> questions) {
        this.assignmentId = assignmentId;
        this.questions = questions;
    }

    public QuizAssignment(Assignment a) {
        this.title = a.title;
        this.assignmentID = a.assignmentID;
        this.courseID = a.courseID;
        this.openDate = a.openDate;
        this.dueDate = a.dueDate;
        this.instructions = a.instructions;
        this.type = a.type;
        this.maxPoints = a.maxPoints;
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
