package objects;

/**
 * Created by Naveen Ganessin on 4/18/2017.
 */
public class Instructor extends User {
    private String officeHours, roomNo;
    public Instructor(){super();};

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
