package ui;

import uikit.TimeManager;
import uikit.UIFont;

import static database.DFDatabase.queue;

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

		new Thread(new TimeManager()).start();

		while(true)
		{
			try
			{
				queue.take().run();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
}
