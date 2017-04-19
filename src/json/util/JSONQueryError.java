package json.util;

import java.util.Map;

/* ERROR CODES
*
* 0: Generic Error
* 1: Duplicate ID
* 2: Duplicate Unique
* 3: No Data Returned
* */

/**
 * Created by michaelschloss on 4/18/17.
 */
public class JSONQueryError
{
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

	public JSONQueryError(int code, String description, Map<String, String> userInfo)
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
