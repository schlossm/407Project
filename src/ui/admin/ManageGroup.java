package ui.admin;

import json.CourseQuery;
import json.QueryCallbackRunnable;
import json.UserQuery;
import json.util.JSONQueryError;
import objects.Course;
import objects.Instructor;
import objects.User;
import objects.userType;
import ui.Window;
import ui.util.ALJTable.*;
import ui.util.Alert;
import ui.util.ButtonType;
import ui.util.UIStrings;
import ui.util.UIVariables;
import uikit.DFNotificationCenter;
import uikit.DFNotificationCenterDelegate;
import uikit.autolayout.uiobjects.ALJTablePanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

enum Process
{
	none, loadingCourse, loadingTeacher, loadingStudent, deleteCourse, addCourse
}

@SuppressWarnings("unchecked")
public class ManageGroup extends ALJTablePanel implements DFNotificationCenterDelegate
{
	private final Map<String, ArrayList<Object>> tableData = new HashMap<>();
	private final CourseQuery courseQuery = new CourseQuery();
	private final UserQuery userQuery = new UserQuery();
	private Group groupToManage = Group.none;
	private Process currentProcess = Process.none;

	private int offset = 0;
	private ALJTableIndex forRowAt;

	private final QueryCallbackRunnable deleteProcessor = (returnedData, error) ->
	{
		boolean shouldReturn = seeIfNeedToShowError(error);
		if (shouldReturn) { return; }
		if (returnedData instanceof Boolean)
		{
			boolean bool = (Boolean) returnedData;
			if (bool)
			{
				tableData.get(titleForHeaderInSectionInTable(table, 1)).remove(forRowAt.item);
				if (tableData.get(titleForHeaderInSectionInTable(table, 1)).size() == 0)
				{
					tableData.remove(titleForHeaderInSectionInTable(table, 1));
				}
				table.reloadData();
			}
			else
			{
				showCouldNotDeleteError();
			}
		}
	};

	public ManageGroup(Group groupToManage)
	{
		setBackground(Color.white);
		setOpaque(true);

		if (groupToManage == Group.none)
		{
			System.err.println("You cannot manage a \"none\" group.");
			System.exit(-1);
			return;
		}

		this.groupToManage = groupToManage;

		ArrayList<Object> starter = new ArrayList<>();
		starter.add("New " + groupToManage);
		tableData.put("", starter);

		if (UIVariables.current.globalUserData.get("All " + groupToManage) != null)
		{
			ArrayList<Object> savedData = (ArrayList<Object>) UIVariables.current.globalUserData.get("All " + groupToManage);
			tableData.put(groupToManage + "s", savedData);
		}

		DFNotificationCenter.defaultCenter.register(this, UIStrings.aLJTablePaneNearEndNotification);

		loadNextGroup();
	}

	public Group currentGroup()
	{
		return groupToManage;
	}

	private final QueryCallbackRunnable processor = (returnedData, error) ->
	{
		if (error != null)
		{
			if (error.code == 3)
			{
				return;
			}
			Alert errorAlert = new Alert("Error", "ABC could not load " + groupToManage + "s.  Please try again.");
			errorAlert.addButton("OK", ButtonType.defaultType, null, false);
			errorAlert.show(Window.current.mainScreen);
			return;
		}
		if (returnedData instanceof ArrayList)
		{
			ArrayList<Object> students = (ArrayList<Object>) returnedData;
			tableData.put(groupToManage + "s", students);
			table.reloadData();
		}
		else
		{
			Alert errorAlert = new Alert("Error", "ABC could not load " + groupToManage + "s.  Please try again.");
			errorAlert.addButton("OK", ButtonType.defaultType, null, false);
			errorAlert.show(Window.current.mainScreen);
		}
	};

	private void loadNextGroup()
	{
		if (groupToManage == Group.courses)
		{
			currentProcess = Process.loadingCourse;
			courseQuery.getAllCourses(100, offset, processor);
		}
		else if (groupToManage == Group.students)
		{
			currentProcess = Process.loadingStudent;
			userQuery.getAllStudents(100, offset, processor);
		}
		else
		{
			currentProcess = Process.loadingTeacher;
			userQuery.getAllInstructors(100, offset, processor);
		}
		offset += 100;
	}

	private void updateSavedData()
	{
		UIVariables.current.globalUserData.put("All " + groupToManage, tableData.get(groupToManage + "s"));
	}

	private void showGenericError()
	{
		Alert alert1 = new Alert("Error", "ABC could add the " + groupToManage + ".  Please try again.");
		alert1.addButton("OK", ButtonType.defaultType, null, false);
		alert1.show(Window.current.mainScreen);
	}

	private boolean checkIfNeedToShowErrorOnUserName(JSONQueryError error, Alert alert)
	{
		if (error != null)
		{
			if (error.code == 1)
			{
				alert.showError("Already Taken", alert.textFieldForIdentifier("username"));
			}
			else
			{
				this.showGenericError();
			}
			return true;
		}
		return false;
	}

	private void add()
	{
		Alert alert = new Alert("New " + groupToManage, "");
		alert.addButton("Submit", ButtonType.defaultType, e ->
		{
			switch (groupToManage)
			{
				case teachers:  //Upload new Instructor
				{
					userQuery.addNewUser(alert.textFieldForIdentifier(groupToManage + ".username").getText(), alert.textFieldForIdentifier(groupToManage + ".password").getText(), alert.textFieldForIdentifier(groupToManage + ".email").getText(), alert.textFieldForIdentifier(groupToManage + ".birthday").getText(), alert.textFieldForIdentifier(groupToManage + ".firstName").getText(), alert.textFieldForIdentifier(groupToManage + ".lastName").getText(), userType.TEACHER, (returnedData, error) ->
					{
						boolean shouldReturn = checkIfNeedToShowErrorOnUserName(error, alert);
						if (shouldReturn) return;
						if (returnedData instanceof Boolean)
						{
							boolean bool = (Boolean) returnedData;
							if (bool)
							{
								userQuery.addUserAsInstructor(alert.textFieldForIdentifier(groupToManage + ".username").getText(), alert.textFieldForIdentifier(groupToManage + ".officeHours").getText(), alert.textFieldForIdentifier(groupToManage + ".roomNumber").getText(), (returnedData1, error1) ->
								{
									if (error1 != null)
									{
										this.showGenericError();
										return;
									}
									if (returnedData1 instanceof Boolean)
									{
										boolean bool1 = (Boolean) returnedData1;
										if (bool1)
										{
											alert.dispose();
											Instructor newUser = new Instructor();
											newUser.setBirthday(alert.textFieldForIdentifier(groupToManage + ".birthday").getText());
											newUser.setUserType(userType.TEACHER);
											newUser.setUserID(alert.textFieldForIdentifier(groupToManage + ".username").getText());
											newUser.setEmail(alert.textFieldForIdentifier(groupToManage + ".email").getText());
											newUser.setFirstName(alert.textFieldForIdentifier(groupToManage + ".firstName").getText());
											newUser.setLastName(alert.textFieldForIdentifier(groupToManage + ".lastName").getText());
											newUser.setOfficeHours(alert.textFieldForIdentifier(groupToManage + ".officeHours").getText());
											newUser.setRoomNo(alert.textFieldForIdentifier(groupToManage + ".roomNumber").getText());

											addNewUserToTable(newUser);
											updateSavedData();
											table.reloadData();
										}
										else
										{
											this.showGenericError();
										}
									}
									else
									{
										this.showGenericError();
									}
								});
							}
							else
							{
								this.showGenericError();
							}
						}
						else
						{
							this.showGenericError();
						}
					});
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

					courseQuery.addCourse(Integer.parseInt(courseID), courseName, courseTitle, description, roomNum, meetingTimes, startDate, endDate, Integer.parseInt(capacity), (returnedData, error) -> {
						if (error != null)
						{
							if (error.code == 1)
							{
								alert.showError("Already Taken", alert.textFieldForIdentifier("courseName"));
							}
							else
							{
								this.showGenericError();
							}
							return;
						}
						if (returnedData instanceof Boolean)
						{
							boolean bool = (Boolean) returnedData;
							if (bool)
							{
								alert.dispose();
								Course courseToAdd = new Course();
								courseToAdd.setCourseID(Integer.parseInt(courseID));
								courseToAdd.setCourseName(courseName);
								courseToAdd.setTitle(courseTitle);
								courseToAdd.setDescription(description);
								courseToAdd.setMeetingTime(meetingTimes);
								courseToAdd.setMaxStorage(10000000);
								courseToAdd.setStartDate(startDate);
								courseToAdd.setEndDate(endDate);
								courseToAdd.setRoomNo(roomNum);
								addNewCourseToTable(courseToAdd);
								updateSavedData();
								table.reloadData();
							}
							else
							{
								this.showGenericError();
							}
						}
						else
						{
							this.showGenericError();
						}
					});
					break;
				}

				case students:  //Upload a new student
				{
					userQuery.addNewUser(alert.textFieldForIdentifier(groupToManage + ".username").getText(), alert.textFieldForIdentifier(groupToManage + ".password").getText(), alert.textFieldForIdentifier(groupToManage + ".email").getText(), alert.textFieldForIdentifier(groupToManage + ".birthday").getText(), alert.textFieldForIdentifier(groupToManage + ".firstName").getText(), alert.textFieldForIdentifier(groupToManage + ".lastName").getText(), userType.STUDENT, (returnedData, error) ->
					{
						if (error != null)
						{
							if (error.code == 1)
							{
								alert.showError("Already Taken", alert.textFieldForIdentifier("username"));
							}
							else
							{
								this.showGenericError();
							}
							return;
						}
						if (returnedData instanceof Boolean)
						{
							boolean bool = (Boolean) returnedData;
							if (bool)
							{
								userQuery.addUserAsStudent(alert.textFieldForIdentifier(groupToManage + ".username").getText(), (returnedData1, error1) ->
								{
									if (error1 != null)
									{
										this.showGenericError();
										return;
									}
									if (returnedData1 instanceof Boolean)
									{
										boolean bool1 = (Boolean) returnedData1;
										if (bool1)
										{
											alert.dispose();
											User newUser = new User();
											newUser.setBirthday(alert.textFieldForIdentifier(groupToManage + ".birthday").getText());
											newUser.setUserType(userType.TEACHER);
											newUser.setUserID(alert.textFieldForIdentifier(groupToManage + ".username").getText());
											newUser.setEmail(alert.textFieldForIdentifier(groupToManage + ".email").getText());
											newUser.setFirstName(alert.textFieldForIdentifier(groupToManage + ".firstName").getText());
											newUser.setLastName(alert.textFieldForIdentifier(groupToManage + ".lastName").getText());

											addNewUserToTable(newUser);
											updateSavedData();
											table.reloadData();
										}
										else
										{
											this.showGenericError();
										}
									}
									else
									{
										this.showGenericError();
									}
								});
							}
							else
							{
								this.showGenericError();
							}
						}
						else
						{
							this.showGenericError();
						}
					});
					break;
				}

				default:
					break;
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
				alert.addTextField("Courses (comma separated CRN)", "teacher.courses", false);
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
				alert.addTextField("Start Date (MM/DD/YYYY)", "course.startDate", false);
				alert.addTextField("End Date (mm/DD/YYYY)", "course.endDate", false);
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
				alert.addTextField("Courses (comma separated CRN)", "teacher.courses", false);
				break;
			}
		}

		alert.show(Window.current.mainScreen);
	}

	private void addNewUserToTable(User newUser)
	{
		addObject(newUser);
	}

	private void addNewCourseToTable(Course courseToAdd)
	{
		addObject(courseToAdd);
	}

	private void addObject(Object object)
	{
		if (tableData.get(groupToManage + "s") != null)
		{
			tableData.get(groupToManage + "s").add(object);
		}
		else
		{
			ArrayList<Object> arrayList = new ArrayList<>();
			arrayList.add(object);
			tableData.put(groupToManage + "s", arrayList);
		}
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
		else
		{
			ALJTableCell newCell = new ALJTableCell(!Objects.equals(tableData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item), "New " + groupToManage) ? ALJTableCellAccessoryViewType.delete : ALJTableCellAccessoryViewType.none);
			if (index.section == 0)
			{
				newCell.titleLabel.setText((String) (tableData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item)));
			}
			else if (groupToManage == Group.teachers)
			{
				Instructor instructor = (Instructor) tableData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item);
				newCell.titleLabel.setText(instructor.getFirstName() + " " + instructor.getLastName());
			}
			else if (groupToManage == Group.students)
			{
				User student = (User) tableData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item);
				newCell.titleLabel.setText(student.getFirstName() + " " + student.getLastName());
			}
			return newCell;
		}
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
					this.forRowAt = forRowAt;
					if (currentProcess == Process.none)
					{
						if (groupToManage == Group.courses)
						{
							currentProcess = Process.deleteCourse;
							Course courseToRemove = (Course) tableData.get(titleForHeaderInSectionInTable(tableView, forRowAt.section)).get(forRowAt.item);
							courseQuery.removeCourse(courseToRemove.getCourseID(), deleteProcessor);
						}
						else if (groupToManage == Group.students)
						{
							User studentToRemove = (User) tableData.get(titleForHeaderInSectionInTable(tableView, forRowAt.section)).get(forRowAt.item);
							userQuery.removeUserAsStudent(studentToRemove.getUserID(), deleteProcessor);
						}
						else if (groupToManage == Group.teachers)
						{
							User studentToRemove = (User) tableData.get(titleForHeaderInSectionInTable(tableView, forRowAt.section)).get(forRowAt.item);
							userQuery.removeUserAsInstructor(studentToRemove.getUserID(), deleteProcessor);
						}
					}

				}, false);
				deleteConfirmation.addButton("No", ButtonType.cancel, null, false);
				deleteConfirmation.show(Window.current.mainScreen);
				break;
			}

			default:
				break;
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
		if (Objects.equals(notificationName, UIStrings.aLJTablePaneNearEndNotification))
		{
			loadNextGroup();
		}
	}

	private boolean seeIfNeedToShowError(JSONQueryError error)
	{
		if (error != null)
		{
			showCouldNotDeleteError();
			return true;
		}
		return false;
	}

	private void showCouldNotDeleteError()
	{
		Alert alert = new Alert("Error", "ABC could not delete the " + groupToManage + ".  Please try again.");
		alert.addButton("OK", ButtonType.defaultType, null, false);
		alert.show(Window.current.mainScreen);
	}
}
