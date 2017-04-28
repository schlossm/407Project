package ui.homepages;

import json.AnnouncementQuery;
import json.StudentQuery;
import objects.Assignment;
import objects.Course;
import objects.Message;
import ui.Window;
import ui.util.*;
import ui.util.ALJTable.*;
import uikit.DFNotificationCenter;
import uikit.UIFont;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class StudentPanel extends ALJPanel implements ALJTableDataSource, ALJTableDelegate
{
	private final ALJTable announcementsTable;
	private final ALJTable thingsDueTable;
	private final ALJTable coursesTable;

	private Map<String, ArrayList<Message>> announcements = new LinkedHashMap<>();
	private ArrayList<Course> courses = new ArrayList<>();
	private final ArrayList<Assignment> thingsDue = new ArrayList<>();

	public StudentPanel()
	{
		//Announcements
		JLabel announcementsTitle = new JLabel("Announcements");
		announcementsTitle.setFont(UIFont.displayHeavy.deriveFont(18f));
		add(announcementsTitle);
		addConstraint(new LayoutConstraint(announcementsTitle, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 20));
		addConstraint(new LayoutConstraint(announcementsTitle, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 20));

		announcementsTable = new ALJTable();
		announcementsTable.dataSource = this;
		announcementsTable.delegate = this;
		add(announcementsTable);
		addConstraint(new LayoutConstraint(announcementsTable, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 0));
		addConstraint(new LayoutConstraint(announcementsTable, LayoutAttribute.top, LayoutRelation.equal, announcementsTitle, LayoutAttribute.bottom, 1.0, 8));
		addConstraint(new LayoutConstraint(announcementsTable, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, 0));
		addConstraint(new LayoutConstraint(announcementsTable, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0/3.0, -4));

		//Things Due
		JLabel thingsDueTitle = new JLabel("");
		thingsDueTitle.setFont(UIFont.displayHeavy.deriveFont(18f));
		add(thingsDueTitle);
		addConstraint(new LayoutConstraint(thingsDueTitle, LayoutAttribute.leading, LayoutRelation.equal, announcementsTable, LayoutAttribute.trailing, 1.0, 24));
		addConstraint(new LayoutConstraint(thingsDueTitle, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 20));

		thingsDueTable = new ALJTable();
		thingsDueTable.dataSource = this;
		thingsDueTable.delegate = this;
		add(thingsDueTable);
		addConstraint(new LayoutConstraint(thingsDueTable, LayoutAttribute.leading, LayoutRelation.equal, announcementsTable, LayoutAttribute.trailing, 1.0, 8));
		addConstraint(new LayoutConstraint(thingsDueTable, LayoutAttribute.top, LayoutRelation.equal, thingsDueTitle, LayoutAttribute.bottom, 1.0, 8));
		addConstraint(new LayoutConstraint(thingsDueTable, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, 0));
		addConstraint(new LayoutConstraint(thingsDueTable, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 2.0/3.0, -4));

		//Courses
		JLabel coursesTitle = new JLabel("Courses");
		coursesTitle.setFont(UIFont.displayHeavy.deriveFont(18f));
		add(coursesTitle);
		addConstraint(new LayoutConstraint(coursesTitle, LayoutAttribute.leading, LayoutRelation.equal, thingsDueTable, LayoutAttribute.trailing, 1.0, 24));
		addConstraint(new LayoutConstraint(coursesTitle, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 20));

		coursesTable = new ALJTable();
		coursesTable.dataSource = this;
		coursesTable.delegate = this;
		add(coursesTable);
		addConstraint(new LayoutConstraint(coursesTable, LayoutAttribute.leading, LayoutRelation.equal, thingsDueTable, LayoutAttribute.trailing, 1.0, 8));
		addConstraint(new LayoutConstraint(coursesTable, LayoutAttribute.top, LayoutRelation.equal, coursesTitle, LayoutAttribute.bottom, 1.0, 8));
		addConstraint(new LayoutConstraint(coursesTable, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, 0));
		addConstraint(new LayoutConstraint(coursesTable, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, 0));

		//TODO: Get the stuff
		new StudentQuery().getCourses(UIVariables.current.currentUser.getUserID(), (returnedData, error) ->
		{
			if (error != null)
			{
				if (error.code == 3)
				{
					return;
				}
				Alert errorAlert = new Alert("Error", "ABC could not load your courses.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null);
				errorAlert.show(Window.current.mainScreen);
				return;
			}
			if (returnedData instanceof ArrayList)
			{
				courses = (ArrayList<Course>)returnedData;
				UIVariables.current.globalUserData.put("allCourses", returnedData);
				coursesTable.reloadData();
			}
			else
			{
				Alert errorAlert = new Alert("Error", "ABC could not load your courses.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null);
				errorAlert.show(Window.current.mainScreen);
			}
		});

		new AnnouncementQuery().getAllAnnouncementForStudent(UIVariables.current.currentUser.getUserID(), (returnedData, error) -> {
			if (error != null)
			{
				if (error.code == 3)
				{
					return;
				}
				Alert errorAlert = new Alert("Error", "ABC could not load your announcements.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null);
				errorAlert.show(Window.current.mainScreen);
				return;
			}
			if (returnedData instanceof ArrayList)
			{
				ArrayList<Message> messages = (ArrayList<Message>)returnedData;

				ArrayList<Message> school = new ArrayList<>();
				ArrayList<Message> course = new ArrayList<>();
				for (Message message : messages)
				{
					if (message.getCourseId() == -1)
					{
						school.add(message);
					}
					else
					{
						course.add(message);
					}
				}
				announcements.put("School", school);
				announcements.put("Course", course);
				announcementsTable.reloadData();

				UIVariables.current.globalUserData.put("allAnnouncements", returnedData);
				announcementsTable.reloadData();
			}
			else
			{
				Alert errorAlert = new Alert("Error", "ABC could not load your courses.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null);
				errorAlert.show(Window.current.mainScreen);
			}
		});
		//TODO: Get the last thing
	}

	@Override
	public void didSelectItemAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		if (table == coursesTable)
		{
			ArrayList<Object> objects = new ArrayList<>();
			objects.add("CourseView");
			objects.add(courses.get(index.item));
			DFNotificationCenter.defaultCenter.post(UIStrings.ABCTabBarButtonClickedNotification, objects);
			ABCTabBar.currentTabBar.forceSelectButton("My Courses");
		}
	}

	@Override
	public int numberOfSectionsIn(ALJTable table)
	{
		if (table == announcementsTable)
		{
			return announcements.keySet().size();
		}
		return 1;
	}

	@Override
	public int numberOfRowsInSectionForTable(ALJTable table, int section)
	{
		if (table == coursesTable)
		{
			return courses.size();
		}
		else if (table == announcementsTable)
		{
			return announcements.get(titleForHeaderInSectionInTable(table, section)).size();
		}
		else
		{
			return thingsDue.size();
		}
	}

	@Override
	public int heightForRow(ALJTable table, int inSection)
	{
		return 44;
	}

	@Override
	public ALJTableCell cellForRowAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		ALJTableCell cell = new ALJTableCell(ALJTableCellAccessoryViewType.none);
		if (table == coursesTable)
		{
			cell.titleLabel.setText(courses.get(index.item).getTitle());
		}
		else if (table == announcementsTable)
		{
			cell.titleLabel.setText(announcements.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item).getTitle());
		}
		else if (table == thingsDueTable)
		{
			cell.titleLabel.setText(thingsDue.get(index.item).getTitle());
		}
		return cell;
	}

	@Override
	public String titleForHeaderInSectionInTable(ALJTable table, int section)
	{
		if (table == announcementsTable)
		{
			return section == 0 ? "School" : "Course";
		}
		return null;
	}

	@Override
	public String titleForFooterInSectionInTable(ALJTable table, int section)
	{
		return null;
	}

	@Override
	public void tableView(ALJTable table, ALJTableCellEditingStyle commit, ALJTableIndex forRowAt) { }
}
