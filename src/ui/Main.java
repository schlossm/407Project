package ui;

import uikit.UIFont;

/**
 * Created by michaelschloss on 1/23/17.
 */
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
