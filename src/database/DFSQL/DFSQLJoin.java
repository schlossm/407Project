package database.DFSQL;

/**
 The possible joins in SQL
 */
@SuppressWarnings("unused")
public enum DFSQLJoin
{
    full(" FULL OUTER"), natural(" NATURAL"), left(" LEFT OUTER"), right(" RIGHT OUTER"), cross(" CROSS"), inner(" INNER");

    private final String text;

    DFSQLJoin(final String text)
    {
        this.text = text;
    }

    @Override public String toString() { return text; }
}
