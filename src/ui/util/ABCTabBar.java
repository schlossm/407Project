package ui.util;

import uikit.DFNotificationCenter;
import uikit.UIFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ABCTabBar extends JPanel implements MLMDelegate, ComponentListener
{
	private JLabel firstName;
	private JLabel lastName;
	private ArrayList<JLabel> buttons = new ArrayList<>();

	public ABCTabBar()
	{
		addComponentListener(this);
		setBackground(Color.WHITE);

		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setBorder(new EmptyBorder(0, 20, 20, 20));

		firstName = new JLabel(UIVariables.current.currentUser.getFirstName(), JLabel.LEFT);
		firstName.setFont(UIFont.displayBlack.deriveFont(34f));
		firstName.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		firstName.setAlignmentX(Component.LEFT_ALIGNMENT);
		firstName.setFocusable(false);
		add(firstName);

		lastName = new JLabel(UIVariables.current.currentUser.getLastName(), JLabel.LEFT);
		lastName.setFont(UIFont.textRegular.deriveFont(22.0f));
		lastName.setBorder(BorderFactory.createEmptyBorder(26,0,0,0));
		lastName.setAlignmentX(Component.LEFT_ALIGNMENT);
		lastName.setVerticalTextPosition(JLabel.BOTTOM);
		lastName.setVerticalAlignment(JLabel.BOTTOM);
		lastName.setFocusable(false);
		add(lastName);

		addCorrectButtons();
	}

	private void addCorrectButtons()
	{
		String[] buttonTitles = new String[0];

		switch (UIVariables.current.currentUser.getUserType())
		{
			case ADMIN:
			{
				buttonTitles = new String[] {"Home", "Grades",/*.*/"My ABCDrive", "Announcements", "Manage Courses", "Manage Teachers", "Manage Students", "Help", "Settings"};
				break;
			}

			case STUDENT:
			{
				buttonTitles = new String[] {"Home", "My Courses",/*............*/"Announcements", "Grades",/*...........................................*/"Help", "Settings"};
				break;
			}

			case TEACHER:
			case TA:
			{
				buttonTitles = new String[] {"Home", "My Courses", "My ABCDrive",/*..............*/"Grades",/*...........................................*/"Help", "Settings"};
				break;
			}
		}


		for (String buttonTitle : buttonTitles)
		{
			JLabel buttonToAdd = new JLabel(buttonTitle);
			buttonToAdd.setMinimumSize(new Dimension(44,44));
			buttonToAdd.setFont(UIFont.textHeavy.deriveFont(9.0f));
			buttonToAdd.addMouseListener(new MouseListenerManager(this));
			buttonToAdd.setHorizontalTextPosition(JLabel.CENTER);
			buttonToAdd.setAlignmentY(CENTER_ALIGNMENT);
			buttons.add(buttonToAdd);
		}
		layoutButtons();
	}

	private void layoutButtons()
	{
		final int numberOfButtons = buttons.size();
		final int width = getBounds().width == 0 ? Integer.MAX_VALUE : getBounds().width;
		final int totalWidthToWorkWith = width - 40 - firstName.getPreferredSize().width - lastName.getPreferredSize().width - 40;
		int totalWidthOfAllButtons = 0;
		for (JLabel button: buttons)
		{
			totalWidthOfAllButtons += button.getPreferredSize().width;
		}

		final int leftoverWidth = Integer.max(totalWidthToWorkWith - totalWidthOfAllButtons, 0);
		final int spaceBetweenEachButton = leftoverWidth/numberOfButtons;
		if (spaceBetweenEachButton < 8)
		{
			for (JLabel button: buttons)
			{
				button.setFont(UIFont.textHeavy.deriveFont(button.getFont().getSize() - 1.0f));
			}
			layoutButtons();
			return;
		}

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
			if (action.getSource() != activeLabel || !activeLabel.isOpaque() || activeLabel == null) return;
			activeLabel.setOpaque(false);
			activeLabel.setBackground(new Color(0,0,0,0));
			DFNotificationCenter.defaultCenter.post(UIStrings.ABCTabBarButtonClickedNotification, activeLabel.getText());
			activeLabel = null;
		}
		else if (eventType == MLMEventType.draggedOut)
		{
			if (action.getSource() != activeLabel) return;
			activeLabel.setOpaque(false);
			activeLabel.setBackground(new Color(0,0,0,0));
		}
	}

	@Override
	public void componentResized(ComponentEvent e)
	{
		removeAll();

		revalidate();
		repaint();

		buttons = new ArrayList<>();
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setBorder(new EmptyBorder(0, 20, 20, 20));

		firstName = new JLabel(UIVariables.current.currentUser.getFirstName(), JLabel.LEFT);
		firstName.setFont(UIFont.displayBlack.deriveFont(34f));
		firstName.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		firstName.setAlignmentX(Component.LEFT_ALIGNMENT);
		firstName.setFocusable(false);
		add(firstName);

		lastName = new JLabel(UIVariables.current.currentUser.getLastName(), JLabel.LEFT);
		lastName.setFont(UIFont.textRegular.deriveFont(22.0f));
		lastName.setBorder(BorderFactory.createEmptyBorder(26,0,0,0));
		lastName.setAlignmentX(Component.LEFT_ALIGNMENT);
		lastName.setVerticalTextPosition(JLabel.BOTTOM);
		lastName.setVerticalAlignment(JLabel.BOTTOM);
		lastName.setFocusable(false);
		add(lastName);

		addCorrectButtons();

		revalidate();
		repaint();
	}

	@Override
	public void componentMoved(ComponentEvent e) { }

	@Override
	public void componentShown(ComponentEvent e) { }

	@Override
	public void componentHidden(ComponentEvent e) { }

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		g.setColor(Color.BLACK);
		g.drawRect(0, this.getHeight() - 1, this.getWidth(), 1);
	}
}


