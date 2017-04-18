package ui.instructor;

import json.AnnouncementQuery;
import objects.Course;
import objects.Message;
import ui.Window;
import ui.util.*;
import ui.util.ALJTable.*;
import uikit.DFNotificationCenter;
import uikit.DFNotificationCenterDelegate;
import uikit.UIFont;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJTablePanel;

import javax.swing.*;
import java.util.*;

@SuppressWarnings("unchecked")
public class InstructorAnnouncements extends ALJTablePanel implements DFNotificationCenterDelegate
{
	private final Map<String, ArrayList<Object>> announcementData = new HashMap<>();

	private Course courseForAnnouncements;

	private AnnouncementQuery query = new AnnouncementQuery();

	private Runnable workToDoOnSuccess = null;
	private Runnable workToDoOnFailure = null;

	private JLabel loadingLabel;

	public InstructorAnnouncements(Course course)
	{
		courseForAnnouncements = course;

		ArrayList<Object> starter = new ArrayList<>();
		starter.add("New Announcement");
		announcementData.put("", starter);

		loadingLabel = new JLabel("Loading");
		loadingLabel.setFont(UIFont.textLight.deriveFont(30f));
		add(loadingLabel);
		addConstraint(new LayoutConstraint(loadingLabel, LayoutAttribute.centerX, LayoutRelation.equal, this, LayoutAttribute.centerX, 1.0, 0));
		addConstraint(new LayoutConstraint(loadingLabel, LayoutAttribute.centerY, LayoutRelation.equal, this, LayoutAttribute.centerY, 1.0, 0));

		if (UIVariables.current.globalUserData.get("announcements" + course.getCourseID()) != null)
		{
			remove(loadingLabel);
			ArrayList<Object> savedAnnouncements = (ArrayList<Object>) UIVariables.current.globalUserData.get("announcements" + course.getCourseID());
			announcementData.put("Announcements", savedAnnouncements);
		}

		DFNotificationCenter.defaultCenter.register(this, UIStrings.returned);
		DFNotificationCenter.defaultCenter.register(this, UIStrings.success);
		DFNotificationCenter.defaultCenter.register(this, UIStrings.failure);
		query.getAllAnnouncementInCourse(course.getCourseID());
	}

	@Override
	public void removeNotify()
	{
		super.removeNotify();
		DFNotificationCenter.defaultCenter.remove(this);
	}

	private void updateSavedInfo()
	{
		UIVariables.current.globalUserData.put("announcements" + courseForAnnouncements.getCourseID(), announcementData.get("Announcements"));
	}

	private void add()
	{
		Alert alert = new Alert("New Announcement", "");
		alert.addButton("Submit", ButtonType.defaultType, e ->
		{
			String timestamp = new Date().toString();
			query.addAnnouncement(alert.textFieldForIdentifier("title").getText(), alert.textFieldForIdentifier("body").getText(), timestamp, UIVariables.current.currentUser.getUserID(), courseForAnnouncements.getCourseID());

			workToDoOnSuccess = () ->
			{
				Message announcement = new Message(-1, alert.textFieldForIdentifier("title").getText(), alert.textFieldForIdentifier("body").getText(), timestamp, UIVariables.current.currentUser.getUserID(), courseForAnnouncements.getCourseID());

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
				updateSavedInfo();
				table.reloadData();
				layoutSubviews();
				alert.dispose();
			};
			workToDoOnFailure = () ->
			{
				Alert errorAlert = new Alert("Error", "ABC could not add the announcement.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null, false);
				errorAlert.show(Window.current.mainScreen);
			};
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
		return inSection == 1 ? 106 : 44;
	}

	@Override
	public ALJTableCell cellForRowAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		if (index.section == 1)
		{
			return new AnnouncementCell((Message) announcementData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item));
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
		return (String) announcementData.keySet().toArray()[section];
	}

	@Override
	public String titleForFooterInSectionInTable(ALJTable table, int section)
	{
		return null;
	}

	@Override
	public void tableView(ALJTable tableView, ALJTableCellEditingStyle commit, ALJTableIndex forRowAt) { }

	@Override
	public void performActionFor(String notificationName, Object userData)
	{
		if (Objects.equals(notificationName, UIStrings.returned))
		{
			if (userData != null)
			{
				remove(loadingLabel);
			}
		}
		else if (Objects.equals(notificationName, UIStrings.success))
		{
			workToDoOnSuccess.run();
		}
		else if (Objects.equals(notificationName, UIStrings.failure))
		{
			workToDoOnFailure.run();
		}
	}
}

