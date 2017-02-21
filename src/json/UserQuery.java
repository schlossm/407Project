package json;

import com.google.gson.JsonObject;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import database.DFDatabase;
import database.DFDatabaseCallbackDelegate;
import database.DFError;
import database.DFSQL.DFSQL;
import database.DFSQL.DFSQLError;
import database.WebServer.DFDataUploaderReturnStatus;
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


    public void getUser(String username) {
        DFSQL dfsql = new DFSQL();
        String[] selectedRows = {"userID", "firstName", "lastName", "email", "birthday", "userType"};
        getUserReturn = true;
        try {
            dfsql.select(selectedRows).from("User").whereEquals("userID", username);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

    public void verifyUserLogin(String username) {
        DFSQL dfsql = new DFSQL();
        String[] selectedRows = {"password"};
        verifyUserLoginReturn = true;
        try {
            dfsql.select(selectedRows).from("User").whereEquals("userID", username);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void returnedData(@Nullable JsonObject jsonObject, @Nullable DFError error) {
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

                User user = new User(usernameReceived);
                user.setEmail(userEmail);
                user.setFirstName(userFirstName);
                user.setLastName(userLastName);
                user.setBirthday(userBirthday);

                DFNotificationCenter.defaultCenter.post(UIStrings.returned, user);
            }catch (NullPointerException e2){
                DFNotificationCenter.defaultCenter.post(UIStrings.returned, null);
            }
        } else if (verifyUserLoginReturn) {
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

        getUserReturn = false;
        getUserExistsReturn = false;
        bufferString = null;
    }

    public boolean addNewUser(String userUserId, String userPassword,  String userEmail, String userBirthday, String userFirstName, String userLastName, userType userUserType){
        int convertedUserType = userTypeToIntConverter(userUserType);
        boolean isaddSuccess;
        String[] rows = {"userID", "password", "email", "birthday", "firstName", "lastName", "userType"};
        String[] values = {userUserId, userPassword, userEmail, userBirthday, userFirstName, userLastName ,String.valueOf(convertedUserType)};
        DFSQL dfsql = new DFSQL();
        try {
            dfsql.insert("User", values, rows);
            debugLog(dfsql.formattedSQLStatement());
            DFDatabase.defaultDatabase.execute(dfsql, this);
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
        } else {
            return 3;
        }
    }

    private userType intToUserTypeConverter(int userTypeInt){
        if(userTypeInt == 1){
            return userType.ADMIN;
        } else if (userTypeInt == 2){
            return userType.TEACHER;
        } else {
            return userType.STUDENT;
        }
    }

    @Override
    public void uploadStatus(@NotNull DFDataUploaderReturnStatus success, @Nullable DFError error) {
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
    }
}
