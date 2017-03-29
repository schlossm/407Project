package ui.common;

import objects.userType;
import ui.util.ALJTable.*;
import ui.util.Alert;
import ui.util.UIVariables;
import uikit.autolayout.uiobjects.ALJTablePanel;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

class FileList extends ALJTablePanel
{
	private Map<String, ArrayList<Object>> fileListData = new HashMap<>();

	FileList()
	{
		if (isInstructor())
		{
			ArrayList<Object> beginner = new ArrayList<>();
			beginner.add("Upload File");
			fileListData.put("", beginner);
		}

		//TODO: Actually load files
	}

	private void add()
	{
		Alert alert = new Alert("New File", "Choose file to upload");

	}

	@Override
	public void didSelectItemAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		if (index.section == 0 && index.item == 0)
		{
			add();
		}
	}

	@Override
	public int numberOfSectionsIn(ALJTable table)
	{
		return fileListData.keySet().size();
	}

	@Override
	public int numberOfRowsInSectionForTable(ALJTable table, int section)
	{
		return fileListData.get(titleForHeaderInSectionInTable(table, section)).size();
	}

	@Override
	public int heightForRow(ALJTable table, int inSection)
	{
		if (inSection == 0)
		{
			return 44;
		}
		else
		{
			return 150;
		}
	}

	@Override
	public ALJTableCell cellForRowAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		if (index.section == 0 && isInstructor())
		{
			ALJTableCell cell = new ALJTableCell(ALJTableCellAccessoryViewType.none);
			cell.titleLabel.setText((String)fileListData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item));
			return cell;
		}
		return new ALJTableCell(ALJTableCellAccessoryViewType.none);
	}

	@Override
	public String titleForHeaderInSectionInTable(ALJTable table, int section)
	{
		return (String)fileListData.keySet().toArray()[section];
	}

	@Override
	public String titleForFooterInSectionInTable(ALJTable table, int section)
	{
		return null;
	}

	@Override
	public void tableView(ALJTable table, ALJTableCellEditingStyle commit, ALJTableIndex forRowAt)
	{

	}

	private boolean isInstructor()
	{
		return EnumSet.of(userType.TA, userType.TEACHER).contains(UIVariables.current.currentUser.getUserType());
	}
}

