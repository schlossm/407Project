package ui.admin;

import ui.util.Alert;
import ui.util.ButtonType;
import ui.Window;
import ui.util.ALJTable.*;
import uikit.autolayout.uiobjects.ALJTablePanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminAnnouncements extends ALJTablePanel
{
	private final Map<String, ArrayList<String>> announcementData = new HashMap<>();

	public AdminAnnouncements()
	{
		super();

		ArrayList<String> starter = new ArrayList<>();
		starter.add("New Announcement");
		announcementData.put("", starter);

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
				ArrayList<String> data = new ArrayList<>();
				data.add(alert.textFieldForIdentifier("title").getText() + ": " + alert.textFieldForIdentifier("body").getText());
				announcementData.put("Announcements", data);
			}
			table.reloadData();
			layoutSubviews();
			alert.dispose();
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
		return 44;
	}

	@Override
	public ALJTableCell cellForRowAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		ALJTableCell newCell = new ALJTableCell(ALJTableCellAccessoryViewType.none);

		newCell.titleLabel.setText(announcementData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item));

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
}
