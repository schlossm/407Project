package ui.homepages;

import json.AdminQuery;
import ui.Window;
import ui.util.Alert;
import ui.util.ButtonType;
import uikit.UIFont;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJPanel;

import javax.print.attribute.standard.NumberOfInterveningJobs;
import javax.swing.*;
import java.awt.*;

public class AdminPanel extends ALJPanel
{
	private final JLabel numStudentsLabel;
	private final JLabel numTeachersLabel;
	private final JLabel numClassesLabel;

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

		loadNumbers();
	}

	private void loadNumbers()
	{
		AdminQuery adminQuery = new AdminQuery();
		adminQuery.getAllCoursesCount((returnedData, error) -> {
			if (error != null)
			{
				if (error.code == 3)
				{
					numClassesLabel.setText("0 Classes");
					return;
				}
				Alert errorAlert = new Alert("Error", "ABC could not load number of courses.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null);
				errorAlert.show(Window.current.mainScreen);
				return;
			}
			if (returnedData instanceof Integer)
			{
				int count = (Integer) returnedData;
				numClassesLabel.setText(count + (count == 1 ? " Class" : " Classes"));
			}
			else
			{
				Alert errorAlert = new Alert("Error", "ABC could not load number of courses.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null);
				errorAlert.show(Window.current.mainScreen);
			}
		});
		adminQuery.getAllInstructorsCount((returnedData, error) -> {
			if (error != null)
			{
				if (error.code == 3)
				{
					numTeachersLabel.setText("0 Instructors");
					return;
				}
				Alert errorAlert = new Alert("Error", "ABC could not load number of instructors.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null);
				errorAlert.show(Window.current.mainScreen);
				return;
			}
			if (returnedData instanceof Integer)
			{
				int count = (Integer) returnedData;
				numTeachersLabel.setText(count + (count == 1 ? " Instructor" : " Instructors"));
			}
			else
			{
				Alert errorAlert = new Alert("Error", "ABC could not load number of instructors.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null);
				errorAlert.show(Window.current.mainScreen);
			}
		});
		adminQuery.getAllStudentsCount((returnedData, error) -> {
			if (error != null)
			{
				if (error.code == 3)
				{
					numStudentsLabel.setText("0 Students");
					return;
				}
				Alert errorAlert = new Alert("Error", "ABC could not load number of students.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null);
				errorAlert.show(Window.current.mainScreen);
				return;
			}
			if (returnedData instanceof Integer)
			{
				int count = (Integer) returnedData;
				numStudentsLabel.setText(count + (count == 1 ? " Student" : " Students"));
			}
			else
			{
				Alert errorAlert = new Alert("Error", "ABC could not load number of students.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null);
				errorAlert.show(Window.current.mainScreen);
			}
		});
	}
}
