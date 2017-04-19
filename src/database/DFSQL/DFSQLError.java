package database.DFSQL;

/**
 * Possible errors `DFSQL` could throw back at you
 */
@SuppressWarnings({"serial", "ThrowableInstanceNeverThrown"})
public class DFSQLError extends Exception
{
	/**
	 * This error is thrown when attribute == value
	 */
	static final DFSQLError whereConditionsCannotBeEqual = new DFSQLError("Where Conditions Cannot Be Equal");

	/**
	 * Specifies that `*` was was given as a parameter
	 */
	static final DFSQLError cannotUseWildcardSpecifier = new DFSQLError("Cannot Use Wildcard Specifier");

	/**
	 * Specifies that the method was delivered "" or null
	 */
	static final DFSQLError cannotUseEmptyValue = new DFSQLError("Cannot Use Empty Value");

	/**
	 * Specifies that the row exceeded 64 characters length
	 */
	static final DFSQLError attributeLengthTooLong = new DFSQLError("Attribute Length Too Long");

	/**
	 * Specifies that the conditions given do not equal each other in size
	 */
	static final DFSQLError conditionsMustBeEqual = new DFSQLError("Conditions Must Be Equal");

	/**
	 * Specifies that the condition has already been formed
	 */
	static final DFSQLError conditionAlreadyExists = new DFSQLError("Condition Already Exists");

	/**
	 * Specifies that the table exceeded 64 characters length
	 */
	static final DFSQLError unexpectedValueFound = new DFSQLError("Unexpected Value Found");

	private DFSQLError(String message)
	{
		super(message);
	}
}
