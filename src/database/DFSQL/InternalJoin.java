package database.DFSQL;

class InternalJoin
{
	final DFSQLJoin joinType;
	final String table;
	final DFSQLClause clause;

	InternalJoin(DFSQLJoin type, String table, DFSQLClause clause)
	{
		this.joinType = type;
		this.table = table;
		this.clause = clause;
	}
}
