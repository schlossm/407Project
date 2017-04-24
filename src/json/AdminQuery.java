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
public class AdminQuery {
    private JsonObject jsonObject;
    public void getAllCourses(QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String[] selectRows = {"COUNT(coursename)"};
        String table = "courses";
        try {
            dfsql.select(selectRows, false, null, null).from(table).limit(1);
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) -> {
                JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
                if (error != null) {
                    //Process the error and return appropriate new error to UI.
                    runnable.run(null, error1);
                    return;
                }
                JsonObject jsonObject;
                if (response instanceof JsonObject) {
                    jsonObject = (JsonObject) response;
                } else {
                    runnable.run(null, error1);
                    return;
                }
                int allCourseCount= 0;
                String courseTitle = null, courseName = null, description = null, roomNo = null, meetingTime = null, startDate = null, endDate = null;
                int courseId = 0, capacity = 0;
                Course course;
                try {
                    allCourseCount = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("90UP").getAsInt();
                } catch (NullPointerException e2) {
                    runnable.run(null, error1);
                }
                runnable.run(allCourseCount, null);
            });
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }
}
