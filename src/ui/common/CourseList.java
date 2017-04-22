package ui.common;

import json.CourseQuery;
import json.InstructorQuery;
import json.StudentQuery;
import objects.Course;
import objects.Instructor;
import objects.userType;
import ui.Window;
import ui.util.ALJTable.ALJTable;
import ui.util.ALJTable.ALJTableCell;
import ui.util.ALJTable.ALJTableCellEditingStyle;
import ui.util.ALJTable.ALJTableIndex;
import ui.util.Alert;
import ui.util.ButtonType;
import ui.util.UIVariables;
import uikit.autolayout.uiobjects.ALJTablePanel;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class CourseList extends ALJTablePanel
{
	private ArrayList<Course> courses = new ArrayList<>();

	public CourseList()
	{
		System.out.println(UIVariables.current.globalUserData.get("allCourses"));
		if (!((ArrayList<Course>) UIVariables.current.globalUserData.get("allCourses")).isEmpty())
		{
			courses = (ArrayList<Course>) UIVariables.current.globalUserData.get("allCourses");
		}
		else
		{
			if (UIVariables.current.currentUser.getUserType() == userType.TEACHER)
			{
				new InstructorQuery().getCourses(UIVariables.current.currentUser.getUserID(), (returnedData, error) ->
				{
					if (error != null)
					{
						Alert errorAlert = new Alert("Error", "ABC could not load your courses.  Please try again.");
						errorAlert.addButton("OK", ButtonType.defaultType, null, false);
						errorAlert.show(Window.current.mainScreen);
						return;
					}
					if (returnedData instanceof ArrayList)
					{
						courses = (ArrayList<Course>) returnedData;
						UIVariables.current.globalUserData.put("allCourses", courses);
						table.reloadData();
					}
					else
					{
						Alert errorAlert = new Alert("Error", "ABC could not load your courses.  Please try again.");
						errorAlert.addButton("OK", ButtonType.defaultType, null, false);
						errorAlert.show(Window.current.mainScreen);
					}
				});
			}
			else
			{
				new StudentQuery().getCourses(UIVariables.current.currentUser.getUserID(), (returnedData, error) ->
				{
					if (error != null)
					{
						if (error.code == 1)
						{
							return;
						}
						Alert errorAlert = new Alert("Error", "ABC could not load your courses.  Please try again.");
						errorAlert.addButton("OK", ButtonType.defaultType, null, false);
						errorAlert.show(Window.current.mainScreen);
						return;
					}
					if (returnedData instanceof ArrayList)
					{
						courses = (ArrayList<Course>)returnedData;
						for (Course course : courses)
						{
							new CourseQuery().getAllInstructorsInCourse(course.getCourseID(), (returnedData1, error1) ->
							{
								if (error1 != null)
								{
									Alert errorAlert = new Alert("Error", "ABC could not load your courses.  Please try again.");
									errorAlert.addButton("OK", ButtonType.defaultType, null, false);
									errorAlert.show(Window.current.mainScreen);
									return;
								}
								if (returnedData1 instanceof ArrayList)
								{
									//TODO: Change to Instructor upon pull request
									course.setTeachers((ArrayList<Instructor>) returnedData1);
								}
								else
								{
									Alert errorAlert = new Alert("Error", "ABC could not load your courses.  Please try again.");
									errorAlert.addButton("OK", ButtonType.defaultType, null, false);
									errorAlert.show(Window.current.mainScreen);
								}
							});
						}
						UIVariables.current.globalUserData.put("allCourses", returnedData);
						table.reloadData();
					}
					else
					{
						Alert errorAlert = new Alert("Error", "ABC could not load your courses.  Please try again.");
						errorAlert.addButton("OK", ButtonType.defaultType, null, false);
						errorAlert.show(Window.current.mainScreen);
					}
				});
			}
		}
	}

	@Override
	public void didSelectItemAtIndexInTable(ALJTable table, ALJTableIndex index)
	{

	}

	@Override
	public int numberOfSectionsIn(ALJTable table)
	{
		return 1;
	}

	@Override
	public int numberOfRowsInSectionForTable(ALJTable table, int section)
	{
		return courses.size();
	}

	@Override
	public int heightForRow(ALJTable table, int inSection)
	{
		return 110;
	}

	@Override
	public ALJTableCell cellForRowAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		CourseListClassCell cell = new CourseListClassCell();
		cell.setCourse(courses.get(index.item));
		return cell;
	}

	@Override
	public String titleForHeaderInSectionInTable(ALJTable table, int section)
	{
		return null;
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
}
