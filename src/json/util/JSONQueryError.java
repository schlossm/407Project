package json.util;

import database.DFError;

import java.util.Map;

/* ERROR CODES
*
* 0: Generic Error
* 1: Duplicate ID
* 2: Duplicate Unique
* 3: No Data Returned
* */

public class JSONQueryError extends DFError
{
	public JSONQueryError(int code, String description, Map<String, String> userInfo)
	{
		super(code, description, userInfo);
	}
}
