package database.DFSQL;

/**
 The possible directions an attribute can be ordered
 */
public enum DFSQLOrderBy
{
    /**
     Order direction
     */
    ascending(" ASC"), descending(" DESC");

    private final String text;

    DFSQLOrderBy(final String text)
    {
        this.text = text;
    }

    @Override public String toString() { return text; }
}
