package database.DFSQL;

/**
 * The Exception class for DFSQL
 */
@SuppressWarnings({"serial", "ThrowableInstanceNeverThrown"})
public class DFSQLError extends Exception
{
	/**
	 * Specifies that the condition has already been formed
	 */
	static final DFSQLError conditionAlreadyExists			    = new DFSQLError("Condition Already Exists");

	/**
	 * Specifies that `*` was was given as a parameter
	 */
	static final DFSQLError cannotUseAllRowsSQLSpecifier 	    = new DFSQLError("Cannot Use All Rows SQL Specifier");

	/**
	 * Specifies that the method was delivered "" or null
	 */
	static final DFSQLError cannotUseEmptyValue 			    = new DFSQLError("Cannot Use Empty Value");

	/**
	 * Specifies that the row exceeded 64 characters length
	 */
	static final DFSQLError rowLengthTooLong 					= new DFSQLError("Row Length Too Long");

	/**
	 * Specifies that the table exceeded 64 characters length
	 */
	static final DFSQLError tableLengthTooLong 				    = new DFSQLError("Table Length Too Long");

	/**
	 * Specifies that the conditions given do not equal each other in size
	 */
	static final DFSQLError conditionsMustBeEqual 			    = new DFSQLError("Conditions Must Be Equal");

	/**
	 * Specifies that the DFSQLConjunctionClause enum given doesn't match expected
	 */
	static final DFSQLError DFSQLConjunctionClauseDoesntMatch   = new DFSQLError("DFSQL Conjunction Clause Doesn't Match What Is Expected");
	
	private DFSQLError(String message)
	{
        super(message);
    }
}
