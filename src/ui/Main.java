package ui;

import objects.Course;
import objects.Grade;
import ui.util.Alert;
import ui.util.ButtonType;
import ui.util.CurrentOS;
import ui.util.UIVariables;
import uikit.TimeManager;
import uikit.UIFont;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import static database.DFDatabase.queue;

//Bounds class for frame saving
class Bounds
{
	int x;
	int y;
	int width;
	int height;
}

//Loader
class Main
{
	public static void main(String[] args)
	{
		//Initialize various platform specific items
		try
		{
			//Apple stuff
			if (UIVariables.current.currentOS == CurrentOS.macOS)
			{
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				System.setProperty("com.apple.mrj.application.apple.menu.about.name", "ABC");
			}
			else
			{
				//System Tray Stuff -- App icon
				//NOTE: Only for Windows and some Linux
				if (SystemTray.isSupported())
				{
					SystemTray.getSystemTray().add(new TrayIcon(ImageIO.read(Main.class.getResourceAsStream("/uikit/images/abcicon.png"))));
				}
			}

			//Desktop Stuff -- About handler and Quit handler
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
					Desktop.getDesktop().setQuitHandler((e, response) -> { UIVariables.current.synchronize(); System.exit(0); });
				}
			}

			//Taskbar stuff -- App icon
			if (Taskbar.isTaskbarSupported())
			{
				if (Taskbar.getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE))
				{
					Taskbar.getTaskbar().setIconImage(ImageIO.read(Main.class.getResourceAsStream("/uikit/images/abcicon.png")));
				}
			}
		}
		catch (Exception ignored) { }

		//Load Fonts
		UIFont.loadIntoGE();

		//Make the window
		new Window();

		//Start the background Time Manager
		new Thread(new TimeManager()).start();

		// Set up (and print) test static Course object
		ArrayList<objects.User> students = new ArrayList<objects.User>();
		ArrayList<objects.User> teachers = new ArrayList<objects.User>();

		Course.testCourse.setCourseID(384053);
		Course.testCourse.setTitle("Software Engineering Project");
		Course.testCourse.setCourseName("CS 407");
		Course.testCourse.setDescription("The course we are writing this code for. How meta of me.");
		Course.testCourse.setMeetingTime("[Time format TBD]");
		Course.testCourse.setAttendanceString("I_AM_HERE!");
		Course.testCourse.setStartDate("[Start Date in TBD format]");
		Course.testCourse.setEndDate("[End Date in TBD format]");
		Course.testCourse.setMeetingTime("[Meeting Time in TBD format]");
		Course.testCourse.setRoomNo("LWSN 1168");
		Course.testCourse.setMaxStorage(1000000);
		Course.testCourse.setStudents(students);
		Course.testCourse.setTeachers(teachers);

		System.out.println(Course.testCourse.toString());

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

		//Will run, on the main thread, any object put into the queue stack.
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
