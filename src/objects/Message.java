package objects;

import java.io.Serializable;

/**
 * Message.java
 * Alex Rosenberg
 */
public class Message implements Serializable {
    private String title;
    private String text;
    private String timestamp;
    private int id;
    private String authorUserId;
    private int courseId;

    public Message() {
        // empty constructor
    }

    public Message(int id, String title, String text, String timestamp, String authorUserId, int courseId) {
        this.title = title;
        this.text = text;
        this.timestamp = timestamp;
        this.id = id;
        this.authorUserId = authorUserId;
        this.courseId = courseId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String newText) {
        this.text = newText;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public int getId() {
        return id;
    }

    public String getAuthorUserId() {
        return authorUserId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAuthorUserId(String authorUserId) {
        this.authorUserId = authorUserId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setTimestamp(String newTimestamp) {
        this.timestamp = newTimestamp;
    }

    public String toString() {
        return "title: " + this.title
                + "\ntext: " + this.text
                + "\ntimestamp: " + this.timestamp;
    }
}
