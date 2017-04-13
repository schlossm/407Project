package objects;

/**
 * Created by Naveen Ganessin on 3/29/2017.
 */
public class CourseGrade {
    private String userId;
    private int courseId;
    private int avgGrade;
    private int sumGrade;

    public CourseGrade(String userId, int courseId, int avgGrade, int sumGrade){
        this.avgGrade = avgGrade;
        this.courseId = courseId;
        this.sumGrade = sumGrade;
        this.userId = userId;
    }

    public int getAvgGrade() {
        return avgGrade;
    }

    public int getSumGrade(){
        return sumGrade;
    }

}
