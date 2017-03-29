package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFDatabaseCallbackDelegate;
import database.DFError;
import database.DFSQL.DFSQL;
import database.DFSQL.DFSQLEquivalence;
import database.DFSQL.DFSQLError;
import database.DFSQL.DFSQLJoin;
import database.WebServer.DFDataUploaderReturnStatus;

/**
 * Created by gauravsrivastava on 3/13/17.
 */
public class InstructorQuery implements DFDatabaseCallbackDelegate {


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
    public void getCourses(String userid) {
        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"courseid"};
        String table1 = "courseinstructormembership";
        String table2 = "students";
        try {
            dfsql.select(selectedRows, false, null, null)
                    .from(table1)
                    .join(DFSQLJoin.left, table2, table1 + ".studentid", table2 + ".id")
                    .where(DFSQLEquivalence.equals, table2 + ".userid",  userid);
            DFDatabase.defaultDatabase.execute(dfsql, this);
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
    public void enterGrade(int assignmentid, String userid, double points) {
        DFSQL dfsql = new DFSQL();

    }

    @Override
    public void returnedData(JsonObject jsonObject, DFError error) {

    }

    @Override
    public void uploadStatus(DFDataUploaderReturnStatus success, DFError error) {

    }
}
