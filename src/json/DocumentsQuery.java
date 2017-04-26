package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFError;
import database.DFSQL.*;
import database.WebServer.DFDataUploaderReturnStatus;
import objects.Grade;
import sun.net.www.http.HttpClient;
import ui.util.UIStrings;
import uikit.DFNotificationCenter;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by gauravsrivastava on 3/30/17.
 */
public class DocumentsQuery {
    private JsonObject jsonObject;

    private void getDocumentOfPath(String path) {

    }

    public void addDocument(File documentFile, String title, String description, String authoruserid, String assignmentid, String courseid, boolean isPrivate) {
        DFSQL dfsql = new DFSQL();
        String table = "Documents";
        String path = documentFile.getName() + "_" + authoruserid + "_" + assignmentid;
        String[] rows = {"title", "description", "authoruserid", "assignmentid", "courseid", "private", "path"};
        String[] values = {title, description, authoruserid, assignmentid, courseid, isPrivate + "", path};
        uploadDocument(documentFile, path);
        try {
			dfsql.insert(table, values, rows);
			DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->
			{

			});
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

	private void uploadDocument(File documentFile, String path) {

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
}
