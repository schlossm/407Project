package json;

import database.DFDatabase;
import objects.User;
import objects.userType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ui.util.UIStrings;
import uikit.DFNotificationCenter;
import uikit.DFNotificationCenterDelegate;

import static org.junit.Assert.assertTrue;

/**
 * Created by Naveen Ganessin on 2/21/2017.
 */
public class UserQueryTest implements DFNotificationCenterDelegate {
    private static UserQuery userQuery;
    private static String name, invalidName;
    private static User user;
    public static boolean loginSuccess;
    /*
         * This method is run only once before anything else
         */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        userQuery = new UserQuery();
    }

    /*
     * This method runs only once after everything is done
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /*
     * This method runs before each test
     */
    @org.junit.Before
    public void setUp() throws Exception {
        name = "testuserStudent";
        invalidName = "testUsermessedUp";
    }

    /*
     * This method runs after each test
     */
    @org.junit.After
    public void tearDown() throws Exception {
        user = null;
        loginSuccess = false;
    }

    /*
     * This is a method to test a method of the
     * target class
     */
    @Test
    public void testGetUser() {
        user = null;
        DFNotificationCenter.defaultCenter.register(this, UIStrings.returned);
        userQuery.getUser(name);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(user.getUserID());
        assertTrue(user.getUserType() == userType.STUDENT);
        assertTrue(user.getUserID().equals(name));
    }

    @Test
    public void testVerifyLoginValidUserValidPassword(){
        DFNotificationCenter.defaultCenter.register(this, UIStrings.success);
        DFNotificationCenter.defaultCenter.register(this, UIStrings.failure);

        String encryptedPassword = "blahblah";

        System.out.println(encryptedPassword);
        userQuery.verifyUserLogin(name, encryptedPassword);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("loginSuccess : "+ loginSuccess);
        assertTrue(loginSuccess);
    }
    @Override
    public void performActionFor(String notificationName, Object userData) {
        if(notificationName.equals(UIStrings.returned)){
            if(userData == null){
                user = null;
            } else{
                user = (User)userData;
            }
            System.out.println("getUser activated in performactionfor");
        } else if (notificationName.equals(UIStrings.success)) {
            System.out.println("verifylogin returned success in performactionfor");
            loginSuccess = true;
        } else if (notificationName.equals(UIStrings.failure)) {
            loginSuccess = false;
            System.out.println("verifylogin returned success in performactionfor");
        }
    }
}
