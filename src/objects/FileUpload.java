package objects;

import java.io.Serializable;

/**
 * FileUpload.java
 * Alex Rosenberg
 */
public class FileUpload implements Serializable {
    private String name;
    private String location;
    private int courseID;
    private boolean isPrivate;

    public FileUpload() {
        // empty constructor
    }

    public FileUpload(String location) {
        // TODO: Load file from DB
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String newLoc) {
        this.location = newLoc;
    }

    public int getCourseID() {
        return this.courseID;
    }

    public void setCourseID(int newID) {
        this.courseID = newID;
    }

    public boolean getIsPrivate() {
        return this.isPrivate;
    }

    public void setIsPrivate(boolean newPrivacy) {
        this.isPrivate = newPrivacy;
    }

    public String toString() {
        return "name: " + this.name
                + "\nlocation: " + this.location
                + "\ncourseID: " + this.courseID
                + "\nisPrivate: " + this.isPrivate;
    }
}
