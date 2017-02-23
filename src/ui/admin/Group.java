package ui.admin;

public enum Group
{
	teachers("Teacher"), students("Student"), courses("Course"), none("None");

	private final String text;

	Group(String text)
	{
		this.text = text;
	}

	@Override
	public String toString()
	{
		return text;
	}
}
