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
    // TODO: privacy field (what type?)

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
}
