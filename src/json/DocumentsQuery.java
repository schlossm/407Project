package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFDatabaseCallbackDelegate;
import database.DFError;
import database.DFSQL.*;
import database.WebServer.DFDataUploaderReturnStatus;

/**
 * Created by gauravsrivastava on 3/30/17.
 */
public class DocumentsQuery implements DFDatabaseCallbackDelegate {



    private void getDocumentOfPath(String path) {

    }

    public void addDocument() {

    }

    public void deleteDocument(int documentid) {
        DFSQL dfsql = new DFSQL();
        String table = "documents";
        Where where = new Where(DFSQLConjunction.none, DFSQLEquivalence.equals, new DFSQLClause("id", documentid + ""));
        try {
            dfsql.delete(table, where);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

    public void getDocument(int documentid) {
        DFSQL dfsql = new DFSQL();
        String[] selectedRows = {"id", "title", "description", "path", "authoruserid", "courseid"};
        String table = "documents";
        try {
            dfsql.select(selectedRows, false, null, null)
                    .from(table)
                    .where(DFSQLEquivalence.equals, "courseid", documentid + "");
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

    public void getAllDocumentsIdsInCourse(int courseid) {
        DFSQL dfsql = new DFSQL();
        String[] selectedRows = {"id", "title", "description", "path", "authoruserid", "courseid"};
        String table = "documents";
        try {
            dfsql.select(selectedRows, false, null, null)
                    .from(table)
                    .where(DFSQLEquivalence.equals, "courseid", courseid + "");
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

    public void getAllDocumentsIds() {
        DFSQL dfsql = new DFSQL();
        String[] selectedRows = {"id", "title", "description", "path", "authoruserid", "courseid"};
        String table = "documents";
        try {
            dfsql.select(selectedRows, false, null, null)
                    .from(table);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }
    @Override
    public void returnedData(JsonObject jsonObject, DFError error) {

    }

    @Override
    public void uploadStatus(DFDataUploaderReturnStatus success, DFError error) {

    }
}
