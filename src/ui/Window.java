package ui;

import ui.util.ABCTabBar;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJFrame;
import uikit.autolayout.uiobjects.ALJPanel;

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
		//TODO: Replace with user's actual name
		mainScreen = new ALJFrame("ABC - Michael Schloss");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		mainScreen.setBounds(0, 0, screenSize.width, screenSize.height);
		mainScreen.setBackground(Color.WHITE);
		mainScreen.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		mainScreen.getContentPane().setLayout(null);

		ALJPanel container = new ALJPanel();
		container.setLayout(null);
		container.setPreferredSize(new Dimension(screenSize.width, screenSize.height));
		container.setBorder(new EmptyBorder(0,0,0,0));

		ABCTabBar tabBar = new ABCTabBar();

		Home home = new Home();

		container.add(tabBar);
		container.add(home);

		container.addConstraint(new LayoutConstraint(tabBar, LayoutAttribute.leading,   LayoutRelation.equal, container,    LayoutAttribute.leading,    1.0, 0));
		container.addConstraint(new LayoutConstraint(tabBar, LayoutAttribute.trailing,  LayoutRelation.equal, container,    LayoutAttribute.trailing,   1.0, 0));
		container.addConstraint(new LayoutConstraint(tabBar, LayoutAttribute.top,       LayoutRelation.equal, container,    LayoutAttribute.top,        1.0, 0));

		container.addConstraint(new LayoutConstraint(home, LayoutAttribute.top,         LayoutRelation.equal, tabBar,       LayoutAttribute.bottom,     1.0, 0));
		container.addConstraint(new LayoutConstraint(home, LayoutAttribute.trailing,    LayoutRelation.equal, container,    LayoutAttribute.trailing,   1.0, 0));
		container.addConstraint(new LayoutConstraint(home, LayoutAttribute.leading,     LayoutRelation.equal, container,    LayoutAttribute.leading,    1.0, 0));
		container.addConstraint(new LayoutConstraint(home, LayoutAttribute.bottom,      LayoutRelation.equal, container,    LayoutAttribute.bottom,     1.0, 0));

		mainScreen.getContentPane().add(container);

		mainScreen.setVisible(true);
		loginFrame.dispose();
	}
}
