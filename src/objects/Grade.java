package objects;

import java.io.Serializable;

/**
 * Grade.java
 * Alex Rosenberg
 */
public class Grade implements Serializable {
    private String score;
    private String userID;
    private int assignmentID;
    private String assignmentName;



    public Grade(String userID, int assignmentID, String score, String assignmentName) {
        this.userID = userID;
        this.assignmentID = assignmentID;
        this.score = score;
        this.assignmentName = assignmentName;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public void setAssignmentName(String assignmentName) {
        this.assignmentName = assignmentName;
    }

    /*public Grade prevGrade(String userID, int assignmentID) {
        // constructor canceled: get previously-created grade with these attributes from the DB
        return null;
    }*/

    public String getScore() {
        return this.score;
    }

    // NOTE: does not change value in DB
    public void setScore(String newScore) {
        this.score = newScore;
    }

    public String getUserID() {
        return this.userID;
    }

    public int getAssignmentID() {
        return this.assignmentID;
    }

    /*
     * NOTE: there are no mutators for userID or assignmentID
     * because there should never be a time that those are set
     * other than upon creation of the Grade object.
     */

    public String toString() {
        return "score: " + this.getScore() + "\nuserID: " + this.getUserID()
                + "\nassignmentID: " + getAssignmentID();
    }
}
