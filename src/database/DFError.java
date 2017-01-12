package database;

import java.util.Map;

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
	final public int code;
	/**
	 * A human readable description of the error
	 */
	final public String description;
	/**
	 * Optional user information.  Strings only
	 */
	final public Map<String, String> userInfo;
	
	public DFError(int code, String description, Map<String, String> userInfo)
	{
		this.code = code;
		this.description = description;
		this.userInfo = userInfo;
	}

	@Override
	public String toString()
	{
		return "Error: " + String.valueOf(code) + ".\nDescription: " + description + "\nUser Info: " + userInfo.toString();
	}
}
