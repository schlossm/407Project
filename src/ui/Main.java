package ui;

import objects.Grade;
import ui.util.CurrentOS;
import ui.util.UIVariables;
import uikit.TimeManager;
import uikit.UIFont;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import static database.DFDatabase.queue;

public class Main
{
	public static void main(String[] args)
	{
		try
		{
			if (UIVariables.current.currentOS == CurrentOS.macOS)
			{
				System.setProperty("apple.laf.useScreenMenuBar", "true");
			}
		}
		catch (Exception ignored) { }
		UIFont.loadIntoGE();
		new Window();

		new Thread(new TimeManager()).start();

		/*
		 * Write random Grades to the cache for UI testing
		 * (be careful because userID and assignmentID will not
		 * point to legitimate database entries)
		 */

		// Create 10 Grade objects and write them to a *.ser cache file
		String filename = UIVariables.current.applicationDirectories.temp + File.separator + "test_grades.ser";

		try
		{
			FileOutputStream fileOut = new FileOutputStream(filename);
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);

			for (int i = 0; i < 10; ++i)
			{
				Grade g = new Grade("0", 0, Integer.toString((i + 1) * 10));

				objOut.writeObject(g);
			}

			objOut.close();
			fileOut.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		/*
		 * End test code
		 */

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
