package database.DFSQL;

/**
 * The possible conjunctions for `WHERE` statements
 * <p>
 * - Note: `.none` does not insert " NONE" into the SQL Statement, it is used as a placeholder
 */
@SuppressWarnings("unused")
public enum DFSQLConjunction
{
	and(" AND"), or(" OR"), none(""), andNot(" AND NOT"), orNot(" OR NOT");

	private final String text;

	DFSQLConjunction(String text)
	{
		this.text = text;
	}

	@Override
	public String toString() { return text; }
}

