package database.DFSQL;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

/**
 * The main SQL class.  DFDatabase uses a custom built SQL wrapper to add a layer of security and overload safety
 */
@SuppressWarnings({"unused", "WeakerAccess", "ResultOfMethodCallIgnored"})
public class DFSQL
{
    private static String WhereStatementComparesNullString = "Where Statement is comparing a null statement.";

    private String[] selectRows = new String[] {};
    private boolean distinctSelect = false;
    private String intoTable = "";
    private String inDB = "";

    String deleteFromTable = "";
    String[] fromTables = new String[] {};
    private InternalJoin[] joinStatements = new InternalJoin[] {};
    private Where[] whereStatements = new Where[] {};
    private OrderBy[] orderByStatements = new OrderBy[] {};
	private String[] groupByStatements = new String[] {};

    private int limitNum = -1;

    private String[] insertRows = new String[] {};
    private String[] insertValues = new String[] {};
    private String[] duplicateKeys = new String[] {};
    private String[] duplicateValues = new String[] {};

    private DFSQLClause[] updateStatements = new DFSQLClause[] {};

    private final ArrayList<DFSQL> appendedSQL = new ArrayList<>();

    public String formattedStatement()
    {
        return formatted();
    }

    public DFSQL() { }

    /**
     * Appends an already built DFSQL object to the callee
     * @param object a DFSQL object.  This object is immutable after this function is called
     * @return self
     */
    public DFSQL append(DFSQL object)
    {
        if (!appendedSQL.contains(object))
            appendedSQL.add(object);

        return this;
    }

	private String insertWhereAndOrderByStatements(String returnString)
	{
		if (whereStatements.length == 0) return returnString;

		returnString += " WHERE";
		StringBuilder returnStringBuilder = new StringBuilder(returnString);
		for (Where whereStatement : whereStatements)
		{
			final String left = whereStatement.clause.attribute;
			String right = whereStatement.clause.value;

			if (whereStatement.equivalence != DFSQLEquivalence.isNull && whereStatement.equivalence != DFSQLEquivalence.isNotNull)
			{
				boolean isNum = false;
				try
				{
					parseInt(right);
					isNum = true;
				}
				catch (Exception ignored) { }
				if (right.contains(" ") || !isNum)
				{
					right = "'" + right + "'";
				}

				returnStringBuilder.append(" ").append(left).append(whereStatement.equivalence).append(right);
			}
			else
			{
				returnStringBuilder.append(" ").append(left).append(whereStatement.equivalence);
			}
		}
		returnString = returnStringBuilder.toString();

		if (groupByStatements.length != 0)
		{
			returnString += " GROUP BY";
			if (groupByStatements.length == 1)
			{
				returnStringBuilder = new StringBuilder(returnString);
				for (String groupByStatement : groupByStatements)
				{
					returnStringBuilder.append(" ").append(groupByStatement);
				}
				returnString = returnStringBuilder.toString();
			}
			else
			{
				returnStringBuilder = new StringBuilder(returnString);
				for (int i = 0; i < groupByStatements.length - 1; i++)
				{
					String groupByStatement = groupByStatements[i];

					returnStringBuilder.append(" ").append(groupByStatement).append(",");
				}
				returnString = returnStringBuilder.toString();
				String groupByStatement = groupByStatements[groupByStatements.length - 1];

				returnString += " " + groupByStatement;
			}
		}

		if (orderByStatements.length == 0) { return returnString; }
		returnString += " ORDERED BY";
		if (orderByStatements.length == 1)
		{
			returnStringBuilder = new StringBuilder(returnString);
			for (OrderBy orderByStatement : orderByStatements)
			{
				String attribute = orderByStatement.attribute;
				String direction = orderByStatement.orderBy.toString();

				returnStringBuilder.append(" ").append(attribute).append(direction);
			}
			returnString = returnStringBuilder.toString();
		}
		else
		{
			returnStringBuilder = new StringBuilder(returnString);
			for (int i = 0; i < orderByStatements.length - 1; i++)
			{
				OrderBy orderByStatement = orderByStatements[i];

				String attribute = orderByStatement.attribute;
				String direction = orderByStatement.orderBy.toString();

				returnStringBuilder.append(" ").append(attribute).append(direction);
			}
			returnString = returnStringBuilder.toString();
			OrderBy orderByStatement = orderByStatements[orderByStatements.length - 1];

			String attribute = orderByStatement.attribute;
			String direction = orderByStatement.orderBy.toString();

			returnString += " " + attribute + direction;
		}

		return returnString;
	}

	private String insertAppendedStatements(String returnString)
	{
		if (appendedSQL.size() == 0)
		{
			return returnString;
		}

		StringBuilder returnStringBuilder = new StringBuilder(returnString);
		for (DFSQL statement : appendedSQL)
		{
			returnStringBuilder.append(" ").append(statement.formatted());
		}
		returnString = returnStringBuilder.toString();

		return returnString;
	}

	private String insertLimit(String returnString)
	{
		if (limitNum <= 0) { return returnString; }

		returnString += " LIMIT " + limitNum;

		return returnString;
	}

    /**
     * @return A human readable formatted SQL statement
     */
    private String formatted()
    {
    	String returnString = "";

    	//DELETE FROM Statements
    	if (!deleteFromTable.isEmpty())
	    {
	    	if (whereStatements.length != 1) { return returnString; }

	    	returnString += "DELETE FROM " + deleteFromTable;
	    	returnString = insertWhereAndOrderByStatements(returnString);
	    	returnString += ";";
	    	returnString = insertAppendedStatements(returnString);
	    	return returnString;
	    }

	    //UPDATE Statements
	    if (updateStatements.length != 0)
	    {
	    	if (fromTables.length == 0 || fromTables.length != 1)
		    {
		    	return returnString;
		    }

		    StringBuilder returnStringBuilder = new StringBuilder("UPDATE `" + fromTables[0] + "` SET ");
		    for (DFSQLClause clause : updateStatements)
		    {
		    	final String left = clause.attribute;
		    	String right = clause.value;

			    boolean isNum = false;
			    try
			    {
				    parseInt(right);
				    isNum = true;
			    }
			    catch (Exception ignored) { }
			    if (right.contains(" ") || !isNum)
			    {
				    right = "'" + right + "'";
			    }
			    returnStringBuilder.append(left).append("=").append(right).append(", ");
		    }
		    returnString = returnStringBuilder.toString();

		    returnString = returnString.substring(0, returnString.length() - 2);

		    returnString = insertWhereAndOrderByStatements(returnString);
	    	returnString += ";";
		    returnString = insertAppendedStatements(returnString);

	    	return returnString;
	    }

	    //INSERT Statements
	    if (insertRows.length != 0)
	    {
	    	if (fromTables.length == 0 || fromTables.length != 1) { return returnString; }

		    StringBuilder returnStringBuilder = new StringBuilder("INSERT INTO `" + fromTables[0] + "`(");
		    for (String row : insertRows)
		    {
		    	returnStringBuilder.append("`").append(row).append("`,");
		    }
		    returnString = returnStringBuilder.toString();

		    returnStringBuilder = new StringBuilder(returnString.substring(0, returnString.length() - 1) + ") VALUES (");
		    for (String value : insertValues)
		    {
			    boolean isNum = false;
			    try
			    {
				    parseInt(value);
				    isNum = true;
			    }
			    catch (Exception ignored) { }
			    if (value.contains(" ") || !isNum)
			    {
				    returnStringBuilder.append("'").append(value).append("',");
			    }
			    else
			    {
			    	returnStringBuilder.append(value).append(",");
			    }
		    }
		    returnString = returnStringBuilder.toString();

		    returnString = returnString.substring(0, returnString.length() - 1) + ")";

	    	if (duplicateKeys.length != 0)
		    {
		    	returnString += " ON DUPLICATE KEY UPDATE ";
		    	int count = 0;
			    returnStringBuilder = new StringBuilder(returnString);
			    for (String row : duplicateKeys)
			    {
			    	returnStringBuilder.append("`").append(row).append("`='").append(duplicateValues[count]).append("',");
			    	count++;
			    }
			    returnString = returnStringBuilder.toString();

			    returnString = returnString.substring(0, returnString.length() - 1);
		    }

		    returnString += ";";

		    returnString = insertAppendedStatements(returnString);

	    	return returnString;
	    }

	    //REST OF THE STUFF

	    if (selectRows.length == 0) return returnString;

    	returnString = "SELECT ";
    	if (distinctSelect)
	    {
	    	returnString += "DISTINCT ";
	    }
	    StringBuilder returnStringBuilder = new StringBuilder(returnString);
	    for (String row : selectRows)
	    {
	    	if (!row.contains("(") && !row.contains(")"))
		    {
		    	returnStringBuilder.append("`").append(row).append("`,");
		    }
		    else
		    {
		    	returnStringBuilder.append(row).append(",");
		    }
	    }
	    returnString = returnStringBuilder.toString();

	    returnStringBuilder = new StringBuilder(returnString.substring(0, returnString.length() - 1) + " FROM ");
	    for (String table : fromTables)
	    {
	    	returnStringBuilder.append("`").append(table).append("`,");
	    }
	    returnString = returnStringBuilder.toString();

	    returnString = returnString.substring(0, returnString.length() - 1);

    	if (joinStatements.length != 0)
	    {
		    returnStringBuilder = new StringBuilder(returnString);
		    for (InternalJoin join : joinStatements)
		    {
		    	final String left = join.clause.attribute;
		    	String right = join.clause.value;

			    boolean isNum = false;
			    try
			    {
				    parseInt(right);
				    isNum = true;
			    }
			    catch (Exception ignored) { }
			    if (right.contains(" ") || !isNum)
			    {
				    right = "'" + right + "',";
			    }

			    returnStringBuilder.append(join.joinType).append(" JOIN `").append(join.table).append(join.joinType != DFSQLJoin.natural ? ("` ON `" + left + "=" + right) : "`");
		    }
		    returnString = returnStringBuilder.toString();
	    }

	    returnString = insertWhereAndOrderByStatements(returnString);
	    returnString = insertLimit(returnString);
    	returnString += ";";
	    returnString = insertAppendedStatements(returnString);

		return returnString;
    }

    private void check(String attribute) throws DFSQLError
    {
        if (attribute.contains("*")) throw DFSQLError.cannotUseWildcardSpecifier;
        if (Objects.equals(attribute, "")) throw DFSQLError.cannotUseEmptyValue;

        String[] specifiers = new String[] {"=", "!=", "<", ">", " NATURAL", " OUTER", " CROSS", " INNER", "\"", "'", " LIKE", " NOT", " ASC", " DESC", "SELECT ", "FROM ", "JOIN ", "WHERE ", "ORDER BY", "IN ", "BETWEEN ", " AND", " OR"};

        for (String specifier : specifiers)
        {
            if (attribute.toUpperCase().contains(specifier)) throw DFSQLError.unexpectedValueFound;
        }

        if (!attribute.contains("."))
        {
            if (attribute.length() > 64)
            {
                throw DFSQLError.attributeLengthTooLong;
            }
        }
        else
        {
            String[] components = attribute.split(Pattern.quote("."));
	        String table = components[0];
	        String row = components[1];

	        if (table.contains("`"))
	        {
	        	if (table.length() > 66)
		        {
		        	throw DFSQLError.attributeLengthTooLong;
		        }
	        }
	        else
	        {
		        if (table.length() > 64)
		        {
			        throw DFSQLError.attributeLengthTooLong;
		        }
	        }
        }
    }

    private void check(String value, DFSQLEquivalence equivalence) throws DFSQLError
    {
        if (Objects.equals(value, "")) { throw DFSQLError.cannotUseEmptyValue; }

        String[] specifiers;
        if (equivalence != DFSQLEquivalence.between && equivalence != DFSQLEquivalence.notBetween)
        {
        	specifiers = new String[] {"=", "!=", "<", ">", " NATURAL", " OUTER", " CROSS", " INNER", ",", "\"", "'", " LIKE", " NOT", " ASC", " DESC", "SELECT ", "FROM ", "JOIN ", "WHERE ", "ORDER BY", "IN ", "BETWEEN ", " AND", " OR"};
        }
        else
        {
        	specifiers = new String[] {"=", "!=", "<", ">", " NATURAL", " OUTER", " CROSS", " INNER", ",", "\"", "'", " LIKE", " NOT", " ASC", " DESC", "SELECT ", "FROM ", "JOIN ", "WHERE ", "ORDER BY", "IN ", "BETWEEN ", " OR"};
        }

	    for (String specifier : specifiers)
	    {
		    if (value.toUpperCase().contains(specifier)) throw DFSQLError.unexpectedValueFound;
	    }
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
    * - Parameter attribute: the attribute to request
    * - Parameter distinct: if the query should return only distinct rows or not.  Defaults to `false`
    * - Parameter into: A table, if any, to copy(insert) this data into.  Defaults to `nil`
    * - Parameter in: An exterior database, if any, the `into` table resides.  A value of `nil` assumes the current working database.  Defaults to `nil`
    * - Returns: An instance of `MSSQL`
    * - Throws: `MSSQLError`: If no attribute specified, `*` is used, is empty, or is greater than 64 characters in length
    */
	public DFSQL select(String attribute, boolean distinct, String into, String in) throws DFSQLError
	{
		if (selectRows.length != 0)
		{
			throw DFSQLError.conditionAlreadyExists;
		}
		check(attribute);

		distinctSelect = distinct;
		selectRows = new String[] { attribute };

		in(into, in);

		return this;
	}

	/**
	 SELECT statement with multiple rows
	 - Parameter attributes: the attributes to request
	 - Parameter distinct: if the query should return only distinct rows or not.  Defaults to `false`
	 - Parameter into: A table, if any, to copy(insert) this data into.  Defaults to `nil`
	 - Parameter in: An exterior database, if any, the `into` table resides.  A value of `nil` assumes the current working database.  Defaults to `nil`
	 - Returns: An instance of `MSSQL`
	 - Throws: `MSSQLError`: If no attributes specified, `*` is used, is empty, or any attribute is greater than 64 characters in length
	 */
	public DFSQL select(String[] attributes, boolean distinct, String into, String in) throws DFSQLError
	{
		if (selectRows.length != 0)
		{
			throw DFSQLError.conditionAlreadyExists;
		}

		for (String attribute : attributes)
		{
			check(attribute);
		}

		distinctSelect = distinct;
		selectRows = attributes;

		in(into, in);

		return this;
	}

	private void in(String into, String in) throws DFSQLError
	{
		if (into != null)
		{
			if (!Objects.equals(intoTable, ""))
			{
				throw DFSQLError.conditionAlreadyExists;
			}
			check(into);
			intoTable = into;

			if (in != null)
			{
				if (!Objects.equals(inDB, ""))
				{
					throw DFSQLError.conditionAlreadyExists;
				}
				check(in);
				inDB = in;
			}
		}
	}

	//MARK: - FROM Constructors

	/**
	 FROM statement with one table
	 - Parameter table: the table to request
	 - Returns: An instance of `MSSQL`
	 - Throws: `MSSQLError` If no table specified, `*` is used, is empty, or table is greater than 64 characters in length
	 */
	public DFSQL from(String table) throws DFSQLError
	{
		if (fromTables.length != 0)
		{
			throw  DFSQLError.conditionAlreadyExists;
		}
		check(table);

		fromTables = new String[] { table };
		return this;
	}

	/**
	 FROM statement with multiple tables
	 - Parameter tables: the tables to request
	 - Returns: An instance of `DFSQL`
	 - Throws: `DFSQLError` If no tables specified, `*` is used, is empty, or if any table is greater than 64 characters in length
	 */
	public DFSQL from(String[] tables) throws DFSQLError
	{
		if (fromTables.length != 0)
		{
			throw  DFSQLError.conditionAlreadyExists;
		}
		for (String table : tables)
		{
			check(table);
		}

		fromTables = tables;
		return this;
	}

	//MARK: - UPDATE SET Constructors

	/**
	 UPDATE statement with one clause
	 - Parameter table: The table to request
	 - Parameter attribute: The left hand side of the clause
	 - Parameter value: The right hand side of the clause
	 - Returns: An instance of `DFSQL`
	 - Throws: `DFSQLError` If a parameter is nil, already exists, `*` is used, is empty, or the `table` | `attribute` is greater than 64 characters in length
	 */
	public DFSQL update(String table, String attribute, String value) throws DFSQLError
	{
		if (fromTables.length != 0 || updateStatements.length != 0)
		{
			throw DFSQLError.conditionAlreadyExists;
		}

		check(table);
		check(attribute);
		check(value, DFSQLEquivalence.lessThan);

		fromTables = new String[] { table };
		updateStatements = new DFSQLClause[] {new DFSQLClause(attribute, value)};

		return this;
	}

	/**
	 UPDATE statements with multiple clauses
	 - Parameter table: The table to request
	 - Parameter clauses: The clauses
	 - Returns: An instance of `MSSQL`
	 - Throws: `MSSQLError` If a parameter is nil, already exists, `*` is used, is empty, or the `table` | `attribute` of any clause is greater than 64 characters in length
	 */
	public DFSQL update(String table, DFSQLClause[] set) throws DFSQLError
	{
		if (fromTables.length != 0 || updateStatements.length != 0)
		{
			throw DFSQLError.conditionAlreadyExists;
		}

		check(table);
		for (DFSQLClause clause : set)
		{
			check(clause.attribute);
			check(clause.value, DFSQLEquivalence.lessThan);
		}

		fromTables = new String[] { table };
		updateStatements = set;

		return this;
	}

	//MARK: - INSERT INTO Constructor

	/**
	 INSERT INTO statement
	 - Parameter table: the table to insert the new row into
	 - Parameter values: the values for entry
	 - Parameter attributes: the attributes to set
	 - Returns: An instance of `MSSQL`
	 - Throws `MSSQLError` If a parameter is null, already exists, values and attributes do not match in size, `*` is used, is empty, or any attribute | table is greater than 64 characters in length
	 */
	public DFSQL insert(String table, String[] values, String[] attributes) throws DFSQLError
	{
		if (fromTables.length != 0 || insertRows.length != 0 || insertValues.length != 0)
		{
			throw DFSQLError.conditionAlreadyExists;
		}

		check(table);
		for (String attribute : attributes)
		{
			check(attribute);
		}

		for (String value : values)
		{
			check(value, DFSQLEquivalence.lessThan);
		}

		fromTables = new String[] { table };
		insertRows = attributes;
		insertValues = values;

		return this;
	}

	/**
	 INSERT ... ON DUPLICATE KEY UPDATE Constructor

	 Use this statement in conjuction with WHERE and/or LIMIT to specify which row to update if not 100% unique.

	 - Parameter attributes: The attributes to update
	 - Parameter values: The values to update to
	 - Returns: An instance of `MSSQL`
	 - Throws `MSSQLError` If a parameter is null, already exists, values and attributes do not match in size, `*` is used, is empty, or any attribute is greater than 64 characters in length
	 */
	public DFSQL onDuplicateKey(String[] attributes, String[] values) throws DFSQLError
	{
		if (insertRows.length == 0 || insertValues.length == 0 || duplicateKeys.length != 0 || duplicateValues.length != 0)
		{
			throw DFSQLError.conditionAlreadyExists;
		}

		for (String attribute : attributes)
		{
			check(attribute);
		}
		for (String value : values)
		{
			check(value, DFSQLEquivalence.lessThan);
		}

		duplicateKeys = attributes;
		duplicateValues = values;

		return this;
	}

	//MARK: - DELETE FROM Constructor

	public DFSQL delete(String from, Where where) throws DFSQLError
	{
		if (!Objects.equals(deleteFromTable, ""))
		{
			throw DFSQLError.conditionAlreadyExists;
		}

		check(where.clause.attribute);
		check(where.clause.value, DFSQLEquivalence.lessThan);

		deleteFromTable = from;
		whereStatements = new Where[] { where };

		return this;
	}

	public DFSQL delete(String from, Where[] where) throws DFSQLError
	{
		if (!Objects.equals(deleteFromTable, ""))
		{
			throw DFSQLError.conditionAlreadyExists;
		}

		for (Where where1: where)
		{
			check(where1.clause.attribute);
			check(where1.clause.value, DFSQLEquivalence.lessThan);
		}

		deleteFromTable = from;
		whereStatements = where;

		return this;
	}

	//MARK: - JOIN Constructors

	/**
	 JOIN statement convenience method
	 - Parameter table: the table to join on
	 - Parameter attribute: the left hand side of the clause
	 - Parameter value: the right hand side of the clause
	 - Returns: An instance of `MSSQL`
	 - Throws: `MSSQLError` If a parameter is null, already exists, `*` is used, is empty, or if the `table` | `attribute` is greater than 64 characters in length
	 */
	public DFSQL join(DFSQLJoin join, String table, String attribute, String value) throws DFSQLError
	{
		if (joinStatements.length != 0)
		{
			throw DFSQLError.conditionAlreadyExists;
		}

		check(table);
		check(attribute);
		check(value, DFSQLEquivalence.lessThan);

		joinStatements = new InternalJoin[] { new InternalJoin(join, table, new DFSQLClause(attribute, value)) };

		return this;
	}

	/**
	 JOIN statement convenience method
	 - Parameter joins: The joins to make
	 - Returns: An instance of `MSSQL`
	 - Throws: `MSSQLError` If a parameter is null, already exists, `*` is used, is empty, or if the `table` | `attribute` of any `Join` is greater than 64 characters in length
	 */
	public DFSQL join(DFSQLJoin join, Join[] joins) throws DFSQLError
	{
		if (joinStatements.length != 0)
		{
			throw DFSQLError.conditionAlreadyExists;
		}

		for (Join joinn : joins)
		{
			check(joinn.table);
			check(joinn.tableOneAttribute);
			check(joinn.tableTwoAttribute);
		}

		ArrayList<InternalJoin> joins1 = new ArrayList<>();

		for (Join joinnn : joins)
		{
			joins1.add(new InternalJoin(join, joinnn.table, new DFSQLClause(joinnn.tableOneAttribute, joinnn.tableTwoAttribute)));
		}

		joinStatements = (InternalJoin[])joins1.toArray();

		return this;
	}

	//MARK: - WHERE Constructors

	/**
	 WHERE ...X... statement
	 - Note: Please use `WhereStatementComparesNullString` as the value if you plan to use `.isNull` or `isNotNull` for the equivalence
	 - Parameter equivalence: The equivalence of the statement
	 - Parameter attribute: The left hand side of the clause
	 - Parameter value: The right hand side of the clause
	 - Returns: An instance of `MSSQL`
	 - Throws: `MSSQLError` If a parameter is null, already exists, `*` is used in an attribute, is empty, or the `attribute` is greater than 64 characters in length
	 */
	public DFSQL where(DFSQLEquivalence equivalence, String attribute, String value) throws DFSQLError
	{
		if (whereStatements.length != 0)
		{
			throw DFSQLError.conditionAlreadyExists;
		}
		if (Objects.equals(attribute, value))
		{
			throw DFSQLError.whereConditionsCannotBeEqual;
		}

		check(attribute);

		if (!Objects.equals(value, WhereStatementComparesNullString))
		{
			check(value, equivalence);
		}

		whereStatements = new Where[] { new Where(DFSQLConjunction.none, equivalence, new DFSQLClause(attribute, value)) };

		return this;
	}

	/**
	 WHERE ...X...[, ...X...] statement
	 - Note: Please use `WhereStatementComparesNullString` as the value if you plan to use `.isNull` or `isNotNull` for the equivalence
	 - Parameter equivalence: The equivalence of each statement
	 - Parameter attribute: The left hand side of the clause
	 - Parameter value: The right hand side of the clause
	 - Returns: An instance of `MSSQL`
	 - Throws: `MSSQLError` If a parameter is null, already exists, `*` is used in an attribute, is empty, or any `attribute` is greater than 64 characters in length
	 */
	public DFSQL where(DFSQLConjunction where, DFSQLEquivalence equivalence, String[] attributes, String[] values) throws DFSQLError
	{
		if (whereStatements.length != 0)
		{
			throw DFSQLError.conditionAlreadyExists;
		}
		if (attributes.length != values.length)
		{
			throw DFSQLError.conditionsMustBeEqual;
		}

		for (String attribute : attributes)
		{
			check(attribute);
		}
		for (String value : values)
		{
			if (!Objects.equals(value, WhereStatementComparesNullString))
			{
				check(value, equivalence);
			}
		}

		ArrayList<Where> wheres = new ArrayList<>();

		for (int i = 0; i < attributes.length - 1; i++)
		{
			String attribute = attributes[i];
			String value = values[i];
			if (Objects.equals(attribute, value))
			{
				throw DFSQLError.whereConditionsCannotBeEqual;
			}

			wheres.add(new Where(where, equivalence, new DFSQLClause(attribute, value)));
		}

		wheres.add(new Where(DFSQLConjunction.none, equivalence, new DFSQLClause(attributes[attributes.length - 1], values[attributes.length - 1])));

		whereStatements = (Where[])wheres.toArray();

		return this;
	}

	/**
	 WHERE ...X...[, ...Y...] statement
	 - Note: Please use `WhereStatementComparesNullString` as the value if you plan to use `.isNull` or `isNotNull` for the equivalence
	 - Parameter custom: A collection of `Where` structs.  The last `Where` struct **MUST** have `.none` as the conjunction
	 - Returns: An instance of `MSSQL`
	 - Throws: `MSSQLError` If a parameter is null, already exists, `*` is used as the `table` or `attribute` parameter of any `Where`, is empty, any `attribute` is greater than 64 characters in length, or if the last `Where` struct does not have `.none` as its conjunction
	 */
	public DFSQL where(Where[] custom) throws DFSQLError
	{
		if (custom.length == 0)
		{
			throw DFSQLError.cannotUseEmptyValue;
		}
		if (whereStatements.length != 0)
		{
			throw DFSQLError.conditionAlreadyExists;
		}

		for (Where where : custom)
		{
			check(where.clause.attribute);
			if (!Objects.equals(where.clause.value, WhereStatementComparesNullString))
			{
				check(where.clause.value, where.equivalence);
			}
			if (Objects.equals(where.clause.attribute, where.clause.value))
			{
				throw DFSQLError.whereConditionsCannotBeEqual;
			}
		}

		if (custom[custom.length - 1].conjunction != DFSQLConjunction.none)
		{
			throw DFSQLError.unexpectedValueFound;
		}

		whereStatements = custom;

		return this;
	}

	//MARK: - ORDER BY Constructors

	/**
	 ORDER BY ... ASC|DESC statement
	 - Parameter attribute: The attribute to order by
	 - Parameter direction: Order ascending or descending
	 - Returns: An instance of `MSSQL`
	 - Throws: `MSSQLError` If a parameter is null, already exists, `*` is used as the `attribute` parameter, is empty, the `attribute` is greater than 64 characters in length
	 */
	public DFSQL orderBy(String attribute, DFSQLOrderBy direction) throws DFSQLError
	{
		if (orderByStatements.length != 0)
		{
			throw DFSQLError.conditionAlreadyExists;
		}
		check(attribute);

		orderByStatements = new OrderBy[] { new OrderBy(attribute, direction) };

		return this;
	}

	/**
	 ORDER BY ... ASC|DESC[, ... ASC|DESC] statement
	 - Parameter attributes: The attributes and directions to order by
	 - Returns: An instance of `MSSQL`
	 - Throws: `MSSQLError` If a parameter is null, already exists, `*` is used as the `attribute` parameter of any `OrderBy`, is empty, the `attribute` of any `OrderBy` is greater than 64 characters in length
	 */
	public DFSQL orderBy(OrderBy[] attributes) throws DFSQLError
	{
		if (orderByStatements.length != 0)
		{
			throw DFSQLError.conditionAlreadyExists;
		}
		for (OrderBy att : attributes)
		{
			check(att.attribute);
		}

		orderByStatements = attributes;
		return this;
	}

	//MARK: - GROUP BY Constructor

	public DFSQL groupBy(String[] attributes) throws DFSQLError
	{
		if (groupByStatements.length != 0)
		{
			throw DFSQLError.conditionAlreadyExists;
		}
		for (String att : attributes)
		{
			check(att);
		}

		groupByStatements = attributes;
		return this;
	}

	//MARK: - LIMIT Constructor

	/**
	 LIMIT X statement

	 Can only be used with SELECT statements
	 - Parameter num: the limit of rows to return for display.  Must be greater than 0
	 - Throws: `MSSQLError` if `num` is less than 1
	 - Returns: An instance of `MSSQL`
	 */
	public DFSQL limit(int num) throws DFSQLError
	{
		if (limitNum != -1)
		{
			throw DFSQLError.conditionAlreadyExists;
		}
		if (num <= 0)
		{
			throw DFSQLError.unexpectedValueFound;
		}

		limitNum = num;
		return this;
	}
}
