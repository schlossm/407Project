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
import java.awt.*;
import java.util.Objects;

public class AdminPanel extends ALJPanel implements DFNotificationCenterDelegate
{
	private JLabel numStudentsLabel, numTeachersLabel, numClassesLabel;

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

		addConstraint(new LayoutConstraint(numStudentsLabel, LayoutAttribute.leading,   LayoutRelation.equal, this,      LayoutAttribute.leading,  1.0, 20));
		addConstraint(new LayoutConstraint(numStudentsLabel, LayoutAttribute.top,       LayoutRelation.equal, this,      LayoutAttribute.top,      1.0, 20));

		addConstraint(new LayoutConstraint(numTeachersLabel, LayoutAttribute.leading,   LayoutRelation.equal, this,      LayoutAttribute.leading,  1.0, 20));
		addConstraint(new LayoutConstraint(numTeachersLabel, LayoutAttribute.top,       LayoutRelation.equal, numStudentsLabel, LayoutAttribute.bottom,   1.0, 40));

		addConstraint(new LayoutConstraint(numClassesLabel, LayoutAttribute.leading,    LayoutRelation.equal, this,      LayoutAttribute.leading,  1.0, 20));
		addConstraint(new LayoutConstraint(numClassesLabel, LayoutAttribute.top,        LayoutRelation.equal, numTeachersLabel, LayoutAttribute.bottom,   1.0, 40));

		DFNotificationCenter.defaultCenter.register(this, UIStrings.oneHourHasPassedNotification);

		loadNumbers();
	}

	@Override
	public void layoutSubviews()
	{
		super.layoutSubviews();
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
		}
	}
}
