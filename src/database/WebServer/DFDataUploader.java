package database.WebServer;

import database.DFDatabaseCallbackDelegate;
import database.DFError;
import database.DFSQL.DFSQL;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static database.DFDatabase.*;
import static database.DFError.*;
import static database.WebServer.DFWebServerDispatch.*;

class DFDataUploader
{
    void uploadDataWith(DFSQL SQLStatement, DFDatabaseCallbackDelegate delegate)
	{
        debugLog(SQLStatement.formattedStatement());

		new Thread(() ->
        {
            try
            {
                debugLog("Uploading Data...");
                String urlParameters  = "Password="+ databaseUserPass + "&Username="+ websiteUserName + "&SQLQuery=" + SQLStatement.formattedStatement();
                if (defaultDatabase.debug() == 1)
                {
                    print(urlParameters);
                }
                byte[] postData       = urlParameters.getBytes(StandardCharsets.UTF_8);
                int    postDataLength = postData.length;
                String request        = website + "/" + writeFile;
                URL    url            = new URL(request);
                HttpURLConnection conn= (HttpURLConnection)url.openConnection();
                conn.setDoOutput(true);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("charset", "utf-8");
                conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                conn.setUseCaches(true);
                try(DataOutputStream wr = new DataOutputStream(conn.getOutputStream()))
                {
                    wr.write(postData);
                }

                Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                for (int c; (c = in.read()) >= 0;)
                    sb.append((char)c);
                String response = sb.toString();

                conn.disconnect();

                debugLog("Data Uploaded! Response: " + response);

                if (Objects.equals(response, ""))
                {
                    Map<String, String> errorInfo = new HashMap<>();
                    errorInfo.put(kMethodName, getMethodName());
                    errorInfo.put(kExpandedDescription, "No data was returned from the database.  Response: " + response);
                    errorInfo.put(kURL, website + "/" + writeFile);
                    errorInfo.put(kSQLStatement, SQLStatement.formattedStatement());
                    DFError error = new DFError(1, "No data was returned", errorInfo);
                    queue.add(() -> delegate.uploadStatus(DFDataUploaderReturnStatus.error, error));
                    return;
                }

                if (response.contains("Success"))
                {
                    queue.add(() -> delegate.uploadStatus(DFDataUploaderReturnStatus.success, null));
                }
                else
                {
                    queue.add(() -> delegate.uploadStatus(DFDataUploaderReturnStatus.failure, null));
                }
            }
            catch(NullPointerException | IOException e)
            {
                if (defaultDatabase.debug() == 1)
                {
                    e.printStackTrace();
                }

                Map<String, String> errorInfo = new HashMap<>();
                errorInfo.put(kMethodName, getMethodName());
                errorInfo.put(kExpandedDescription, "A(n) "+ e.getCause() + " Exception was raised.  Setting DFDatabase -debug to 1 will print the stack trace for this error");
                errorInfo.put(kURL, website + "/" + writeFile);
                errorInfo.put(kSQLStatement, SQLStatement.formattedStatement());
                DFError error = new DFError(0, "There was a(n) " + e.getCause() + " error", errorInfo);
                queue.add(() -> delegate.uploadStatus(DFDataUploaderReturnStatus.error, error));
            }
        }).start();
	}
}
