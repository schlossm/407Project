package json;

import com.google.gson.JsonObject;
import database.DFDatabaseCallbackDelegate;
import database.DFError;
import database.WebServer.DFDataUploaderReturnStatus;
import objects.QuizAssignment;

/**
 * Created by gauravsrivastava on 4/18/17.
 */
public class AssignmentQuery  {


    public void addQuiz(QuizAssignment quizAssignment, int courseid) {

    }

    public void removeQuiz(int quizid) {

    }

    public void getQuiz(int quizid) {

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
