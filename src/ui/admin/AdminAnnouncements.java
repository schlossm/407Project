package ui.admin;

import json.AnnouncementQuery;
import objects.Message;
import ui.Window;
import ui.util.ALJTable.*;
import ui.util.Alert;
import ui.util.ButtonType;
import ui.util.UIStrings;
import ui.util.UIVariables;
import uikit.DFNotificationCenter;
import uikit.DFNotificationCenterDelegate;
import uikit.autolayout.uiobjects.ALJTablePanel;

import java.util.*;

public class AdminAnnouncements extends ALJTablePanel implements DFNotificationCenterDelegate
{
	private final Map<String, ArrayList<Object>> announcementData = new HashMap<>();

	private AnnouncementQuery announcementQuery = new AnnouncementQuery();

	private Runnable workToDoOnSuccess = null;
	private Runnable workToDoOnFailure = null;

	public AdminAnnouncements()
	{
		super();

		ArrayList<Object> starter = new ArrayList<>();
		starter.add("New Announcement");
		announcementData.put("", starter);

		DFNotificationCenter.defaultCenter.register(this, UIStrings.returned);
		announcementQuery.getAllAnnouncementInCourse(-1);
	}

	private void add()
	{
		Alert alert = new Alert("New Announcement", "");
		alert.addButton("Submit", ButtonType.defaultType, e ->
		{
			String timestamp = new Date().toString();
			workToDoOnSuccess = () ->
			{
				if (announcementData.get("Announcements") != null)
					announcementData.get("Announcements").add(new Message(alert.textFieldForIdentifier("title").getText(), alert.textFieldForIdentifier("body").getText(), timestamp));
				else
				{
					ArrayList<Object> data = new ArrayList<>();
					Message message = new Message(alert.textFieldForIdentifier("title").getText(), alert.textFieldForIdentifier("body").getText(), timestamp);
					data.add(message);
					announcementData.put("Announcements", data);
				}
				table.reloadData();
				alert.dispose();
			};

			workToDoOnFailure = () ->
			{
				Alert errorAlert = new Alert("Error", "ABC could not add the announcement.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null, false);
				errorAlert.show(Window.current.mainScreen);
			};

			announcementQuery.addAnnouncement(alert.textFieldForIdentifier("title").getText(), alert.textFieldForIdentifier("body").getText(), timestamp, UIVariables.current.currentUser.getUserID(), -1);
			workToDoOnSuccess.run();

		}, true);
		alert.addButton("Cancel", ButtonType.cancel, null, false);

		alert.addTextField("Title", "title", false);
		alert.addTextField("Body", "body", false);

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
		if (inSection == 1)
		{
			return 106;
		}

		return 44;
	}

	@Override
	public ALJTableCell cellForRowAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		ALJTableCell newCell = new ALJTableCell(ALJTableCellAccessoryViewType.none);

		newCell.titleLabel.setText(((Message)announcementData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item)).getTitle());

		return newCell;
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

	@Override
	public void performActionFor(String notificationName, Object userData)
	{
		if (Objects.equals(notificationName, UIStrings.returned))
		{
			if (userData != null)
			{

			}
		}
	}
}
