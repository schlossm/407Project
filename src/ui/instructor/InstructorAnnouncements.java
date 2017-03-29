package ui.instructor;

import objects.Course;
import ui.Window;
import ui.util.ALJTable.*;
import ui.util.Alert;
import ui.util.ButtonType;
import uikit.UIFont;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJTablePanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InstructorAnnouncements extends ALJTablePanel
{
	private final Map<String, ArrayList<Object>> announcementData = new HashMap<>();

	Course courseForAnnouncements;

	private JLabel loadingLabel;

	public InstructorAnnouncements(Course course)
	{
		super();

		ArrayList<Object> starter = new ArrayList<>();
		starter.add("New Announcement");
		announcementData.put("", starter);

		loadingLabel = new JLabel("Loading");
		loadingLabel.setFont(UIFont.textLight.deriveFont(30f));
		add(loadingLabel);
		addConstraint(new LayoutConstraint(loadingLabel, LayoutAttribute.centerX, LayoutRelation.equal, this, LayoutAttribute.centerX, 1.0, 0));
		addConstraint(new LayoutConstraint(loadingLabel, LayoutAttribute.centerY, LayoutRelation.equal, this, LayoutAttribute.centerY, 1.0, 0));

		//TODO: Load proper data from database and save it
	}

	private void add()
	{
		Alert alert = new Alert("New Announcement", "");
		alert.addButton("Submit", ButtonType.defaultType, e ->
		{
			//TODO: Upload this announcement
			if (announcementData.get("Announcements") != null)
				announcementData.get("Announcements").add(alert.textFieldForIdentifier("title").getText() + ": " + alert.textFieldForIdentifier("body").getText());
			else
			{
				TestAnnouncement announcement = new TestAnnouncement();
				announcement.title = alert.textFieldForIdentifier("title").getText();
				announcement.body = alert.textFieldForIdentifier("body").getText();
				announcement.courseName = courseForAnnouncements.getCourseName();

				if (announcementData.get("Announcements") == null)
				{
					ArrayList<Object> announcements = new ArrayList<>();
					announcements.add(announcement);
					announcementData.put("Announcements", announcements);
				}
				else
				{
					ArrayList<Object> announcements = announcementData.get("Announcements");
					announcements.add(announcement);
					announcementData.put("Announcements", announcements);
				}
				table.reloadData();
			}
			table.reloadData();
			layoutSubviews();
			alert.dispose();
		}, true);
		alert.addButton("Cancel", ButtonType.cancel, null, false);

		alert.addTextField("Title", "title", false);
		alert.addTextField("Body", "body", false);

		alert.addCheckBox("Email students?", "emailCheck");

		alert.show(Window.current.mainScreen);
	}

	@Override
	public void didSelectItemAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		if (index.item == 0 && index.section == 0)
		{
			add();
		}
	}

	@Override
	public int numberOfSectionsIn(ALJTable table)
	{
		return announcementData.keySet().size();
	}

	@Override
	public int numberOfRowsInSectionForTable(ALJTable table, int section)
	{
		return announcementData.get(titleForHeaderInSectionInTable(table, section)).size();
	}

	@Override
	public int heightForRow(ALJTable table, int inSection)
	{
		return inSection == 1 ? 90 : 44;
	}

	@Override
	public ALJTableCell cellForRowAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		if (index.section == 1)
		{
			return new AnnouncementCell((TestAnnouncement) announcementData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item));
		}
		else
		{
			ALJTableCell cell = new ALJTableCell(ALJTableCellAccessoryViewType.none);
			cell.titleLabel.setText((String) announcementData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item));
			return cell;
		}
	}

	@Override
	public String titleForHeaderInSectionInTable(ALJTable table, int section)
	{
		return (String)announcementData.keySet().toArray()[section];
	}

	@Override
	public String titleForFooterInSectionInTable(ALJTable table, int section)
	{
		return null;
	}

	@Override
	public void tableView(ALJTable tableView, ALJTableCellEditingStyle commit, ALJTableIndex forRowAt) { }
}

class TestAnnouncement
{
	String title;
	String body;
	String courseName;
}

class AnnouncementCell extends ALJTableCell
{
	AnnouncementCell(TestAnnouncement assignment)
	{
		super(ALJTableCellAccessoryViewType.delete);

		removeConstraintsFor(titleLabel);

		titleLabel.setText(assignment.title);
		titleLabel.setFont(UIFont.textBold.deriveFont(11f));

		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 24));
		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));
		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.trailing, LayoutRelation.equal, accessoryView, LayoutAttribute.leading, 1.0, -8));

		JLabel detailLabelOne = new JLabel(assignment.body);
		detailLabelOne.setFont(UIFont.textLight.deriveFont(9f));
		add(detailLabelOne);
		addConstraint(new LayoutConstraint(detailLabelOne, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 24));
		addConstraint(new LayoutConstraint(detailLabelOne, LayoutAttribute.top, LayoutRelation.equal, titleLabel, LayoutAttribute.bottom, 1.0, 8));
		addConstraint(new LayoutConstraint(detailLabelOne, LayoutAttribute.trailing, LayoutRelation.equal, accessoryView, LayoutAttribute.leading, 1.0, -8));

		JLabel detailLabelTwo = new JLabel(assignment.courseName);
		detailLabelTwo.setFont(UIFont.textLight.deriveFont(9f));
		add(detailLabelTwo);
		addConstraint(new LayoutConstraint(detailLabelTwo, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 24));
		addConstraint(new LayoutConstraint(detailLabelTwo, LayoutAttribute.top, LayoutRelation.equal, detailLabelOne, LayoutAttribute.bottom, 1.0, 8));
		addConstraint(new LayoutConstraint(detailLabelTwo, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, -8));
		addConstraint(new LayoutConstraint(detailLabelTwo, LayoutAttribute.trailing, LayoutRelation.equal, accessoryView, LayoutAttribute.leading, 1.0, -8));
	}
}
