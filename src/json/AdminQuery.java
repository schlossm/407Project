package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFSQL.DFSQL;
import database.DFSQL.DFSQLError;
import json.util.JSONQueryError;
import objects.Course;

import java.util.ArrayList;

/**
 * Created by Naveen Ganessin on 4/24/2017.
 */
public class AdminQuery
{
	private JsonObject jsonObject;

	public void getAllCoursesCount(QueryCallbackRunnable runnable)
	{
		DFSQL dfsql = new DFSQL();
		String[] selectRows = {"COUNT(coursename) AS courseCount"};
		String table = "courses";
		try
		{
			dfsql.select(selectRows, false, null, null).from(table).limit(1);
			DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->
			{
				JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
				if (error != null)
				{
					//Process the error and return appropriate new error to UI.
					runnable.run(null, error1);
					return;
				}
				JsonObject jsonObject;
				if (response instanceof JsonObject)
				{
					jsonObject = (JsonObject) response;
				}
				else
				{
					runnable.run(null, error1);
					return;
				}
				int allCourseCount = 0;
				try
				{
					allCourseCount = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("courseCount").getAsInt();
				}
				catch (NullPointerException e2)
				{
					runnable.run(null, error1);
				}
				runnable.run(allCourseCount, null);
			});
		}
		catch (DFSQLError e1)
		{
			e1.printStackTrace();
		}
	}

	public void getAllStudentsCount(QueryCallbackRunnable runnable)
	{
		DFSQL dfsql = new DFSQL();
		String[] selectRows = {"COUNT(id) AS studentsCount"};
		String table = "students";
		try
		{
			dfsql.select(selectRows, false, null, null).from(table).limit(1);
			DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->
			{
				JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
				if (error != null)
				{
					//Process the error and return appropriate new error to UI.
					runnable.run(null, error1);
					return;
				}
				JsonObject jsonObject;
				if (response instanceof JsonObject)
				{
					jsonObject = (JsonObject) response;
				}
				else
				{
					runnable.run(null, error1);
					return;
				}
				int allStudentsCount = 0;

				try
				{
					allStudentsCount = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("studentsCount").getAsInt();
				}
				catch (NullPointerException e2)
				{
					runnable.run(null, error1);
				}
				runnable.run(allStudentsCount, null);
			});
		}
		catch (DFSQLError e1)
		{
			e1.printStackTrace();
		}
	}

	public void getAllInstructorsCount(QueryCallbackRunnable runnable)
	{
		DFSQL dfsql = new DFSQL();
		String[] selectRows = {"COUNT(id) AS instructorCount"};
		String table = "instructor";
		try
		{
			dfsql.select(selectRows, false, null, null).from(table).limit(1);
			DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->
			{
				JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
				if (error != null)
				{
					//Process the error and return appropriate new error to UI.
					runnable.run(null, error1);
					return;
				}
				JsonObject jsonObject;
				if (response instanceof JsonObject)
				{
					jsonObject = (JsonObject) response;
				}
				else
				{
					runnable.run(null, error1);
					return;
				}
				int allInstructorCount = 0;

				try
				{
					allInstructorCount = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("instructorCount").getAsInt();
				}
				catch (NullPointerException e2)
				{
					runnable.run(null, error1);
				}
				runnable.run(allInstructorCount, null);
			});
		}
		catch (DFSQLError e1)
		{
			e1.printStackTrace();
		}
	}
}
