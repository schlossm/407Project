package objects;

/**
 * User.java
 * Alex Rosenberg
 */
public class User {
    private String userID;
    private String email;
    private String birthday;
    private standing standing;
    private String firstName;
    private String lastName;
    private userType userType;
    private int maxStorage;

    public User(String userID) {
        // TODO: get user with userID from database and return it
    }

    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String newID) {
        this.userID = newID;
        // TODO: set the user's ID in the DB
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
        // TODO: set the user's email in the DB
    }

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String newBirthday) {
        this.birthday = newBirthday;
        // TODO: set the user's birthday in the DB
    }

    public standing getStanding() {
        return this.standing;
    }

    public void setStanding(standing newStanding) {
        this.standing = newStanding;
        // TODO: set the user's standing in the DB
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String newFirst) {
        this.firstName = newFirst;
        // TODO: set the user's firstName in the DB
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String newLast) {
        this.lastName = newLast;
        // TODO: set user's lastName in the DB
    }

    public userType getUserType() {
        return this.userType;
    }

    public void setUserType(userType newUserType) {
        this.userType = newUserType;
        // TODO: set user's userType in the DB
    }

    public int getMaxStorage() {
        return this.maxStorage;
    }

    public void setMaxStorage(int newMax) {
        this.maxStorage = newMax;
        // TODO: set user's maxStorage in the DB
    }

    public String toString() {
        return "userID: " + this.getUserID() + "\nemail: " + this.getEmail()
                + "\nbirthday: " + this.getBirthday() + "\nstanding: " + this.getStanding()
                + "\nfirstName: " + this.getFirstName() + "\nlastName: " + this.getLastName()
                + "\nuserType: " + this.getUserType() + "\nmaxStorage: " + this.getMaxStorage();
    }
}
