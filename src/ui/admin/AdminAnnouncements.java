package ui.admin;

import json.AnnouncementQuery;
import objects.Message;
import ui.ErrorMessage;
import ui.Window;
import ui.util.ALJTable.*;
import ui.util.Alert;
import ui.util.AnnouncementCell;
import ui.util.ButtonType;
import ui.util.UIVariables;
import uikit.autolayout.uiobjects.ALJTablePanel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class AdminAnnouncements extends ALJTablePanel
{
	private final Map<String, ArrayList<Object>> announcementData = new HashMap<>();

	private final AnnouncementQuery announcementQuery = new AnnouncementQuery();

	public AdminAnnouncements()
	{
		super();

		ArrayList<Object> starter = new ArrayList<>();
		starter.add("New Announcement");
		announcementData.put("", starter);

		announcementQuery.getAllAnnouncementInCourse(-1, (returnedData, error) ->
		{
			if (error != null)
			{
				if (error.code == 3)
				{
					return;
				}
				Alert errorAlert = new Alert("Error", "ABC could not load the announcements.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null);
				errorAlert.show(Window.current.mainScreen);
				return;
			}
			if (returnedData instanceof ArrayList)
			{
				ArrayList<Object> messages = (ArrayList<Object>) returnedData;
				announcementData.put("Announcements", messages);
			}
		});
	}

	private void add()
	{
		Alert alert = new Alert("New Announcement", "");
		alert.addButton("Submit", ButtonType.defaultType, e ->
			                                                  announcementQuery.addAnnouncement(alert.textFieldForIdentifier("title").getText(), alert.textFieldForIdentifier("body").getText(), UIVariables.current.currentUser.getUserID(), -1, (returnedData, error) ->
			                                                  {
				                                                  boolean shouldReturn = ErrorMessage.defaultMessageManager.checkIfNeedToShowErrorForLoadingAnnouncements(error);
				                                                  if (shouldReturn) { return; }

				                                                  if (returnedData instanceof Boolean)
				                                                  {
					                                                  boolean bool = (Boolean) returnedData;
					                                                  if (bool)
					                                                  {
						                                                  if (announcementData.get("Announcements") != null)
						                                                  { announcementData.get("Announcements").add(new Message(alert.textFieldForIdentifier("title").getText(), alert.textFieldForIdentifier("body").getText(), new Date().toString(), UIVariables.current.currentUser.getUserID(), -1)); }
						                                                  else
						                                                  {
							                                                  ArrayList<Object> data = new ArrayList<>();
							                                                  Message message = new Message(alert.textFieldForIdentifier("title").getText(), alert.textFieldForIdentifier("body").getText(), new Date().toString(), UIVariables.current.currentUser.getUserID(), -1);
							                                                  data.add(message);
							                                                  announcementData.put("Announcements", data);
						                                                  }
						                                                  table.reloadData();
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
		if (index.section == 1)
		{
			return new AnnouncementCell(((Message) announcementData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item)));
		}
		else
		{
			ALJTableCell newCell = new ALJTableCell(ALJTableCellAccessoryViewType.none);
			newCell.titleLabel.setText((String) announcementData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item));
			return newCell;
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
