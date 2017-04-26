package ui.instructor;

import json.AnnouncementQuery;
import objects.Course;
import objects.Message;
import ui.ErrorMessage;
import ui.Window;
import ui.util.ALJTable.*;
import ui.util.Alert;
import ui.util.AnnouncementCell;
import ui.util.ButtonType;
import ui.util.UIVariables;
import uikit.UIFont;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJTablePanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class InstructorAnnouncements extends ALJTablePanel
{
	private final Map<String, ArrayList<Object>> announcementData = new HashMap<>();

	private final Course courseForAnnouncements;

	private final AnnouncementQuery query = new AnnouncementQuery();

	private final JLabel loadingLabel;

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

		query.getAllAnnouncementInCourse(course.getCourseID(), (returnedData, error) ->
		{
			if (error != null)
			{
				if (error.code == 3) return;
				Alert errorAlert = new Alert("Error", "ABC could not load the announcements.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null);
				errorAlert.show(Window.current.mainScreen);
				return;
			}
			if (returnedData instanceof ArrayList)
			{
				remove(loadingLabel);
				ArrayList<Object> messages = (ArrayList<Object>) returnedData;
				announcementData.put("Announcements", messages);
				UIVariables.current.globalUserData.put("announcements" + course.getCourseID(), messages);
				table.clearAndReload();
			}
		});
	}

	private void updateSavedInfo()
	{
		UIVariables.current.globalUserData.put("announcements" + courseForAnnouncements.getCourseID(), announcementData.get("Announcements"));
	}

	private void add()
	{
		Alert alert = new Alert("New Announcement", "");
		alert.addButton("Submit", ButtonType.defaultType, e ->
			                                                  query.addAnnouncement(alert.textFieldForIdentifier("title").getText(), alert.textFieldForIdentifier("body").getText(), UIVariables.current.currentUser.getUserID(), courseForAnnouncements.getCourseID(), (returnedData, error) ->
			                                                  {
				                                                  boolean shouldReturn = ErrorMessage.defaultMessageManager.checkIfNeedToShowErrorForLoadingAnnouncements(error);
				                                                  if (shouldReturn) { return; }

				                                                  if (returnedData instanceof Boolean)
				                                                  {
					                                                  boolean bool = (Boolean) returnedData;
					                                                  if (bool)
					                                                  {
						                                                  Message announcement = new Message(alert.textFieldForIdentifier("title").getText(), alert.textFieldForIdentifier("body").getText(), new Date().toString(), UIVariables.current.currentUser.getUserID(), courseForAnnouncements.getCourseID());

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
					                                                  }
					                                                  else
					                                                  {
						                                                  Alert errorAlert = new Alert("Error", "ABC could not add the announcement.  Please try again.");
						                                                  errorAlert.addButton("OK", ButtonType.defaultType, null);
						                                                  errorAlert.show(Window.current.mainScreen);
					                                                  }
				                                                  }
			                                                  }));
		alert.addButton("Cancel", ButtonType.cancel, null);

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
}

