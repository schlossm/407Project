package json;

import database.DFDatabase;
import org.junit.*;

import java.io.File;

/**
 * Created by gauravsrivastava on 4/26/17.
 */
public class DocumentsQueryTest {
    /*
	* This method is run only once before anything else
	*/
    @BeforeClass
    public static void setUpBeforeClass() throws Exception { }

    public static void main(String[] args)
    {
        DFDatabase.defaultDatabase.enableDebug();
        DocumentsQueryTest test = new DocumentsQueryTest();
        test.testAddDocument();
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
    public void testAddDocument() {
        DocumentsQuery documentsQuery = new DocumentsQuery();
        File file = new File("/Users/michaelschloss/Documents/Git Repositories/407Project/InstructorQuery.java");
        System.out.println(file.getPath());
        documentsQuery.addDocument(file, "instructorquery", "query me this", "mschlos", "44", "0", 1, ((returnedData, error) ->
        {
            System.out.println(returnedData);
        }));
    }
}
