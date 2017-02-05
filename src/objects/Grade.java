package objects;

/**
 * Grade.java
 * Alex Rosenberg
 */
public class Grade {
    private String score;
    private String userID;
    private int assignmentID;

    public Grade(String userID, int assignmentID, String score) {
        this.userID = userID;
        this.assignmentID = assignmentID;
        this.score = score;
    }

    public prevGrade(String userID, int assignmentID) {
        // TODO: get previously-created grade with these attributes from the DB
    }

    public String getScore() {
        return this.score;
    }

    public void setScore(String newScore) {
        this.score = newScore;
        // TODO: set score in DB
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
