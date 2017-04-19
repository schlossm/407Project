package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFSQL.*;
import database.WebServer.DFDataUploaderReturnStatus;
import json.util.JSONQueryError;

/**
 * Created by gauravsrivastava on 3/13/17.
 */
public class InstructorQuery {

    private class GradeParameter {
        public int assignmentid;
        public int studentid;
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
        String selectedRows[] = {"courses.courseid", "courses.id", "courses.coursename", "courses.courseID"};
        String table1 = "courseinstructormembership";
        String table2 = "students";
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
                if(error != null) {
                    JSONQueryError error1;
                    if(error.code == 1) {
                        error1 = new JSONQueryError(3, "No data returned", null);
                        runnable.run(null, error1);
                    }
                }
                if(response instanceof JsonObject) {

                }
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
        String selectedRows[] = {"studentid"};
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
                    gradeParameter.studentid = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("studentid").getAsInt();
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

    public void enterGradeHelper(int assignmentid, int studentid, double points, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String[] rows = {"studentid", "assignmentid", "grade"};
        String[] values = {"" + assignmentid, "" + studentid , "" + points};

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
