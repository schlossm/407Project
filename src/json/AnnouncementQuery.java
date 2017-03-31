package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFDatabaseCallbackDelegate;
import database.DFError;
import database.DFSQL.*;
import database.WebServer.DFDataUploaderReturnStatus;

/**
 * Created by gauravsrivastava on 3/29/17.
 */
public class AnnouncementQuery implements DFDatabaseCallbackDelegate {

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

    }

    @Override
    public void uploadStatus(DFDataUploaderReturnStatus success, DFError error) {

    }
}
