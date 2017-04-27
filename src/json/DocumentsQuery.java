package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFError;
import database.DFSQL.*;
import database.WebServer.DFDataUploaderReturnStatus;
import json.util.JSONQueryError;
import objects.Assignment;
import objects.FileUpload;
import okhttp3.*;
import ui.common.AssignmentsList;
import ui.util.UIVariables;


import javax.print.attribute.standard.Media;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by gauravsrivastava on 3/30/17.
 */
public class DocumentsQuery {
    private JsonObject jsonObject;

    private void getDocumentOfPath(String path) {
        MediaType mediaType = MediaType.parse("multipart/form-data;");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://mascomputech.com/abc/downloadFile.php?filename=" + path)
                .build();
        try {
            Response response = client.newCall(request).execute();
            InputStream is = response.body().byteStream();
            BufferedInputStream input = new BufferedInputStream(is);
            OutputStream output = new FileOutputStream(UIVariables.current.applicationDirectories.temp + path);
            byte[] data = new byte[1024];
            int count = 0;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void addDocument(File documentFile, String title, String description, String authoruserid, String assignmentid, String courseid, boolean isPrivate, QueryCallbackRunnable runnable) {
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
                else
                {
                    runnable.run(null, new JSONQueryError(0, "Internal Error", null));
                }
			});
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

	private void uploadDocument(File documentFile, String path) {
		OkHttpClient client = new OkHttpClient();
        Path source = Paths.get(documentFile.getPath());
        try {
            String contentType = Files.probeContentType(source);
            MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("multipart/form-data");
            Request request = new Request.Builder()
                    .url("https://mascomputech.com/abc/uploadDocument.php")
                    .post(RequestBody.create(MEDIA_TYPE_MARKDOWN,
                            "Content-Disposition: form-data; name=\"userfile\"; filename=\"" + documentFile.getName()+"\"\r\nContent-Type:" + contentType + "\r\n\r\n\r\n"))
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
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

    public void getDocument(int documentid, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String[] selectedRows = {"id", "title", "description", "path", "authoruserid", "courseid"};
        String table = "Documents";
        try {
            dfsql.select(selectedRows, false, null, null)
                    .from(table)
                    .where(DFSQLEquivalence.equals, "id", documentid + "");
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->
            {
                if(error != null) {
                    JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
                    runnable.run(null, error1);
                    return;
                }
                if(response instanceof JsonObject) {
                    jsonObject = (JsonObject) response;
                }
                else {
                    return;
                }
                String path = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("path").getAsString();
                getDocumentOfPath(path);
                runnable.run(null, null);
            });
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

    public void getAllDocumentsIdsInCourse(int courseid, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String[] selectedRows = {"id", "title", "description", "path", "authoruserid", "courseid", "private", "assignmentid"};
        String table = "Documents";
        boolean checkPrivate = !AssignmentsList.isInstructor();
        try {
            if(checkPrivate) {
                String[] attrs = {"courseid", "private"};
                String[] values = {courseid + "", "false"};
                dfsql.select(selectedRows, false, null, null)
                        .from(table)
                        .where(DFSQLConjunction.and, DFSQLEquivalence.equals, attrs, values);
            } else {
                dfsql.select(selectedRows, false, null, null)
                        .from(table)
                        .where(DFSQLEquivalence.equals, "courseid", courseid + "");
            }
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->
            {
                if(error != null) {
                    JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
                    runnable.run(null, error1);
                    return;
                }
                if(response instanceof JsonObject) {
                    jsonObject = (JsonObject) response;
                }
                else {
                    return;
                }
                String title = null, description = null, path = null, authoruserid = null;
                int id = 0, courseId = 0, assignmentid = 0;
                boolean isPrivate = false;
                ArrayList<FileUpload> fileUploads = new ArrayList<>();
                JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
                try
                {
                    for (int i = 0; i < jsonObject.get("Data").getAsJsonArray().size(); ++i)
                    {
                        id = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("id").getAsInt();
                        title = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("title").getAsString();
                        description = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("description").getAsString();
                        path = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("path").getAsString();
                        authoruserid = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("authoruserid").getAsString();
                        courseId = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("courseid").getAsInt();
                        assignmentid = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("assignmentid").getAsInt();
                        isPrivate = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("private").getAsBoolean();

                        FileUpload fileUpload = new FileUpload(title, description, path, authoruserid, courseId, assignmentid, isPrivate);
                        fileUploads.add(fileUpload);
                    }
                    runnable.run(fileUploads, null);
                }
                catch (NullPointerException e2)
                {
                    e2.printStackTrace();
                    runnable.run(null, error1);
                }

            });
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

}
