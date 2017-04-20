package database.DFSQL;

/**
 * Defines a `WHERE` statement i.e. "`WHERE` ...X..."
 * <p>
 * - Note: Please use `DFSQL.WhereStatementComparesNullString` as the value of `.clause` if you plan to use `.isNull` or `isNotNull` for the equivalence
 */
public class Where
{
	/**
	 * The conjunction to join this `WHERE` statement to the next one
	 */
	final DFSQLConjunction conjunction;

	/**
	 * The equivalence comparator to compare the attribute and value
	 */
	final DFSQLEquivalence equivalence;

	/**
	 * The attribute and value.
	 * <p>
	 * - Note: Please use `DFSQL.WhereStatementComparesNullString` as the value if you plan to use `.isNull` or `isNotNull` for the equivalence
	 */
	final DFSQLClause clause;

	public Where(DFSQLConjunction conjunction, DFSQLEquivalence equivalence, DFSQLClause clause)
	{

		this.conjunction = conjunction;
		this.equivalence = equivalence;
		this.clause = clause;
	}
}
