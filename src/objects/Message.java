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

    public Message() {
        // empty constructor
    }

    public Message(String title, String text, String timestamp) {
        this.title = title;
        this.text = text;
        this.timestamp = timestamp;
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

    public void setTimestamp(String newTimestamp) {
        this.timestamp = newTimestamp;
    }

    public String toString() {
        return "title: " + this.title
                + "\ntext: " + this.text
                + "\ntimestamp: " + this.timestamp;
    }
}
