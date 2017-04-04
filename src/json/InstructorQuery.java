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
public class InstructorQuery implements DFDatabaseCallbackDelegate {

    private class GradeParameter {
        public int assignmentid;
        public int studentid;
        public double points;

        public GradeParameter(int assignmentid) {
            this.assignmentid = assignmentid;
        }
    }

    GradeParameter gradeParameter;
    private JsonObject jsonObject;


    public InstructorQuery() {
        this.gradeParameter = new GradeParameter(-1);
    }


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
        String selectedRows[] = {"studentid"};
        String table = "students";
        try {
            dfsql.select(selectedRows, false, null, null)
                    .from(table)
                    .where(DFSQLEquivalence.equals, "userid", userid);
            DFDatabase.defaultDatabase.execute(dfsql, this);
            gradeParameter.assignmentid = assignmentid;
            gradeParameter.points = points;
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

    public void enterGradeHelper(int assignmentid, int studentid, double points) {
        DFSQL dfsql = new DFSQL();
        String[] rows = {"studentid", "assignmentid", "grade"};
        String[] values = {"" + assignmentid, "" + studentid , "" + points};

        try {
            dfsql.insert("grades", values, rows);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
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
        if(gradeParameter.assignmentid != -1) {
            gradeParameter.studentid = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("studentid").getAsInt();
            enterGradeHelper(gradeParameter.assignmentid, gradeParameter.studentid, gradeParameter.points);
            gradeParameter.assignmentid = -1;
        }
        returnHandler();
    }

    private void returnHandler(){

    }
    @Override
    public void uploadStatus(DFDataUploaderReturnStatus success, DFError error) {

    }
}
