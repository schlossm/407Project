package objects;

import java.io.Serializable;

/**
 * Assignment.java
 * Alex Rosenberg
 */
public class Assignment implements Serializable {
    private String title;
    private int assignmentID;
    private int courseID;
    private String openDate;
    private String dueDate;
    private String instructions;

    public Assignment(int assignmentID) {
        this.assignmentID = assignmentID;
    }

    public Assignment(String title, String instructions, int courseID, String openDate, String dueDate) {
        this.title = title;
        this.instructions = instructions;
        this.courseID = courseID;
        this.openDate = openDate;
        this.dueDate = dueDate;
    }

    public Assignment() {
        // empty
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public int getAssignmentID() {
        return this.assignmentID;
    }

    public void setAssignmentID(int newAssignmentID) {
        this.assignmentID = newAssignmentID;
    }

    public int getCourseID() {
        return this.courseID;
    }

    public void setCourseID(int newCourseID) {
        this.courseID = newCourseID;
    }

    public String getOpenDate() {
        return this.openDate;
    }

    public void setOpenDate(String newOpen) {
        this.openDate = newOpen;
    }

    public String getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(String newDue) {
        this.dueDate = newDue;
    }

    public String getInstructions() {
        return this.instructions;
    }

    public void setInstructions(String newInstr) {
        this.instructions = newInstr;
    }

    public String toString() {
        return "title: " + this.title
                + "\nassignmentID: " + this.assignmentID
                + "\ncourseID: " + this.courseID
                + "\nopenDate: " + this.openDate
                + "\ndueDate: " + this.dueDate
                + "\ninstructions: " + this.instructions;
    }
}
