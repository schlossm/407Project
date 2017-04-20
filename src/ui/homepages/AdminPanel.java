package ui.homepages;

import ui.util.UIStrings;
import uikit.DFNotificationCenter;
import uikit.DFNotificationCenterDelegate;
import uikit.UIFont;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class AdminPanel extends ALJPanel implements DFNotificationCenterDelegate
{
	private final JLabel numStudentsLabel;
	private final JLabel numTeachersLabel;
	private final JLabel numClassesLabel;
	private final JScrollPane scrollPane;

	public AdminPanel()
	{
		setBackground(Color.white);

		numStudentsLabel = new JLabel("Loading students...", JLabel.LEFT);
		numStudentsLabel.setFont(UIFont.displayLight.deriveFont(32f));
		numStudentsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		numStudentsLabel.setFocusable(false);
		add(numStudentsLabel);

		numTeachersLabel = new JLabel("Loading teachers...", JLabel.LEFT);
		numTeachersLabel.setFont(UIFont.displayLight.deriveFont(32f));
		numTeachersLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		numTeachersLabel.setFocusable(false);
		add(numTeachersLabel);

		numClassesLabel = new JLabel("Loading classes...", JLabel.LEFT);
		numClassesLabel.setFont(UIFont.displayLight.deriveFont(32f));
		numClassesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		numClassesLabel.setFocusable(false);
		add(numClassesLabel);

		addConstraint(new LayoutConstraint(numStudentsLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 20));
		addConstraint(new LayoutConstraint(numStudentsLabel, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 20));
		addConstraint(new LayoutConstraint(numStudentsLabel, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.centerX, 1.0, -1));

		addConstraint(new LayoutConstraint(numTeachersLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 20));
		addConstraint(new LayoutConstraint(numTeachersLabel, LayoutAttribute.top, LayoutRelation.equal, numStudentsLabel, LayoutAttribute.bottom, 1.0, 40));
		addConstraint(new LayoutConstraint(numTeachersLabel, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.centerX, 1.0, -1));

		addConstraint(new LayoutConstraint(numClassesLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 20));
		addConstraint(new LayoutConstraint(numClassesLabel, LayoutAttribute.top, LayoutRelation.equal, numTeachersLabel, LayoutAttribute.bottom, 1.0, 40));
		addConstraint(new LayoutConstraint(numClassesLabel, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.centerX, 1.0, -1));

		DFNotificationCenter.defaultCenter.register(this, UIStrings.oneHourHasPassedNotification);

		scrollPane = new JScrollPane();
		initializeAnnouncements();

		loadNumbers();
	}

	private void initializeAnnouncements()
	{
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		ScrollPaneLayout layout = (ScrollPaneLayout) (scrollPane.getLayout());
		layout.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		layout.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		scrollPane.getVerticalScrollBar().setVisible(false);
		scrollPane.setWheelScrollingEnabled(true);
		add(scrollPane);

		addConstraint(new LayoutConstraint(scrollPane, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.centerX, 1.0, 0));
		addConstraint(new LayoutConstraint(scrollPane, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 0));
		addConstraint(new LayoutConstraint(scrollPane, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, 0));
		addConstraint(new LayoutConstraint(scrollPane, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, 0));

		loadAnnouncements();
	}

	private void loadAnnouncements()
	{
		//FIXME: Actually load announcements
	}

	private void loadNumbers()
	{
		//FIXME: Actually load the number of students, teachers, and classes
		numStudentsLabel.setText("0 Students");
		numTeachersLabel.setText("0 Teachers");
		numClassesLabel.setText("0 Classes");
	}

	@Override
	public void performActionFor(String notificationName, Object userData)
	{
		if (Objects.equals(notificationName, UIStrings.oneHourHasPassedNotification))
		{
			loadNumbers();
			loadAnnouncements();
		}
	}
}
