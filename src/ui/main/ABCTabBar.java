package ui.main;

import uikit.UIFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class ABCTabBar extends JPanel
{
	private JLabel firstName;
	private JLabel lastName;
	private ArrayList<JButton> buttons = new ArrayList<>();

	public ABCTabBar()
	{
		this.setBackground(Color.WHITE);
		this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		this.setBorder(new EmptyBorder(40, 40, 20, 40));

		//TODO: Replace with first name of user
		firstName = new JLabel("Michael", JLabel.LEFT);
		firstName.setFont(UIFont.displayBlack.deriveFont(38f));
		firstName.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		firstName.setAlignmentX(Component.LEFT_ALIGNMENT);
		firstName.setFocusable(false);
		this.add(firstName);

		//TODO: Replace with last name of user
		lastName = new JLabel("Schloss", JLabel.LEFT);
		lastName.setFont(UIFont.textRegular.deriveFont(24.0f));
		lastName.setBorder(BorderFactory.createEmptyBorder(26,0,0,0));
		lastName.setAlignmentX(Component.LEFT_ALIGNMENT);
		lastName.setVerticalTextPosition(JLabel.BOTTOM);
		lastName.setVerticalAlignment(JLabel.BOTTOM);
		lastName.setFocusable(false);
		this.add(lastName);
		addCorrectButtons();
	}

	private void addCorrectButtons()
	{
		String[] buttonTitles;

		//TODO: Get userType
		//TODO: switch userType

		//User is a  Student
		buttonTitles = new String[] {"Home", "My Courses",/*............*/"Announcements", "Grades",/*...........................................*/"Help", "Settings"};

		//User is a  Teacher
		buttonTitles = new String[] {"Home", "My Courses", "My ABCDrive",/*..............*/"Grades",/*...........................................*/"Help", "Settings"};

		//User is an Admin
		buttonTitles = new String[] {"Home",/*...........*/"My ABCDrive", "Announcements", "Manage Courses", "Manage Teachers", "Manage Students", "Help", "Settings"};
	}
}
