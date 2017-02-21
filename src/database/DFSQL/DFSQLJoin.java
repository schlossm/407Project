package database.DFSQL;

/**
 The possible joins in SQL
 */
public enum DFSQLJoin
{
    full(" FULL OUTER"), natural(" NATURAL"), left(" LEFT OUTER"), right(" RIGHT OUTER"), cross(" CROSS"), inner(" INNER");

    private final String text;

    private DFSQLJoin(final String text)
    {
        this.text = text;
    }

    @Override public String toString() { return text; }
}
