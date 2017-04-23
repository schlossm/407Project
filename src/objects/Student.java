package objects;

/**
 * Created by Naveen Ganessin on 4/21/2017.
 */
public class Student extends User{
    private int studentId;

    public Student() {
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
}
