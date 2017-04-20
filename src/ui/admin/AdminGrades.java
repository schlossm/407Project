package ui.admin;

import objects.Grade;
import ui.util.UIStrings;
import ui.util.UIVariables;
import uikit.DFNotificationCenter;
import uikit.DFNotificationCenterDelegate;
import uikit.UIFont;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJPanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Objects;

import static java.lang.Integer.parseInt;

public class AdminGrades extends ALJPanel implements DFNotificationCenterDelegate
{
	private final JLabel ninetyPlusLabel;
	private final JLabel eightyToNinetyLabel;
	private final JLabel seventyToEightyLabel;
	private final JLabel sixtyToSeventyLabel;
	private final JLabel belowSixtyLabel;

	public AdminGrades()
	{
		setBackground(Color.white);

		ninetyPlusLabel = new JLabel("Loading grades 90+", JLabel.LEFT);
		ninetyPlusLabel.setFont(UIFont.textLight.deriveFont(18f));
		ninetyPlusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		ninetyPlusLabel.setFocusable(false);
		add(ninetyPlusLabel);

		eightyToNinetyLabel = new JLabel("Loading grades 80-90", JLabel.LEFT);
		eightyToNinetyLabel.setFont(UIFont.textLight.deriveFont(18f));
		eightyToNinetyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		eightyToNinetyLabel.setFocusable(false);
		add(eightyToNinetyLabel);

		seventyToEightyLabel = new JLabel("Loading grades 70-80", JLabel.LEFT);
		seventyToEightyLabel.setFont(UIFont.textLight.deriveFont(18f));
		seventyToEightyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		seventyToEightyLabel.setFocusable(false);
		add(seventyToEightyLabel);

		sixtyToSeventyLabel = new JLabel("Loading grades 60-70", JLabel.LEFT);
		sixtyToSeventyLabel.setFont(UIFont.textLight.deriveFont(18f));
		sixtyToSeventyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		sixtyToSeventyLabel.setFocusable(false);
		add(sixtyToSeventyLabel);

		belowSixtyLabel = new JLabel("Loading grades 0-60", JLabel.LEFT);
		belowSixtyLabel.setFont(UIFont.textLight.deriveFont(18f));
		belowSixtyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		belowSixtyLabel.setFocusable(false);
		add(belowSixtyLabel);

		addConstraint(new LayoutConstraint(ninetyPlusLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 20));
		addConstraint(new LayoutConstraint(ninetyPlusLabel, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 20));

		addConstraint(new LayoutConstraint(eightyToNinetyLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 20));
		addConstraint(new LayoutConstraint(eightyToNinetyLabel, LayoutAttribute.top, LayoutRelation.equal, ninetyPlusLabel, LayoutAttribute.bottom, 1.0, 40));

		addConstraint(new LayoutConstraint(seventyToEightyLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 20));
		addConstraint(new LayoutConstraint(seventyToEightyLabel, LayoutAttribute.top, LayoutRelation.equal, eightyToNinetyLabel, LayoutAttribute.bottom, 1.0, 40));

		addConstraint(new LayoutConstraint(sixtyToSeventyLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 20));
		addConstraint(new LayoutConstraint(sixtyToSeventyLabel, LayoutAttribute.top, LayoutRelation.equal, seventyToEightyLabel, LayoutAttribute.bottom, 1.0, 40));

		addConstraint(new LayoutConstraint(belowSixtyLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 20));
		addConstraint(new LayoutConstraint(belowSixtyLabel, LayoutAttribute.top, LayoutRelation.equal, sixtyToSeventyLabel, LayoutAttribute.bottom, 1.0, 40));

		DFNotificationCenter.defaultCenter.register(this, UIStrings.oneHourHasPassedNotification);

		loadGrades();

		layoutSubviews();
		layoutSubviews();
	}

	private void loadGrades()
	{
		//FIXME: Fix me
		//For now we use the test grades

		int num90Plus = 0;
		int num80To90 = 0;
		int num70To80 = 0;
		int num60To70 = 0;
		int below60 = 0;

		String filename = UIVariables.current.applicationDirectories.temp + File.separator + "test_grades.ser";

		// Read the 10 Grade objects back into memory
		try
		{
			FileInputStream fileIn = new FileInputStream(filename);
			ObjectInputStream objIn = new ObjectInputStream(fileIn);

			for (int i = 0; i < 10; ++i)
			{
				Grade g = (Grade) objIn.readObject();

				int gradeInt = parseInt(g.getScore());
				if (gradeInt >= 90)
				{
					num90Plus++;
				}
				else if (gradeInt >= 80)
				{
					num80To90++;
				}
				else if (gradeInt >= 70)
				{
					num70To80++;
				}
				else if (gradeInt >= 60)
				{
					num60To70++;
				}
				else
				{
					below60++;
				}
			}

			objIn.close();
			fileIn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		ninetyPlusLabel.setText(num90Plus + " grades 90+");
		eightyToNinetyLabel.setText(num80To90 + " grades 80-89");
		seventyToEightyLabel.setText(num70To80 + " grades 70-79");
		sixtyToSeventyLabel.setText(num60To70 + " grades 60-69");
		belowSixtyLabel.setText(below60 + " grades <60");
	}

	@Override
	public void performActionFor(String notificationName, Object userData)
	{
		if (Objects.equals(notificationName, UIStrings.oneHourHasPassedNotification))
		{
			loadGrades();
		}
	}
}
