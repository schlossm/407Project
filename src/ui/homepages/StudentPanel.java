package ui.homepages;

import ui.util.ALJTable.ALJTable;
import uikit.UIFont;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJPanel;

import javax.swing.*;

public class StudentPanel extends ALJPanel
{
	private ALJTable announcementsTable;
	private ALJTable thingsDueTable;
	private ALJTable courses;

	public StudentPanel()
	{
		JLabel announcementsTitle = new JLabel("Announcements");
		announcementsTitle.setFont(UIFont.displayHeavy.deriveFont(18f));
		add(announcementsTitle);
		addConstraint(new LayoutConstraint(announcementsTitle, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 20));
		addConstraint(new LayoutConstraint(announcementsTitle, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 20));

	}
}
