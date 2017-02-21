package ui;

import ui.util.CurrentOS;
import ui.util.UIVariables;
import uikit.TimeManager;
import uikit.UIFont;

import static database.DFDatabase.queue;

import objects.Grade;

import java.io.*;
import java.util.ArrayList;

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
		String filename = "cache" + File.separator + "test_grades.ser";

		try {
			FileOutputStream fileOut = new FileOutputStream(filename);
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);

			for (int i = 0; i < 10; ++i) {
				Grade g = new Grade("0", 0, Integer.toString((i + 1) * 10));
				System.out.println(g.getScore()); // print scores in order they are written

				objOut.writeObject(g);
			}

			objOut.close();
			fileOut.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		// Read the 10 Grade objects back into memory
		try {
			ArrayList<Grade> grades = new ArrayList<Grade>();
			FileInputStream fileIn = new FileInputStream(filename);
			ObjectInputStream objIn = new ObjectInputStream(fileIn);

			for (int i = 0; i < 10; ++i) {
				Grade g = (Grade) objIn.readObject();
				grades.add(g);

				System.out.println(g.getScore()); // print scores in order they are read
			}

			objIn.close();
			fileIn.close();
		}

		catch (Exception e) {
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
