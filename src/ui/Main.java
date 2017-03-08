package ui;

import objects.Grade;
import ui.util.CurrentOS;
import ui.util.UIVariables;
import uikit.TimeManager;
import uikit.UIFont;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import static database.DFDatabase.queue;

class Bounds
{
	int x;
	int y;
	int width;
	int height;
}

class Main
{
	public static void main(String[] args)
	{
		try
		{
			if (UIVariables.current.currentOS == CurrentOS.macOS)
			{
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				System.setProperty("com.apple.mrj.application.apple.menu.about.name", "ABC");
			}
			if (Desktop.isDesktopSupported())
			{
				if (Desktop.getDesktop().isSupported(Desktop.Action.APP_ABOUT))
				{
					Desktop.getDesktop().setAboutHandler(e ->
					                                     {
						                                     Alert alert = new Alert("About", "ABC is a collaborative project designed to aid in school communications.");
						                                     alert.addButton("OK", ButtonType.defaultType, null, false);
						                                     alert.show(Window.current.mainScreen != null ? Window.current.mainScreen : Window.current.loginFrame);
					                                     });
				}
				if (Desktop.getDesktop().isSupported(Desktop.Action.APP_QUIT_HANDLER))
				{
					Desktop.getDesktop().setQuitHandler((e, response) -> UIVariables.current.writeFrame());
				}
			}
			if (Taskbar.isTaskbarSupported())
			{
				if (Taskbar.getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE))
				{
					Taskbar.getTaskbar().setIconImage(ImageIO.read(Main.class.getResourceAsStream("/uikit/images/abcicon.png")));
				}
			}
			if (SystemTray.isSupported())
			{
				SystemTray.getSystemTray().add(new TrayIcon(ImageIO.read(Main.class.getResourceAsStream("/uikit/images/abcicon.png"))));
			}

		}
		catch (Exception ignored) { }
		UIFont.loadIntoGE();
		new Window();

		new Thread(new TimeManager()).start();

		//MARK: - Test Data

		String filename = UIVariables.current.applicationDirectories.temp + File.separator + "test_grades.abc";

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

		while (true)
		{
			try
			{
				if (queue.size() != 0) { queue.take().run(); }
			}
			catch (InterruptedException e)
			{
				System.err.print("The application queue has encountered an error");
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
}
