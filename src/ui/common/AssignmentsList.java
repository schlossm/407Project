package ui.common;

import json.AssignmentQuery;
import json.DocumentsQuery;
import objects.Assignment;
import objects.Course;
import objects.userType;
import ui.Window;
import ui.util.ALJTable.*;
import ui.util.Alert;
import ui.util.ButtonType;
import ui.util.UIVariables;
import uikit.UIFont;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJTablePanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.EnumSet;

@SuppressWarnings("unchecked")
class AssignmentsList extends ALJTablePanel
{
	private JLabel loadingLabel;
	private ArrayList<Assignment> assignments = new ArrayList<>();

	private Course course;

	AssignmentsList(Course course)
	{
		this.course = course;

		loadingLabel = new JLabel("Loading");
		loadingLabel.setFont(UIFont.textLight.deriveFont(30f));
		loadingLabel.setHorizontalTextPosition(JLabel.CENTER);
		add(loadingLabel, 2);
		addConstraint(new LayoutConstraint(loadingLabel, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 0));
		addConstraint(new LayoutConstraint(loadingLabel, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, 0));
		addConstraint(new LayoutConstraint(loadingLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 0));
		addConstraint(new LayoutConstraint(loadingLabel, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, 0));

		new AssignmentQuery().getAllAssignmentsInCourse(course.getCourseID(), (returnedData, error) -> {
			if (error != null)
			{
				if (error.code == 3)
				{
					loadingLabel.setText("No Assignments Yet!");
					return;
				}
				Alert errorAlert = new Alert("Error", "ABC could not load the assignments for this course.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null, false);
				errorAlert.show(Window.current.mainScreen);
				return;
			}
			if (returnedData instanceof ArrayList)
			{
				assignments = (ArrayList<Assignment>)returnedData;
				remove(loadingLabel);
				table.reloadData();
			}
			else
			{
				Alert errorAlert = new Alert("Error", "ABC could not load the assignments for this course.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null, false);
				errorAlert.show(Window.current.mainScreen);
			}
		});
	}

	private void add()
	{
		Alert alert = new Alert("Add New Assignment", null);
		alert.addButton("Cancel", ButtonType.defaultType, null, false);
		alert.addTextField("Title", "assignment.title", false);
		alert.addTextField("Due Date (MM/DD/YYYY HH:MMA/P", "assignment.dueDate", false);
		alert.addDropDown(new String[] {"Loading Files"}, 0, "assignments.fileChooser");
		alert.show(Window.current.mainScreen);

		new DocumentsQuery().getAllDocumentsIdsInCourse(course.getCourseID());
	}

	@Override
	public void didSelectItemAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		if (isInstructor())
		{
			if (index.section == 1)
			{
				add();
			}
		}
		//TODO: Finish this once methods are available
	}

	@Override
	public int numberOfSectionsIn(ALJTable table)
	{
		if (isInstructor()) return 2;
		return 1;
	}

	@Override
	public int numberOfRowsInSectionForTable(ALJTable table, int section)
	{
		if (isInstructor())
		{
			if (section == 0)
			{
				return 1;
			}
		}
		return assignments.size();
	}

	@Override
	public int heightForRow(ALJTable table, int inSection)
	{
		return 44;
	}

	@Override
	public ALJTableCell cellForRowAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		if (isInstructor())
		{
			if (index.section == 0)
			{
				ALJTableCell cell = new ALJTableCell(ALJTableCellAccessoryViewType.none);
				cell.titleLabel.setText("New Assignment");
				return cell;
			}
		}
		return null;
	}

	@Override
	public String titleForHeaderInSectionInTable(ALJTable table, int section)
	{
		if (isInstructor())
		{
			if (section == 0)
			{
				return "";
			}
			return "Assignments";
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

	private boolean isInstructor()
	{
		return EnumSet.of(userType.TA, userType.TEACHER).contains(UIVariables.current.currentUser.getUserType());
	}
}
