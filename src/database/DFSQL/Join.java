package database.DFSQL;

/**
 * Defines a `JOIN` statement
 */
public class Join
{
	final String table;
	final String tableOneAttribute;
	final String tableTwoAttribute;

	@SuppressWarnings("unused")
	public Join(String table, String tableOneAttribute, String tableTwoAttribute)
	{
		this.table = table;
		this.tableOneAttribute = tableOneAttribute;
		this.tableTwoAttribute = tableTwoAttribute;
	}
}

