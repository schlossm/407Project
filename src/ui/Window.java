package ui;

import ui.admin.AdminGrades;
import ui.admin.Group;
import ui.admin.ManageGroup;
import ui.util.ABCTabBar;
import ui.util.UIStrings;
import uikit.DFNotificationCenter;
import uikit.DFNotificationCenterDelegate;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJFrame;
import uikit.autolayout.uiobjects.ALJPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

class Window implements DFNotificationCenterDelegate
{
	static Window current;

	private JFrame loginFrame;
	private ALJPanel activePanel;
	private ALJPanel container;
	private ABCTabBar tabBar;

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

		DFNotificationCenter.defaultCenter.register(this, UIStrings.ABCTabBarButtonClickedNotification);
		//TODO: Replace with user's actual name
		ALJFrame mainScreen = new ALJFrame("ABC - Michael Schloss");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		mainScreen.setBounds(0, 0, screenSize.width, screenSize.height);
		mainScreen.setBackground(Color.WHITE);
		mainScreen.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		mainScreen.getContentPane().setLayout(null);

		container = new ALJPanel();
		container.setLayout(null);
		container.setPreferredSize(new Dimension(screenSize.width, screenSize.height));
		container.setBorder(new EmptyBorder(0,0,0,0));

		tabBar = new ABCTabBar();

		activePanel = new Home();

		container.add(tabBar);
		container.add(activePanel);

		container.addConstraint(new LayoutConstraint(tabBar, LayoutAttribute.leading,   LayoutRelation.equal, container,    LayoutAttribute.leading,    1.0, 0));
		container.addConstraint(new LayoutConstraint(tabBar, LayoutAttribute.trailing,  LayoutRelation.equal, container,    LayoutAttribute.trailing,   1.0, 0));
		container.addConstraint(new LayoutConstraint(tabBar, LayoutAttribute.top,       LayoutRelation.equal, container,    LayoutAttribute.top,        1.0, 0));

		container.addConstraint(new LayoutConstraint(activePanel, LayoutAttribute.top,         LayoutRelation.equal, tabBar,       LayoutAttribute.bottom,     1.0, 0));
		container.addConstraint(new LayoutConstraint(activePanel, LayoutAttribute.trailing,    LayoutRelation.equal, container,    LayoutAttribute.trailing,   1.0, 0));
		container.addConstraint(new LayoutConstraint(activePanel, LayoutAttribute.leading,     LayoutRelation.equal, container,    LayoutAttribute.leading,    1.0, 0));
		container.addConstraint(new LayoutConstraint(activePanel, LayoutAttribute.bottom,      LayoutRelation.equal, container,    LayoutAttribute.bottom,     1.0, 0));

		mainScreen.getContentPane().add(container);

		mainScreen.setVisible(true);
		loginFrame.dispose();
	}

	@Override
	public void performActionFor(String notificationName, Object userData)
	{
		if (!(userData instanceof String)) return;

		String buttonClicked = (String)userData;

		switch (buttonClicked)
		{
			case "Manage Teachers":
			{
				if (activePanel instanceof ManageGroup)
				{
					if (((ManageGroup)activePanel).currentGroup() == Group.teachers)
					{
						return;
					}
					else
					{
						container.remove(activePanel);

						activePanel = new ManageGroup(Group.teachers);
					}
				}
				else
				{
					container.remove(activePanel);

					activePanel = new ManageGroup(Group.teachers);
				}
				break;
			}

			case "Manage Students":
			{
				if (activePanel instanceof ManageGroup)
				{
					if (((ManageGroup)activePanel).currentGroup() == Group.students)
					{
						return;
					}
					else
					{
						container.remove(activePanel);

						activePanel = new ManageGroup(Group.students);
					}
				}
				else
				{
					container.remove(activePanel);

					activePanel = new ManageGroup(Group.students);
				}
				break;
			}

			case "Manage Courses":
			{
				if (activePanel instanceof ManageGroup)
				{
					if (((ManageGroup)activePanel).currentGroup() == Group.courses)
					{
						return;
					}
					else
					{
						container.remove(activePanel);

						activePanel = new ManageGroup(Group.courses);
					}
				}
				else
				{
					container.remove(activePanel);

					activePanel = new ManageGroup(Group.courses);
				}
				break;
			}

			case "Grades":
			{
				//TODO: Actually get usertype, but for now assume Admin

				if (activePanel instanceof AdminGrades)
				{
					return;
				}
				else
				{
					container.remove(activePanel);

					activePanel = new AdminGrades();
				}

				break;
			}

			case "Home":
			{
				if (activePanel instanceof Home)
				{
					return;
				}
				else
				{
					container.remove(activePanel);

					activePanel = new Home();
				}
				break;
			}

			default: break;
		}

		container.add(activePanel);

		container.addConstraint(new LayoutConstraint(activePanel, LayoutAttribute.top,         LayoutRelation.equal, tabBar,       LayoutAttribute.bottom,     1.0, 0));
		container.addConstraint(new LayoutConstraint(activePanel, LayoutAttribute.trailing,    LayoutRelation.equal, container,    LayoutAttribute.trailing,   1.0, 0));
		container.addConstraint(new LayoutConstraint(activePanel, LayoutAttribute.leading,     LayoutRelation.equal, container,    LayoutAttribute.leading,    1.0, 0));
		container.addConstraint(new LayoutConstraint(activePanel, LayoutAttribute.bottom,      LayoutRelation.equal, container,    LayoutAttribute.bottom,     1.0, 0));

		container.layoutSubviews();
		activePanel.layoutSubviews();
		activePanel.repaint();
		activePanel.layoutSubviews();
	}
}
