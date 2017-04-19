package json;

import database.DFDatabase;
import json.util.JSONQueryError;
import objects.User;
import objects.userType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static database.DFDatabase.queue;

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
        DFDatabase.defaultDatabase.enableDebug();
        UserQueryTest test = new UserQueryTest();
        test.testGetUser();

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
        userQuery = new UserQuery();
        userQuery.addNewUser("blahuserid", "testpass", "aslghjalskgjsadg@zlsnfalksnf.com", "2017-01-01", "Michael", "Schloss", userType.STUDENT, new QueryCallbackRunnable() {
            @Override
            public void run(Object returnedData, JSONQueryError error)
            {
                System.out.println("Returned Data: " + returnedData);
                System.out.println("Error: " + error);
            }
        });
    }
}
