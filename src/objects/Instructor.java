package objects;

/**
 * Created by Naveen Ganessin on 4/18/2017.
 */
public class Instructor extends User {
    private String officeHours, roomNo;
    private int instructorId;
    public Instructor(){super();};

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public String getOfficeHours() {
        return officeHours;
    }

    public void setOfficeHours(String officeHours) {
        this.officeHours = officeHours;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }
}
