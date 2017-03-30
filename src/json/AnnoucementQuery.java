package json;

import com.google.gson.JsonObject;
import database.DFDatabaseCallbackDelegate;
import database.DFError;
import database.WebServer.DFDataUploaderReturnStatus;

/**
 * Created by gauravsrivastava on 3/29/17.
 */
public class AnnoucementQuery implements DFDatabaseCallbackDelegate {

    public void addAnnouncement() {

    }

    public void removeAnnouncement() {

    }

    public void getAllAnnouncement() {

    }

    public void getAllAnnouncementInCourse(int courseid) {

    }



    @Override
    public void returnedData(JsonObject jsonObject, DFError error) {

    }

    @Override
    public void uploadStatus(DFDataUploaderReturnStatus success, DFError error) {

    }
}
