package objects;

import java.io.Serializable;

/**
 * FileUpload.java
 * Alex Rosenberg
 */
public class FileUpload implements Serializable {
    private int documentid;
    private String title;
    private String description;
    private String path;
    private String authoruserid;
    private int courseid;
    private int assignmentid;
    private int isPrivate;

    public int getDocumentid() {
        return documentid;
    }

    public void setDocumentid(int documentid) {
        this.documentid = documentid;
    }

    public FileUpload(String title, String description, String path, String authoruserid, int courseid, int assignmentid, int isPrivate, int documentid) {
        this.title = title;
        this.description = description;
        this.path = path;
        this.authoruserid = authoruserid;
        this.courseid = courseid;
        this.assignmentid = assignmentid;
        this.isPrivate = isPrivate;
    }

    public FileUpload() {
        // empty constructor
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAuthoruserid() {
        return authoruserid;
    }

    public void setAuthoruserid(String authoruserid) {
        this.authoruserid = authoruserid;
    }

    public int getCourseID() {
        return courseid;
    }

    public void setCourseID(int courseID) {
        this.courseid = courseid;
    }

    public int getAssignmentid() {
        return assignmentid;
    }

    public void setAssignmentid(int assignmentid) {
        this.assignmentid = assignmentid;
    }

    public int getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(int isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String toString() {
        return "title: " + this.title
                + "\ndescription: " + this.description
                + "\npath: " + this.path
                + "\nauthoruserid: " + this.authoruserid
                + "\ncourseid: " + this.courseid
                + "\nassignmentid" + this.assignmentid
                + "\nisPrivate" + this.isPrivate;
    }
}
