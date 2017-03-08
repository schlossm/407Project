package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFDatabaseCallbackDelegate;
import database.DFError;
import database.DFSQL.*;
import database.WebServer.DFDataUploaderReturnStatus;
import objects.Course;
import objects.User;
import objects.userType;
import ui.util.UIStrings;
import uikit.DFNotificationCenter;

import static database.DFDatabase.debugLog;

/**
 * Created by Naveen Ganessin on 3/6/2017.
 */
public class CourseQuery implements DFDatabaseCallbackDelegate{
    private boolean getCourseReturn, getUserExistsReturn;
    private DFDataUploaderReturnStatus uploadSuccess;
    private JsonObject jsonObject;
    private String bufferString;
    private boolean verifyUserLoginReturn;


    /**
     * Method to get info of a course given courseID
     *@param  courseID courseID to get info of
     */
    public void getCourse(String courseID) {
        DFSQL dfsql = new DFSQL();
        getCourseReturn = true;
        String[] selectedRows = {"id", "courseID", "coursename", "description", "roomno", "meetingtime", "startdate", "enddate"};
        try {
            dfsql.select(selectedRows, false, null, null).from("courses").where(DFSQLEquivalence.equals, "courseID", courseID);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Method to add a class into the system
     * @param id id of class such as 11111
     * @param courseID  like CS407
     * @param courseName name of the course such as Software Engineering
     * @param description description of the couse
     * @param roomno room no where the class meets
     * @param meetingtime time and days the class meets
     * @param startdate start date of the course in the format of YYYY/MM/DD
     * @param enddate end date of the course in the format of YYYY/MM/DD
     */
    public void addCourse(int id, String courseID, String courseName, String description, String roomno, String meetingtime, String startdate, String enddate) {
        DFSQL dfsql = new DFSQL();
        String[] rows = {"id", "courseID", "courseID", "coursename", "description", "roomno", "meetingtime", "startdate", "enddate"};
        String[] values = {"" + id, courseID, courseName, courseName, description, roomno, meetingtime, startdate, enddate};
        try {
            dfsql.insert("courses", rows, values);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    /**
     * remove a course from the db
     * @param id id of course to be deleted
     */
    public void removeCourse(int id) {
        DFSQL dfsql = new DFSQL();
        try {
            dfsql.delete("courses", new Where(DFSQLConjunction.none, DFSQLEquivalence.equals, new DFSQLClause("id", "" + id)));
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    

    @Override
    public void returnedData(JsonObject jsonObject, DFError error) {
        System.out.println("triggered returnedData");
        this.jsonObject = null;
        if(error != null){
            DFDatabase.print(error.toString());
            this.jsonObject = null;
        } else {
            this.jsonObject = jsonObject;
        }
        returnHandler();
    }

    private void returnHandler(){
        System.out.println("triggers return handler");
        if(getCourseReturn){
            String courseTitle = null, courseName = null, description = null, roomNo = null, meetingTime = null, startDate = null, endDate = null;
            int courseId = 0;
            int userTypeInt = 0;
            userType userType = null;
            try {
                courseId = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsInt();
                courseTitle = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("courseID").getAsString();
                courseName = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("coursename").getAsString();
                description = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("description").getAsString();
                roomNo = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("roomno").getAsString();
                meetingTime = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("meetingtime").getAsString();
                startDate = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("startdate").getAsString();
                endDate = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("enddate").getAsString();

            }catch (NullPointerException e2){
                DFNotificationCenter.defaultCenter.post(UIStrings.returned, null);
            }
            Course course = new Course();
            course.setCourseID(courseId);
            course.setTitle(courseTitle);
            course.setCourseName(courseName);
            course.setDescription(description);
            course.setRoomNo(roomNo);
            course.setMeetingTime(meetingTime);
            course.setStartDate(startDate);
            course.setEndDate(endDate);

            /* Wait for Alex to implement the rest of the fields */
            DFNotificationCenter.defaultCenter.post(UIStrings.returned, course);
            System.out.println("getUser posting user to returned");
            getCourseReturn = false;
        } else if (verifyUserLoginReturn) {
            System.out.println("gets to verifyuserlogin");
            String databasePassword = "";
            try {
                databasePassword = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("password").getAsString();
                if(databasePassword.equals(bufferString)){DFNotificationCenter.defaultCenter.post(UIStrings.success, Boolean.TRUE);
                    debugLog("verifylogin returned success");}
                else {DFNotificationCenter.defaultCenter.post(UIStrings.failure, Boolean.FALSE);
                    debugLog("verifylogin returned fail cause passwords don't match");}
            } catch (NullPointerException e2){
                DFNotificationCenter.defaultCenter.post(UIStrings.failure, Boolean.FALSE);
                debugLog("verifylogin returned nothing");
            }
            verifyUserLoginReturn = false;
        } else if (getUserExistsReturn) {
            String usernameReceived = null;
            try {
                usernameReceived = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("userID").getAsString();
            }catch (NullPointerException e2){
                DFNotificationCenter.defaultCenter.post(UIStrings.exists, false);
            }
            if(usernameReceived != null){
                DFNotificationCenter.defaultCenter.post(UIStrings.exists, true);
            }
        }

        getUserExistsReturn = false;
        bufferString = null;
    }

    @Override
    public void uploadStatus(DFDataUploaderReturnStatus success, DFError error) {

    }
}
