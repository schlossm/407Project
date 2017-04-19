package ui.util;

import uikit.DFNotificationCenter;
import uikit.UIFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
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
	private JLabel activeLabel;
	private int activePos = -1;
	private JLabel selectedLabel;
	private boolean isPressingDown = false;
	public ABCTabBar()
	{
		addComponentListener(this);
		setBackground(Color.WHITE);

		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setBorder(new EmptyBorder(0, 20, 20, 20));

		firstName = new JLabel(UIVariables.current.currentUser.getFirstName(), JLabel.LEFT);
		firstName.setFont(UIFont.displayBlack.deriveFont(34f));
		firstName.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		firstName.setAlignmentX(Component.LEFT_ALIGNMENT);
		firstName.setFocusable(false);
		add(firstName);

		lastName = new JLabel(UIVariables.current.currentUser.getLastName(), JLabel.LEFT);
		lastName.setFont(UIFont.textRegular.deriveFont(22.0f));
		lastName.setBorder(BorderFactory.createEmptyBorder(26, 0, 0, 0));
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
				buttonTitles = new String[]{"Home", "Grades",/*.*/"My ABCDrive", "Announcements", "Manage Courses", "Manage Teachers", "Manage Students", "Help", "Settings"};
				break;
			}

			case STUDENT:
			{
				buttonTitles = new String[]{"Home", "My Courses",/*.............................*/ "Grades",/*...........................................*/"Help", "Settings"};
				break;
			}

			case TEACHER:
			case TA:
			{
				buttonTitles = new String[]{"Home", "My Courses", "My ABCDrive",/*..............*/"Grades",/*...........................................*/"Help", "Settings"};
				break;
			}
		}

		for (String buttonTitle : buttonTitles)
		{
			JLabel buttonToAdd = new JLabel(buttonTitle);
			buttonToAdd.setMinimumSize(new Dimension(44, 44));
			buttonToAdd.setFont(UIFont.textHeavy.deriveFont(9.0f));
			buttonToAdd.addMouseListener(new MouseListenerManager(this));
			buttonToAdd.setHorizontalTextPosition(JLabel.CENTER);
			buttonToAdd.setAlignmentY(CENTER_ALIGNMENT);
			buttonToAdd.setBorder(new LineBorder(Color.white, 1, true));
			buttons.add(buttonToAdd);
		}
		layoutButtons();

		if (activeLabel == null || activePos == -1)
		{
			buttons.get(0).setBorder(new LineBorder(Color.black, 1, true));
			activeLabel = buttons.get(0);
			activePos = 0;
		}
		else
		{
			activeLabel = buttons.get(activePos);
			activeLabel.setBorder(new LineBorder(Color.black, 1, true));
		}
	}

	private void layoutButtons()
	{
		final int numberOfButtons = buttons.size();
		final int width = getBounds().width == 0 ? Integer.MAX_VALUE : getBounds().width;
		final int totalWidthToWorkWith = width - 40 - firstName.getPreferredSize().width - lastName.getPreferredSize().width - 40;
		int totalWidthOfAllButtons = 0;
		for (JLabel button : buttons)
		{
			totalWidthOfAllButtons += button.getPreferredSize().width;
		}

		final int leftoverWidth = Integer.max(totalWidthToWorkWith - totalWidthOfAllButtons, 0);
		final int spaceBetweenEachButton = leftoverWidth / numberOfButtons;
		if (spaceBetweenEachButton < 8)
		{
			for (JLabel button : buttons)
			{
				button.setFont(UIFont.textHeavy.deriveFont(button.getFont().getSize() - 1.0f));
			}
			layoutButtons();
			return;
		}

		for (JLabel button : buttons)
		{
			add(Box.createRigidArea(new Dimension(spaceBetweenEachButton, 0)));
			add(button);
		}
	}

	@Override
	public void mousePoint(MouseEvent action, MLMEventType eventType)
	{
		if (eventType == MLMEventType.pressed)
		{
			if (activeLabel != null)
			{
				activeLabel.setBorder(new LineBorder(Color.white, 1, true));
			}
			isPressingDown = true;
			selectedLabel = (JLabel) action.getSource();
			selectedLabel.setBorder(new LineBorder(Color.black, 1, true));
		}
		else if (eventType == MLMEventType.draggedIn && isPressingDown)
		{
			if (action.getSource() != selectedLabel) { return; }
			selectedLabel.setBorder(new LineBorder(Color.black, 1, true));
		}
		else if (eventType == MLMEventType.released && isPressingDown)
		{
			isPressingDown = false;
			if (!(new Rectangle(0, 0, selectedLabel.getWidth(), selectedLabel.getHeight()).contains(action.getPoint())))
			{
				activeLabel.setBorder(new LineBorder(Color.black, 1, true));
				return;
			}
			activeLabel = selectedLabel;
			activePos = buttons.indexOf(selectedLabel);
			DFNotificationCenter.defaultCenter.post(UIStrings.ABCTabBarButtonClickedNotification, selectedLabel.getText());
			selectedLabel = null;
		}
		else if (eventType == MLMEventType.draggedOut && isPressingDown)
		{
			if (action.getSource() != selectedLabel) { return; }
			selectedLabel.setBorder(new LineBorder(Color.white, 1, true));
		}
	}

	@Override
	public void componentResized(ComponentEvent e)
	{
		removeAll();

		buttons = new ArrayList<>();
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setBorder(new EmptyBorder(0, 20, 20, 20));

		firstName = new JLabel(UIVariables.current.currentUser.getFirstName(), JLabel.LEFT);
		firstName.setFont(UIFont.displayBlack.deriveFont(34f));
		firstName.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		firstName.setAlignmentX(Component.LEFT_ALIGNMENT);
		firstName.setFocusable(false);
		add(firstName);

		lastName = new JLabel(UIVariables.current.currentUser.getLastName(), JLabel.LEFT);
		lastName.setFont(UIFont.textRegular.deriveFont(22.0f));
		lastName.setBorder(BorderFactory.createEmptyBorder(26, 0, 0, 0));
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


