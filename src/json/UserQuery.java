package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFDatabaseCallbackDelegate;
import database.DFDatabaseCallbackRunnable;
import database.DFError;
import database.DFSQL.*;
import database.WebServer.DFDataUploaderReturnStatus;
import json.util.JSONQueryError;
import objects.User;
import objects.userType;
import ui.util.UIStrings;
import uikit.DFNotificationCenter;

import static database.DFDatabase.debugLog;

/**
 * Created by Naveen Ganessin
 */

public class UserQuery implements DFDatabaseCallbackDelegate {
    private boolean getUserReturn, getUserExistsReturn;
    private DFDataUploaderReturnStatus uploadSuccess;
    private JsonObject jsonObject;
    private String bufferString;
    private boolean verifyUserLoginReturn;


    public void getUser(String username, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        getUserReturn = true;
        String[] selectedRows = {"userID", "firstName", "lastName", "email", "birthday", "userType"};
        try {
            dfsql.select(selectedRows, false, null, null).from("users").where(DFSQLEquivalence.equals, "userID", username);
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->
            {
                if(error != null)
                {
                    //Process the error and return appropriate new error to UI.
					JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
					runnable.run(null, error1);
                    return;
                }
                JsonObject jsonObject;
                if (response instanceof JsonObject)
                {
                    jsonObject = (JsonObject)response;
                }
                else
                {
                    return;
                }
                String usernameReceived = null, userEmail = null, userBirthday = null, userFirstName = null, userLastName = null;
                int userTypeInt;
                userType userType = null;
                try {
                    usernameReceived = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("userID").getAsString();
                    userEmail = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("email").getAsString();
                    userBirthday = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("birthday").getAsString();
                    userFirstName = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("firstName").getAsString();
                    userLastName = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("lastName").getAsString();
                    userTypeInt = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("userType").getAsInt();
                    userType = intToUserTypeConverter(userTypeInt);

                }catch (NullPointerException e2){
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
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }


    public void removeUser(String username, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        try {
            dfsql.delete("users", new Where(DFSQLConjunction.none, DFSQLEquivalence.equals, new DFSQLClause("userid", username)));
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->{
                if(error != null)
                {
                    //Process the error and return appropriate new error to UI.
                    JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
                    runnable.run(null, error1);
                    return;
                }
            });
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

    public void verifyUserLogin(String username, String password) {
        DFSQL dfsql = new DFSQL();
        bufferString = password;
        String[] selectedRows = {"password"};
        verifyUserLoginReturn = true;
        try {
            dfsql.select(selectedRows, false, null, null).from("users").where(DFSQLEquivalence.equals, "userID", username);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

    public boolean addUserAsStudent(String username) {
        boolean isaddSuccess;
        DFSQL dfsql = new DFSQL();
        String attr = "userType";
        String value = userTypeToIntConverter(userType.STUDENT) + "";
        String[] rows = {"userid"};
        String[] values = {username};
        try {
            dfsql.update("users", attr, value).where(DFSQLEquivalence.equals, "userid", username);
            debugLog(dfsql.formattedStatement());
            DFDatabase.defaultDatabase.execute(dfsql, this);
            dfsql.insert("students", values, rows);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
        isaddSuccess = uploadSuccess == DFDataUploaderReturnStatus.success;
        return  isaddSuccess;
    }

    public boolean removeUserAsStudent(String username) {
        boolean isaddSuccess;
        DFSQL dfsql = new DFSQL();
        String attr = "userType";
        String value = "0";
        try {
            dfsql.update("users", attr, value).where(DFSQLEquivalence.equals, "userid", username);
            debugLog(dfsql.formattedStatement());
            DFDatabase.defaultDatabase.execute(dfsql, this);
            dfsql.delete(   "students",
                            new Where(  DFSQLConjunction.none,
                                        DFSQLEquivalence.equals,
                                        new DFSQLClause("userid", username)
                            )
                        );
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
        isaddSuccess = uploadSuccess == DFDataUploaderReturnStatus.success;
        return  isaddSuccess;
    }

    public boolean addUserAsInstructor(String username, String officehours, String roomno) {
        boolean isaddSuccess;
        DFSQL dfsql = new DFSQL();
        String attr = "userType";
        String value = userTypeToIntConverter(userType.TEACHER) + "";
        String[] rows = {"userid", "officehours", "roomno"};
        String[] values = {username, officehours, roomno};
        try {
            dfsql.update("users", attr, value).where(DFSQLEquivalence.equals, "userid", username);
            debugLog(dfsql.formattedStatement());
            DFDatabase.defaultDatabase.execute(dfsql, this);
            dfsql.insert("instructor", values, rows);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
        isaddSuccess = uploadSuccess == DFDataUploaderReturnStatus.success;
        return  isaddSuccess;
    }

    public boolean removeUserAsInstructor(String username) {
        boolean isaddSuccess;
        DFSQL dfsql = new DFSQL();
        String attr = "userType";
        String value = "0";
        try {
            dfsql.update("users", attr, value).where(DFSQLEquivalence.equals, "userid", username);
            debugLog(dfsql.formattedStatement());
            DFDatabase.defaultDatabase.execute(dfsql, this);
            dfsql.delete(   "instructor",
                    new Where(  DFSQLConjunction.none,
                            DFSQLEquivalence.equals,
                            new DFSQLClause("userid", username)
                    )
            );
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
        isaddSuccess = uploadSuccess == DFDataUploaderReturnStatus.success;
        return  isaddSuccess;
    }

    @Override
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


    private void returnHandler(){
        System.out.println("triggers return handler");
        if(getUserReturn){
            String usernameReceived = null, userEmail = null, userBirthday = null, userFirstName = null, userLastName = null;
            int userTypeInt = 0;
            userType userType = null;
            try {
                usernameReceived = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("userID").getAsString();
                userEmail = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("email").getAsString();
                userBirthday = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("birthday").getAsString();
                userFirstName = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("firstName").getAsString();
                userLastName = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("lastName").getAsString();
                userTypeInt = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("userType").getAsInt();
                userType = intToUserTypeConverter(userTypeInt);

            }catch (NullPointerException e2){
                DFNotificationCenter.defaultCenter.post(UIStrings.returned, null);
            }
            User user = new User();
            user.setUserID(usernameReceived);
            user.setEmail(userEmail);
            user.setFirstName(userFirstName);
            user.setLastName(userLastName);
            user.setBirthday(userBirthday);
            user.setUserType(userType);

            DFNotificationCenter.defaultCenter.post(UIStrings.returned, user);
            System.out.println("getUser posting user to returned");
            getUserReturn = false;
        } else if (verifyUserLoginReturn) {
            System.out.println("gets to verifyuserlogin");
            String databasePassword = "";
            try {
                databasePassword = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("password").getAsString();
                if(databasePassword.equals(bufferString)){DFNotificationCenter.defaultCenter.post(UIStrings.success, Boolean.TRUE);
                    debugLog("verifylogin returned success");}
                else {DFNotificationCenter.defaultCenter.post(UIStrings.failure, Boolean.FALSE);
                    debugLog("verifylogin returned fail cause passwords don't match");}
            } catch (NullPointerException e2){
                DFNotificationCenter.defaultCenter.post(UIStrings.failure, Boolean.FALSE);
                debugLog("verifylogin returned nothing");
            }
            verifyUserLoginReturn = false;
        } else if (getUserExistsReturn) {
            String usernameReceived = null;
            try {
                usernameReceived = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("userID").getAsString();
            }catch (NullPointerException e2){
                DFNotificationCenter.defaultCenter.post(UIStrings.exists, false);
            }
            if(usernameReceived != null){
                DFNotificationCenter.defaultCenter.post(UIStrings.exists, true);
            }
        }

        getUserExistsReturn = false;
        bufferString = null;
    }

    public boolean addNewUser(String userUserId, String userPassword,  String userEmail, String userBirthday, String userFirstName, String userLastName, userType userUserType, QueryCallbackRunnable runnable){
        int convertedUserType = userTypeToIntConverter(userUserType);
        boolean isaddSuccess;
        String[] rows = {"userID", "password", "email", "birthday", "firstName", "lastName", "userType"};
        String[] values = {userUserId, userPassword, userEmail, userBirthday, userFirstName, userLastName ,String.valueOf(convertedUserType)};
        DFSQL dfsql = new DFSQL();
        try {
            dfsql.insert("users", values, rows);
            debugLog(dfsql.formattedStatement());
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) ->
            {
            	if (error != null)
	            {
	            	if (error.code == 2)

		            JSONQueryError error1;

	            	JSONQueryError error1 = new JSONQueryError(0, "Internal Error", null);
	            	runnable.run(null, error1);
	            	return;
	            }
            });
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
        isaddSuccess = uploadSuccess == DFDataUploaderReturnStatus.success;
		/*
		if(isaddSuccess){
			return getUser(userName);
		} else {
			return null;
		}*/
        return isaddSuccess;
    }

    private int userTypeToIntConverter(userType userType){
        if(userType == userType.ADMIN){
            return 1;
        } else if (userType == userType.TEACHER){
            return 2;
        } else if(userType == userType.STUDENT){
            return 3;
        } else {
            return 4;
        }
    }

    private userType intToUserTypeConverter(int userTypeInt){
        if(userTypeInt == 1){
            return userType.ADMIN;
        } else if (userTypeInt == 2){
            return userType.TEACHER;
        } else if (userTypeInt == 3){
            return userType.STUDENT;
        } else{
            return userType.TA;
        }
    }

    @Override
    public void uploadStatus(DFDataUploaderReturnStatus success, DFError error) {
        this.uploadSuccess = null;
        if(success == DFDataUploaderReturnStatus.success){
            debugLog("success uploading this");
        } else if (success == DFDataUploaderReturnStatus.failure) {
            debugLog("Failure uploading this");
        }
        else if(success == DFDataUploaderReturnStatus.error){
            debugLog("Error uploading this");
            DFDatabase.print(error.toString());
        } else {
            debugLog("I have no clue!");
        }
        this.uploadSuccess = success;
    }
    public static void main(String[] args){
        UserQuery userQuery = new UserQuery();
        userQuery.getUser("testUserStudent");
    }
}
