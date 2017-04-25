package database.WebServer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import database.DFDatabaseCallbackRunnable;
import database.DFError;
import database.DFSQL.DFSQL;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static database.DFDatabase.*;
import static database.DFError.*;
import static database.WebServer.DFWebServerDispatch.*;

class DFDataDownloader
{
	void downloadDataWith(DFSQL SQLStatement, DFDatabaseCallbackRunnable runnable)
	{
		debugLog(SQLStatement.formattedStatement());
		String calleeMethod = getMethodNameOfSuperMethod();
		new Thread(() ->
		           {
			           try
			           {
				           debugLog("Downloading Data...");
				           String urlParameters = "Password=" + databaseUserPass + "&Username=" + websiteUserName + "&SQLQuery=" + SQLStatement.formattedStatement();
				           byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
				           int postDataLength = postData.length;
				           String request = website + "/" + readFile;
				           URL url = new URL(request);
				           HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				           conn.setDoOutput(true);
				           conn.setInstanceFollowRedirects(false);
				           conn.setRequestMethod("POST");
				           conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				           conn.setRequestProperty("charset", "utf-8");
				           conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
				           conn.setUseCaches(false);
				           try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream()))
				           {
					           wr.write(postData);
				           }

				           Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
				           StringBuilder sb = new StringBuilder();
				           for (int c; (c = in.read()) >= 0; )
				           { sb.append((char) c); }
				           String response = sb.toString();
				           if (!(Objects.equals(response, "") || response.contains("No Data")))
				           { DFWebServerDispatch.current.dataSizePrinter.printDataSize(response.length()); }
				           else
				           { DFWebServerDispatch.current.dataSizePrinter.printDataSize(0); }

				           debugLog("Data Downloaded!");
				           debugLog(response);

				           conn.disconnect();

				           if (Objects.equals(response, "") || response.contains("No Data"))
				           {
					           Map<String, String> errorInfo = new HashMap<>();
					           errorInfo.put(kMethodName, calleeMethod);
					           errorInfo.put(kExpandedDescription, "No data was returned from the database.  Response: " + response);
					           errorInfo.put(kURL, website + "/" + readFile);
					           errorInfo.put(kSQLStatement, SQLStatement.formattedStatement());
					           DFError error = new DFError(1, "No data was returned", errorInfo);
					           debugLog(error);
					           queue.add(() ->
					                     {
						                     debugLog("Queue Executed");
						                     runnable.run(null, error);
					                     });
				           }
				           else if (response.contains("Table") && response.contains("doesn't exist"))
				           {
					           Map<String, String> errorInfo = new HashMap<>();
					           errorInfo.put(kMethodName, calleeMethod);
					           errorInfo.put(kExpandedDescription, "The table attempting to retrieve data from does not exist.  Response: " + response);
					           errorInfo.put(kURL, website + "/" + readFile);
					           errorInfo.put(kSQLStatement, SQLStatement.formattedStatement());
					           DFError error = new DFError(4, "The specified table doesn't exist", errorInfo);
					           debugLog(error);
					           queue.add(() ->
					                     {
						                     debugLog("Queue Executed");
						                     runnable.run(null, error);
					                     });
				           }
				           else if (response.contains("You have an error in your SQL syntax"))
				           {
					           Map<String, String> errorInfo = new HashMap<>();
					           errorInfo.put(kMethodName, calleeMethod);
					           errorInfo.put(kExpandedDescription, "The SQL statement is malformed.  Response: " + response);
					           errorInfo.put(kURL, website + "/" + readFile);
					           errorInfo.put(kSQLStatement, SQLStatement.formattedStatement());
					           DFError error = new DFError(5, "SQL Syntax Error", errorInfo);
					           debugLog(error);
					           queue.add(() ->
					                     {
						                     debugLog("Queue Executed");
						                     runnable.run(null, error);
					                     });
				           }
				           else if (response.contains("Unknown column"))
				           {
					           Map<String, String> errorInfo = new HashMap<>();
					           errorInfo.put(kMethodName, calleeMethod);
					           errorInfo.put(kExpandedDescription, "Encountered an invalid column name.  Response: " + response);
					           errorInfo.put(kURL, website + "/" + readFile);
					           errorInfo.put(kSQLStatement, SQLStatement.formattedStatement());
					           DFError error = new DFError(6, "Unknown Column", errorInfo);
					           debugLog(error);
					           queue.add(() ->
					                     {
						                     debugLog("Queue Executed");
						                     runnable.run(null, error);
					                     });
				           }
				           else if (response.contains("ambiguous"))
				           {
					           Map<String, String> errorInfo = new HashMap<>();
					           errorInfo.put(kMethodName, calleeMethod);
					           errorInfo.put(kExpandedDescription, "Encountered an Ambiguous column.  Response: " + response);
					           errorInfo.put(kURL, website + "/" + readFile);
					           errorInfo.put(kSQLStatement, SQLStatement.formattedStatement());
					           DFError error = new DFError(7, "Ambiguous column", errorInfo);
					           debugLog(error);
					           queue.add(() ->
					                     {
						                     debugLog("Queue Executed");
						                     runnable.run(null, error);
					                     });
				           }
				           else
				           {
					           Gson gsonConverter = new Gson();
					           JsonObject object = gsonConverter.fromJson(response, JsonObject.class);
					           debugLog("Inserting Into Queue: " + object + ",: " + runnable);
					           queue.add(() ->
					                     {
						                     debugLog("Queue Executed");
						                     runnable.run(object, null);
					                     });
					           debugLog("Queue Size: " + queue.size());
				           }
			           }
			           catch (Exception e)
			           {
				           if (defaultDatabase.debug() == 1)
				           {
					           e.printStackTrace();
				           }

				           Map<String, String> errorInfo = new HashMap<>();
				           errorInfo.put(kMethodName, calleeMethod);
				           errorInfo.put(kExpandedDescription, "A(n) " + e.getCause() + " Exception was raised.  Setting DFDatabase -debug to 1 will print the stack trace for this error");
				           errorInfo.put(kURL, website + "/" + readFile);
				           errorInfo.put(kSQLStatement, SQLStatement.formattedStatement());
				           DFError error = new DFError(0, "There was a(n) " + e.getCause() + " error", errorInfo);
				           queue.add(() -> runnable.run(null, error));
			           }

		           }).start();
	}
}
