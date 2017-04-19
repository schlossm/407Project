package objects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Course.java
 * Alex Rosenberg
 */
public class Course extends Object implements Serializable {
    private int courseID;
    private String title;
    private String courseName;
    private String description;
    private String roomNo;
    private int capacity;
    private ArrayList<User> teachers;
    private ArrayList<User> students;
    private String meetingTime;
    private String startDate;
    private String endDate;
    private ArrayList<Message> messages;
    private String attendanceString;
    private int maxStorage;
    public static Course testCourse;

    public Course() {
        // empty constructor
    }

    public Course(int courseID, String title, String startDate, String endDate) {
        this.courseID = courseID;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCourseID() {
        return this.courseID;
    }

    public void setCourseID(int newCourseID) {
        this.courseID = newCourseID;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public void setCourseName(String newCourseName) {
        this.courseName = newCourseName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    public String getMeetingTime() {
        return this.meetingTime;
    }

    public void setMeetingTime(String newMeetingTime) {
        this.meetingTime = newMeetingTime;
    }

    public ArrayList<User> getTeachers() {
        return this.teachers;
    }

    public String getRoomNo() {
        return this.roomNo;
    }

    public void setRoomNo(String newRoomNo) {
        this.roomNo = newRoomNo;
    }

    public void setTeachers(ArrayList<User> newTeachers) {
        this.teachers = newTeachers;
    }

    public void addTeacher(User teacher) {
        this.teachers.add(teacher);
    }

    public ArrayList<User> getStudents() {
        return this.students;
    }

    public void setStudents(ArrayList<User> newStudents) {
        this.students = newStudents;
    }

    public void addStudent(User student) {
        this.students.add(student);
    }

    public String getStartDate() {
        return this.startDate;
    }

    public void setStartDate(String newStart) {
        this.startDate = newStart;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public void setEndDate(String newEnd) {
        this.endDate = newEnd;
    }

    public ArrayList<Message> getMessages() {
        return this.messages;
    }

    public void setMessages(ArrayList<Message> newMessages) {
        this.messages = newMessages;
    }

    public void addMessage(Message m) {
        this.messages.add(m);
    }

    public String getAttendanceString() {
        return this.attendanceString;
    }

    public void setAttendanceString(String newString) {
        this.attendanceString = newString;
    }

    public int getMaxStorage() {
        return this.maxStorage;
    }

    public void setMaxStorage(int newMax) {
        this.maxStorage = newMax;
    }

    public String toString() {
        return "courseID: " + this.courseID
                + "\ntitle: " + this.title
                + "\ncourseName: " + this.courseName
                + "\ndescription" + this.description
                + "\nroomNo: " + this.roomNo
                + "\nteachers: " + this.teachers.toString()
                + "\nstudents: " + this.students.toString()
                + "\nstartDate: " + this.startDate
                + "\ncapcity: " + this.capacity
                + "\nendDate: " + this.endDate
                + "\nmessages: " + this.messages.toString()
                + "\nattendanceString: " + this.attendanceString
                + "\nmaxStorage: " + this.maxStorage;
    }
}
