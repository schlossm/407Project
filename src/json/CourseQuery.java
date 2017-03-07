package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFDatabaseCallbackDelegate;
import database.DFError;
import database.DFSQL.DFSQL;
import database.DFSQL.DFSQLEquivalence;
import database.DFSQL.DFSQLError;
import database.WebServer.DFDataUploaderReturnStatus;
import objects.User;
import objects.userType;
import ui.util.UIStrings;
import uikit.DFNotificationCenter;

import static database.DFDatabase.debugLog;

/**
 * Created by Naveen Ganessin on 3/6/2017.
 */
public class CourseQuery implements DFDatabaseCallbackDelegate{
    private boolean getUserReturn, getUserExistsReturn;
    private DFDataUploaderReturnStatus uploadSuccess;
    private JsonObject jsonObject;
    private String bufferString;
    private boolean verifyUserLoginReturn;

    public void getCourse(String username) {
        DFSQL dfsql = new DFSQL();
        getUserReturn = true;
        String[] selectedRows = {"userID", "firstName", "lastName", "email", "birthday", "userType"};
        try {
            dfsql.select(selectedRows, false, null, null).from("users").where(DFSQLEquivalence.equals, "userID", username);
            DFDatabase.defaultDatabase.execute(dfsql, this);
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
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

    @Override
    public void uploadStatus(DFDataUploaderReturnStatus success, DFError error) {

    }
}
