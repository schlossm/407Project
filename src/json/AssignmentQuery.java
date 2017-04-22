package json;

import com.google.gson.JsonObject;
import database.DFDatabase;
import database.DFDatabaseCallbackDelegate;
import database.DFDatabaseCallbackRunnable;
import database.DFError;
import database.DFSQL.*;
import database.WebServer.DFDataUploaderReturnStatus;
import json.util.JSONQueryError;
import objects.*;

import java.util.ArrayList;

/**
 * Created by gauravsrivastava on 4/18/17.
 */
public class AssignmentQuery  {

    private JsonObject jsonObject;

    public void addQuiz(QuizAssignment quizAssignment, int courseid, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String[] rowsfortable1 = {"name", "courseid", "maxpoints", "type", "deadline"};
        String table1 = "assignment";
        String[] valuesfortable1 = {quizAssignment.getTitle(), quizAssignment.getCourseID() + "", quizAssignment.getMaxPoints() + "", "quiz", quizAssignment.getDueDate()};
        final int[] lastAssignmentId = {-1};
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
                                JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
                                if (error != null) {
                                    //Process the error and return appropriate new error to UI.
                                    runnable.run(null, error1);
                                    return;
                                }
                                if (response instanceof JsonObject) {
                                    jsonObject = (JsonObject) response;
                                    lastAssignmentId[0] = jsonObject.get("Data").getAsJsonArray().get(0).getAsJsonObject().get("LAST_INSERT_ID()").getAsInt();
                                } else {
                                    runnable.run(null, error1);
                                    return;
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
        ArrayList<Question> questionsToAdd = quizAssignment.getQuestions();
        String[] rowsfortable2 = {"assignmentid", "question", "correct", "choices", "points"};
        String table2 = "question";
        for (Question q: questionsToAdd) {
            String choices = q.getChoices().get(0);
            for(int i = 1; i < q.getChoices().size(); i++) {
                choices = choices + "\t" + q.getChoices().get(i);
            }
            String[] valuesfortable2 = {lastAssignmentId[0] + "", q.getQuestion(), q.getCorrectChoice(), choices, q.getPoints() + ""};
            try {
                dfsql.insert(table2, valuesfortable2, rowsfortable2);
                DFDatabase.defaultDatabase.execute(dfsql, (response, error) -> {
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
            } catch (DFSQLError dfsqlError) {
                dfsqlError.printStackTrace();
            }
        }
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


    /**
     * id, name, duedate
     * @param courseid
     */
    public void getAllQuizzesInCourse(int courseid, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String[] selectedRows = {"assignmentId", "name", "type", "courseid", "deadline"};
        String[] attrs = {"courseid", "type"};
        String[] values = {courseid + "", "quiz"};
        String table = "assignment";
        try {
            dfsql.select(selectedRows, false, null, null).from(table)
                    .where(DFSQLConjunction.and, DFSQLEquivalence.equals, attrs, values);
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
                ArrayList<Assignment> allQuizAssignmentsInCourse = new ArrayList<Assignment>();
                int assignmentId, crn;
                String name, type, deadline;
                Assignment assignment = null;
                JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
                try {
                    for (int i = 0; i < jsonObject.get("Data").getAsJsonArray().size(); ++i) {
                        name = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString();
                        type = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("type").getAsString();
                        deadline = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("deadline").getAsString();
                        assignmentId = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("assignmentId").getAsInt();
                        crn = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("courseid").getAsInt();

                        assignment.setTitle(name);
                        assignment.setAssignmentID(assignmentId);
                        assignment.setCourseID(crn);
                        assignment.setType(type);
                        assignment.setDueDate(deadline);
                        allQuizAssignmentsInCourse.add(assignment);
                    }
                }catch (NullPointerException e2){
                    e2.printStackTrace();
                    runnable.run(null, error1);
                }
                runnable.run(allQuizAssignmentsInCourse, null);
            });
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    /**
     * Returns all info of an assignment given the assignment id
     * @param assignmentid
     */
    public void getAssignmentInfo(int assignmentid, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String[] selectedRows = {"assignmentid, name, courseid", "maxpoints", "type", "deadline"};
        String table = "assignment";
        try {
            dfsql.select(selectedRows, false, null, null).from(table).where(DFSQLEquivalence.equals, "assignmentid", "" + assignmentid);
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
                ArrayList<Assignment> allAssignmentsInCourse = new ArrayList<Assignment>();
                int assignmentId, crn;
                String name, type, deadline;
                Assignment assignment = null;
                JSONQueryError error1 = new JSONQueryError(0, "Some Error", null/*User info if needed*/);
                try {
                    for (int i = 0; i < jsonObject.get("Data").getAsJsonArray().size(); ++i) {
                        name = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString();
                        type = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("type").getAsString();
                        deadline = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("deadline").getAsString();
                        assignmentId = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("assignmentId").getAsInt();
                        crn = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("courseid").getAsInt();

                        assignment.setTitle(name);
                        assignment.setAssignmentID(assignmentId);
                        assignment.setCourseID(crn);
                        assignment.setType(type);
                        assignment.setDueDate(deadline);
                    }
                }catch (NullPointerException e2){
                    e2.printStackTrace();
                    runnable.run(null, error1);
                }
                runnable.run(assignment, null);
            });
        } catch (DFSQLError dfsqlError) {
            dfsqlError.printStackTrace();
        }
    }

    public void addAssignment(int courseid, String name, String deadline, double maxPoints, String type, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String[] rows = {"name", "courseid", "maxpoints", "type", "deadline"};
        String[] values = {name, courseid + "","" + maxPoints, type, deadline};
        String table = "assignment";
        try {
            dfsql.insert(table, values, rows);
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
        } catch (DFSQLError e1)
        {
            e1.printStackTrace();
        }
    }

    /**
     * remove an assignment from a course
     * @param assignmentid remove assignment given assignment
     */
    public void removeAssignment(int assignmentid, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String table = "assignment";
        try {
            dfsql.delete(table, new Where(DFSQLConjunction.none, DFSQLEquivalence.equals, new DFSQLClause("assignmentid", "" + assignmentid)));
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
        } catch (DFSQLError e1)
        {
            e1.printStackTrace();
        }
    }

    /**
     * Get all the assignments of a course
     * Return a list of all ids of an assignment
     * @param courseid courseid such as 1111 of the course
     */
    public void getAllAssignmentsInCourse(int courseid, QueryCallbackRunnable runnable) {
        DFSQL dfsql = new DFSQL();
        String selectedRows[] = {"assignmentid", "name"};
        String table = "assignment";
        try {
            dfsql.select(selectedRows, false, null, null).from(table).where(DFSQLEquivalence.equals, "courseid", "" + courseid);
            DFDatabase.defaultDatabase.execute(dfsql, (response, error) -> {
                System.out.println(response);
                System.out.println(error);
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
                    return;
                }
                ArrayList<Assignment> allAssignmentsInCourse = new ArrayList<Assignment>();
                String name = null;
                int assignmentId = 0;
                Assignment assignment;
                try {
                    for (int i = 0; i < jsonObject.get("Data").getAsJsonArray().size(); ++i) {
                        assignmentId = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("assignmentid").getAsInt();
                        name = jsonObject.get("Data").getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString();


                        assignment = new Assignment();

                        assignment.setAssignmentID(assignmentId);
                        assignment.setTitle(name);
                        allAssignmentsInCourse.add(assignment);
                    }
                }catch (NullPointerException e2){runnable.run(null, error1);}
                runnable.run(allAssignmentsInCourse, null);
            });
        } catch (DFSQLError e1) {
            e1.printStackTrace();
        }
    }


}
