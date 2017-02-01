package uikit.autolayout;

/**
 * Created by michaelschloss on 1/26/17.
 */
public enum LayoutRelation
{
	lessThanOrEqual, equal, greaterThanOrEqual;

	@Override
	public String toString()
	{
		if (this == lessThanOrEqual)
		{
			return "<=";
		}
		else if (this == equal)
		{
			return "==";
		}
		else if (this == greaterThanOrEqual)
		{
			return ">=";
		}
		return "NULL";
	}
}
