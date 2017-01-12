package database.DFSQL;

/**
 * The various clauses to perform Joins and Where Statements
 */

public enum DFSQLConjunctionClause
{
	/**
	 * Used for WHERE
	 */
	and, or, none,

	/**
	 * Used for WHERE
	 */
	equals, notEquals, greaterThan, lessThan, greaterThanOrEqualTo, lessThanOrEqualTo,

	/**
	 * Used for JOIN and WHERE
	 */
	leftOuter, rightOuter, fullOuter, cross, inner, natural
}