package ui;

import ui.util.ABCTabBar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

class Window
{
	static Window current;

	private JFrame loginFrame;
	private JFrame mainScreen;

	Window()
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
		mainScreen = new JFrame("ABC - Michael Schloss");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		mainScreen.setBounds(0, 0, screenSize.width, screenSize.height);
		mainScreen.setBackground(Color.WHITE);
		mainScreen.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		layout.setHgap(0);
		layout.setVgap(0);

		JPanel container = new JPanel();
		container.setLayout(layout);
		container.setPreferredSize(new Dimension(screenSize.width, screenSize.height));
		container.setBorder(new EmptyBorder(0,0,0,0));

		ABCTabBar tabBar = new ABCTabBar();

		Home home = new Home();
		home.setPreferredSize(new Dimension(screenSize.width, screenSize.height - tabBar.getPreferredSize().height));
		home.setMinimumSize(new Dimension(600, 600));

		container.add(tabBar);
		container.add(home);
		mainScreen.add(container);

		mainScreen.setVisible(true);
		loginFrame.dispose();
	}
}
