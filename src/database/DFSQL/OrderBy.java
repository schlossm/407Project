package database.DFSQL;

/**
 Defines an `ORDERED BY` statement
 */
public class OrderBy
{
    final String attribute;

    final DFSQLOrderBy orderBy;

    public OrderBy(String attribute, DFSQLOrderBy orderedBy)
    {
        this.attribute = attribute;
        orderBy = orderedBy;
    }
}
