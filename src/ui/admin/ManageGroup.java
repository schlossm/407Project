package ui.admin;

import objects.Course;
import ui.util.Alert;
import ui.util.ButtonType;
import ui.Window;
import ui.util.ALJTable.*;
import uikit.autolayout.uiobjects.ALJTablePanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ManageGroup extends ALJTablePanel
{
	private Group groupToManage = Group.none;

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

		ArrayList<String> fakeData = new ArrayList<>();
		fakeData.add("Michael Schloss");
		groupData.put(groupToManage + "s", fakeData);

		setBackground(Color.white);
		setOpaque(true);

		//TODO: Load proper data from database and save it
	}

	public Group currentGroup()
	{
		return groupToManage;
	}

	private void add()
	{
		Alert alert = new Alert("New " + groupToManage, "");
		alert.addButton("Submit", ButtonType.defaultType, e ->
		{
			//TODO: Upload this user
			switch (groupToManage)
			{
				case teachers:
				{
					groupData.get(groupToManage + "s").add(alert.textFieldForIdentifier(groupToManage + ".firstName").getText() + " " + alert.textFieldForIdentifier(groupToManage + ".lastName").getText());
					break;
				}
				default:
					return;
			}
			table.reloadData();
			layoutSubviews();
			alert.dispose();
		}, true);
		alert.addButton("Cancel", ButtonType.cancel, null, false);

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
		return groupData.keySet().size();
	}

	@Override
	public int numberOfRowsInSectionForTable(ALJTable table, int section)
	{
		return groupData.get(titleForHeaderInSectionInTable(table, section)).size();
	}

	@Override
	public int heightForRow(ALJTable table, int inSection)
	{
		if (groupToManage == Group.courses && inSection > 0)
		{
			return 110;
		}
		else
		{
			return 44;
		}
	}

	@Override
	public ALJTableCell cellForRowAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		if (groupToManage == Group.courses && index.section > 0)
		{
			ClassCell newCell = new ClassCell(ALJTableCellAccessoryViewType.delete);
			Course course = new Course();
			course.setCourseID(0);
			course.setTitle("Mathematics I");
			course.setCourseName("Mathematics1-2017-001");
			course.setRoomNo("LWSN B148");
			course.setMeetingTime("10:30 AM - 9:20 AM");
			course.setMaxStorage(104857600);
			newCell.setCourse(course);
			return newCell;
		}

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
				Alert deleteConfirmation = new Alert("Confirm Delete", null);
				deleteConfirmation.addButton("Yes", ButtonType.destructive, e ->
				{
					//TODO: Actually delete from database
					groupData.get(titleForHeaderInSectionInTable(tableView, forRowAt.section)).remove(forRowAt.item);
					if (groupData.get(titleForHeaderInSectionInTable(tableView, forRowAt.section)).size() == 0)
					{
						groupData.remove(titleForHeaderInSectionInTable(tableView, forRowAt.section));
					}
					tableView.reloadData();
				}, false);
				deleteConfirmation.addButton("No", ButtonType.cancel, null, false);
				deleteConfirmation.show(Window.current.mainScreen);
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
