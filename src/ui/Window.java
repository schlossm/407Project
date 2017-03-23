package ui;

import net.sf.plist.NSBoolean;
import net.sf.plist.NSString;
import ui.admin.AdminAnnouncements;
import ui.admin.AdminGrades;
import ui.admin.Group;
import ui.admin.ManageGroup;
import ui.util.ABCTabBar;
import ui.util.Bounds;
import ui.util.UIStrings;
import ui.util.UIVariables;
import uikit.DFNotificationCenter;
import uikit.DFNotificationCenterDelegate;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJFrame;
import uikit.autolayout.uiobjects.ALJPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class Window implements DFNotificationCenterDelegate, WindowFocusListener
{
	public static Window current;

	JFrame loginFrame;
	public ALJPanel activePanel;
	private ALJPanel container;
	private ABCTabBar tabBar;
	private Login loginPanel;
	public ALJFrame mainScreen;

	Window()
	{
		current = this;

		if (UIVariables.current.currentUser != null)
		{
			postLogin();
			return;
		}

		showLogin();
	}

	private void showLogin()
	{
		loginFrame = new JFrame("ABC");
		loginFrame.addWindowFocusListener(this);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = Integer.min(screenSize.width / 5 * 4, 1344);
		int height = Integer.min(screenSize.height / 5 * 4, 840);
		loginFrame.setBounds(width != 1344 ? screenSize.width / 10 : screenSize.width / 2 - 1344 / 2, height != 840 ? screenSize.height / 10 : screenSize.height / 2 - 420, width, height);
		loginFrame.setBackground(Color.WHITE);
		loginFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		loginFrame.setUndecorated(true);

		loginPanel = new Login(loginFrame);
		loginFrame.add(loginPanel);

		try
		{
			loginFrame.setIconImage(ImageIO.read(Main.class.getResourceAsStream("/uikit/images/abcicon.png")));
		}
		catch (Exception ignored) { }

		loginFrame.setVisible(true);
	}

	void postLogin()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Bounds bounds = new Bounds();
		bounds.x = 0;
		bounds.y = 0;
		bounds.width = screenSize.width;
		bounds.height = screenSize.height;
		try
		{
			if (UIVariables.current.valueFor("savesState").toBoolean())
			{
				bounds.x = UIVariables.current.valueFor("X").toInteger();
				bounds.y = UIVariables.current.valueFor("Y").toInteger();
				bounds.width = UIVariables.current.valueFor("W").toInteger();
				bounds.height = UIVariables.current.valueFor("H").toInteger();
			}
		}
		catch (Exception ignored) { }

		DFNotificationCenter.defaultCenter.register(this, UIStrings.ABCTabBarButtonClickedNotification);
		mainScreen = new ALJFrame("ABC - " + UIVariables.current.currentUser.getFirstName() + " " + UIVariables.current.currentUser.getLastName());
		mainScreen.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
		mainScreen.setBackground(Color.WHITE);
		mainScreen.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainScreen.addComponentListener(new ComponentListener()
		{
			private void writeFrame()
			{
				try
				{
					if (((NSBoolean) UIVariables.current.valueFor("savesState")).bool())
					{
						UIVariables.current.writeValue("X", new NSString(String.valueOf(mainScreen.getX())));
						UIVariables.current.writeValue("Y", new NSString(String.valueOf(mainScreen.getY())));
						UIVariables.current.writeValue("W", new NSString(String.valueOf(mainScreen.getWidth())));
						UIVariables.current.writeValue("H", new NSString(String.valueOf(mainScreen.getHeight())));
					}
				}
				catch (Exception ignored) { }
			}

			@Override
			public void componentResized(ComponentEvent e)
			{
				writeFrame();
			}

			@Override
			public void componentMoved(ComponentEvent e)
			{

			}

			@Override
			public void componentShown(ComponentEvent e)
			{
				writeFrame();
			}

			@Override
			public void componentHidden(ComponentEvent e)
			{

			}
		});

		mainScreen.getContentPane().setLayout(null);

		try
		{
			mainScreen.setIconImage(ImageIO.read(Main.class.getResourceAsStream("/uikit/images/abcicon.png")));
		}
		catch (Exception ignored) { }

		mainScreen.addWindowListener(new WindowListener()
		{
			@Override
			public void windowOpened(WindowEvent e)
			{ }

			@Override
			public void windowClosing(WindowEvent e)
			{
				try
				{
					if (((NSBoolean) UIVariables.current.valueFor("savesState")).bool())
					{
						UIVariables.current.writeValue("X", new NSString(String.valueOf(Window.current.mainScreen.getX())));
						UIVariables.current.writeValue("Y", new NSString(String.valueOf(Window.current.mainScreen.getY())));
						UIVariables.current.writeValue("W", new NSString(String.valueOf(Window.current.mainScreen.getWidth())));
						UIVariables.current.writeValue("H", new NSString(String.valueOf(Window.current.mainScreen.getHeight())));
						UIVariables.current.synchronize();
					}
				}
				catch (Exception ignored) { }
			}

			@Override
			public void windowClosed(WindowEvent e)
			{ }

			@Override
			public void windowIconified(WindowEvent e)
			{ }

			@Override
			public void windowDeiconified(WindowEvent e)
			{ }

			@Override
			public void windowActivated(WindowEvent e)
			{ }

			@Override
			public void windowDeactivated(WindowEvent e)
			{ }
		});

		container = new ALJPanel();
		container.setLayout(null);
		container.setPreferredSize(new Dimension(screenSize.width, screenSize.height));
		container.setBorder(new EmptyBorder(0, 0, 0, 0));

		tabBar = new ABCTabBar();

		activePanel = new Home();

		container.add(tabBar);
		container.add(activePanel);

		container.addConstraint(new LayoutConstraint(tabBar, LayoutAttribute.leading, LayoutRelation.equal, container, LayoutAttribute.leading, 1.0, 0));
		container.addConstraint(new LayoutConstraint(tabBar, LayoutAttribute.trailing, LayoutRelation.equal, container, LayoutAttribute.trailing, 1.0, 0));
		container.addConstraint(new LayoutConstraint(tabBar, LayoutAttribute.top, LayoutRelation.equal, container, LayoutAttribute.top, 1.0, 0));

		container.addConstraint(new LayoutConstraint(activePanel, LayoutAttribute.top, LayoutRelation.equal, tabBar, LayoutAttribute.bottom, 1.0, 0));
		container.addConstraint(new LayoutConstraint(activePanel, LayoutAttribute.trailing, LayoutRelation.equal, container, LayoutAttribute.trailing, 1.0, 0));
		container.addConstraint(new LayoutConstraint(activePanel, LayoutAttribute.leading, LayoutRelation.equal, container, LayoutAttribute.leading, 1.0, 0));
		container.addConstraint(new LayoutConstraint(activePanel, LayoutAttribute.bottom, LayoutRelation.equal, container, LayoutAttribute.bottom, 1.0, 0));

		mainScreen.getContentPane().add(container);

		mainScreen.setVisible(true);
		mainScreen.setMinimumSize(new Dimension(800, 600));
		DFNotificationCenter.defaultCenter.remove(loginPanel);
		if (loginPanel != null)
		{
			loginFrame.dispose();
		}
		loadMenuForLoggedInUser();
	}

	@Override
	public void performActionFor(String notificationName, Object userData)
	{
		if (!(userData instanceof String)) { return; }

		String buttonClicked = (String) userData;

		switch (buttonClicked)
		{
			case "Announcements":
			{
				switch (UIVariables.current.currentUser.getUserType())
				{
					case TA:
					{
						break;
					}

					case ADMIN:
					{
						if (activePanel instanceof AdminAnnouncements)
						{
							return;
						}
						container.remove(activePanel);
						activePanel = new AdminAnnouncements();
						break;
					}

					case TEACHER:
					{
						break;
					}

					default: return;
				}
				break;
			}
			case "Manage Teachers":
			{
				if (activePanel instanceof ManageGroup)
				{
					if (((ManageGroup) activePanel).currentGroup() == Group.teachers)
					{
						return;
					}
					container.remove(activePanel);
					activePanel = new ManageGroup(Group.teachers);
					break;
				}
				container.remove(activePanel);
				activePanel = new ManageGroup(Group.teachers);
				break;
			}

			case "Manage Students":
			{
				if (activePanel instanceof ManageGroup)
				{
					if (((ManageGroup) activePanel).currentGroup() == Group.students)
					{
						return;
					}
					container.remove(activePanel);
					activePanel = new ManageGroup(Group.students);
					break;
				}
				container.remove(activePanel);
				activePanel = new ManageGroup(Group.students);
				break;
			}

			case "Manage Courses":
			{
				if (activePanel instanceof ManageGroup)
				{
					if (((ManageGroup) activePanel).currentGroup() == Group.courses)
					{
						return;
					}
					container.remove(activePanel);
					activePanel = new ManageGroup(Group.courses);
					break;
				}
				container.remove(activePanel);
				activePanel = new ManageGroup(Group.courses);
				break;
			}

			case "Grades":
			{
				//TODO: Actually get usertype, but for now assume Admin

				if (activePanel instanceof AdminGrades)
				{
					return;
				}
				container.remove(activePanel);
				activePanel = new AdminGrades();
				break;
			}

			case "Home":
			{
				if (activePanel instanceof Home)
				{
					return;
				}
				container.remove(activePanel);
				activePanel = new Home();
				break;
			}

			case "Settings":
			{
				if (activePanel instanceof Settings)
				{
					return;
				}
				container.remove(activePanel);
				activePanel = new Settings();
				break;
			}

			default:
			{
				System.out.println(buttonClicked + " is an unsupported view at this time.  Please wait for its implementation.");
				return;
			}
		}

		container.add(activePanel);

		container.addConstraint(new LayoutConstraint(activePanel, LayoutAttribute.top, LayoutRelation.equal, tabBar, LayoutAttribute.bottom, 1.0, 0));
		container.addConstraint(new LayoutConstraint(activePanel, LayoutAttribute.trailing, LayoutRelation.equal, container, LayoutAttribute.trailing, 1.0, 0));
		container.addConstraint(new LayoutConstraint(activePanel, LayoutAttribute.leading, LayoutRelation.equal, container, LayoutAttribute.leading, 1.0, 0));
		container.addConstraint(new LayoutConstraint(activePanel, LayoutAttribute.bottom, LayoutRelation.equal, container, LayoutAttribute.bottom, 1.0, 0));

		container.layoutSubviews();
		activePanel.layoutSubviews();
		activePanel.repaint();
		activePanel.layoutSubviews();
	}

	private void loadMenuForLoggedInUser()
	{
		JMenuBar menuBar = new JMenuBar();

		//FILE

		JMenuItem logout = new JMenuItem("Log Out");
		logout.addActionListener(e ->
		                         {
			                         boolean success = new File(UIVariables.current.applicationDirectories.library + ".user.abc").delete();
			                         if (success)
			                         {
				                         mainScreen.dispose();
				                         showLogin();
			                         }
		                         });

		JMenu file = new JMenu("File");
		file.add(logout);

		//ADD EVERYTHING

		menuBar.add(file);
		mainScreen.setJMenuBar(menuBar);
	}

	@Override
	public void windowGainedFocus(WindowEvent e)
	{
		if (Taskbar.isTaskbarSupported() && Taskbar.getTaskbar().isSupported(Taskbar.Feature.USER_ATTENTION))
		{
			Taskbar.getTaskbar().requestUserAttention(false, false);
		}
	}

	@Override
	public void windowLostFocus(WindowEvent e)
	{

	}
}
