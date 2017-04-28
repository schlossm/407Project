package json;

/**
 * Created by Naveen Ganessin on 4/21/2017.
 */

import database.DFDatabase;
import org.junit.*;

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
	public void testCount()
	{
		AdminQuery adminQuery = new AdminQuery();
		adminQuery.getAllStudentsCount((returnedData, error) ->
		                               {
			                               System.out.println("Returned Data: " + returnedData);
			                               System.out.println("Error: " + error);
                                       });
	}

	@Test
	public void testListOfStudentGrades()
	{
		InstructorQuery instructorQuery = new InstructorQuery();
		instructorQuery.getGradeOfAllStudentsInCourse(123456, (returnedData, error) ->
		{
			System.out.println("Returned Data: " + returnedData);
			System.out.println("Error: " + error);
			System.exit(0);
		});
	}
}

