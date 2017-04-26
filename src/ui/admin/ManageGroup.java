package ui.admin;

import json.*;
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
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static database.DFDatabase.queue;

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

	private QueryCallbackRunnable deleteProcessor;

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
			errorAlert.addButton("OK", ButtonType.defaultType, null);
			errorAlert.show(Window.current.mainScreen);
			return;
		}
		if (returnedData instanceof ArrayList)
		{
			ArrayList<Object> students = (ArrayList<Object>) returnedData;
			tableData.put(groupToManage + "s", students);
			table.reloadData();
			updateSavedData();
		}
		else
		{
			Alert errorAlert = new Alert("Error", "ABC could not load " + groupToManage + "s.  Please try again.");
			errorAlert.addButton("OK", ButtonType.defaultType, null);
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
		Alert alert1 = new Alert("Error", "ABC could not add the " + groupToManage + ".  Please try again.");
		alert1.addButton("OK", ButtonType.defaultType, null);
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
						if (shouldReturn) { return; }
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

					courseQuery.addCourse(Integer.parseInt(courseID), courseName, courseTitle, description, roomNum, meetingTimes, startDate, endDate, Integer.parseInt(capacity), 500*1024*1024, (returnedData, error) ->
					{
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
								courseToAdd.setMaxStorage(500*1024*1024);
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
			ALJTableCell newCell = new ALJTableCell(!Objects.equals(tableData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item), "New " + groupToManage) ? ALJTableCellAccessoryViewType.info : ALJTableCellAccessoryViewType.none);
			if (index.section == 0)
			{
				newCell.titleLabel.setText((String) (tableData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item)));
			}
			else if (groupToManage == Group.teachers || groupToManage == Group.students)
			{
				User user = (User) tableData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item);
				newCell.titleLabel.setText(user.getFirstName() + " " + user.getLastName());
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
			case info:
			{
				String title;
				if (groupToManage == Group.courses)
				{
					title = ((Course) tableData.get(groupToManage + "s").get(forRowAt.item)).getTitle();
				}
				else if (groupToManage == Group.teachers)
				{
					title = ((Instructor) tableData.get(groupToManage + "s").get(forRowAt.item)).getFirstName() + " " + ((Instructor) tableData.get(groupToManage + "s").get(forRowAt.item)).getLastName();
				}
				else
				{
					title = ((User) tableData.get(groupToManage + "s").get(forRowAt.item)).getFirstName() + " " + ((User) tableData.get(groupToManage + "s").get(forRowAt.item)).getLastName();
				}

				Alert more = new Alert(title, "");
				more.addButton("Delete", ButtonType.destructive, e ->
				{
					Alert deleteConfirmation;
					deleteConfirmation = new Alert("Confirm Delete", null);

					if (deleteProcessor == null)
					{
						deleteProcessor = (returnedData, error) ->
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
					}

					deleteConfirmation.addButton("Yes", ButtonType.destructive, e2 ->
					{
						if (currentProcess != Process.none)
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

					});
					deleteConfirmation.addButton("No", ButtonType.cancel, null);
					deleteConfirmation.show(Window.current.mainScreen);


				});
				if (groupToManage != Group.courses)
				{
					more.addButton("Modify User Information", ButtonType.plain,  e -> {

					});
					more.addButton("Modify Course(s)", ButtonType.plain,  e -> {
						User user = (User) tableData.get(titleForHeaderInSectionInTable(tableView, forRowAt.section)).get(forRowAt.item);
						showCourseModificationListFor(user);
					});
				}
				more.addButton("Cancel", ButtonType.cancel, null);
				more.show(Window.current.mainScreen);
			}

			default:
				break;
		}
	}

	private void showCourseModificationListFor(User user)
	{
		Alert modifyCourses = new Alert("Courses for " + user.getFirstName() + " " + user.getLastName(), "Loading Current Courses...");
		if (user.getUserType() == userType.STUDENT)
		{
			StudentQuery query = new StudentQuery();
			query.getCourses(user.getUserID(), (returnedData, error) -> {
				if (error != null)
				{
					if (error.code == 3)
					{
						modifyCourses.setMessage("No Courses Yet!");
						expandModifyAlert(modifyCourses, user);
						return;
					}
					Alert alert1 = new Alert("Error", "ABC could not load the courses.  Please try again.");
					alert1.addButton("OK", ButtonType.defaultType, null);
					alert1.show(Window.current.mainScreen);

					return;
				}
				if (returnedData instanceof ArrayList)
				{
					ArrayList<Course> courses = (ArrayList<Course>) returnedData;
					StringBuilder stringBuilder = new StringBuilder();
					for (Course course : courses)
					{
						stringBuilder.append("<b>").append(course.getTitle()).append(":</b> ").append(course.getCourseID()).append("\n");
					}
					modifyCourses.setMessage(stringBuilder.toString());
					expandModifyAlert(modifyCourses, user);
				}
				else
				{
					Alert alert1 = new Alert("Error", "ABC could not load the courses.  Please try again.");
					alert1.addButton("OK", ButtonType.defaultType, null);
					alert1.show(Window.current.mainScreen);
				}
			});
		}
		else
		{
			InstructorQuery query = new InstructorQuery();
			query.getCourses(user.getUserID(), (returnedData, error) -> {
				if (error != null)
				{
					if (error.code == 3)
					{
						modifyCourses.setMessage("No Courses Yet!");
						expandModifyAlert(modifyCourses, user);
						return;
					}
					Alert alert1 = new Alert("Error", "ABC could not load the courses.  Please try again.");
					alert1.addButton("OK", ButtonType.defaultType, null);
					alert1.show(Window.current.mainScreen);

					return;
				}
				if (returnedData instanceof ArrayList)
				{
					ArrayList<Course> courses = (ArrayList<Course>) returnedData;
					StringBuilder stringBuilder = new StringBuilder();
					for (Course course : courses)
					{
						stringBuilder.append("<b>").append(course.getTitle()).append(":</b> ").append(course.getCourseID()).append("\n");
					}
					modifyCourses.setMessage(stringBuilder.toString());
					expandModifyAlert(modifyCourses, user);
				}
				else
				{
					Alert alert1 = new Alert("Error", "ABC could not load the courses.  Please try again.");
					alert1.addButton("OK", ButtonType.defaultType, null);
					alert1.show(Window.current.mainScreen);
				}
			});
		}
		modifyCourses.show(Window.current.mainScreen);
	}

	private void expandModifyAlert(Alert modifyCourses, User user)
	{
		modifyCourses.addTextField("CRN", "crn", false);
		modifyCourses.addButton("Add Course", ButtonType.plain, (ActionEvent e) -> {
			int crn = Integer.valueOf(modifyCourses.textFieldForIdentifier("crn").getText());
			if (user.getUserType() == userType.STUDENT)
			{
				new CourseQuery().addStudentToCourse(crn, user.getUserID(), (returnedData1, error1) ->
				{
					if (error1 != null)
					{
						Alert alert1 = new Alert("Error", "ABC could not add the student to the course.  Please try again.");
						alert1.addButton("OK", ButtonType.defaultType, e1 -> showCourseModificationListFor(user));
						alert1.show(Window.current.mainScreen);
						return;
					}
					if (returnedData1 instanceof Boolean)
					{
						boolean bool = (Boolean) returnedData1;
						if (bool)
						{
							queue.add(() -> showCourseModificationListFor(user));
						}
						else
						{
							Alert alert1 = new Alert("Error", "ABC could not add the student to the course.  Please try again.");
							alert1.addButton("OK", ButtonType.defaultType, e1 -> showCourseModificationListFor(user));
							alert1.show(Window.current.mainScreen);
						}
					}
					else
					{
						Alert alert1 = new Alert("Error", "ABC could not add the student to the course.  Please try again.");
						alert1.addButton("OK", ButtonType.defaultType, e1 -> showCourseModificationListFor(user));
						alert1.show(Window.current.mainScreen);
					}
				});
			}
			else
			{
				new CourseQuery().addInstructorToCourse(crn, user.getUserID(), (returnedData1, error1) ->
				{
					if (error1 != null)
					{
						Alert alert1 = new Alert("Error", "ABC could not add the instructor to the course.  Please try again.");
						alert1.addButton("OK", ButtonType.defaultType, e1 -> showCourseModificationListFor(user));
						alert1.show(Window.current.mainScreen);
						return;
					}
					if (returnedData1 instanceof Boolean)
					{
						boolean bool = (Boolean) returnedData1;
						if (bool)
						{
							queue.add(() -> showCourseModificationListFor(user));
						}
						else
						{
							Alert alert1 = new Alert("Error", "ABC could not add the instructor to the course.  Please try again.");
							alert1.addButton("OK", ButtonType.defaultType, e1 -> showCourseModificationListFor(user));
							alert1.show(Window.current.mainScreen);
						}
					}
					else
					{
						Alert alert1 = new Alert("Error", "ABC could not add the instructor to the course.  Please try again.");
						alert1.addButton("OK", ButtonType.defaultType, e1 -> showCourseModificationListFor(user));
						alert1.show(Window.current.mainScreen);
					}
				});
			}
		});
		modifyCourses.addButton("Remove Course", ButtonType.plain, e -> {
			int crn = Integer.valueOf(modifyCourses.textFieldForIdentifier("crn").getText());
			if (user.getUserType() == userType.STUDENT)
			{
				new CourseQuery().removeStudentInCourse(crn, user.getUserID(), (returnedData1, error1) ->
				{
					if (error1 != null)
					{
						Alert alert1 = new Alert("Error", "ABC could not remove the student from the course.  Please try again.");
						alert1.addButton("OK", ButtonType.defaultType, null);
						alert1.show(Window.current.mainScreen);
						return;
					}
					if (returnedData1 instanceof Boolean)
					{
						boolean bool = (Boolean) returnedData1;
						if (bool)
						{
							showCourseModificationListFor(user);
						}
						else
						{
							Alert alert1 = new Alert("Error", "ABC could not remove the student from the course.  Please try again.");
							alert1.addButton("OK", ButtonType.defaultType, null);
							alert1.show(Window.current.mainScreen);
						}
					}
					else
					{
						Alert alert1 = new Alert("Error", "ABC could not remove the student from the course.  Please try again.");
						alert1.addButton("OK", ButtonType.defaultType, null);
						alert1.show(Window.current.mainScreen);
					}
				});
			}
			else
			{
				new CourseQuery().removeInstructorInCourse(crn, user.getUserID(), (returnedData1, error1) ->
				{
					if (error1 != null)
					{
						Alert alert1 = new Alert("Error", "ABC could not remove the instructor from the course.  Please try again.");
						alert1.addButton("OK", ButtonType.defaultType, null);
						alert1.show(Window.current.mainScreen);
						return;
					}
					if (returnedData1 instanceof Boolean)
					{
						boolean bool = (Boolean) returnedData1;
						if (bool)
						{
							showCourseModificationListFor(user);
						}
						else
						{
							Alert alert1 = new Alert("Error", "ABC could not remove the instructor from the course.  Please try again.");
							alert1.addButton("OK", ButtonType.defaultType, null);
							alert1.show(Window.current.mainScreen);
						}
					}
					else
					{
						Alert alert1 = new Alert("Error", "ABC could not remove the instructor from the course.  Please try again.");
						alert1.addButton("OK", ButtonType.defaultType, null);
						alert1.show(Window.current.mainScreen);
					}
				});
			}
		});
		modifyCourses.addButton("Cancel", ButtonType.cancel, null);
		modifyCourses.dispose();
		modifyCourses.show(Window.current.mainScreen);
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
		alert.addButton("OK", ButtonType.defaultType, null);
		alert.show(Window.current.mainScreen);
	}
}
