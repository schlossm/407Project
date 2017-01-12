package database.DFSQL;

/**
 * A class defining a JOIN single clause
 */
public class JoinParam
{
	final String table;
	final String leftHandSide;
	final String rightHandSide;
	
	@SuppressWarnings("unused")
	public JoinParam(String table, String leftHandSide, String rightHandSide)
	{
		this.table = table;
		this.leftHandSide = leftHandSide;
		this.rightHandSide = rightHandSide;
	}
}
