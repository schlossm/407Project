package database.DFSQL;

/**
 * A class for holding a generic clause
 */
public class DFSQLClauseStruct
{
	final String leftHandSide;
	final String rightHandSide;
	
	public DFSQLClauseStruct(String left, String right)
	{
		leftHandSide = left;
		rightHandSide = right;
	}
}
