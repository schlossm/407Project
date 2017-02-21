package database.DFSQL;

/**
 The possible comparators for `WHERE` statements
 */
public enum DFSQLEquivalence
{
    /**
     Single number comparator
     */
    equals("="), notEquals("!="), lessThan("<"), greaterThan(">"), lessThanOrEqualTo("<="), greaterThanOrEqualTo(">="),

    /**
     String comparator
     */
    like(" LIKE "), notLike(" NOT LIKE "),

    /**
     Number array comparator
    */
    between(" BETWEEN "), notBetween(" NOT BETWEEN "), in(" IN "), notIn(" NOT IN "),

    /**
     Value existence
     */
    isNull(" IS NULL "), isNotNull(" IS NOT NULL ");

    private final String text;

    DFSQLEquivalence(String text)
    {
        this.text = text;
    }

    @Override public String toString() { return text; }
}
