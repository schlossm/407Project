package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFDatabaseCallbackDelegate;
import database.DFError;
import database.DFSQL.*;
import database.WebServer.DFDataUploaderReturnStatus;
import objects.Course;
import objects.userType;
import ui.util.UIStrings;
import uikit.DFNotificationCenter;

import java.util.ArrayList;

import static database.DFDatabase.debugLog;

/**
 * Created by Naveen Ganessin on 3/6/2017.
 */
public class CourseQuery implements DFDatabaseCallbackDelegate{
    private boolean getCourseReturn, getAllInstructorsInCourseReturn, getAllStudentsInCourseReturn, getAssignmentInfoReturn, getAllAssignmentsReturn;
    private int courseidForInsertionInstrutor = -1;
    private int courseidForDeletionInstructor = -1;
    private int courseidForInsertionStudent = -1;
    private int courseidForDeletionStudent = -1;

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

    /**
     * add an instructor to a course
     * @param courseid courseid of the course such as 11111
     * @param userid userid of the instructor given that the user is already an instructor
     */
    public void addInstructorToCourse(int courseid, String userid) {

        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"id"};
        courseidForInsertionInstrutor = courseid;
        try {
            dfsql.select(selectedRows, false, null, null).from("instructor").where(DFSQLEquivalence.equals, "userid", userid);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    private void addInstructorToCourseGiven(int courseid, int instructorid) {
        DFSQL dfsql = new DFSQL();
        String[] rows = {"courseid", "instructorid"};
        String[] values = {"" + courseid, "" + instructorid};
        String table = "courseintructormembership";
        try {
            dfsql.insert(table, rows, values);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        }catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    /**
     * remove an Instructor from the course
     * @param courseid remove instructor from courseid
     * @param userid remove instructor of userid
     */
    public void removeInstructorInCourse(int courseid, String userid) {
        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"id"};
        courseidForDeletionInstructor = courseid;
        try {
            dfsql.select(selectedRows, false, null, null).from("instructor").where(DFSQLEquivalence.equals, "userid", userid);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    private void removeInstructorFromCourseGiven(int courseid, int instructorid) {
        DFSQL dfsql = new DFSQL();
        String table = "courseintructormembership";
        try {
            dfsql.delete(table, new Where(DFSQLConjunction.none, DFSQLEquivalence.equals, new DFSQLClause("instructorid", "" + instructorid)));
            DFDatabase.defaultDatabase.execute(dfsql, this);
        }catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    /**
     * List of all Instructors teaching a course
     * Returns a list of all userid(string) of the instructors in a course
     * @param courseid courseid of the course such as 11111
     */
    public void getAllInstructorsInCourse(int courseid) {
        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"userid"}; //username
        String tables[] = {"courseinstructormembership", "instructor"};
        getAllStudentsInCourseReturn = true;
        try {
            dfsql.select(selectedRows, false, null, null).from(tables).where(DFSQLEquivalence.equals, "courseid", "" + courseid);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    /**
     * Add a student to a course
     * @param courseid courseid of the course such as 11111
     * @param userid userid of the student to add to the course
     */
    public void addStudentToCourse(int courseid, String userid) {

        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"id"};
        courseidForInsertionStudent = courseid;
        try {
            dfsql.select(selectedRows, false, null, null).from("students").where(DFSQLEquivalence.equals, "userid", userid);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    private void addStudentToCourseGiven(int courseid, int studentid) {
        DFSQL dfsql = new DFSQL();
        String[] rows = {"courseid", "studentid"};
        String[] values = {"" + courseid, "" + studentid};
        String table = "coursestudentmembership";
        try {
            dfsql.insert(table, rows, values);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        }catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    /**
     * remove a Student from the course
     * @param courseid remove student from courseid
     * @param userid remove student of userid
     */
    public void removeStudentInCourse(int courseid, String userid) {
        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"id"};
        courseidForDeletionInstructor = courseid;
        try {
            dfsql.select(selectedRows, false, null, null).from("students").where(DFSQLEquivalence.equals, "userid", userid);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    private void removeStudentFromCourseGiven(int courseid, int studentid) {
        DFSQL dfsql = new DFSQL();
        String table = "courseinstructormembership";
        try {
            dfsql.delete(table, new Where(DFSQLConjunction.none, DFSQLEquivalence.equals, new DFSQLClause("studentid", "" + studentid)));
            DFDatabase.defaultDatabase.execute(dfsql, this);
        }catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    /**
     * List of all Students enrolled in the course
     * List of all userid (String) of students in the course
     * @param courseid courseid of the course such as 11111
     */
    public void getAllStudentsInCourse(int courseid) {
        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"userid"};
        String tables[] = {"coursestudentmembership", "students"};
        getAllStudentsInCourseReturn = true;
        try {
            dfsql.select(selectedRows, false, null, null).from(tables).where(DFSQLEquivalence.equals, "courseid", "" + courseid);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    /**
     * Returns all info of an assignment given the assignment id
     * @param assignmentid
     */
    public void getAssignmentInfo(int assignmentid) {
        DFSQL dfsql = new DFSQL();
        String[] selectedRows = {"id"};
        String table = "";
        getAssignmentInfoReturn = true;
        try {
            dfsql.select(selectedRows, false, null, null).from(table).where(DFSQLEquivalence.equals, "id", "" + assignmentid);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

    public void addAssignment(int courseid, String name, String deadline, double maxPoints, String type) {
        DFSQL dfsql = new DFSQL();
        String[] rows = {"courseid", "name", "deadline", "maxpoints", "type"};
        String[] values = {courseid + "", name, deadline, "" + maxPoints, type};
        String table = "";
        try {
            dfsql.insert(table, rows, values);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    /**
     * remove an assignment from a course
     * @param assignmentid remove assignment given assignment
     */
    public void removeAssignment(int assignmentid) {
        DFSQL dfsql = new DFSQL();
        String table = "";
        try {
            dfsql.delete(table, new Where(DFSQLConjunction.none, DFSQLEquivalence.equals, new DFSQLClause("id", "" + assignmentid)));
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    /**
     * Get all the assignments of a course
     * Return a list of all ids of an assignment
     * @param courseid courseid such as 1111 of the course
     */
    public void getAllAssignments(int courseid) {
        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"userid"};
        String table = "assignment";
        getAllAssignmentsReturn = true;
        try {
            dfsql.select(selectedRows, false, null, null).from(table).where(DFSQLEquivalence.equals, "courseid", "" + courseid);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }


    @Override
    public void returnedData(JsonObject jsonObject, DFError error) {
        System.out.println("triggered returnedData");
        this.jsonObject = null;
        if(courseidForInsertionInstrutor != -1) {
            int instructorid = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsInt();
            addInstructorToCourseGiven(courseidForInsertionInstrutor, instructorid);
            courseidForInsertionInstrutor = -1;
        }
        else if(courseidForDeletionInstructor != -1) {
            int instructorid = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsInt();
            removeInstructorFromCourseGiven(courseidForDeletionInstructor, instructorid);
            courseidForDeletionInstructor = -1;
        }
        else if(courseidForInsertionStudent != -1) {
            int studentid = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsInt();
            addStudentToCourseGiven(courseidForInsertionStudent, studentid);
            courseidForInsertionStudent = -1;
        }
        else if(courseidForDeletionStudent != -1) {
            int studentid = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsInt();
            removeStudentFromCourseGiven(courseidForDeletionStudent, studentid);
            courseidForDeletionStudent = -1;
        }
        else if(error != null){
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
        } else if (getAllInstructorsInCourseReturn) {
            ArrayList<String> allInstructorsInCourse = new ArrayList<String>();
            String instructorUserId = null;

            try {
                for (int i = 0; i < jsonObject.get("Data").getAsJsonArray().size(); ++i) {
                    instructorUserId = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("userid").getAsString();
                    allInstructorsInCourse.add(instructorUserId);
                }
            }catch (NullPointerException e2){
                DFNotificationCenter.defaultCenter.post(UIStrings.returned, null);
            }
            DFNotificationCenter.defaultCenter.post(UIStrings.returned, allInstructorsInCourse);
            getAllInstructorsInCourseReturn = false;
        } else if (getAllStudentsInCourseReturn) {
            ArrayList<String> allStudentsInCourse = new ArrayList<String>();
            String studentId = null;

            try {
                for (int i = 0; i < jsonObject.get("Data").getAsJsonArray().size(); ++i) {
                    studentId = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("userid").getAsString();
                    allStudentsInCourse.add(studentId);
                }
            }catch (NullPointerException e2){
                DFNotificationCenter.defaultCenter.post(UIStrings.returned, null);
            }
            DFNotificationCenter.defaultCenter.post(UIStrings.returned, allStudentsInCourse);
            getAllStudentsInCourseReturn = false;
        } else if (getAssignmentInfoReturn) {
            getAssignmentInfoReturn = false;
        } else if (getAllAssignmentsReturn) {
            
            getAllAssignmentsReturn = false;
        }

        bufferString = null;
    }

    @Override
    public void uploadStatus(DFDataUploaderReturnStatus success, DFError error) {

    }
}
