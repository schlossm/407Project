package json;

import database.DFDatabase;
import org.junit.*;

public class UserQueryTest
{
	/*
	* This method is run only once before anything else
	*/
	@BeforeClass
	public static void setUpBeforeClass() throws Exception { }

	public static void main(String[] args)
	{
		DFDatabase.defaultDatabase.enableDebug();
		UserQueryTest test = new UserQueryTest();
		test.testGetUser();
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
	public void testGetUser()
	{
		UserQuery userQuery = new UserQuery();

		new InstructorQuery().enterGrade(1, "mschlos", 50, (returnedData, error) ->
		{
			System.out.println("Returned Data: " + returnedData);
			System.out.println("Error: " + error);
		});
	}
}
