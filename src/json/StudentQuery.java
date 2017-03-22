package json;

import com.google.gson.JsonObject;
import database.DFDatabaseCallbackDelegate;
import database.DFError;
import database.DFSQL.DFSQL;
import database.WebServer.DFDataUploaderReturnStatus;

/**
 * Created by gauravsrivastava on 3/13/17.
 */
public class StudentQuery implements DFDatabaseCallbackDelegate {

    /**
     * Given a userid this returns the studentid
     * @param userid
     */
    private void getStudentid(int userid) {
        DFSQL dfsql = new DFSQL();

    }

    /**
     * given a userid returns a list of courses the student is enrolled in
     * returns a list of courseids of the course
     * @param userid
     */
    public void getCourses(int userid) {

    }

    /**
     * given the assignmentid and the userid it given the grade of the course
     * returns the grade(double)
     * @param assignmentid
     * @param userid
     */
    public void getGrade(int assignmentid, int userid) {

    }

    /**
     * get the total sum of the grades and the percentage
     * returns two fields
     * @param userid
     * @param courseid
     */
    public void getCourseGrade(int userid, int courseid) {

    }



    @Override
    public void returnedData(JsonObject jsonObject, DFError error) {

    }

    @Override
    public void uploadStatus(DFDataUploaderReturnStatus success, DFError error) {

    }
}
