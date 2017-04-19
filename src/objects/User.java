package objects;

import java.io.Serializable;

/**
 * User.java
 * Alex Rosenberg
 */
public class User implements Serializable {
    private String userID;
    private String email;
    private String birthday;
    private standing standing;
    private String firstName;
    private String lastName;
    private userType userType;
    private int maxStorage;

    public User(){};

	public String getUserID() {
        return this.userID;
    }

    public void setUserID(String newID) {
        this.userID = newID;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String newBirthday) {
        this.birthday = newBirthday;
    }

    public standing getStanding() {
        return this.standing;
    }

    public void setStanding(standing newStanding) {
        this.standing = newStanding;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String newFirst) {
        this.firstName = newFirst;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String newLast) {
        this.lastName = newLast;
    }

    public userType getUserType() {
        return this.userType;
    }

    public void setUserType(userType newUserType) {
        this.userType = newUserType;
    }

    public int getMaxStorage() {
        return this.maxStorage;
    }

    public void setMaxStorage(int newMax) {
        this.maxStorage = newMax;
    }

    public String toString() {
        return "userID: " + this.getUserID() + "\nemail: " + this.getEmail()
                + "\nbirthday: " + this.getBirthday() + "\nstanding: " + this.getStanding()
                + "\nfirstName: " + this.getFirstName() + "\nlastName: " + this.getLastName()
                + "\nuserType: " + this.getUserType() + "\nmaxStorage: " + this.getMaxStorage();
    }
}
