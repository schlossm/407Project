package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFDatabaseCallbackDelegate;
import database.DFError;
import database.DFSQL.*;
import database.WebServer.DFDataUploaderReturnStatus;

/**
 * Created by gauravsrivastava on 3/13/17.
 */
public class StudentQuery implements DFDatabaseCallbackDelegate {

    /**
     * given a userid returns a list of courses the student is enrolled in
     * returns a list of courseids, title, meetingtime of the course
     * @param userid
     */
    public void getCourses(String userid) {
        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"courseid"};
        String table1 = "coursestudentmembership";
        String table2 = "students";
        String table3 = "";
        try {
            dfsql.select(selectedRows, false, null, null)
                    .from(table1)
                    .join(DFSQLJoin.left, table2, table1 + ".studentid", table2 + ".id")
                    .where(DFSQLEquivalence.equals, table2 + ".userid", "" + userid);
            DFDatabase.defaultDatabase.execute(dfsql, this);
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
    public void getGrade(int assignmentid, String userid) {
        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"userid"}; //username
        String table1 = "grades";
        String table2 = "students";
        String[] attributes = {"userid", "assignmentid"};
        String[] values = {"" + userid, "" + assignmentid};
        try {
            dfsql.select(selectedRows, false, null, null)
                    .from(table1)
                    .join(DFSQLJoin.left, table2, "grade.studentid", "students.id")
                    .where(DFSQLConjunction.and, DFSQLEquivalence.equals, attributes, values);
            DFDatabase.defaultDatabase.execute(dfsql, this);
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
    public void getCourseGrade(String userid, int courseid) {
        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"Avg(grade)", "Sum(grade)"};
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
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }

    }



    @Override
    public void returnedData(JsonObject jsonObject, DFError error) {

    }

    @Override
    public void uploadStatus(DFDataUploaderReturnStatus success, DFError error) {

    }
}
