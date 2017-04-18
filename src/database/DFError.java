package database;

import java.util.Map;

/*
 * ERROR CODES:
 *
 * -3: Empty SQL Statement
 * 0 : Unknown Java Error
 * 1 : No data
 * 2 : Duplicate Primary Key
 * 3 : Duplicate Unique Key
 * 4 : Table doesn't exist
 * */

/**
 * The error class for DFDatabase.  More powerful than implementing the Exception class
 */
public class DFError
{
	public static final String kSQLStatement = "SQL Statement";
	public static final String kURL = "URL";
	public static final String kMethodName = "Method Name";
	public static final String kExpandedDescription = "Expanded Description";

	/**
	 * The error code
	 */
	public final int code;
	/**
	 * A human readable description of the error
	 */
	private final String description;
	/**
	 * Optional user information.  Strings only
	 */
	private final Map<String, String> userInfo;

	public DFError(int code, String description, Map<String, String> userInfo)
	{
		this.code = code;
		this.description = description;
		this.userInfo = userInfo;
	}

	@Override
	public String toString()
	{
		return "Error Code: " + String.valueOf(code) + ".\nError Description: " + description + "\nError Info: " + userInfo.toString();
	}
}
