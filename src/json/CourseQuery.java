package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFDatabaseCallbackRunnable;
import database.DFError;
import database.DFSQL.*;
import database.WebServer.DFDataUploaderReturnStatus;
import json.util.JSONQueryError;
import objects.*;

import java.util.ArrayList;

/**
 * Created by Naveen Ganessin on 3/6/2017.
 */
public class CourseQuery{

    private DFDataUploaderReturnStatus uploadSuccess;
    private JsonObject jsonObject;

    public void getAllGrades(QueryCallbackRunnable runnable){
        DFSQL dfsql = new DFSQL();
        String[] selectRows = {"(SELECT COUNT(grade) FROM `grades` WHERE grade >= 90.00) AS 90UP",
                "(SELECT COUNT(grade) FROM `grades` WHERE grade >= 80.00 AND grade < 90.00) AS 80UP",
                "(SELECT COUNT(grade) FROM `grades` WHERE grade >= 70.00 AND grade < 80.00) AS 70UP",
                "(SELECT COUNT(grade) FROM `grades` WHERE grade >= 60.00 AND grade < 70.00) AS 60UP",
                "(SELECT COUNT(grade) FROM `grades` WHERE grade < 60.00) AS 60BE"};
        String table = "grades";
        try {
            dfsql.select(selectRows, false, null, null).from(table).limit(1);
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) -> {
                JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
                if (error != null) {
                    //Process the error and return appropriate new error to UI.
                    runnable.run(null, error1);
                    return;
                }
                JsonObject jsonObject;
                if (response instanceof JsonObject) {
                    jsonObject = (JsonObject) response;
                } else {
                    runnable.run(null, error1);
                    return;
                }
                ArrayList<Integer> allGrades = new ArrayList<Integer>();
                String courseTitle = null, courseName = null, description = null, roomNo = null, meetingTime = null, startDate = null, endDate = null;
                int courseId = 0, capacity = 0;
                Course course;
                try {
                        allGrades.add(jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("90UP").getAsInt());
                    allGrades.add(jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("80UP").getAsInt());
                    allGrades.add(jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("70UP").getAsInt());
                    allGrades.add(jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("60UP").getAsInt());
                    allGrades.add(jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("60BE").getAsInt());

                }catch (NullPointerException e2){runnable.run(null, error1);}
                runnable.run(allGrades, null);
            });
        } catch (DFSQLError e1){
            e1.printStackTrace();
        }
    }

    public void getAllCourses(int limit, int offset, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String[] selectRows = {"id", "courseid", "coursename", "capacity", "description", "roomno", "meetingtime", "startdate", "enddate"};
        String table = "courses";
        try {
            dfsql.select(selectRows, false, null, null)
                    .from(table)
                    .limit(limit)
                    .offset(offset);
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) -> {
                JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
                if (error != null) {
                    //Process the error and return appropriate new error to UI.
                    runnable.run(null, error1);
                    return;
                }
                JsonObject jsonObject;
                if (response instanceof JsonObject) {
                    jsonObject = (JsonObject) response;
                } else {
                    runnable.run(null, error1);
                    return;
                }
                ArrayList<Course> allCourses = new ArrayList<Course>();
                String courseTitle = null, courseName = null, description = null, roomNo = null, meetingTime = null, startDate = null, endDate = null;
                int courseId = 0, capacity = 0;
                Course course;
                try {
                    for (int i = 0; i < jsonObject.get("Data").getAsJsonArray().size(); ++i) {
                        courseTitle = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("coursename").getAsString();
                        courseName = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("courseid").getAsString();
                        description = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("description").getAsString();
                        roomNo = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("roomno").getAsString();
                        meetingTime = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("meetingtime").getAsString();
                        courseId = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsInt();
                        capacity = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("capacity").getAsInt();
                        endDate = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("startdate").getAsString();
                        startDate = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("enddate").getAsString();

                        course = new Course();

                        course.setCourseID(courseId);
                        course.setCapacity(capacity);
                        course.setCourseName(courseName);
                        course.setDescription(description);
                        course.setEndDate(endDate);
                        course.setStartDate(startDate);
                        course.setMeetingTime(meetingTime);
                        course.setRoomNo(roomNo);
                        course.setTitle(courseTitle);
                        allCourses.add(course);
                    }
                }catch (NullPointerException e2){runnable.run(null, error1);}
                runnable.run(allCourses, null);
            });
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }
    /**
     * Method to get info of a course given courseID
     *@param  courseID courseID to get info of
     */
    public void getCourse(String courseID, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String[] selectedRows = {"id", "courseID", "coursename", "description", "roomno", "meetingtime", "startdate", "enddate", "capacity"};
        try {
            dfsql.select(selectedRows, false, null, null).from("courses").where(DFSQLEquivalence.equals, "courseID", courseID);
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) -> {
                System.out.println(response);
                System.out.println(error);
                JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
                if (error != null) {
                    //Process the error and return appropriate new error to UI.
                    runnable.run(null, error1);
                    return;
                }
                JsonObject jsonObject;
                if (response instanceof JsonObject) {
                    jsonObject = (JsonObject) response;
                } else {
                    return;
                }
                String courseTitle = null, courseName = null, description = null, roomNo = null, meetingTime = null, startDate = null, endDate = null;
                int courseId = 0, capacity = 0;
                Course course = null;
                try {
                        int i = 0;
                        courseTitle = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("courseid").getAsString();
                        courseName = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("coursename").getAsString();
                        description = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("description").getAsString();
                        roomNo = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("roomno").getAsString();
                        meetingTime = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("meetingtime").getAsString();
                        courseId = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsInt();
                        capacity = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("capacity").getAsInt();
                        endDate = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("startdate").getAsString();
                        startDate = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("enddate").getAsString();

                        course = new Course();

                        course.setCourseID(courseId);
                        course.setCapacity(capacity);
                        course.setCourseName(courseName);
                        course.setDescription(description);
                        course.setEndDate(endDate);
                        course.setStartDate(startDate);
                        course.setMeetingTime(meetingTime);
                        course.setRoomNo(roomNo);
                        course.setTitle(courseTitle);
                }catch (NullPointerException e2){runnable.run(null, error1);}
                runnable.run(course, null);
            });
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

    public void addCourse(int id, String courseID, String courseName, String description, String roomno, String meetingtime, String startdate, String enddate, int capacity, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String[] rows = {"id", "courseID", "coursename", "description", "roomno", "meetingtime", "startdate", "enddate", "capacity"};
        String[] values = {"" + id, courseID, courseName, description, roomno, meetingtime, startdate, enddate, "" + capacity};
        try {
            dfsql.insert("courses", values, rows);
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->
            {
                if (error != null)
                {
                    JSONQueryError error1;
                    if (error.code == 2)
                    {
                        error1 = new JSONQueryError(1, "Duplicate ID", null);
                    }
                    else if (error.code == 3)
                    {
                        error1 = new JSONQueryError(2, "Duplicate Unique Entry", null);
                    }
                    else
                    {
                        error1 = new JSONQueryError(0, "Internal Error", null);
                    }
                    runnable.run(null, error1);
                    return;
                }

                if (response instanceof DFDataUploaderReturnStatus)
                {
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
                else
                {
                    runnable.run(null, new JSONQueryError(0, "Internal Error", null));
                }
            });
        } catch (DFSQLError e1)
        {
            e1.printStackTrace();
        }
    }

    /**
     * remove a course from the db
     * @param id id of course to be deleted
     */
    public void removeCourse(int id, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        try {
            dfsql.delete("courses", new Where(DFSQLConjunction.none, DFSQLEquivalence.equals, new DFSQLClause("id", "" + id)));
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->
            {
                if (error != null)
                {
                    JSONQueryError error1;
                    if (error.code == 2)
                    {
                        error1 = new JSONQueryError(1, "Duplicate ID", null);
                    }
                    else if (error.code == 3)
                    {
                        error1 = new JSONQueryError(2, "Duplicate Unique Entry", null);
                    }
                    else
                    {
                        error1 = new JSONQueryError(0, "Internal Error", null);
                    }
                    runnable.run(null, error1);
                    return;
                }

                if (response instanceof DFDataUploaderReturnStatus)
                {
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
                else
                {
                    runnable.run(null, new JSONQueryError(0, "Internal Error", null));
                }
            });
        } catch (DFSQLError e1)
        {
            e1.printStackTrace();
        }
    }

    /**
     * add an instructor to a course
     * @param courseid courseid of the course such as 11111
     * @param userid userid of the instructor given that the user is already an instructor
     */
    public void addInstructorToCourse(int courseid, String userid, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"id"};
        try {
            dfsql.select(selectedRows, false, null, null).from("instructor").where(DFSQLEquivalence.equals, "userid", userid);
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) -> {

                if(error != null) {
                    runnable.run(null, new JSONQueryError(0, "Internal Error", null));
                }
                if(response instanceof JsonObject) {
                    jsonObject = (JsonObject) response;
                    int instructorid = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsInt();
                    addInstructorToCourseGiven(courseid, instructorid, runnable);
                } else {
                    runnable.run(null, new JSONQueryError(0, "Internal Error", null));
                }
            });

        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    private void addInstructorToCourseGiven(int courseid, int instructorid, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String[] rows = {"courseid", "instructorid"};
        String[] values = {"" + courseid, "" + instructorid};
        String table = "courseinstructormembership";
        try {
            dfsql.insert(table, values, rows);
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) -> {
                if(error != null)  {
                    JSONQueryError error1 = new JSONQueryError(0, "Internal Error", null);
                    runnable.run(null, error1);
                    return;
                }
                if(response instanceof DFDataUploaderReturnStatus){
                    DFDataUploaderReturnStatus returnStatus = (DFDataUploaderReturnStatus) response;
                    if (returnStatus == DFDataUploaderReturnStatus.success)
                    {
                        runnable.run(true, null);
                    }
                    else
                    {
                        runnable.run(false, null);
                    }

                }
                else {
                    runnable.run(null, new JSONQueryError(0, "Internal Error", null));
                }
            });
        }catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    /**
     * remove an Instructor from the course
     * @param courseid remove instructor from courseid
     * @param userid remove instructor of userid
     */
    public void removeInstructorInCourse(int courseid, String userid, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"id"};
        try {
            dfsql.select(selectedRows, false, null, null).from("instructor").where(DFSQLEquivalence.equals, "userid", userid);
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) -> {
                if(error != null) {
                    return;
                }
                if(response instanceof JsonObject) {
                    jsonObject = (JsonObject) response;
                    int instructorid = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsInt();
                    removeInstructorFromCourseGiven(courseid, instructorid, runnable);
                } else {
                    return;
                }
            });
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    private void removeInstructorFromCourseGiven(int courseid, int instructorid, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String table = "courseinstructormembership";
        Where[] where = new Where[2];
        where[0] = new Where(DFSQLConjunction.and, DFSQLEquivalence.equals, new DFSQLClause("instructorid", "" + instructorid));
        where[1] = new Where(DFSQLConjunction.none, DFSQLEquivalence.equals, new DFSQLClause("courseid", "" + courseid));
        try {
            dfsql.delete(table, where);
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) -> {
                if(error != null)  {
                    JSONQueryError error1 = new JSONQueryError(0, "Internal Error", null);
                    runnable.run(null, error1);
                    return;
                }
                if(response instanceof DFDataUploaderReturnStatus){
                    DFDataUploaderReturnStatus returnStatus = (DFDataUploaderReturnStatus) response;
                    if (returnStatus == DFDataUploaderReturnStatus.success)
                    {
                        runnable.run(true, null);
                    }
                    else
                    {
                        runnable.run(false, null);
                    }

                }
                else {
                    runnable.run(null, new JSONQueryError(0, "Internal Error", null));
                }
            });
        }catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    /**
     * List of all Instructors teaching a course
     * Returns a list of all userid(string) of the instructors in a course
     * @param courseid courseid of the course such as 11111
     */
    public void getAllInstructorsInCourse(int courseid, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();

        String selectedRows[] = {"users.userid", "email", "firstname", "lastname", "instructor.id", "roomno", "officehours"}; //username

        String table1 = "courseinstructormembership";
        String table2 = "instructor";
        String table3 = "users";

        Join[] joins = new Join[] {
                new Join(table2, table1 + ".instructorid", table2 + ".id"),
                new Join(table3, table3 + ".userid", table2 + ".userid") };

        try {
            dfsql.select(selectedRows, false, null, null)
                    .from(table1)
                    .join(DFSQLJoin.left, joins)
                    .where(DFSQLEquivalence.equals, "courseid", "" + courseid);
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
                    ArrayList<Instructor> allCoursesForInstructor = new ArrayList<Instructor>();
                    int instructorId;
                    String userId, officeHours, roomNo, email, firstName, lastName;
                    Instructor instructor = null;
                    JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
                    try {
                        for (int i = 0; i < jsonObject.get("Data").getAsJsonArray().size(); ++i) {
                            userId = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("userid").getAsString();
                            email = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get(selectedRows[1]).getAsString();
                            firstName = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get(selectedRows[2]).getAsString();
                            lastName = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get(selectedRows[3]).getAsString();
                            instructorId = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsInt();
                            roomNo = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get(selectedRows[5]).getAsString();
                            officeHours = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get(selectedRows[6]).getAsString();

                            instructor = new Instructor();
                            instructor.setRoomNo(roomNo);
                            instructor.setOfficeHours(officeHours);
                            instructor.setEmail(email);
                            instructor.setFirstName(firstName);
                            instructor.setLastName(lastName);
                            instructor.setUserID(userId);
                            instructor.setUserType(userType.TEACHER);
                            instructor.setInstructorId(instructorId);
                            allCoursesForInstructor.add(instructor);
                        }
                    }catch (NullPointerException e2){
                        e2.printStackTrace();
                        runnable.run(null, error1);
                    }
                    runnable.run(allCoursesForInstructor, null);
                });
            } catch (DFSQLError dfsqlError) {
                dfsqlError.printStackTrace();
            }
    }

    /**
     * Add a student to a course
     * @param courseid courseid of the course such as 11111
     * @param userid userid of the student to add to the course
     */
    public void addStudentToCourse(int courseid, String userid, QueryCallbackRunnable runnable) {

        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"id"};
        try {
            dfsql.select(selectedRows, false, null, null).from("students").where(DFSQLEquivalence.equals, "userid", userid);
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) -> {
                if(error != null) {
                    runnable.run(null, new JSONQueryError(0, "Internal Error", null));
                    return;
                }
                if(response instanceof JsonObject) {
                    jsonObject = (JsonObject) response;
                    int studentid = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsInt();
                    addStudentToCourseGiven(courseid, studentid, runnable);
                } else {
                    return;
                }
            });
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    private boolean addStudentToCourseGiven(int courseid, int studentid, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String[] rows = {"courseid", "studentid"};
        String[] values = {"" + courseid, "" + studentid};
        String table = "coursestudentmembership";
        try {
            dfsql.insert(table, values, rows);
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) -> {
                if(error != null)  {
                    JSONQueryError error1 = new JSONQueryError(0, "Internal Error", null);
                    runnable.run(null, error1);
                    return;
                }
                if(response instanceof DFDataUploaderReturnStatus){
                    DFDataUploaderReturnStatus returnStatus = (DFDataUploaderReturnStatus) response;
                    if (returnStatus == DFDataUploaderReturnStatus.success)
                    {
                        runnable.run(true, null);
                    }
                    else
                    {
                        runnable.run(false, null);
                    }

                }
                else {
                    runnable.run(null, new JSONQueryError(0, "Internal Error", null));
                }
            });
        }catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
        return uploadSuccess == DFDataUploaderReturnStatus.success;
    }

    /**
     * remove a Student from the course
     * @param courseid remove student from courseid
     * @param userid remove student of userid
     */
    public void removeStudentInCourse(int courseid, String userid, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"id"};
        try {
            dfsql.select(selectedRows, false, null, null).from("students").where(DFSQLEquivalence.equals, "userid", userid);
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) -> {
                if(error != null) {
                    runnable.run(null, new JSONQueryError(0, "Internal Error", null));
                    return;
                }
                if(response instanceof JsonObject) {
                    jsonObject = (JsonObject) response;
                    int studentid = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsInt();
                    removeStudentFromCourseGiven(courseid, studentid, runnable);
                } else {
                    return;
                }
            });
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    private void removeStudentFromCourseGiven(int courseid, int studentid, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String table = "courseinstructormembership";
        Where[] where = new Where[2];
        where[0] = new Where(DFSQLConjunction.and, DFSQLEquivalence.equals, new DFSQLClause("studentid", "" + studentid));
        where[1] = new Where(DFSQLConjunction.none, DFSQLEquivalence.equals, new DFSQLClause("courseid", "" + courseid));
        try {
            dfsql.delete(table, where);
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) -> {
                if(error != null)  {
                    JSONQueryError error1 = new JSONQueryError(0, "Internal Error", null);
                    runnable.run(null, error1);
                    return;
                }
                if(response instanceof DFDataUploaderReturnStatus){
                    DFDataUploaderReturnStatus returnStatus = (DFDataUploaderReturnStatus) response;
                    if (returnStatus == DFDataUploaderReturnStatus.success)
                    {
                        runnable.run(true, null);
                    }
                    else
                    {
                        runnable.run(false, null);
                    }

                }
                else {
                    runnable.run(null, new JSONQueryError(0, "Internal Error", null));
                }
            });
        }catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    /**
     * List of all Students enrolled in the course
     * List of all userid (String) of students in the course
     * @param courseid courseid of the course such as 11111
     */
    public void getAllStudentsInCourse(int courseid, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"users.userid", "email", "firstname", "lastname", "students.id"}; //username
        String table1 = "coursestudentmembership";
        String table2 = "students";
        String table3 = "users";

        Join[] joins = new Join[] {
                new Join(table2, table1 + ".studentid", table2 + ".id"),
                new Join(table3, table3 + ".userid", table2 + ".userid") };

        try {
            dfsql.select(selectedRows, false, null, null)
                    .from(table1)
                    .join(DFSQLJoin.left, joins)
                    .where(DFSQLEquivalence.equals, "courseid", "" + courseid);
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
                ArrayList<Student> allStudentsForCourse = new ArrayList<Student>();
                int studentId;
                String userId, officeHours, roomNo, email, firstName, lastName;
                Student student = null;
                JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
                try {
                    for (int i = 0; i < jsonObject.get("Data").getAsJsonArray().size(); ++i) {
                        userId = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("userid").getAsString();
                        email = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get(selectedRows[1]).getAsString();
                        firstName = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get(selectedRows[2]).getAsString();
                        lastName = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get(selectedRows[3]).getAsString();
                        studentId = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsInt();

                        student = new Student();
                        student.setEmail(email);
                        student.setFirstName(firstName);
                        student.setLastName(lastName);
                        student.setUserID(userId);
                        student.setUserType(userType.STUDENT);
                        student.setStudentId(studentId);
                        allStudentsForCourse.add(student);
                    }
                }catch (NullPointerException e2){
                    e2.printStackTrace();
                    runnable.run(null, error1);
                }
                runnable.run(allStudentsForCourse, null);
            });
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }


}
