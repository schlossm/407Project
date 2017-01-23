package ui;

import uikit.UIFont;

public class Main
{
	public static void main(String[] args)
	{
		try
		{
			if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("mac"))
			{
				System.setProperty("apple.laf.useScreenMenuBar", "true");
			}
		}
		catch (Exception ignored) { }
		UIFont.loadIntoGE();
		new Window();
	}
}
