package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFSQL.*;
import json.util.JSONQueryError;
import objects.Course;
import objects.Grade;
import java.util.ArrayList;

/**
 * Created by gauravsrivastava on 3/13/17.
 */
public class StudentQuery {
    private JsonObject jsonObject;
    private boolean getCoursesReturn, getGradeReturn, getCourseGradeReturn;
    /**
     * given a userid returns a list of courses the student is enrolled in
     * returns a list of courseids, title, meetingtime of the course
     * returns a list of courseids, title, meetingtime of the course
     * @param userid
     */
    public void getCourses(String userid, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"courses.courseid", "courses.id", "courses.coursename", "courses.meetingtime", "courses.roomno"};
        String table1 = "coursestudentmembership";
        String table2 = "students";
        String table3 = "courses";

        Join[] joins = new Join[] {
                new Join(table2, table1 + ".studentID", table2 + ".id"),
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
                int crn;
                String title, courseName, meetingTime, roomNo;
                Course course = null;
                JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
                try {
                    for (int i = 0; i < jsonObject.get("Data").getAsJsonArray().size(); ++i) {
                        title = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("courseid").getAsString();
                        courseName = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsString();
                        crn = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("coursename").getAsInt();
                        meetingTime = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("meetingtime").getAsString();
                        roomNo = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("roomno").getAsString();

                        course = new Course();
                        course.setTitle(title);
                        course.setMeetingTime(meetingTime);
                        course.setRoomNo(roomNo);
                        course.setCourseID(crn);
                        course.setCourseName(courseName);
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
     * given the assignmentid and the userid it given the grade of the course
     * returns the grade(double)
     * @param assignmentid
     * @param userid
     */
    public void getGrade(int assignmentid, String userid, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"grade", "assignmentid", "userid"}; //username
        String table1 = "grades";
        String table2 = "students";
        String[] attributes = {"userid", "assignmentid"};
        String[] values = {"" + userid, "" + assignmentid};
        try {
            dfsql.select(selectedRows, false, null, null)
                    .from(table1)
                    .join(DFSQLJoin.left, table2, "grade.studentid", "students.id")
                    .where(DFSQLConjunction.and, DFSQLEquivalence.equals, attributes, values);
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
                JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
                if (response instanceof JsonObject) {
                    jsonObject = (JsonObject) response;
                } else {
                    return;
                }
                int points = 0;
                int assignmentId = 0;
                String userId = "";
                try {
                    points = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("grade").getAsInt();
                    assignmentId = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("assignmentid").getAsInt();
                    userId = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("userid").getAsString();
                }catch (NullPointerException e2){
                    runnable.run(null, error1);
                }
                Grade grade = new Grade(userId, assignmentId, String.valueOf(points));
            /* Wait for Alex to implement the rest of the fields */
                runnable.run(grade, null);
                System.out.println("getUser posting user to returned");
            });
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    /**
     * get the total sum of the grades and the percentage
     * returns two fields
     * @param userid
     * @param courseid
     */
    public void getCourseGrade(String userid, int courseid, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"Avg(Grade)", "Sum(Grade)"};
        String table1 = "grades";
        String table2 = "assignment";
        String table3 = "students";
        String[] attributes = {"userid", "courseid"};
        String[] values = {"" + userid, "" + courseid};
        try {
            dfsql.select(selectedRows, false, null, null).from(table1)
                    .join(DFSQLJoin.left, table2, "grades.assignmentid", "assignment.assignmentid")
                    .join(DFSQLJoin.left, table3, "grades.studentid", "students.id")
                    .where(DFSQLConjunction.and, DFSQLEquivalence.equals, attributes, values);
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
                JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
                int points = 0;
                int assignmentId = 0;
                String userId = "";
                try {
                    points = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("grade").getAsInt();
                    assignmentId = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("assignmentid").getAsInt();
                    userId = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("userid").getAsString();
                }catch (NullPointerException e2){
                    runnable.run(null, error1);
                }
                Grade grade = new Grade(userId, assignmentId, String.valueOf(points));
            /* Wait for Alex to implement the rest of the fields */
                runnable.run(grade, null);
            });
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

}
