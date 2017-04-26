package ui;

import database.DFDatabase;
import ui.util.Alert;
import ui.util.ButtonType;
import ui.util.CurrentOS;
import ui.util.UIVariables;
import uikit.TimeManager;
import uikit.UIFont;

import javax.imageio.ImageIO;
import java.awt.*;

//Loader
class Main
{
	public static void main(String[] args)
	{
		DFDatabase.defaultDatabase.enableDebug();
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
						                                     alert.addButton("OK", ButtonType.defaultType, null);
						                                     alert.show(Window.current.mainScreen != null ? Window.current.mainScreen : Window.current.loginFrame);
					                                     });
				}
				if (Desktop.getDesktop().isSupported(Desktop.Action.APP_QUIT_HANDLER))
				{
					Desktop.getDesktop().setQuitHandler((e, response) ->
					                                    {
						                                    UIVariables.current.synchronize();
						                                    System.exit(0);
					                                    });
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
	}
}
