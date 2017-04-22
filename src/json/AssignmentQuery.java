package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFDatabaseCallbackDelegate;
import database.DFDatabaseCallbackRunnable;
import database.DFError;
import database.DFSQL.DFSQL;
import database.DFSQL.DFSQLError;
import database.WebServer.DFDataUploaderReturnStatus;
import json.util.JSONQueryError;
import objects.Question;
import objects.QuizAssignment;

import java.util.ArrayList;

/**
 * Created by gauravsrivastava on 4/18/17.
 */
public class AssignmentQuery  {


    public void addQuiz(QuizAssignment quizAssignment, int courseid, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String[] rowsfortable2 = {"assignmentid", "question", "correct", "choices", "points"};
        String[] rowsfortable1 = {"name", "courseid", "maxpoints", "type", "deadline"};
        String table1 = "assignment";
        String[] valuesfortable1 = {quizAssignment.getTitle(), quizAssignment.getCourseID() + "", quizAssignment.getMaxPoints() + "", "quiz", quizAssignment.getDueDate()};
        try {
            dfsql.insert(table1, valuesfortable1, rowsfortable1);
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
                    DFDataUploaderReturnStatus returnStatus = (DFDataUploaderReturnStatus)response;
                    if (returnStatus == DFDataUploaderReturnStatus.success)
                    {
                        try {
                            dfsql.select("LAST_INSERT_ID()", false, null, null);
                            DFDatabase.defaultDatabase.execute(dfsql, (response1, error12) -> {
                                if(error != null) {

                                    return;
                                }
                                if (response instanceof JsonObject) {

                                }

                            });
                        } catch (DFSQLError e1)
                        {
                            e1.printStackTrace();
                        }
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
        } catch (DFSQLError e1)
        {
            e1.printStackTrace();
        }


        int lastAssignmentId = -1;


        String[]valuesfortable2 = {lastAssignmentId + "", };
        String table2 = "question";
    }

    public void removeQuiz(int quizid) {

    }

    public void getQuiz(int assignmentId, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String[] selectRows = {"assignmentId", "point", "question", "choices", "correctAnswer"};
        String table = "courses";

        try {
            dfsql.select(selectRows, false, null, null);
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) -> {
                System.out.println(response);
                System.out.println(error);
                if (error != null) {
                    //Process the error and return appropriate new error to UI.
                    JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
                    runnable.run(null, error1);
                    return;
                }
                JsonObject jsonObject;
                if (response instanceof JsonObject) {
                    jsonObject = (JsonObject) response;
                } else {
                    return;
                }
                ArrayList<Question> quizQuestions = new ArrayList<Question>();
                int returnedAssignmentId;
                QuizAssignment quizAssignment = null;
                double points = 0.0;
                String question, correctAnswer, choices;
                ArrayList<String> choicesList;
                Question questionObject;
                JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
                try {
                    for (int i = 0; i < jsonObject.get("Data").getAsJsonArray().size(); ++i) {
                        returnedAssignmentId = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get(selectRows[0]).getAsInt();
                        points = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get(selectRows[1]).getAsDouble();
                        question = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get(selectRows[2]).getAsString();
                        choices = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get(selectRows[3]).getAsString();
                        correctAnswer = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get(selectRows[4]).getAsString();
                        choicesList = parseChoiceList(choices);
                        questionObject = new Question(question, choicesList, correctAnswer, points);

                        quizQuestions.add(questionObject);
                    }
                    quizAssignment = new QuizAssignment(assignmentId, quizQuestions);
                }catch (NullPointerException e2){
                    runnable.run(null, error1);
                }
                runnable.run(quizAssignment, null);
            });
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    private ArrayList<String>parseChoiceList(String choices){
        ArrayList<String> choiceList = new ArrayList<String>();
        for (String retval: choices.split("\t")) {
            System.out.println(retval);
            choiceList.add(retval);
        }
        return choiceList;
    }

    public void modifyquiz(int quizid) {

    }

    public void addGradeForQuiz(int quizid, double grade) {

    }

    /**
     * returns the
     * @param quizid
     * @param userid
     * @param studentQuizAnswer
     */
    public void submitQuizByStudent(int quizid, String userid, QuizAssignment studentQuizAnswer) {

    }


    /**
     * id, name, duedate
     * @param courseid
     */
    public void getAllQuizes(int courseid) {

    }


}
