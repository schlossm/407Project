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
import ui.util.UIStrings;
import uikit.DFNotificationCenter;

import static database.DFDatabase.debugLog;

/**
 * Created by Naveen Ganessin
 */

public class UserQuery implements DFDatabaseCallbackDelegate {
    private boolean getUserReturn, getUserExistsReturn;
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
            try {
                usernameReceived = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("userID").getAsString();
                userEmail = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("email").getAsString();
                userBirthday = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("birthday").getAsString();
                userFirstName = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("firstName").getAsString();
                userLastName = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("lastName").getAsString();
                userTypeInt = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("userType").getAsInt();
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

    @Override
    public void uploadStatus(@NotNull DFDataUploaderReturnStatus success, @Nullable DFError error) {

    }
    public static void main(String[] args){
        UserQuery userQuery = new UserQuery();
    }
}
