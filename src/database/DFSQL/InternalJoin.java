package database.DFSQL;

class InternalJoin
{
	DFSQLJoin joinType;
	String table;
	DFSQLClause clause;
	
	InternalJoin(DFSQLJoin type, String table, DFSQLClause clause)
	{
		this.joinType = type;
		this.table = table;
		this.clause = clause;
	}
}
