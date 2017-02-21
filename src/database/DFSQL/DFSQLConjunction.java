package database.DFSQL;

/**
 The possible conjunctions for `WHERE` statements

 - Note: `.none` does not insert " NONE" into the SQL Statement, it is used as a placeholder
 */
public enum DFSQLConjunction
{
    and(" AND"), or(" OR"), none(" NONE"), andNot(" AND NOT"), orNot(" OR NOT");

    private final String text;

    DFSQLConjunction(String text)
    {
        this.text = text;
    }

    @Override public String toString() { return text; }
}

