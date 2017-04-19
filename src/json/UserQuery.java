package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFError;
import database.DFSQL.*;
import database.WebServer.DFDataUploaderReturnStatus;
import json.util.JSONQueryError;
import objects.Instructor;
import objects.User;
import objects.userType;
import ui.util.UIStrings;
import uikit.DFNotificationCenter;

import java.util.ArrayList;

import static database.DFDatabase.debugLog;

/**
 * Created by Naveen Ganessin
 */
@SuppressWarnings({"unused", "RedundantCast", "unchecked", "ConstantConditions"})
public class UserQuery
{
	private String bufferString;

	public void getAllStudents(int limit, int offset, QueryCallbackRunnable runnable)
	{
		DFSQL dfsql = new DFSQL();
		String[] selectedRows = {"students.userid", "email", "firstname", "lastname", "password", "birthday"};
		String table1 = "students";
		String table2 = "users";
		try
		{
			dfsql.select(selectedRows, false, null, null)
			     .from(table1)
			     .join(DFSQLJoin.left, table2, "students.userid", "users.userid")
			     .limit(limit)
			     .offset(offset);
			DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->
			{
				System.out.println(response);
				System.out.println(error);
				if (error != null)
				{
					//Process the error and return appropriate new error to UI.
					JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
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
					return;
				}
				ArrayList<User> allStudents = new ArrayList<>();
				JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
				String usernameReceived, userEmail, userBirthday, userFirstName, userLastName;
				userType userType;
				User user;
				try
				{
					for (int i = 0; i < jsonObject.get("Data").getAsJsonArray().size(); ++i)
					{
						usernameReceived = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("userid").getAsString();
						userEmail = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("email").getAsString();
						userBirthday = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("birthday").getAsString();
						userFirstName = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("firstname").getAsString();
						userLastName = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("lastname").getAsString();
						userType = objects.userType.STUDENT;

						user = new User();

						user.setUserID(usernameReceived);
						user.setEmail(userEmail);
						user.setFirstName(userFirstName);
						user.setLastName(userLastName);
						user.setBirthday(userBirthday);
						user.setUserType(userType);
						allStudents.add(user);
					}
				}
				catch (NullPointerException e2) {runnable.run(null, error1);}
				runnable.run(allStudents, null);
			});
		}
		catch (DFSQLError e1)
		{
			e1.printStackTrace();
		}
	}

	/**
	 * get all instructors
	 */
	public void getAllInstructors(int limit, int offset, QueryCallbackRunnable runnable)
	{
		DFSQL dfsql = new DFSQL();
		String[] selectedRows = {"instructor.userid", "email", "firstname", "lastname", "password", "birthday", "officehours", "roomno"};
		String table1 = "instructor";
		String table2 = "users";
		try
		{
			dfsql.select(selectedRows, false, null, null)
			     .from(table1)
			     .join(DFSQLJoin.left, table2, "instructor.userid", "users.userid")
			     .limit(limit)
			     .offset(offset);
			DFDatabase.defaultDatabase.execute(dfsql, (Object response, DFError error) ->
			{
				System.out.println(response);
				System.out.println(error);
				if (error != null)
				{
					//Process the error and return appropriate new error to UI.
					JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
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
					return;
				}
				ArrayList<User> allInstructors = new ArrayList<>();
				JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
				String usernameReceived, userEmail, userBirthday, userFirstName, userLastName, userOfficeHours, userRoomNo;
				userType userType;
				Instructor instructor;
				try
				{
					for (int i = 0; i < jsonObject.get("Data").getAsJsonArray().size(); ++i)
					{
						usernameReceived = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("userid").getAsString();
						userEmail = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("email").getAsString();
						userBirthday = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("birthday").getAsString();
						userFirstName = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("firstname").getAsString();
						userLastName = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("lastname").getAsString();
						userOfficeHours = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("officehours").getAsString();
						userRoomNo = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("roomno").getAsString();
						userType = objects.userType.TEACHER;

						instructor = new Instructor();

						instructor.setUserID(usernameReceived);
						instructor.setEmail(userEmail);
						instructor.setFirstName(userFirstName);
						instructor.setLastName(userLastName);
						instructor.setBirthday(userBirthday);
						instructor.setUserType(userType);
						instructor.setOfficeHours(userOfficeHours);
						instructor.setRoomNo(userRoomNo);
						allInstructors.add(instructor);
					}
				}
				catch (NullPointerException e2) {e2.printStackTrace(); runnable.run(null, error1);}
				runnable.run(allInstructors, null);
			});
		}
		catch (DFSQLError e1)
		{
			e1.printStackTrace();
		}
	}

	public void getUser(String username, QueryCallbackRunnable runnable)
	{
		DFSQL dfsql = new DFSQL();
		String[] selectedRows = {"userID", "firstName", "lastName", "email", "birthday", "userType"};
		try
		{
			dfsql.select(selectedRows, false, null, null).from("users").where(DFSQLEquivalence.equals, "userID", username);
			DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->
			{
				System.out.println(response);
				System.out.println(error);
				if (error != null)
				{
					//Process the error and return appropriate new error to UI.
					JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
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
					return;
				}
				String usernameReceived = null, userEmail = null, userBirthday = null, userFirstName = null, userLastName = null;
				int userTypeInt;
				userType userType = null;
				try
				{
					usernameReceived = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("userID").getAsString();
					userEmail = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("email").getAsString();
					userBirthday = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("birthday").getAsString();
					userFirstName = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("firstName").getAsString();
					userLastName = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("lastName").getAsString();
					userTypeInt = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("userType").getAsInt();
					userType = intToUserTypeConverter(userTypeInt);

				}
				catch (NullPointerException e2)
				{
					DFNotificationCenter.defaultCenter.post(UIStrings.returned, null);
				}
				User user = new User();
				user.setUserID(usernameReceived);
				user.setEmail(userEmail);
				user.setFirstName(userFirstName);
				user.setLastName(userLastName);
				user.setBirthday(userBirthday);
				user.setUserType(userType);

				runnable.run(user, null);
			});
		}
		catch (DFSQLError e1)
		{
			e1.printStackTrace();
		}
	}


	public void removeUser(String username, QueryCallbackRunnable runnable)
	{
		DFSQL dfsql = new DFSQL();
		try
		{
			dfsql.delete("users", new Where(DFSQLConjunction.none, DFSQLEquivalence.equals, new DFSQLClause("userid", username)));
			DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->
			{
				if (error != null)
				{
					//Process the error and return appropriate new error to UI.
					JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
					runnable.run(null, error1);
					return;
				}
				DFDataUploaderReturnStatus uploadStatus;
				if (response instanceof DFDataUploaderReturnStatus)
				{
					uploadStatus = (DFDataUploaderReturnStatus) response;
					if (uploadStatus == DFDataUploaderReturnStatus.success)
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
		}
		catch (DFSQLError e1)
		{
			e1.printStackTrace();
		}
	}

	public void verifyUserLogin(String username, String password, QueryCallbackRunnable runnable)
	{
		DFSQL dfsql = new DFSQL();
		bufferString = password;
		String[] selectedRows = {"password"};
		try
		{
			dfsql.select(selectedRows, false, null, null).from("users").where(DFSQLEquivalence.equals, "userID", username);
			DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->
			{
				if (error != null)
				{
					//Process the error and return appropriate new error to UI.
					JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
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
					return;
				}
				String databasePassword;
				try
				{
					databasePassword = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("password").getAsString();
					if (databasePassword.equals(bufferString))
					{
						runnable.run(Boolean.TRUE, null);
						debugLog("verifylogin returned success");
					}
					else
					{
						runnable.run(Boolean.FALSE, null);
						debugLog("verifylogin returned fail cause passwords don't match");
					}
				}
				catch (NullPointerException e2)
				{
					runnable.run(Boolean.FALSE, null);
					debugLog("verifylogin returned nothing");
				}
			});
		}
		catch (DFSQLError e1)
		{
			e1.printStackTrace();
		}
	}

	public void addUserAsStudent(String username, QueryCallbackRunnable runnable)
	{
		DFSQL dfsql = new DFSQL();
		String attr = "userType";
		String value = userTypeToIntConverter(userType.STUDENT) + "";
		String[] rows = {"userid"};
		String[] values = {username};
		try
		{
			dfsql.update("users", attr, value).where(DFSQLEquivalence.equals, "userid", username);
			debugLog(dfsql.formattedStatement());
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
						try
						{
							DFSQL dfsql1 = new DFSQL().insert("students", values, rows);
							DFDatabase.defaultDatabase.execute(dfsql1, (response1, error12) ->
							{
								if (response1 instanceof DFDataUploaderReturnStatus)
								{
									DFDataUploaderReturnStatus returnStatus1 = (DFDataUploaderReturnStatus) response1;
									if (returnStatus1 == DFDataUploaderReturnStatus.success)
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
						}
						catch (DFSQLError e2)
						{
							runnable.run(false, new JSONQueryError(0, "Internal Error", null));
						}
					}
					else
					{
						runnable.run(false, new JSONQueryError(0, "Internal Error", null));
					}
				}
				else
				{
					runnable.run(null, new JSONQueryError(0, "Internal Error", null));
				}
			});
		}
		catch (DFSQLError e1)
		{
			e1.printStackTrace();
		}
	}

	public void removeUserAsStudent(String username, QueryCallbackRunnable runnable)
	{
		DFSQL dfsql = new DFSQL();
		String attr = "userType";
		String value = "0";
		try
		{
			dfsql.update("users", attr, value).where(DFSQLEquivalence.equals, "userid", username);
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
						try
						{
							DFSQL dfsql1 = new DFSQL().delete("students",
							                                  new Where(DFSQLConjunction.none,
							                                            DFSQLEquivalence.equals,
							                                            new DFSQLClause("userid", username)));
							DFDatabase.defaultDatabase.execute(dfsql1, (response1, error12) ->
							{
								if (response1 instanceof DFDataUploaderReturnStatus)
								{
									DFDataUploaderReturnStatus returnStatus1 = (DFDataUploaderReturnStatus) response1;
									if (returnStatus1 == DFDataUploaderReturnStatus.success)
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
						}
						catch (DFSQLError e2)
						{
							runnable.run(false, new JSONQueryError(0, "Internal Error", null));
						}
					}
					else
					{
						runnable.run(false, new JSONQueryError(0, "Internal Error", null));
					}
				}
				else
				{
					runnable.run(null, new JSONQueryError(0, "Internal Error", null));
				}
			});
		}
		catch (DFSQLError e1)
		{
			e1.printStackTrace();
		}
	}

	public void addUserAsInstructor(String username, String officehours, String roomno, QueryCallbackRunnable runnable)
	{
		DFSQL dfsql = new DFSQL();
		String attr = "userType";
		String value = userTypeToIntConverter(userType.TEACHER) + "";
		String[] rows = {"userid", "officehours", "roomno"};
		String[] values = {username, officehours, roomno};
		try
		{
			dfsql.update("users", attr, value).where(DFSQLEquivalence.equals, "userid", username);
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
						try
						{
							DFSQL dfsql1 = new DFSQL().insert("instructor", values, rows);
							DFDatabase.defaultDatabase.execute(dfsql1, (response1, error12) ->
							{
								if (response1 instanceof DFDataUploaderReturnStatus)
								{
									DFDataUploaderReturnStatus returnStatus1 = (DFDataUploaderReturnStatus) response1;
									if (returnStatus1 == DFDataUploaderReturnStatus.success)
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
						}
						catch (DFSQLError e2)
						{
							runnable.run(false, new JSONQueryError(0, "Internal Error", null));
						}
					}
					else
					{
						runnable.run(false, new JSONQueryError(0, "Internal Error", null));
					}
				}
				else
				{
					runnable.run(null, new JSONQueryError(0, "Internal Error", null));
				}
			});
		}
		catch (DFSQLError e1)
		{
			e1.printStackTrace();
		}
	}

	public void removeUserAsInstructor(String username, QueryCallbackRunnable runnable)
	{
		DFSQL dfsql = new DFSQL();
		String attr = "userType";
		String value = "0";
		try
		{
			dfsql.update("users", attr, value).where(DFSQLEquivalence.equals, "userid", username);
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
						try
						{
							DFSQL dfsql1 = new DFSQL().delete("instructor",
							                                  new Where(DFSQLConjunction.none,
							                                            DFSQLEquivalence.equals,
							                                            new DFSQLClause("userid", username))
							);
							DFDatabase.defaultDatabase.execute(dfsql1, (response1, error12) ->
							{
								if (response1 instanceof DFDataUploaderReturnStatus)
								{
									DFDataUploaderReturnStatus returnStatus1 = (DFDataUploaderReturnStatus) response1;
									if (returnStatus1 == DFDataUploaderReturnStatus.success)
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
						}
						catch (DFSQLError e2)
						{
							runnable.run(false, new JSONQueryError(0, "Internal Error", null));
						}
					}
					else
					{
						runnable.run(false, new JSONQueryError(0, "Internal Error", null));
					}
				}
				else
				{
					runnable.run(null, new JSONQueryError(0, "Internal Error", null));
				}
			});
		}
		catch (DFSQLError e1)
		{
			e1.printStackTrace();
		}
	}


	public void addNewUser(String userUserId, String userPassword, String userEmail, String userBirthday, String userFirstName, String userLastName, userType userUserType, QueryCallbackRunnable runnable)
	{
		int convertedUserType = userTypeToIntConverter(userUserType);
		String[] rows = {"userID", "password", "email", "birthday", "firstName", "lastName", "userType"};
		String[] values = {userUserId, userPassword, userEmail, userBirthday, userFirstName, userLastName, String.valueOf(convertedUserType)};
		DFSQL dfsql = new DFSQL();
		try
		{
			dfsql.insert("users", values, rows);
			debugLog(dfsql.formattedStatement());
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
		}
		catch (DFSQLError e1)
		{
			e1.printStackTrace();
		}
	}

	private int userTypeToIntConverter(userType userType)
	{
		if (userType == objects.userType.ADMIN)
		{
			return 1;
		}
		else if (userType == objects.userType.TEACHER)
		{
			return 2;
		}
		else if (userType == objects.userType.STUDENT)
		{
			return 3;
		}
		else
		{
			return 4;
		}
	}

	private userType intToUserTypeConverter(int userTypeInt)
	{
		if (userTypeInt == 1)
		{
			return userType.ADMIN;
		}
		else if (userTypeInt == 2)
		{
			return userType.TEACHER;
		}
		else if (userTypeInt == 3)
		{
			return userType.STUDENT;
		}
		else
		{
			return userType.TA;
		}
	}
}
