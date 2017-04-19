package json;

import objects.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by Naveen Ganessin on 2/21/2017.
 */
public class UserQueryTest
{
    private static UserQuery userQuery;
    private static String name;
    private static User user;
    public static boolean loginSuccess;
    /*
         * This method is run only once before anything else
         */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception { userQuery = new UserQuery(); }

    public static void main(String[] args)
    {
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
    @org.junit.Before
    public void setUp() throws Exception
    {
        name = "admin";
    }

    /*
     * This method runs after each test
     */
    @org.junit.After
    public void tearDown() throws Exception
    {
        user = null;
        loginSuccess = false;
    }

    /*
     * This is a method to test a method of the
     * target class
     */
    @Test
    public void testGetUser()
    {
        user = null;
        userQuery.getUser(name, (returnedData, error) ->
        {
            System.out.println(returnedData);
            System.out.println(error);
        });
    }
}
