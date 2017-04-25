package json;

/**
 * Created by Naveen Ganessin on 4/21/2017.
 */
import database.DFDatabase;
import org.junit.*;

import static database.DFDatabase.queue;

public class CourseQueryTest
{
    /*
    * This method is run only once before anything else
    */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception { }

    public static void main(String[] args)
    {
        DFDatabase.defaultDatabase.enableDebug();
        CourseQueryTest test = new CourseQueryTest();
        test.testListOfStudentGrades();

        while (true)
        {
            try
            {
                if (queue.size() != 0) { queue.take().run(); }
            }
            catch (InterruptedException e)
            {
                System.err.print("The application queue has encountered an error");
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    /*
     * This method runs only once after everything is done
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception { }

    /*
     * This method runs before each test
     */
    @Before
    public void setUp() throws Exception
    { }

    /*
     * This method runs after each test
     */
    @After
    public void tearDown() throws Exception
    { }

    /*
     * This is a method to test a method of the
     * target class
     */
    @Test
    public void testGetAllGrades()
    {
        CourseQuery courseQuery = new CourseQuery();
        courseQuery.getAllInstructorsInCourse(123456, (returnedData, error) ->
        {
            System.out.println("Returned Data: " + returnedData);
            System.out.println("Error: " + error);
        });
    }

    @Test
    public void testCount(){
        AdminQuery adminQuery = new AdminQuery();
        adminQuery.getAllStudentsCount((returnedData, error) ->
        {
            System.out.println("Returned Data: " + returnedData);
            System.out.println("Error: " + error);
        });
    }

    @Test
    public  void testListOfStudentGrades(){
        StudentQuery studentQuery = new StudentQuery();
        studentQuery.getAllGradeInCourse("mshlos", 123456, (returnedData, error) ->
        {
            System.out.println("Returned Data: " + returnedData);
            System.out.println("Error: " + error);
        });
    }
}

