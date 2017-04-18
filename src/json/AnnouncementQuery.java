package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFDatabaseCallbackDelegate;
import database.DFError;
import database.DFSQL.*;
import database.WebServer.DFDataUploaderReturnStatus;
import objects.Grade;
import objects.Message;
import ui.util.UIStrings;
import uikit.DFNotificationCenter;

import java.util.ArrayList;

/**
 * Created by gauravsrivastava on 3/29/17.
 */
public class AnnouncementQuery implements DFDatabaseCallbackDelegate {
    private boolean getAllAnnouncementInCourseReturn, getGradeReturn, getCourseGradeReturn;
    private JsonObject jsonObject;

    /**
     * Add an announcement
     * @param title
     * @param content
     * @param timestamp
     * @param authoruserid
     * @param courseid
     */
    public void addAnnouncement(String title, String content, String timestamp, String authoruserid, int courseid) {
        DFSQL dfsql = new DFSQL();
        String[] rows = {"title", "content", "timestamp", "authoruserid", "courseid"};
        String[] values = {title, content, timestamp, authoruserid, "" + courseid};
        String table = "announcement";
        try {
            dfsql.insert(table, values, rows);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

    /**
     * delete a announcement given an announcementid
     * @param announcementid
     */
    public void removeAnnouncement(int announcementid) {
        DFSQL dfsql = new DFSQL();
        String table = "announcement";
        Where where = new Where(DFSQLConjunction.none, DFSQLEquivalence.equals, new DFSQLClause("id", announcementid + ""));
        try {
            dfsql.delete(table, where);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

    /**
     * get all announcements
     */
    public void getAllAnnouncement() {
        DFSQL dfsql = new DFSQL();
        String[] selectedRows = {"id", "title", "content", "timestamp", "authoruserid", "courseid"};
        String table = "announcement";
        try {
            dfsql.select(selectedRows, false, null, null)
                    .from(table);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

    /**
     * get all announcements in a course given courseid. if courseid = -1 returns all announcements outside a course
     * @param courseid
     */
    public void getAllAnnouncementInCourse(int courseid) {
        DFSQL dfsql = new DFSQL();
        String[] selectedRows = {"id", "title", "content", "timestamp", "authoruserid", "courseid"};
        String table = "announcement";
        getAllAnnouncementInCourseReturn = true;
        try {
            dfsql.select(selectedRows, false, null, null)
                    .from(table)
                    .where(DFSQLEquivalence.equals, "courseid", courseid + "");
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError e1) {
            e1.printStackTrace();
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

    private void returnHandler() {
        if(getAllAnnouncementInCourseReturn){
            ArrayList<Message> allCoursesForInstructor = new ArrayList<Message>();
            int courseId, id;
            String authorUserId = null;

            try {
                for (int i = 0; i < jsonObject.get("Data").getAsJsonArray().size(); ++i) {
                    courseId = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("courseid").getAsInt();
                }
            }catch (NullPointerException e2){
                DFNotificationCenter.defaultCenter.post(UIStrings.returned, null);
            }
            DFNotificationCenter.defaultCenter.post(UIStrings.returned, allCoursesForInstructor);
            getAllAnnouncementInCourseReturn = false;
        } else if(getGradeReturn){
            int points = 0;
            int assignmentId = 0;
            String userId = "";
            try {
                points = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("grade").getAsInt();
                assignmentId = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("assignmentid").getAsInt();
                userId = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("userid").getAsString();
            }catch (NullPointerException e2){
                DFNotificationCenter.defaultCenter.post(UIStrings.returned, null);
            }
            Grade grade = new Grade(userId, assignmentId, String.valueOf(points));
            /* Wait for Alex to implement the rest of the fields */
            DFNotificationCenter.defaultCenter.post(UIStrings.returned, grade);
            System.out.println("getUser posting user to returned");
            getGradeReturn = false;
        } else if(getCourseGradeReturn){
            int points = 0;
            int assignmentId = 0;
            String userId = "";
            try {
                points = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("grade").getAsInt();
                assignmentId = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("assignmentid").getAsInt();
                userId = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("userid").getAsString();
            }catch (NullPointerException e2){
                DFNotificationCenter.defaultCenter.post(UIStrings.returned, null);
            }
            Grade grade = new Grade(userId, assignmentId, String.valueOf(points));
            /* Wait for Alex to implement the rest of the fields */
            DFNotificationCenter.defaultCenter.post(UIStrings.returned, grade);
            System.out.println("getUser posting user to returned");
            getGradeReturn = false;
        }
    }
    @Override
    public void uploadStatus(DFDataUploaderReturnStatus success, DFError error) {

    }
}
