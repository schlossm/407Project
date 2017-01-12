package database.DFSQL;

class JoinStruct 
{
	final DFSQLConjunctionClause joinType;
	final String table;
	final DFSQLClauseStruct clause;
	
	JoinStruct(DFSQLConjunctionClause type, String table, DFSQLClauseStruct clause)
	{
		this.joinType = type;
		this.table = table;
		this.clause = clause;
	}
}
