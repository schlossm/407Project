package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFError;
import database.DFSQL.*;
import database.WebServer.DFDataUploaderReturnStatus;
import objects.Grade;
import ui.util.UIStrings;
import uikit.DFNotificationCenter;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by gauravsrivastava on 3/30/17.
 */
public class DocumentsQuery {
    private JsonObject jsonObject;
    private boolean getAllDocumentsIdsInCourseReturn, getGradeReturn, getCourseGradeReturn;


    private void getDocumentOfPath(String path) {

    }

    public void addDocument(File documentFile, String title, String description, String authoruserid, String assignmentid, String courseid, boolean isPrivate) {
        DFSQL dfsql = new DFSQL();
        String table = "Documents";
        String path = doc;
        String[] rows = {"title", "description", "authouserid", "assignmentid", "courseid", "private", "path"};
        String[] values = {title, description, authoruserid, assignmentid, courseid, isPrivate + "", path};
        try {

        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

    public void deleteDocument(int documentid) {
        DFSQL dfsql = new DFSQL();
        String table = "Documents";
        Where where = new Where(DFSQLConjunction.none, DFSQLEquivalence.equals, new DFSQLClause("id", documentid + ""));
        try {
            dfsql.delete(table, where);
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->
            {

            });
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

    public void getDocument(int documentid) {
        DFSQL dfsql = new DFSQL();
        String[] selectedRows = {"id", "title", "description", "path", "authoruserid", "courseid"};
        String table = "Documents";
        try {
            dfsql.select(selectedRows, false, null, null)
                    .from(table)
                    .where(DFSQLEquivalence.equals, "courseid", documentid + "");
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->
            {

            });
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

    public void getAllDocumentsIdsInCourse(int courseid) {
        DFSQL dfsql = new DFSQL();
        String[] selectedRows = {"id", "title", "description", "path", "authoruserid", "courseid"};
        String table = "Documents";
        getAllDocumentsIdsInCourseReturn = true;
        try {
            dfsql.select(selectedRows, false, null, null)
                    .from(table)
                    .where(DFSQLEquivalence.equals, "courseid", courseid + "");
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->
            {

            });
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

    public void getAllDocumentsIds() {
        DFSQL dfsql = new DFSQL();
        String[] selectedRows = {"id", "title", "description", "path", "authoruserid", "courseid"};
        String table = "Documents";
        try {
            dfsql.select(selectedRows, false, null, null)
                    .from(table);
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->
            {

            });
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }
    public void returnedData(JsonObject jsonObject, DFError error) {
        System.out.println("triggered returnedData");
        this.jsonObject = null;
        if(error != null){
            DFDatabase.print(error.toString());
            this.jsonObject = null;
        } else {
            this.jsonObject = jsonObject;
        }
        returnHandler();
    }

    private void returnHandler() {
        if(getAllDocumentsIdsInCourseReturn){
            ArrayList<Integer> allCoursesForInstructor = new ArrayList<Integer>();
            int documentId = 0;
            try {
                for (int i = 0; i < jsonObject.get("Data").getAsJsonArray().size(); ++i) {
                    documentId = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsInt();
                    allCoursesForInstructor.add(documentId);
                }
            }catch (NullPointerException e2){
                DFNotificationCenter.defaultCenter.post(UIStrings.returned, null);
            }
            DFNotificationCenter.defaultCenter.post(UIStrings.returned, allCoursesForInstructor);
            getAllDocumentsIdsInCourseReturn = false;
        } else if(getGradeReturn){
            int points = 0;
            int assignmentId = 0;
            String userId = "";
            try {
                points = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("grade").getAsInt();
                assignmentId = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("assignmentid").getAsInt();
                userId = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("userid").getAsString();
            }catch (NullPointerException e2){
                DFNotificationCenter.defaultCenter.post(UIStrings.returned, null);
            }
            Grade grade = new Grade(userId, assignmentId, String.valueOf(points));
>>>>>>> Stashed changes
            /* Wait for Alex to implement the rest of the fields */
			DFNotificationCenter.defaultCenter.post(UIStrings.returned, grade);
			System.out.println("getUser posting user to returned");
			getGradeReturn = false;
		}
	}

	public void uploadStatus(DFDataUploaderReturnStatus success, DFError error)
	{

	}
}
