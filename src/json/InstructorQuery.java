package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFSQL.*;
import database.WebServer.DFDataUploaderReturnStatus;
import json.util.JSONQueryError;
import objects.Course;

import java.util.ArrayList;

/**
 * Created by gauravsrivastava on 3/13/17.
 */
public class InstructorQuery {

    private class GradeParameter {
        public int assignmentid;
        public String studentid;
        public double points;

        public GradeParameter(int assignmentid) {
            this.assignmentid = assignmentid;
        }
    }


    private JsonObject jsonObject;


    /**
     * given a userid it returns the instructorid
     * @param userid
     */
    private void getInstructorId(int userid) {

    }

    /**
     * return the a list of courseids, title, name of the course given the userid
     *  title, name as well todo
     * @param userid
     */
    public void getCourses(String userid, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"courses.courseid", "courses.id", "courses.coursename", "courses.meetingtime", "courses.roomno", "courses.maxStorage"};
        String table1 = "courseinstructormembership";
        String table2 = "instructor";
        String table3 = "courses";

        Join[] joins = new Join[] {
        new Join(table2, table1 + ".instructorid", table2 + ".id"),
        new Join(table3, table3 + ".id", table1 + ".courseid") };

        try {
            dfsql.select(selectedRows, false, null, null)
                    .from(table1)
                    .join(DFSQLJoin.left, joins)
                    .where(DFSQLEquivalence.equals, table2 + ".userid",  userid);
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) -> {
                System.out.println(response);
                System.out.println(error);
                if (error != null) {
                    //Process the error and return appropriate new error to UI.
                    JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
                    runnable.run(null, error1);
                    return;
                }
                JsonObject jsonObject;
                if (response instanceof JsonObject) {
                    jsonObject = (JsonObject) response;

                } else {
                    return;
                }
                ArrayList<Course> allCourses = new ArrayList<Course>();
                int crn, maxStorage;
                String title, courseName, meetingTime, roomNo;
                Course course = null;
                JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
                try {
                    for (int i = 0; i < jsonObject.get("Data").getAsJsonArray().size(); ++i) {
                        title = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("coursename").getAsString();
                        courseName = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("courseid").getAsString();
                        crn = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsInt();
                        meetingTime = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("meetingtime").getAsString();
                        roomNo = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("roomno").getAsString();
                        maxStorage = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("maxStorage").getAsInt();
                        course = new Course();
                        course.setTitle(title);
                        course.setMeetingTime(meetingTime);
                        course.setRoomNo(roomNo);
                        course.setCourseID(crn);
                        course.setCourseName(courseName);
                        course.setMaxStorage(maxStorage);
                        allCourses.add(course);
                    }
                }catch (NullPointerException e2){
                    runnable.run(null, error1);
                }
                runnable.run(allCourses, null);
            });
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    /**
     * given a userid of the student and the assignemtid, enter the points scored for that assignment by the student
     * @param assignmentid
     * @param userid
     * @param points
     */
    public void enterGrade(int assignmentid, String userid, double points, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"userid"};
        String table = "students";
        try {
            dfsql.select(selectedRows, false, null, null)
                    .from(table)
                    .where(DFSQLEquivalence.equals, "userid", userid);
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) -> {
                if( error != null) {
                    JSONQueryError error1 = new JSONQueryError(0, "Internal Error", null);
                    runnable.run(null, error1);
                    return;
                }
                if(response instanceof JsonObject) {
                    jsonObject = (JsonObject) response;
                    GradeParameter gradeParameter = new GradeParameter(assignmentid);
                    gradeParameter.points = points;
                    gradeParameter.studentid = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("userid").getAsString();
                    enterGradeHelper(gradeParameter.assignmentid, gradeParameter.studentid, gradeParameter.points, runnable);
                } else {
                    JSONQueryError error1 = new JSONQueryError(0, "Internal Error", null);
                    runnable.run(null, error1);
                }

            });
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

    private void enterGradeHelper(int assignmentid, String studentid, double points, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String[] rows = {"studentid", "assignmentid", "grade"};
        String[] values = {studentid, "" + assignmentid , "" + points};

        try {
            dfsql.insert("grades", values, rows);
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) -> {
                if(error != null) {

                }
                if(response instanceof DFDataUploaderReturnStatus) {
                    DFDataUploaderReturnStatus returnStatus = (DFDataUploaderReturnStatus)response;
                    if (returnStatus == DFDataUploaderReturnStatus.success)
                    {
                        runnable.run(true, null);
                    }
                    else
                    {
                        runnable.run(false, null);
                    }
                }

            });
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }
}
