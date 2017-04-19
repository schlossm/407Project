package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFSQL.*;
import json.util.JSONQueryError;
import objects.Grade;
import objects.Message;
import ui.util.UIStrings;
import uikit.DFNotificationCenter;

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
	public void addAnnouncement(String title, String content, String timestamp, String authoruserid, int courseid, QueryCallbackRunnable runnable)
	{
		DFSQL dfsql = new DFSQL();
		String[] rows = {"title", "content", "timestamp", "authoruserid", "courseid"};
		String[] values = {title, content, timestamp, authoruserid, "" + courseid};
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
				if (response instanceof Boolean)
				{
					boolean bool = (Boolean) response;
					runnable.run(bool, null);
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
					else {
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

	private void returnHandler()
	{
		if (getAllAnnouncementInCourseReturn)
		{
			ArrayList<Message> allAnouncementsInCourse = new ArrayList<Message>();
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
					allAnouncementsInCourse.add(announcement);
				}
			}
			catch (NullPointerException e2)
			{
				DFNotificationCenter.defaultCenter.post(UIStrings.returned, null);
			}
			DFNotificationCenter.defaultCenter.post(UIStrings.returned, allAnouncementsInCourse);
			getAllAnnouncementInCourseReturn = false;
		}
		else if (getGradeReturn)
		{
			int points = 0;
			int assignmentId = 0;
			String userId = "";
			try
			{
				points = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("grade").getAsInt();
				assignmentId = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("assignmentid").getAsInt();
				userId = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("userid").getAsString();
			}
			catch (NullPointerException e2)
			{
				DFNotificationCenter.defaultCenter.post(UIStrings.returned, null);
			}
			Grade grade = new Grade(userId, assignmentId, String.valueOf(points));
	        /* Wait for Alex to implement the rest of the fields */
			DFNotificationCenter.defaultCenter.post(UIStrings.returned, grade);
			System.out.println("getUser posting user to returned");
			getGradeReturn = false;
		}
		else if (getCourseGradeReturn)
		{
			int points = 0;
			int assignmentId = 0;
			String userId = "";
			try
			{
				points = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("grade").getAsInt();
				assignmentId = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("assignmentid").getAsInt();
				userId = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("userid").getAsString();
			}
			catch (NullPointerException e2)
			{
				DFNotificationCenter.defaultCenter.post(UIStrings.returned, null);
			}
			Grade grade = new Grade(userId, assignmentId, String.valueOf(points));
            /* Wait for Alex to implement the rest of the fields */
			DFNotificationCenter.defaultCenter.post(UIStrings.returned, grade);
			System.out.println("getUser posting user to returned");
			getGradeReturn = false;
		}
	}
}
