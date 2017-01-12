package database.DFSQL;

/**
 * The struct that defines a WHERE statement clause
 */
@SuppressWarnings("WeakerAccess") public class WhereStruct
{
	final DFSQLConjunctionClause conjunction;
	final DFSQLConjunctionClause joiner;
	final DFSQLClauseStruct clause;
	
	public WhereStruct(DFSQLConjunctionClause conjunction, DFSQLConjunctionClause joiner, DFSQLClauseStruct clause) throws DFSQLError
	{
		if (conjunction != DFSQLConjunctionClause.and && conjunction != DFSQLConjunctionClause.none && conjunction != DFSQLConjunctionClause.or) throw DFSQLError.DFSQLConjunctionClauseDoesntMatch;
		if (joiner != DFSQLConjunctionClause.equals && joiner != DFSQLConjunctionClause.notEquals && joiner != DFSQLConjunctionClause.greaterThan && joiner != DFSQLConjunctionClause.greaterThanOrEqualTo && joiner != DFSQLConjunctionClause.lessThan && joiner != DFSQLConjunctionClause.lessThanOrEqualTo) throw DFSQLError.DFSQLConjunctionClauseDoesntMatch;

		this.conjunction    = conjunction;
		this.joiner         = joiner;
		this.clause         = clause;
	}
}
