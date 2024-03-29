package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFSQL.*;
import database.WebServer.DFDataUploaderReturnStatus;
import json.util.JSONQueryError;
import objects.Message;

import java.util.ArrayList;

/**
 * Created by gauravsrivastava on 3/29/17.
 */
public class AnnouncementQuery
{
	private boolean getAllAnnouncementInCourseReturn, getGradeReturn, getCourseGradeReturn;
	private JsonObject jsonObject;

	/**
	 * Add an announcement
	 */
	public void addAnnouncement(String title, String content, String authoruserid, int courseid, QueryCallbackRunnable runnable)
	{
		DFSQL dfsql = new DFSQL();
		String[] rows = {"title", "content", "authoruserid", "courseid"};
		String[] values = {title, content, authoruserid, "" + courseid};
		String table = "Announcement";
		try
		{
			dfsql.insert(table, values, rows);
			DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->
			{
				if (error != null)
				{
					JSONQueryError error1 = new JSONQueryError(0, "Internal Error", null);
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
					JSONQueryError error1 = new JSONQueryError(0, "Internal Error", null);
					runnable.run(null, error1);
				}
			});
		}
		catch (DFSQLError e1)
		{
			e1.printStackTrace();
		}
	}

	/**
	 * get all announcements in a course given courseid. if courseid = -1 returns all announcements outside a course
	 */
	public void getAllAnnouncementInCourse(int courseid, QueryCallbackRunnable runnable)
	{
		DFSQL dfsql = new DFSQL();
		String[] selectedRows = {"id", "title", "content", "timestamp", "authoruserid", "courseid"};
		String table = "Announcement";
		getAllAnnouncementInCourseReturn = true;
		try
		{
			dfsql.select(selectedRows, false, null, null)
			     .from(table)
			     .where(DFSQLEquivalence.equals, "courseid", courseid + "");
			DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->
			{
				if (error != null)
				{
					JSONQueryError error1;
					if (error.code == 1)
					{
						error1 = new JSONQueryError(3, "No Data", null);
					}
					else
					{
						error1 = new JSONQueryError(0, "Internal Error", null);
					}
					runnable.run(null, error1);
					return;
				}
				if (response instanceof JsonObject)
				{
					JsonObject jsonObject = (JsonObject) response;

					ArrayList<Message> allAnnouncementsInCourse = new ArrayList<Message>();
					int courseId;
					String authorUserId, title, content, timestamp;
					Message announcement;

					try
					{
						for (int i = 0; i < jsonObject.get("Data").getAsJsonArray().size(); ++i)
						{
							courseId = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("courseid").getAsInt();
							authorUserId = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("authoruserid").getAsString();
							title = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("title").getAsString();
							content = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("content").getAsString();
							timestamp = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("timestamp").getAsString();
							announcement = new Message(title, content, timestamp, authorUserId, courseId);
							allAnnouncementsInCourse.add(announcement);
						}
					}
					catch (NullPointerException e2)
					{
						JSONQueryError error1 = new JSONQueryError(0, "Internal Error", null);
						runnable.run(null, error1);
					}
					runnable.run(allAnnouncementsInCourse, null);
				}
				else
				{
					JSONQueryError error1 = new JSONQueryError(0, "Internal Error", null);
					runnable.run(null, error1);
				}
			});
		}
		catch (DFSQLError e1)
		{
			e1.printStackTrace();
		}
	}

	public void getAllAnnouncementForStudent(String userid, QueryCallbackRunnable runnable)
	{
		DFSQL dfsql = new DFSQL();
		DFSQL dfsql1 = new DFSQL();
		String[] selectedRows = {"Announcement.id", "title", "content", "timestamp", "authoruserid", "Announcement.courseid"};
		String table1 = "Announcement";
		String table2 = "coursestudentmembership";
		String table3 = "users";
		Join[] joins = new Join[]{
			new Join(table2, table1 + ".courseid", table2 + ".courseid"),
			new Join(table3, table3 + ".userid", table2 + ".studentid")};
		try
		{
			dfsql.select(selectedRows, false, null, null)
			     .from(table1)
			     .join(DFSQLJoin.left, joins)
			     .where(DFSQLEquivalence.equals, "Announcement.courseid", "" + "coursestudentmembership.courseid");
			dfsql1.select(selectedRows, false, null, null)
			      .from(table1)
			      .where(DFSQLEquivalence.equals, "courseid", "-1");
			dfsql.union(dfsql1);
			DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->
			{
				if (error != null)
				{
					JSONQueryError error1;
					if (error.code == 1)
					{
						error1 = new JSONQueryError(3, "No Data", null);
					}
					else
					{
						error1 = new JSONQueryError(0, "Internal Error", null);
					}
					runnable.run(null, error1);
					return;
				}
				if (response instanceof JsonObject)
				{
					JsonObject jsonObject = (JsonObject) response;

					ArrayList<Message> allAnnouncementsInCourse = new ArrayList<Message>();
					int courseId;
					String authorUserId, title, content, timestamp;
					Message announcement;

					try
					{
						for (int i = 0; i < jsonObject.get("Data").getAsJsonArray().size(); ++i)
						{
							courseId = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("courseid").getAsInt();
							authorUserId = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("authoruserid").getAsString();
							title = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("title").getAsString();
							content = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("content").getAsString();
							timestamp = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("timestamp").getAsString();
							announcement = new Message(title, content, timestamp, authorUserId, courseId);
							allAnnouncementsInCourse.add(announcement);
						}
					}
					catch (NullPointerException e2)
					{
						JSONQueryError error1 = new JSONQueryError(0, "Internal Error", null);
						runnable.run(null, error1);
					}
					runnable.run(allAnnouncementsInCourse, null);
				}
				else
				{
					JSONQueryError error1 = new JSONQueryError(0, "Internal Error", null);
					runnable.run(null, error1);
				}
			});
		}
		catch (DFSQLError dfsqlError)
		{
			dfsqlError.printStackTrace();
		}
	}


}
