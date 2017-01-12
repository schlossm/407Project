package database.DFSQL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

/**
 * The main SQL class.  DFDatabase uses a custom built SQL wrapper to add a layer of security and overload safety
 */
@SuppressWarnings("unused") public class DFSQL
{	
    private String[] selectRows;
    private String[] fromTables;
    private ArrayList<JoinStruct> joinStatements;
    private ArrayList<WhereStruct> whereStatements;
    
    private String[] insertRows;
    private String[] insertData;
    
    private DFSQLClauseStruct[] updateStatements;
    private final ArrayList<DFSQL> appendedDFSQL;
    
    public DFSQL()
    {
        appendedDFSQL = new ArrayList<>();
    }

    /**
     * Appends an already built DFSQL object to the callee
     * @param object a DFSQL object.  This object is immutable after this function is called
     * @return self
     */
    public DFSQL append(DFSQL object)
    {
    	appendedDFSQL.add(object);
        
        return this;
    }

    /**
     * @return A human readable formatted SQL statement
     */
    public final String formattedSQLStatement()
    {
    	String returnString;
    	if (updateStatements != null)	//This will be an UPDATE SET
    	{
    		if (fromTables == null)		{ return ""; }
    		if (fromTables.length != 1)	{ return ""; }
    		
    		returnString = "UPDATE " + fromTables[0] + " SET ";
    		
    		for (DFSQLClauseStruct statement : updateStatements)
    		{
    			String left = statement.leftHandSide;
    			String right = statement.rightHandSide;
    			
    			if (right.contains(" ") || hasCharacter(right))
    			{
    				right = "'" + right + "'";
    			}
    			
    			returnString += left + "=" + right + ", ";
    		}
    		
    		returnString = returnString.substring(0, returnString.length() - 2);
    		
    		if (whereStatements != null)
        	{
        		returnString += " WHERE";
        		for (WhereStruct whereStatement : whereStatements)
        		{
        			String left = whereStatement.clause.leftHandSide;
        			String right = whereStatement.clause.rightHandSide;
        			
        			if (right.contains(" ") || hasCharacter(right))
        			{
        				right = "'" + right + "'";
        			}
        			String joiner = "";
    				if (whereStatement.joiner == DFSQLConjunctionClause.equals)
    				{
    					joiner = "=";
    				}
    				else if (whereStatement.joiner == DFSQLConjunctionClause.notEquals)
    				{
    					joiner = "!=";
    				}
    				else if (whereStatement.joiner == DFSQLConjunctionClause.greaterThan)
    				{
    					joiner = ">";
    				}
    				else if (whereStatement.joiner == DFSQLConjunctionClause.greaterThanOrEqualTo)
    				{
    					joiner = ">=";
    				}
    				else if (whereStatement.joiner == DFSQLConjunctionClause.lessThan)
    				{
    					joiner = "<";
    				}
    				else if (whereStatement.joiner == DFSQLConjunctionClause.lessThanOrEqualTo)
    				{
    					joiner = "<=";
    				}
    				
        			if (whereStatement.conjunction == DFSQLConjunctionClause.none)
        			{
        				returnString += " " + left + joiner + right;
        			}
        			else
        			{
        				String conjunction = "";
        				if (whereStatement.conjunction == DFSQLConjunctionClause.and)
        				{
        					conjunction = " AND";
        				}
        				else if (whereStatement.conjunction == DFSQLConjunctionClause.or)
        				{
        					conjunction = " OR";
        				}
        				returnString += " " + left + joiner + right + conjunction;
        			}
        		}
        	}
        	
        	returnString += ";";

		    if (appendedDFSQL.size() != 0)
		    {
			    for (DFSQL statement : appendedDFSQL)
			    {
				    returnString += " " + statement.formattedSQLStatement();
			    }
		    }

    		return returnString;
    	}
    	
    	if (insertRows != null)
    	{
    		if (fromTables == null)		{ return ""; }
    		if (fromTables.length != 1)	{ return ""; }
    		
    		returnString = "INSERT INTO `" + fromTables[0] + "`(";
    		for (String row : insertRows)
    		{
    			returnString += "`" + row + "`,";
    		}
    		returnString = returnString.substring(0, returnString.length() - 1) + ") VALUES (";
    		for (String value : insertData)
    		{
    			if (value.contains(" ") || hasCharacter(value))
    			{
    				returnString += "'" + value + "',";
    			}
    			else
    			{
    				returnString += value + ",";
    			}
    		}
    		returnString = returnString.substring(0, returnString.length() - 1) + ");";

		    if (appendedDFSQL.size() != 0)
		    {
			    for (DFSQL statement : appendedDFSQL)
			    {
				    returnString += " " + statement.formattedSQLStatement();
			    }
		    }

    		return returnString;
    	}
    	
    	if (selectRows == null) return ""; //We're assuming all that's left is SELECT statements
    	
    	returnString = "SELECT ";
    	for (String row : selectRows)
    	{
    		returnString += row + ",";
    	}
    	returnString = returnString.substring(0, returnString.length() - 1) + " FROM ";
    	for (String table : fromTables)
    	{
    		returnString += "`" + table + "`,";
    	}
    	returnString = returnString.substring(0, returnString.length() - 1);
    	
    	if (joinStatements != null)
    	{
    		for (JoinStruct joinStatement : joinStatements)
    		{
    			String left = joinStatement.clause.leftHandSide;
    			String right = joinStatement.clause.rightHandSide;
    			
    			if (right.contains(" "))
    			{
    				right = "'" + right + "'";
    			}

    			if (joinStatement.joinType == DFSQLConjunctionClause.natural)
    			{
    				returnString += " NATURAL JOIN `" + joinStatement.table + "`";
    			}
    			else if (joinStatement.joinType == DFSQLConjunctionClause.leftOuter)
    			{
    				returnString += " LEFT OUTER JOIN `" + joinStatement.table + "` ON `" + left + "`=" + right;
    			}
    			else if (joinStatement.joinType == DFSQLConjunctionClause.rightOuter)
    			{
    				returnString += " RIGHT OUTER JOIN `" + joinStatement.table + "` ON `" + left + "`=" + right;
    			}
    			else if (joinStatement.joinType == DFSQLConjunctionClause.fullOuter)
    			{
    				returnString += " FULL OUTER JOIN `" + joinStatement.table + "` ON `" + left + "`=" + right;
    			}
    			else if (joinStatement.joinType == DFSQLConjunctionClause.cross)
    			{
    				returnString += " CROSS JOIN `" + joinStatement.table + "` ON `" + left + "`=" + right;
    			}
    			else if (joinStatement.joinType == DFSQLConjunctionClause.inner)
    			{
    				returnString += " INNER JOIN `" + joinStatement.table + "` ON `" + left + "`=" + right;
    			}
    		}
    	}
    	
    	if (whereStatements != null)
    	{
    		returnString += " WHERE";
    		for (WhereStruct whereStatement : whereStatements)
    		{
    			String left = whereStatement.clause.leftHandSide;
    			String right = whereStatement.clause.rightHandSide;
    			
    			if (right.contains(" ") || hasCharacter(right))
    			{
    				right = "'" + right + "'";
    			}
    			String joiner = "";
				if (whereStatement.joiner == DFSQLConjunctionClause.equals)
				{
					joiner = "=";
				}
				else if (whereStatement.joiner == DFSQLConjunctionClause.notEquals)
				{
					joiner = "!=";
				}
				else if (whereStatement.joiner == DFSQLConjunctionClause.greaterThan)
				{
					joiner = ">";
				}
				else if (whereStatement.joiner == DFSQLConjunctionClause.greaterThanOrEqualTo)
				{
					joiner = ">=";
				}
				else if (whereStatement.joiner == DFSQLConjunctionClause.lessThan)
				{
					joiner = "<";
				}
				else if (whereStatement.joiner == DFSQLConjunctionClause.lessThanOrEqualTo)
				{
					joiner = "<=";
				}
				
    			if (whereStatement.conjunction == DFSQLConjunctionClause.none)
    			{
    				returnString += " " + left + joiner + right;
    			}
    			else
    			{
    				String conjunction = "";
    				if (whereStatement.conjunction == DFSQLConjunctionClause.and)
    				{
    					conjunction = " AND";
    				}
    				else if (whereStatement.conjunction == DFSQLConjunctionClause.or)
    				{
    					conjunction = " OR";
    				}
    				returnString += " " + left + joiner + right + conjunction;
    			}
    		}
    	}
    	
    	returnString += ";";

	    if (appendedDFSQL.size() != 0)
	    {
		    for (DFSQL statement : appendedDFSQL)
		    {
			    returnString += " " + statement.formattedSQLStatement();
		    }
	    }

    	return returnString;
    }
    
    private boolean hasCharacter(String string)
    {
    	String  lowered = string.toLowerCase();
        return  lowered.contains("a") || lowered.contains("b") || lowered.contains("c") ||
                lowered.contains("d") || lowered.contains("e") || lowered.contains("f") ||
                lowered.contains("g") || lowered.contains("h") || lowered.contains("i") ||
                lowered.contains("j") || lowered.contains("k") || lowered.contains("l") ||
                lowered.contains("m") || lowered.contains("n") || lowered.contains("o") ||
                lowered.contains("p") || lowered.contains("q") || lowered.contains("r") ||
                lowered.contains("s") || lowered.contains("t") || lowered.contains("u") ||
                lowered.contains("v") || lowered.contains("w") || lowered.contains("x") ||
                lowered.contains("y") || lowered.contains("z");
    }
    
    //MARK: - SELECT Constructors

    /**
     * SELECT statement with 1 row
     * @param row the attribute to request
     * @return self
     * @throws DFSQLError If no row specified, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL select(String row) throws DFSQLError
    {
        if (selectRows != null)         { throw DFSQLError.conditionAlreadyExists; }
        if (row.contains("*")) 		   	{ throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(row, ""))               	{ throw DFSQLError.cannotUseEmptyValue; }
        if (row.length() > 64)			{ throw DFSQLError.rowLengthTooLong; }
        
        selectRows = new String[] { row };
        
        return this;
    }

    /**
     * SELECT statement with multiple row
     * @param rows the attributes to request
     * @return self
     * @throws DFSQLError If no rows specified, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL select(String[] rows) throws DFSQLError
    {
        if (selectRows != null)	{ throw DFSQLError.conditionAlreadyExists; }
        
        for (String row : rows)
        {
            if (row.contains("*"))	{ throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(row, ""))         	{ throw DFSQLError.cannotUseEmptyValue; }
            if (row.length() > 64)	{ throw DFSQLError.rowLengthTooLong; }
        }
        
        selectRows = rows;
        
        return this;
    }
    
    //MARK: - FROM Constructors

    /**
     * FROM statement with one table
     * @param table the table to request
     * @return self
     * @throws DFSQLError If no table specified, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL from(String table) throws DFSQLError
    {
        if (fromTables != null)         { throw DFSQLError.conditionAlreadyExists; }
        if (table.contains("*"))        { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(table, ""))  { throw DFSQLError.cannotUseEmptyValue; }
        if (table.length() > 64)        { throw DFSQLError.tableLengthTooLong; }

        fromTables = new String[] {table};
        
        return this;
    }

    /**
     * FROM statement with multiple tables
     * @param tables the tables to request
     * @return self
     * @throws DFSQLError If no tables specified, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL from(String[] tables) throws DFSQLError
    {
        if (fromTables != null) { throw DFSQLError.conditionAlreadyExists; }

        for (String table : tables)
        {
            if (table.contains("*"))        { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(table, ""))  { throw DFSQLError.cannotUseEmptyValue; }
            if (table.length() > 64)        { throw DFSQLError.tableLengthTooLong; }
        }
        
        fromTables = tables;
        
        return this;
    }
    
    //MARK: - UPDATE SET Constructor

    /**
     * UPDATE statement with one clause
     * @param table the table to request
     * @param leftHandSide the left hand side of the clause
     * @param rightHandSide the right hand side of the clause
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL update(String table, String leftHandSide, String rightHandSide) throws DFSQLError
    {
        if (fromTables != null)         { throw DFSQLError.conditionAlreadyExists; }
        if (table.contains("*"))        { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(table, ""))  { throw DFSQLError.cannotUseEmptyValue; }
        if (table.length() > 64)        { throw DFSQLError.tableLengthTooLong; }

        fromTables = new String[] {table};

        if (leftHandSide.contains("*"))         { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(leftHandSide, ""))   { throw DFSQLError.cannotUseEmptyValue; }
        if (leftHandSide.length() > 64)         { throw DFSQLError.rowLengthTooLong; }
        if (rightHandSide.contains("*"))        { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(rightHandSide, ""))  { throw DFSQLError.cannotUseEmptyValue; }

        updateStatements = new DFSQLClauseStruct[] {new DFSQLClauseStruct(leftHandSide, rightHandSide)};
        
        return this;
    }

    /**
     * UPDATE statement with multiple clauses
     * @param table the table to request
     * @param statements the clause(s)
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL update(String table, DFSQLClauseStruct[] statements) throws DFSQLError
    {
        if (fromTables != null)         { throw DFSQLError.conditionAlreadyExists; }
        if (table.contains("*"))        { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(table, ""))  { throw DFSQLError.cannotUseEmptyValue; }
        if (table.length() > 64)        { throw DFSQLError.tableLengthTooLong; }

        fromTables = new String[] {table};
        
        for (DFSQLClauseStruct statement : statements)
        {
            if (statement.leftHandSide.contains("*"))           { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.leftHandSide, ""))     { throw DFSQLError.cannotUseEmptyValue; }
            if (statement.leftHandSide.length() > 64)           { throw DFSQLError.rowLengthTooLong; }

            if (statement.rightHandSide.contains("*"))          { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.rightHandSide, ""))    { throw DFSQLError.cannotUseEmptyValue; }
        }
        
        updateStatements = statements;
        
        return this;
    }
    
    //MARK: - INSERT INTO Constructor

    /**
     * INSERT INTO statement
     * @param table the table to insert into
     * @param values the values for the entry
     * @param rows the attributes to insert into
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, values and rows do not match, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL insert(String table, String[] values, String[] rows) throws DFSQLError
    {
        if (fromTables != null) { throw DFSQLError.conditionAlreadyExists; }
        if (insertData != null) { throw DFSQLError.conditionAlreadyExists; }
        if (insertRows != null) { throw DFSQLError.conditionAlreadyExists; }

        if (values.length != rows.length) { throw DFSQLError.conditionsMustBeEqual; }
        if (table.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(table, "")) { throw DFSQLError.cannotUseEmptyValue; }
        if (table.length() > 64) { throw DFSQLError.tableLengthTooLong; }

        fromTables = new String[] {table};
        
        for (String value : values)
        {
            if (value.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(value, "")) { throw DFSQLError.cannotUseEmptyValue; }
        }
        
        for (String row : rows)
        {
            if (row.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(row, "")) { throw DFSQLError.cannotUseEmptyValue; }
            if (row.length() > 64) { throw DFSQLError.rowLengthTooLong; }
        }
        
        insertData = values;
        insertRows = rows;
        
        return this;
    }
    
    //MARK: - JOIN ON Constructors

    /**
     * JOIN statement convenience method
     * @param table the table to join on
     * @param leftHandSide the left hand side of the clause
     * @param rightHandSide the right hand side of the clause
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL joinOn(String table, String leftHandSide, String rightHandSide) throws DFSQLError
    {
        if (joinStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        if (leftHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(leftHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }
        if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }
        if (rightHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(rightHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }

        if (table.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(table, "")) { throw DFSQLError.cannotUseEmptyValue; }
        if (table.length() > 64) { throw DFSQLError.tableLengthTooLong; }

        joinStatements = new ArrayList<>();
        joinStatements.add(new JoinStruct(DFSQLConjunctionClause.natural, table, new DFSQLClauseStruct(leftHandSide, rightHandSide)));
        
        return this;
    }

    /**
     * JOIN statement convenience method
     * @param joins a variable amount of JOIN clauses using NATURAL JOIN
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL joinOn(JoinParam[] joins) throws DFSQLError
    {
        if (joinStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        joinStatements = new ArrayList<>();
        
        for (JoinParam statement : joins)
        {
            if (statement.leftHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.leftHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }
            if (statement.leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }

            if (statement.rightHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.rightHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }

            if (statement.table.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.table, "")) { throw DFSQLError.cannotUseEmptyValue; }
            if (statement.table.length() > 64) { throw DFSQLError.tableLengthTooLong; }

            joinStatements.add(new JoinStruct(DFSQLConjunctionClause.natural, statement.table, new DFSQLClauseStruct(statement.leftHandSide, statement.rightHandSide)));
        }
        
        return this;
    }

    /**
     * NATURAL JOIN statement
     * @param table the table to join on
     * @param leftHandSide the left hand side of the clause
     * @param rightHandSide the right hand side of the clause
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL joinNatural(String table, String leftHandSide, String rightHandSide) throws DFSQLError
    {
        if (joinStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        if (leftHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(leftHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }
        if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }

        if (rightHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(rightHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }

        if (table.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(table, "")) { throw DFSQLError.cannotUseEmptyValue; }
        if (table.length() > 64) { throw DFSQLError.tableLengthTooLong; }

        joinStatements = new ArrayList<>();
        joinStatements.add(new JoinStruct(DFSQLConjunctionClause.natural, table, new DFSQLClauseStruct(leftHandSide, rightHandSide)));
        
        return this;
    }

    /**
     * NATURAL JOIN statement
     * @param joins a variable amount of JOIN clauses using NATURAL JOIN
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL joinNatural(JoinParam[] joins) throws DFSQLError
    {
        if (joinStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        joinStatements = new ArrayList<>();
        
        for (JoinParam statement : joins)
        {
            if (statement.leftHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.leftHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }
            if (statement.leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }

            if (statement.rightHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.rightHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }

            if (statement.table.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.table, "")) { throw DFSQLError.cannotUseEmptyValue; }
            if (statement.table.length() > 64) { throw DFSQLError.tableLengthTooLong; }

            joinStatements.add(new JoinStruct(DFSQLConjunctionClause.natural, statement.table, new DFSQLClauseStruct(statement.leftHandSide, statement.rightHandSide)));
        }
        
        return this;
    }

    /**
     * LEFT OUTER JOIN statement
     * @param table the table to join on
     * @param leftHandSide the left hand side of the clause
     * @param rightHandSide the right hand side of the clause
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL joinLeftOuter(String table, String leftHandSide, String rightHandSide) throws DFSQLError
    {
        if (joinStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        if (leftHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(leftHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }
        if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }

        if (rightHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(rightHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }

        if (table.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(table, "")) { throw DFSQLError.cannotUseEmptyValue; }
        if (table.length() > 64) { throw DFSQLError.tableLengthTooLong; }

        joinStatements = new ArrayList<>();
        joinStatements.add(new JoinStruct(DFSQLConjunctionClause.leftOuter, table, new DFSQLClauseStruct(leftHandSide, rightHandSide)));
        
        return this;
    }

    /**
     * LEFT OUTER JOIN statement
     * @param joins a variable amount of JOIN clauses using NATURAL JOIN
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL joinLeftOuter(JoinParam[] joins) throws DFSQLError
    {
        if (joinStatements != null) { throw DFSQLError.conditionAlreadyExists; }
        joinStatements = new ArrayList<>();
        
        for (JoinParam statement : joins)
        {
            if (statement.leftHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.leftHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }
            if (statement.leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }

            if (statement.rightHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.rightHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }

            if (statement.table.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.table, "")) { throw DFSQLError.cannotUseEmptyValue; }
            if (statement.table.length() > 64) { throw DFSQLError.tableLengthTooLong; }

            joinStatements.add(new JoinStruct(DFSQLConjunctionClause.leftOuter, statement.table, new DFSQLClauseStruct(statement.leftHandSide, statement.rightHandSide)));
        }
        
        return this;
    }

    /**
     * RIGHT OUTER JOIN statement
     * @param table the table to join on
     * @param leftHandSide the left hand side of the clause
     * @param rightHandSide the right hand side of the clause
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL joinRightOuter(String table, String leftHandSide, String rightHandSide) throws DFSQLError
    {
        if (joinStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        if (leftHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(leftHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }
        if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }

        if (rightHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(rightHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }

        if (table.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(table, "")) { throw DFSQLError.cannotUseEmptyValue; }
        if (table.length() > 64) { throw DFSQLError.tableLengthTooLong; }

        joinStatements = new ArrayList<>();
        joinStatements.add(new JoinStruct(DFSQLConjunctionClause.rightOuter, table, new DFSQLClauseStruct(leftHandSide, rightHandSide)));
        
        return this;
    }

    /**
     * RIGHT OUTER JOIN statement
     * @param joins a variable amount of JOIN clauses using NATURAL JOIN
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL joinRightOuter(JoinParam[] joins) throws DFSQLError
    {
        if (joinStatements != null) { throw DFSQLError.conditionAlreadyExists; }
        joinStatements = new ArrayList<>();
        
        for (JoinParam statement : joins)
        {
            if (statement.leftHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.leftHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }
            if (statement.leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }

            if (statement.rightHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.rightHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }

            if (statement.table.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.table, "")) { throw DFSQLError.cannotUseEmptyValue; }
            if (statement.table.length() > 64) { throw DFSQLError.tableLengthTooLong; }

            joinStatements.add(new JoinStruct(DFSQLConjunctionClause.rightOuter, statement.table, new DFSQLClauseStruct(statement.leftHandSide, statement.rightHandSide)));
        }
        
        return this;
    }

    /**
     * FULL OUTER JOIN statement
     * @param table the table to join on
     * @param leftHandSide the left hand side of the clause
     * @param rightHandSide the right hand side of the clause
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL joinFullOuter(String table, String leftHandSide, String rightHandSide) throws DFSQLError
    {
        if (joinStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        if (leftHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(leftHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }
        if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }

        if (rightHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(rightHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }

        if (table.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(table, "")) { throw DFSQLError.cannotUseEmptyValue; }
        if (table.length() > 64) { throw DFSQLError.tableLengthTooLong; }

        joinStatements = new ArrayList<>();
        joinStatements.add(new JoinStruct(DFSQLConjunctionClause.fullOuter, table, new DFSQLClauseStruct(leftHandSide, rightHandSide)));
        
        return this;
    }

    /**
     * FULL OUTER JOIN statement
     * @param joins a variable amount of JOIN clauses using NATURAL JOIN
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL joinFullOuter(JoinParam[] joins) throws DFSQLError
    {
        if (joinStatements != null) { throw DFSQLError.conditionAlreadyExists; }
        joinStatements = new ArrayList<>();
        
        for (JoinParam statement : joins)
        {
            if (statement.leftHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.leftHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }
            if (statement.leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }

            if (statement.rightHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.rightHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }

            if (statement.table.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.table, "")) { throw DFSQLError.cannotUseEmptyValue; }
            if (statement.table.length() > 64) { throw DFSQLError.tableLengthTooLong; }

            joinStatements.add(new JoinStruct(DFSQLConjunctionClause.fullOuter, statement.table, new DFSQLClauseStruct(statement.leftHandSide, statement.rightHandSide)));
        }
        
        return this;
    }

    /**
     * CROSS JOIN statement
     * @param table the table to join on
     * @param leftHandSide the left hand side of the clause
     * @param rightHandSide the right hand side of the clause
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL joinCross(String table, String leftHandSide, String rightHandSide) throws DFSQLError
    {
        if (joinStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        if (leftHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(leftHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }
        if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }

        if (rightHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(rightHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }

        if (table.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(table, "")) { throw DFSQLError.cannotUseEmptyValue; }
        if (table.length() > 64) { throw DFSQLError.tableLengthTooLong; }

        joinStatements = new ArrayList<>();
        joinStatements.add(new JoinStruct(DFSQLConjunctionClause.cross, table, new DFSQLClauseStruct(leftHandSide, rightHandSide)));
        
        return this;
    }

    /**
     * CROSS JOIN statement
     * @param joins a variable amount of JOIN clauses using NATURAL JOIN
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL joinCross(JoinParam[] joins) throws DFSQLError
    {
        if (joinStatements != null) { throw DFSQLError.conditionAlreadyExists; }
        joinStatements = new ArrayList<>();
        
        for (JoinParam statement : joins)
        {
            if (statement.leftHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.leftHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }
            if (statement.leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }

            if (statement.rightHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.rightHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }

            if (statement.table.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.table, "")) { throw DFSQLError.cannotUseEmptyValue; }
            if (statement.table.length() > 64) { throw DFSQLError.tableLengthTooLong; }

            joinStatements.add(new JoinStruct(DFSQLConjunctionClause.cross, statement.table, new DFSQLClauseStruct(statement.leftHandSide, statement.rightHandSide)));
        }
        
        return this;
    }

    /**
     * INNER JOIN statement
     * @param table the table to join on
     * @param leftHandSide the left hand side of the clause
     * @param rightHandSide the right hand side of the clause
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL joinInner(String table, String leftHandSide, String rightHandSide) throws DFSQLError
    {
        if (joinStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        if (leftHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(leftHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }
        if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }

        if (rightHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(rightHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }

        if (table.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(table, "")) { throw DFSQLError.cannotUseEmptyValue; }
        if (table.length() > 64) { throw DFSQLError.tableLengthTooLong; }

        joinStatements = new ArrayList<>();
        joinStatements.add(new JoinStruct(DFSQLConjunctionClause.inner, table, new DFSQLClauseStruct(leftHandSide, rightHandSide)));
        
        return this;
    }

    /**
     * INNER JOIN statement
     * @param joins a variable amount of JOIN clauses using NATURAL JOIN
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL joinInner(JoinParam[] joins) throws DFSQLError
    {
        if (joinStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        joinStatements = new ArrayList<>();
        
        for (JoinParam statement : joins)
        {
            if (statement.leftHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.leftHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }
            if (statement.leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }

            if (statement.rightHandSide.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.rightHandSide, "")) { throw DFSQLError.cannotUseEmptyValue; }

            if (statement.table.contains("*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (Objects.equals(statement.table, "")) { throw DFSQLError.cannotUseEmptyValue; }
            if (statement.table.length() > 64) { throw DFSQLError.tableLengthTooLong; }

            joinStatements.add(new JoinStruct(DFSQLConjunctionClause.inner, statement.table, new DFSQLClauseStruct(statement.leftHandSide, statement.rightHandSide)));
        }
        
        return this;
    }
    
    //MARK: - WHERE Constructors
    
    //MARK: Single

    /**
     * WHERE ...=... statement
     * @param leftHandSide the left hand side of the clause
     * @param rightHandSide the right hand side of the clause
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL whereEquals(String leftHandSide, String rightHandSide) throws DFSQLError
    {
        if (whereStatements != null) { throw DFSQLError.conditionAlreadyExists; }
        if (Objects.equals(leftHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(rightHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }

        whereStatements = new ArrayList<>();
        whereStatements.add(new WhereStruct(DFSQLConjunctionClause.none, DFSQLConjunctionClause.equals, new DFSQLClauseStruct(leftHandSide, rightHandSide)));
        
        return this;
    }

    /**
     * WHERE ...!=... statement
     * @param leftHandSide the left hand side of the clause
     * @param rightHandSide the right hand side of the clause
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL whereDoesNotEqual(String leftHandSide, String rightHandSide) throws DFSQLError
    {
        if (whereStatements != null) { throw DFSQLError.conditionAlreadyExists; }
        if (Objects.equals(leftHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(rightHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }

        whereStatements = new ArrayList<>();
        whereStatements.add(new WhereStruct(DFSQLConjunctionClause.none, DFSQLConjunctionClause.notEquals, new DFSQLClauseStruct(leftHandSide, rightHandSide)));
        
        return this;
    }

    /**
     * WHERE ...&lt;... statement
     * @param leftHandSide the left hand side of the clause
     * @param rightHandSide the right hand side of the clause
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL whereIsLessThan(String leftHandSide, String rightHandSide) throws DFSQLError
    {
        if (whereStatements != null) { throw DFSQLError.conditionAlreadyExists; }
        if (Objects.equals(leftHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(rightHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }

        whereStatements = new ArrayList<>();
        whereStatements.add(new WhereStruct(DFSQLConjunctionClause.none, DFSQLConjunctionClause.lessThan, new DFSQLClauseStruct(leftHandSide, rightHandSide)));
        
        return this;
    }

    /**
     * WHERE ...&lt;=... statement
     * @param leftHandSide the left hand side of the clause
     * @param rightHandSide the right hand side of the clause
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL whereLessThanOrEqual(String leftHandSide, String rightHandSide) throws DFSQLError
    {
        if (whereStatements != null) { throw DFSQLError.conditionAlreadyExists; }
        if (Objects.equals(leftHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(rightHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }

        whereStatements = new ArrayList<>();
        whereStatements.add(new WhereStruct(DFSQLConjunctionClause.none, DFSQLConjunctionClause.lessThanOrEqualTo, new DFSQLClauseStruct(leftHandSide, rightHandSide)));

        return this;
    }

    /**
     * WHERE ...&gt;... statement
     * @param leftHandSide the left hand side of the clause
     * @param rightHandSide the right hand side of the clause
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL whereGreaterThan(String leftHandSide, String rightHandSide) throws DFSQLError
    {
        if (whereStatements != null) { throw DFSQLError.conditionAlreadyExists; }
        if (Objects.equals(leftHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(rightHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }

        whereStatements = new ArrayList<>();
        whereStatements.add(new WhereStruct(DFSQLConjunctionClause.none, DFSQLConjunctionClause.greaterThan, new DFSQLClauseStruct(leftHandSide, rightHandSide)));
        
        return this;
    }

    /**
     * WHERE ...&gt;=... statement
     * @param leftHandSide the left hand side of the clause
     * @param rightHandSide the right hand side of the clause
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL whereGreaterThanOrEqual(String leftHandSide, String rightHandSide) throws DFSQLError
    {
        if (whereStatements != null) { throw DFSQLError.conditionAlreadyExists; }
        if (Objects.equals(leftHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (Objects.equals(rightHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }

        whereStatements = new ArrayList<>();
        whereStatements.add(new WhereStruct(DFSQLConjunctionClause.none, DFSQLConjunctionClause.greaterThanOrEqualTo, new DFSQLClauseStruct(leftHandSide, rightHandSide)));
        
        return this;
    }
    
    //MARK: AND

    /**
     * WHERE ...=... AND ...=... statement
     * @param leftHandSides the left hand sides of the clauses
     * @param rightHandSides the right hand sides of the clauses
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, leftHandSides and rightHandSides are not equal, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL whereAndEquals(String[] leftHandSides, String[] rightHandSides) throws DFSQLError
    {
        if (leftHandSides.length != rightHandSides.length) { throw DFSQLError.conditionsMustBeEqual; }
        if (whereStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        whereStatements = new ArrayList<>();
        
        for (String leftHandSide : leftHandSides)
        {
            if (Objects.equals(leftHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }
        }
        
        for (String rightHandSide : rightHandSides)
        {
            if (Objects.equals(rightHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        }
        
        for (int index = 0; index < leftHandSides.length - 1; index++)
        {
            String lhs = leftHandSides[index];
            String rhs = rightHandSides[index];
            
            whereStatements.add(new WhereStruct(DFSQLConjunctionClause.and, DFSQLConjunctionClause.equals, new DFSQLClauseStruct(lhs, rhs)));
        }
        
        whereStatements.add(new WhereStruct(DFSQLConjunctionClause.none, DFSQLConjunctionClause.equals, new DFSQLClauseStruct(leftHandSides[leftHandSides.length - 1], rightHandSides[rightHandSides.length - 1])));
        
        return this;
    }

    /**
     * WHERE ...!=... AND ...!=... statement
     * @param leftHandSides the left hand sides of the clauses
     * @param rightHandSides the right hand sides of the clauses
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, leftHandSides and rightHandSides are not equal, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL whereAndDoesNotEqual(String[] leftHandSides, String[] rightHandSides) throws DFSQLError
    {
        if (leftHandSides.length != rightHandSides.length) { throw DFSQLError.conditionsMustBeEqual; }
        if (whereStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        whereStatements = new ArrayList<>();
        
        for (String leftHandSide : leftHandSides)
        {
            if (Objects.equals(leftHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }
        }
        
        for (String rightHandSide : rightHandSides)
        {
            if (Objects.equals(rightHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        }
        
        for (int index = 0; index < leftHandSides.length - 1; index++)
        {
            String lhs = leftHandSides[index];
            String rhs = rightHandSides[index];
            
            whereStatements.add(new WhereStruct(DFSQLConjunctionClause.and, DFSQLConjunctionClause.notEquals, new DFSQLClauseStruct(lhs, rhs)));
        }
        
        whereStatements.add(new WhereStruct(DFSQLConjunctionClause.none, DFSQLConjunctionClause.notEquals, new DFSQLClauseStruct(leftHandSides[leftHandSides.length - 1], rightHandSides[rightHandSides.length - 1])));
        
        return this;
    }

    /**
     * WHERE ...&gt;... AND ...&gt;... statement
     * @param leftHandSides the left hand sides of the clauses
     * @param rightHandSides the right hand sides of the clauses
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, leftHandSides and rightHandSides are not equal, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL whereAndGreaterThan(String[] leftHandSides, String[] rightHandSides) throws DFSQLError
    {
        if (leftHandSides.length != rightHandSides.length) { throw DFSQLError.conditionsMustBeEqual; }
        if (whereStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        whereStatements = new ArrayList<>();
        
        for (String leftHandSide : leftHandSides)
        {
            if (Objects.equals(leftHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }
        }
        
        for (String rightHandSide : rightHandSides)
        {
            if (Objects.equals(rightHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        }
        
        for (int index = 0; index < leftHandSides.length - 1; index++)
        {
            String lhs = leftHandSides[index];
            String rhs = rightHandSides[index];
            
            whereStatements.add(new WhereStruct(DFSQLConjunctionClause.and, DFSQLConjunctionClause.greaterThan, new DFSQLClauseStruct(lhs, rhs)));
        }
        
        whereStatements.add(new WhereStruct(DFSQLConjunctionClause.none, DFSQLConjunctionClause.greaterThan, new DFSQLClauseStruct(leftHandSides[leftHandSides.length - 1], rightHandSides[rightHandSides.length - 1])));
        
        return this;
    }

    /**
     * WHERE ...&gt;=... AND ...&gt;=... statement
     * @param leftHandSides the left hand sides of the clauses
     * @param rightHandSides the right hand sides of the clauses
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, leftHandSides and rightHandSides are not equal, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL whereAndGreaterThanOrEqual(String[] leftHandSides, String[] rightHandSides) throws DFSQLError
    {
        if (leftHandSides.length != rightHandSides.length) { throw DFSQLError.conditionsMustBeEqual; }
        if (whereStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        whereStatements = new ArrayList<>();
        
        for (String leftHandSide : leftHandSides)
        {
            if (Objects.equals(leftHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }
        }
        
        for (String rightHandSide : rightHandSides)
        {
            if (Objects.equals(rightHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        }
        
        for (int index = 0; index < leftHandSides.length - 1; index++)
        {
            String lhs = leftHandSides[index];
            String rhs = rightHandSides[index];
            
            whereStatements.add(new WhereStruct(DFSQLConjunctionClause.and, DFSQLConjunctionClause.greaterThanOrEqualTo, new DFSQLClauseStruct(lhs, rhs)));
        }
        
        whereStatements.add(new WhereStruct(DFSQLConjunctionClause.none, DFSQLConjunctionClause.greaterThanOrEqualTo, new DFSQLClauseStruct(leftHandSides[leftHandSides.length - 1], rightHandSides[rightHandSides.length - 1])));
        
        return this;
    }

    /**
     * WHERE ...&lt;... AND ...&lt;... statement
     * @param leftHandSides the left hand sides of the clauses
     * @param rightHandSides the right hand sides of the clauses
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, leftHandSides and rightHandSides are not equal, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL whereAndLessThan(String[] leftHandSides, String[] rightHandSides) throws DFSQLError
    {
        if (leftHandSides.length != rightHandSides.length) { throw DFSQLError.conditionsMustBeEqual; }
        if (whereStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        whereStatements = new ArrayList<>();
        
        for (String leftHandSide : leftHandSides)
        {
            if (Objects.equals(leftHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }
        }
        
        for (String rightHandSide : rightHandSides)
        {
            if (Objects.equals(rightHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        }
        
        for (int index = 0; index < leftHandSides.length - 1; index++)
        {
            String lhs = leftHandSides[index];
            String rhs = rightHandSides[index];
            
            whereStatements.add(new WhereStruct(DFSQLConjunctionClause.and, DFSQLConjunctionClause.lessThan, new DFSQLClauseStruct(lhs, rhs)));
        }
        
        whereStatements.add(new WhereStruct(DFSQLConjunctionClause.none, DFSQLConjunctionClause.lessThan, new DFSQLClauseStruct(leftHandSides[leftHandSides.length - 1], rightHandSides[rightHandSides.length - 1])));
        
        return this;
    }

    /**
     * WHERE ...&lt;=... AND ...&lt;=... statement
     * @param leftHandSides the left hand sides of the clauses
     * @param rightHandSides the right hand sides of the clauses
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, leftHandSides and rightHandSides are not equal, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL whereAndLessThanOrEqual(String[] leftHandSides, String[] rightHandSides) throws DFSQLError
    {
        if (leftHandSides.length != rightHandSides.length) { throw DFSQLError.conditionsMustBeEqual; }
        if (whereStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        whereStatements = new ArrayList<>();
        
        for (String leftHandSide : leftHandSides)
        {
            if (Objects.equals(leftHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }
        }
        
        for (String rightHandSide : rightHandSides)
        {
            if (Objects.equals(rightHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        }
        
        for (int index = 0; index < leftHandSides.length - 1; index++)
        {
            String lhs = leftHandSides[index];
            String rhs = rightHandSides[index];
            
            whereStatements.add(new WhereStruct(DFSQLConjunctionClause.and, DFSQLConjunctionClause.lessThanOrEqualTo, new DFSQLClauseStruct(lhs, rhs)));
        }
        
        whereStatements.add(new WhereStruct(DFSQLConjunctionClause.none, DFSQLConjunctionClause.lessThanOrEqualTo, new DFSQLClauseStruct(leftHandSides[leftHandSides.length - 1], rightHandSides[rightHandSides.length - 1])));
        
        return this;
    }
    
    //MARK: OR

    /**
     * WHERE ...=... OR ...=... statement
     * @param leftHandSides the left hand sides of the clauses
     * @param rightHandSides the right hand sides of the clauses
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, leftHandSides and rightHandSides are not equal, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL whereOrEquals(String[] leftHandSides, String[] rightHandSides) throws DFSQLError
    {
        if (leftHandSides.length != rightHandSides.length) { throw DFSQLError.conditionsMustBeEqual; }
        if (whereStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        whereStatements = new ArrayList<>();
        
        for (String leftHandSide : leftHandSides)
        {
            if (Objects.equals(leftHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }
        }
        
        for (String rightHandSide : rightHandSides)
        {
            if (Objects.equals(rightHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        }
        
        for (int index = 0; index < leftHandSides.length - 1; index++)
        {
            String lhs = leftHandSides[index];
            String rhs = rightHandSides[index];
            
            whereStatements.add(new WhereStruct(DFSQLConjunctionClause.or, DFSQLConjunctionClause.equals, new DFSQLClauseStruct(lhs, rhs)));
        }
        
        whereStatements.add(new WhereStruct(DFSQLConjunctionClause.none, DFSQLConjunctionClause.equals, new DFSQLClauseStruct(leftHandSides[leftHandSides.length - 1], rightHandSides[rightHandSides.length - 1])));
        
        return this;
    }

    /**
     * WHERE ...!=... OR ...!=... statement
     * @param leftHandSides the left hand sides of the clauses
     * @param rightHandSides the right hand sides of the clauses
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, leftHandSides and rightHandSides are not equal, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL whereOrNotEqual(String[] leftHandSides, String[] rightHandSides) throws DFSQLError
    {
        if (leftHandSides.length != rightHandSides.length) { throw DFSQLError.conditionsMustBeEqual; }
        if (whereStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        whereStatements = new ArrayList<>();
        
        for (String leftHandSide : leftHandSides)
        {
            if (Objects.equals(leftHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }
        }
        
        for (String rightHandSide : rightHandSides)
        {
            if (Objects.equals(rightHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        }
        
        for (int index = 0; index < leftHandSides.length - 1; index++)
        {
            String lhs = leftHandSides[index];
            String rhs = rightHandSides[index];
            
            whereStatements.add(new WhereStruct(DFSQLConjunctionClause.or, DFSQLConjunctionClause.notEquals, new DFSQLClauseStruct(lhs, rhs)));
        }
        
        whereStatements.add(new WhereStruct(DFSQLConjunctionClause.none, DFSQLConjunctionClause.notEquals, new DFSQLClauseStruct(leftHandSides[leftHandSides.length - 1], rightHandSides[rightHandSides.length - 1])));
        
        return this;
    }

    /**
     * WHERE ...&gt;... OR ...&gt;... statement
     * @param leftHandSides the left hand sides of the clauses
     * @param rightHandSides the right hand sides of the clauses
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, leftHandSides and rightHandSides are not equal, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL whereOrGreaterThan(String[] leftHandSides, String[] rightHandSides) throws DFSQLError
    {
        if (leftHandSides.length != rightHandSides.length) { throw DFSQLError.conditionsMustBeEqual; }
        if (whereStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        whereStatements = new ArrayList<>();
        
        for (String leftHandSide : leftHandSides)
        {
            if (Objects.equals(leftHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }
        }
        
        for (String rightHandSide : rightHandSides)
        {
            if (Objects.equals(rightHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        }
        
        for (int index = 0; index < leftHandSides.length - 1; index++)
        {
            String lhs = leftHandSides[index];
            String rhs = rightHandSides[index];
            
            whereStatements.add(new WhereStruct(DFSQLConjunctionClause.or, DFSQLConjunctionClause.greaterThan, new DFSQLClauseStruct(lhs, rhs)));
        }
        
        whereStatements.add(new WhereStruct(DFSQLConjunctionClause.none, DFSQLConjunctionClause.greaterThan, new DFSQLClauseStruct(leftHandSides[leftHandSides.length - 1], rightHandSides[rightHandSides.length - 1])));
        
        return this;
    }

    /**
     * WHERE ...&gt;=... OR ...&gt;=... statement
     * @param leftHandSides the left hand sides of the clauses
     * @param rightHandSides the right hand sides of the clauses
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, leftHandSides and rightHandSides are not equal, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL whereOrGreaterThanOrEqual(String[] leftHandSides, String[] rightHandSides) throws DFSQLError
    {
        if (leftHandSides.length != rightHandSides.length) { throw DFSQLError.conditionsMustBeEqual; }
        if (whereStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        whereStatements = new ArrayList<>();
        
        for (String leftHandSide : leftHandSides)
        {
            if (Objects.equals(leftHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }
        }
        
        for (String rightHandSide : rightHandSides)
        {
            if (Objects.equals(rightHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        }

        for (int index = 0; index < leftHandSides.length - 1; index++)
        {
            String lhs = leftHandSides[index];
            String rhs = rightHandSides[index];
            
            whereStatements.add(new WhereStruct(DFSQLConjunctionClause.or, DFSQLConjunctionClause.greaterThanOrEqualTo, new DFSQLClauseStruct(lhs, rhs)));
        }
        
        whereStatements.add(new WhereStruct(DFSQLConjunctionClause.none, DFSQLConjunctionClause.greaterThanOrEqualTo, new DFSQLClauseStruct(leftHandSides[leftHandSides.length - 1], rightHandSides[rightHandSides.length - 1])));
        
        return this;
    }

    /**
     * WHERE ...&lt;... OR ...&lt;... statement
     * @param leftHandSides the left hand sides of the clauses
     * @param rightHandSides the right hand sides of the clauses
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, leftHandSides and rightHandSides are not equal, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL whereOrLessThan(String[] leftHandSides, String[] rightHandSides) throws DFSQLError
    {
        if (leftHandSides.length != rightHandSides.length) { throw DFSQLError.conditionsMustBeEqual; }
        if (whereStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        whereStatements = new ArrayList<>();
        
        for (String leftHandSide : leftHandSides)
        {
            if (Objects.equals(leftHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }
        }
        
        for (String rightHandSide : rightHandSides)
        {
            if (Objects.equals(rightHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        }
        
        for (int index = 0; index < leftHandSides.length - 1; index++)
        {
            String lhs = leftHandSides[index];
            String rhs = rightHandSides[index];
            
            whereStatements.add(new WhereStruct(DFSQLConjunctionClause.or, DFSQLConjunctionClause.lessThan, new DFSQLClauseStruct(lhs, rhs)));
        }
        
        whereStatements.add(new WhereStruct(DFSQLConjunctionClause.none, DFSQLConjunctionClause.lessThan, new DFSQLClauseStruct(leftHandSides[leftHandSides.length - 1], rightHandSides[rightHandSides.length - 1])));
        
        return this;
    }

    /**
     * WHERE ...&lt;=... OR ...&lt;=... statement
     * @param leftHandSides the left hand sides of the clauses
     * @param rightHandSides the right hand sides of the clauses
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, leftHandSides and rightHandSides are not equal, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL whereOrLessThanOrEqual(String[] leftHandSides, String[] rightHandSides) throws DFSQLError
    {
        if (leftHandSides.length != rightHandSides.length) { throw DFSQLError.conditionsMustBeEqual; }

        if (whereStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        whereStatements = new ArrayList<>();
        
        for (String leftHandSide : leftHandSides)
        {
            if (Objects.equals(leftHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }
        }
        
        for (String rightHandSide : rightHandSides)
        {
            if (Objects.equals(rightHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        }
        
        for (int index = 0; index < leftHandSides.length - 1; index++)
        {
            String lhs = leftHandSides[index];
            String rhs = rightHandSides[index];
            
            whereStatements.add(new WhereStruct(DFSQLConjunctionClause.or, DFSQLConjunctionClause.lessThanOrEqualTo, new DFSQLClauseStruct(lhs, rhs)));
        }
        
        whereStatements.add(new WhereStruct(DFSQLConjunctionClause.none, DFSQLConjunctionClause.lessThanOrEqualTo, new DFSQLClauseStruct(leftHandSides[leftHandSides.length - 1], rightHandSides[rightHandSides.length - 1])));
        
        return this;
    }
    
    //MARK: Custom

    /**
     * Custom WHERE statement.  Can take any combination of equalities and conjunctions
     * @param custom the clauses
     * @return self
     * @throws DFSQLError If a parameter is null, already exists, leftHandSides and rightHandSides are not equal, `*` is used, is empty, or is greater than 64 in length
     */
    public DFSQL whereCustom(WhereStruct[] custom) throws DFSQLError
    {
        if (custom.length == 0) { throw DFSQLError.cannotUseEmptyValue; }
        if (whereStatements != null) { throw DFSQLError.conditionAlreadyExists; }

        for (WhereStruct whereClause : custom)
        {
        	DFSQLClauseStruct SQLClause = whereClause.clause;

            if (Objects.equals(SQLClause.leftHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
            if (SQLClause.leftHandSide.length() > 64) { throw DFSQLError.rowLengthTooLong; }
            if (Objects.equals(SQLClause.rightHandSide, "*")) { throw DFSQLError.cannotUseAllRowsSQLSpecifier; }
        }
       
        whereStatements = new ArrayList<>();
        Collections.addAll(whereStatements, custom);
        
        return this;
    }
}
