package database.DFSQL;

/**
 A helper struct to combine an attribute-value pair
 */
public class DFSQLClause
{
	final String attribute;
	final String value;

	public DFSQLClause(String attribute, String value)
	{
		this.attribute  = attribute;
		this.value      = value;
	}
}
