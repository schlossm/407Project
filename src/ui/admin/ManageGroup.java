package ui.admin;

import json.CourseQuery;
import json.InstructorQuery;
import json.StudentQuery;
import json.UserQuery;
import objects.Course;
import objects.User;
import ui.Window;
import ui.util.ALJTable.*;
import ui.util.Alert;
import ui.util.ButtonType;
import ui.util.UIStrings;
import uikit.DFNotificationCenter;
import uikit.DFNotificationCenterDelegate;
import uikit.autolayout.uiobjects.ALJTablePanel;

import java.awt.*;
import java.util.*;

enum Process
{
	none, loadingCourse, loadingTeacher, loadingStudent,
	deleteTeacher, deleteCourse, deleteStudent,
	addTeacher, addCourse, addStudent
}

public class ManageGroup extends ALJTablePanel implements DFNotificationCenterDelegate
{
	private Group groupToManage = Group.none;

	private final Map<String, ArrayList<Object>> tableData = new HashMap<>();

	private final CourseQuery courseQuery = new CourseQuery();
	private StudentQuery studentQuery = new StudentQuery();
	private final UserQuery userQuery = new UserQuery();
	private InstructorQuery instructorQuery = new InstructorQuery();

	private Process currentProcess = Process.none;

	private Runnable workToDoOnSuccess = null;
	private Runnable workToDoOnFailure = null;

	public ManageGroup(Group groupToManage)
	{
		setBackground(Color.white);
		setOpaque(true);

		if (groupToManage == Group.none)
		{
			System.err.println("You cannot manage a \"none\" group.");
			return;
		}

		this.groupToManage = groupToManage;

		ArrayList<Object> starter = new ArrayList<>();
		starter.add("New " + groupToManage);
		tableData.put("", starter);

		DFNotificationCenter.defaultCenter.register(this, UIStrings.returned);
		DFNotificationCenter.defaultCenter.register(this, UIStrings.success);
		DFNotificationCenter.defaultCenter.register(this, UIStrings.failure);


		//This view is only visible to admin so we don't have to do any checking Michael.
		if (groupToManage == Group.courses)
		{
			currentProcess = Process.loadingCourse;
			//TODO: Fill In with getAllCourses
		}
		else if (groupToManage == Group.students)
		{
			currentProcess = Process.loadingStudent;
			//TODO: Fill In with getAllStudents
		}
		else
		{
			currentProcess = Process.loadingTeacher;
			//TODO: Fill In with getAllTeachers
		}
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
			//TODO: Upload this thing
			switch (groupToManage)
			{
				case teachers:  //Upload new Instructor
				{
					tableData.get(groupToManage + "s").add(alert.textFieldForIdentifier(groupToManage + ".firstName").getText() + " " + alert.textFieldForIdentifier(groupToManage + ".lastName").getText());
					break;
				}

				case courses:   //Upload a new course
				{
					String courseTitle = alert.textFieldForIdentifier(groupToManage + ".courseTitle").getText();
					String courseName = alert.textFieldForIdentifier(groupToManage + ".courseName").getText();
					String courseID = alert.textFieldForIdentifier(groupToManage + ".courseIdentifier").getText();
					String meetingTimes = alert.textFieldForIdentifier(groupToManage + ".meetingTimes").getText();
					String description = alert.textFieldForIdentifier(groupToManage + ".description").getText();
					String capacity = alert.textFieldForIdentifier(groupToManage + ".capacity").getText();
					String startDate = alert.textFieldForIdentifier(groupToManage + ".startDate").getText();
					String endDate = alert.textFieldForIdentifier(groupToManage + ".endDate").getText();
					String roomNum = alert.textFieldForIdentifier(groupToManage + ".roomNumber").getText();

					currentProcess = Process.addCourse;

					courseQuery.addCourse(Integer.parseInt(courseID), courseName, courseTitle, description, roomNum, meetingTimes, startDate, endDate);
					workToDoOnSuccess = alert::dispose;
					break;
				}

				default: break;
			}
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
				alert.addTextField("Course Name", "course.courseName", false);
				alert.addTextField("Course Registration Number", "course.courseIdentifier", false);
				alert.addTextField("Meeting Time(s)", "course.meetingTimes", false);
				alert.addTextField("Description", "course.description", false);
				alert.addTextField("Capacity", "course.capacity", false);
				alert.addTextField("Room Number", "course.roomNumber", false);
				alert.addTextField("Start Date (mm/dd/yyyy)", "course.startDate", false);
				alert.addTextField("End Date (mm/dd/yyyy)", "course.endDate", false);
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
		return tableData.keySet().size();
	}

	@Override
	public int numberOfRowsInSectionForTable(ALJTable table, int section)
	{
		return tableData.get(titleForHeaderInSectionInTable(table, section)).size();
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
			ClassCell newCell = new ClassCell();
			Course course = (Course) (tableData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item));
			newCell.setCourse(course);
			return newCell;
		}

		ALJTableCell newCell = new ALJTableCell(!Objects.equals(tableData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item), "New " + groupToManage) ? ALJTableCellAccessoryViewType.delete : ALJTableCellAccessoryViewType.none);

		newCell.titleLabel.setText((String) (tableData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item)));

		return newCell;
	}

	@Override
	public String titleForHeaderInSectionInTable(ALJTable table, int section)
	{
		return (String) tableData.keySet().toArray()[section];
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
					if (currentProcess == Process.none)
					{
						workToDoOnSuccess = () ->
						{
							tableData.get(titleForHeaderInSectionInTable(tableView, forRowAt.section)).remove(forRowAt.item);
							if (tableData.get(titleForHeaderInSectionInTable(tableView, forRowAt.section)).size() == 0)
							{
								tableData.remove(titleForHeaderInSectionInTable(tableView, forRowAt.section));
							}
							tableView.reloadData();
						};
						workToDoOnFailure = () ->
						{
							Alert alert = new Alert("Error", "ABC could not delete the " + groupToManage + ".  Please try again.");
							alert.addButton("OK", ButtonType.defaultType, null, false);
							alert.show(Window.current.mainScreen);
						};

						if (groupToManage == Group.courses)
						{
							currentProcess = Process.deleteCourse;
							Course courseToRemove = (Course) tableData.get(titleForHeaderInSectionInTable(tableView, forRowAt.section)).get(forRowAt.item);
							courseQuery.removeCourse(courseToRemove.getCourseID());
						}
						else if (groupToManage == Group.students)
						{
							currentProcess = Process.deleteStudent;
							User studentToRemove = (User) tableData.get(titleForHeaderInSectionInTable(tableView, forRowAt.section)).get(forRowAt.item);
							userQuery.removeUserAsStudent(studentToRemove.getUserID());
						}
					}

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

	@Override
	public void performActionFor(String notificationName, Object userData)
	{
		if (currentProcess == Process.none)
		{
			return;
		}
		else if (currentProcess == Process.loadingCourse)
		{
			if (Objects.equals(notificationName, UIStrings.returned))
			{
				Course[] courses = (Course[]) userData;

				ArrayList<Object> aCourses = new ArrayList<>();
				aCourses.addAll(Arrays.asList(courses));
				tableData.put(groupToManage + "s", aCourses);
			}
		}
		else if (currentProcess == Process.loadingStudent || currentProcess == Process.loadingTeacher)
		{
			if (Objects.equals(notificationName, UIStrings.returned))
			{
				User[] users = (User[]) userData;

				ArrayList<Object> aCourses = new ArrayList<>();
				aCourses.addAll(Arrays.asList(users));
				tableData.put(groupToManage + "s", aCourses);
			}
		}
		else
		{
			if (Objects.equals(notificationName, UIStrings.failure))
			{
				if (workToDoOnFailure != null)
				{
					workToDoOnFailure.run();
				}
			}
			else if (Objects.equals(notificationName, UIStrings.success))
			{
				if (workToDoOnSuccess != null)
				{
					System.out.println("Hello");
					workToDoOnSuccess.run();
				}
			}
		}

		currentProcess = Process.none;
	}
}
