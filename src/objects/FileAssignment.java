package objects;

import java.io.Serializable;

/**
 * FileAssignment.java
 * Alex Rosenberg
 */
public class FileAssignment extends Assignment implements Serializable {
    private String filename;

    public FileAssignment(String filename) {
        this.filename = filename;
    }

    public FileAssignment() {
        // empty
    }

    public String toString() {
        return super.toString()
                + "\nfilename: " + this.filename;
    }
}
