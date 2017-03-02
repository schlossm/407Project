package ui.admin;

import ui.Alert;
import ui.ButtonType;
import ui.Window;
import ui.util.ALJTable.*;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class ManageGroup extends ALJPanel implements ALJTableDataSource, ALJTableDelegate
{
	private Group groupToManage = Group.none;
	private ALJTable manageTable;

	private final Map<String, ArrayList<String>> groupData = new HashMap<>();

	public ManageGroup(Group groupToManage)
	{
		ArrayList<String> starter = new ArrayList<>();
		starter.add("New " + groupToManage);
		groupData.put("", starter);
		if (groupToManage == Group.none)
		{
			System.err.println("You cannot manage a \"none\" group.");
			return;
		}
		this.groupToManage = groupToManage;

		setBackground(Color.white);
		setOpaque(true);

		manageTable = new ALJTable();
		manageTable.heightForRow = 66;
		manageTable.dataSource = this;
		manageTable.delegate = this;

		add(manageTable);

		addConstraint(new LayoutConstraint(manageTable, LayoutAttribute.leading,    LayoutRelation.equal, this, LayoutAttribute.leading,    1.0, 0));
		addConstraint(new LayoutConstraint(manageTable, LayoutAttribute.top,        LayoutRelation.equal, this, LayoutAttribute.top,        1.0, 0));
		addConstraint(new LayoutConstraint(manageTable, LayoutAttribute.trailing,   LayoutRelation.equal, this, LayoutAttribute.trailing,   1.0, 0));
		addConstraint(new LayoutConstraint(manageTable, LayoutAttribute.bottom,     LayoutRelation.equal, this, LayoutAttribute.bottom,     1.0, 0));

		//TODO: Load proper data from database and save it
	}

	@Override
	public void layoutSubviews()
	{
		super.layoutSubviews();
		if (!manageTable.isLoaded())
		{
			manageTable.reloadData();
			manageTable.layoutSubviews();
			repaint();
		}
		else
		{
			manageTable.layoutSubviews();
			repaint();
		}
	}

	public Group currentGroup()
	{
		return groupToManage;
	}

	public void add()
	{
		Alert alert = new Alert("New " + groupToManage, "");
		alert.addButton("Submit", ButtonType.defaultType, e ->
		{
			Map<String, JTextField> textFields = alert.getTextFields();
			//TODO: Actually do something with this
		});
		alert.addButton("Cancel", ButtonType.cancel, null);

		switch (groupToManage)
		{
			case teachers:
			{
				alert.addTextField("Username", "teacher.username", false);
				alert.addTextField("Password", "teacher.password", true);
				alert.addTextField("First Name", "teacher.firstName", false);
				alert.addTextField("Last Name", "teacher.lastName", false);
				alert.addTextField("Email", "teacher.email", false);
				alert.addTextField("Birthday (MM/DD/YYYY)", "teacher.birthday", false);
				alert.addTextField("Office Hours", "teacher.officeHours", false);
				alert.addTextField("Room Number", "teacher.roomNumber", false);
				break;
			}

			case courses:
			{
				alert.addTextField("Course Title (Friendly Name)", "course.courseTitle", false);
				alert.addTextField("Course Identifier", "course.courseIdentifier", true);
				alert.addTextField("Teacher (username)", "course.teacher", false);
				alert.addTextField("Meeting Time(s)", "course.meetingTimes", false);
				alert.addTextField("Description", "course.description", false);
				alert.addTextField("Capacity", "course.capacity", false);
				alert.addTextField("Room Number", "course.roomNumber", false);
				break;
			}

			case none:
			{
				System.err.println("Cannot add .none type");
				System.exit(-1);
				break;
			}

			case students:
			{
				alert.addTextField("Username", "student.username", false);
				alert.addTextField("Password", "student.password", true);
				alert.addTextField("First Name", "student.firstName", false);
				alert.addTextField("Last Name", "student.lastName", false);
				alert.addTextField("Email", "student.email", false);
				alert.addTextField("Birthday (MM/DD/YYYY)", "student.birthday", false);
				break;
			}
		}

		alert.show(Window.current.mainScreen);
	}

	@Override
	public int numberOfSectionsIn(ALJTable table)
	{
		return 1;
	}

	@Override
	public int numberOfRowsInSectionForTable(ALJTable table, int section)
	{
		return groupData.size();
	}

	@Override
	public ALJTableCell cellForRowAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		ALJTableCell newCell = new ALJTableCell(!Objects.equals(groupData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item), "New " + groupToManage) ? ALJTableCellAccessoryViewType.delete : ALJTableCellAccessoryViewType.none);

		newCell.titleLabel.setText(groupData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item));

		return newCell;
	}

	@Override
	public String titleForHeaderInSectionInTable(ALJTable table, int section)
	{
		return (String)groupData.keySet().toArray()[section];
	}

	@Override
	public String titleForFooterInSectionInTable(ALJTable table, int section)
	{
		return "";
	}

	@Override
	public void tableView(ALJTable tableView, ALJTableCellEditingStyle commit, ALJTableIndex forRowAt)
	{
		switch (commit)
		{
			case delete:
			{
				groupData.get(titleForHeaderInSectionInTable(tableView, forRowAt.section)).remove(forRowAt.item);
				if (groupData.get(titleForHeaderInSectionInTable(tableView, forRowAt.section)).size() == 0)
				{
					groupData.remove(titleForHeaderInSectionInTable(tableView, forRowAt.section));
				}
				break;
			}

			default: break;
		}
	}

	@Override
	public void didSelectItemAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		if (index.item == 0 && index.section == 0)
		{
			add();
		}
	}
}
