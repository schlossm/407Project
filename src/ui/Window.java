package ui;

import ui.main.ABCTabBar;

import javax.swing.*;
import java.awt.*;

public class Window
{
	static Window current;

	private JFrame loginFrame;
	private JFrame mainScreen;

	public Window()
	{
		current = this;
		loginFrame = new JFrame("ABC");

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		loginFrame.setBounds(screenSize.width/10, screenSize.height/10, screenSize.width/5 * 4, screenSize.height/5 * 4);
		loginFrame.setBackground(Color.WHITE);
		loginFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		loginFrame.setUndecorated(true);

		Login loginPanel = new Login(loginFrame);
		loginFrame.add(loginPanel);

		loginFrame.setVisible(true);
	}

	void postLogin()
	{
		mainScreen = new JFrame("ABC");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		mainScreen.setBounds(0, 0, screenSize.width, screenSize.height);
		mainScreen.setBackground(Color.WHITE);
		mainScreen.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		mainScreen.add(new ABCTabBar());

		mainScreen.setVisible(true);
		loginFrame.setVisible(false);
	}

	static String convertToMultiline(String orig)
	{
		return "<html> <head> <style type=\"text/css\"> body { font-family: SF UI Text; font-size: 12px; text-align: center; } </style> </head> <body>" + orig.replaceAll("\n", "<br>") + "</body> </html>";
	}
}
