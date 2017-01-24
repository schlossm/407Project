package ui.main;

import ui.MLMDelegate;
import ui.MLMEventType;
import ui.MouseListenerManager;
import uikit.UIFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ABCTabBar extends JPanel implements MLMDelegate
{
	private JLabel firstName;
	private JLabel lastName;
	private ArrayList<JLabel> buttons = new ArrayList<>();

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

		for (String buttonTitle : buttonTitles)
		{
			JLabel buttonToAdd = new JLabel(buttonTitle);
			buttonToAdd.setMinimumSize(new Dimension(44,44));
			buttonToAdd.setFont(UIFont.textHeavy.deriveFont(9.0f));
			buttonToAdd.addMouseListener(new MouseListenerManager(this));
			buttonToAdd.setHorizontalTextPosition(JLabel.CENTER);
			buttons.add(buttonToAdd);
		}
		layoutButtons();
	}

	private void layoutButtons()
	{
		final int numberOfButtons = buttons.size();
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final int totalWidthToWorkWith = screenSize.width - 40 - firstName.getPreferredSize().width - lastName.getPreferredSize().width - 40;
		int totalWidthOfAllButtons = 0;
		for (JLabel button: buttons)
		{
			totalWidthOfAllButtons += button.getPreferredSize().width;
		}

		final int leftoverWidth = Integer.max(totalWidthToWorkWith - totalWidthOfAllButtons, 0);
		if (leftoverWidth < buttons.size())
		{
			for (JLabel button: buttons)
			{
				button.setFont(button.getFont().deriveFont(button.getFont().getSize() - 1));
			}
			layoutButtons();
			return;
		}

		final int spaceBetweenEachButton = leftoverWidth/buttons.size();

		for (JLabel button: buttons)
		{
			add(Box.createRigidArea(new Dimension(spaceBetweenEachButton, 0)));
			add(button);
		}
	}

	private JLabel activeLabel;

	@Override
	public void mousePoint(MouseEvent action, MLMEventType eventType)
	{
		if (eventType == MLMEventType.pressed)
		{
			activeLabel = (JLabel)action.getSource();
			activeLabel.setOpaque(true);
			activeLabel.setBackground(Color.lightGray);
		}
		else if (eventType == MLMEventType.draggedIn)
		{
			if (action.getSource() != activeLabel) return;
			activeLabel.setOpaque(true);
			activeLabel.setBackground(Color.lightGray);
		}
		else if (eventType == MLMEventType.released)
		{

			if (action.getSource() != activeLabel || !activeLabel.isOpaque()) return;
			activeLabel.setOpaque(false);
			activeLabel.setBackground(new Color(0,0,0,0));
			activeLabel = null;
			//TODO: Process Click
		}
		else if (eventType == MLMEventType.draggedOut)
		{
			if (action.getSource() != activeLabel) return;
			activeLabel.setOpaque(false);
			activeLabel.setBackground(new Color(0,0,0,0));
		}
	}
}


