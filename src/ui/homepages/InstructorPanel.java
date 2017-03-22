package ui.homepages;

import objects.Course;
import ui.admin.ClassCell;
import ui.util.ALJTable.*;
import uikit.UIFont;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJPanel;

import javax.swing.*;
import java.util.ArrayList;

public class InstructorPanel extends ALJPanel implements ALJTableDataSource
{
	private ALJTable nextDueTable;
	private ALJTable courseList;

	private ArrayList<TestAssignment> assignments = new ArrayList<>();
	private ArrayList<Course> courses = new ArrayList<>();

	public InstructorPanel()
	{
		JLabel label = new JLabel("Next Due");
		label.setFont(UIFont.displayHeavy.deriveFont(24f));
		add(label);
		addConstraint(new LayoutConstraint(label, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));
		addConstraint(new LayoutConstraint(label, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));

		nextDueTable = new ALJTable();
		nextDueTable.dataSource = this;
		add(nextDueTable);
		addConstraint(new LayoutConstraint(nextDueTable, LayoutAttribute.top, LayoutRelation.equal, label, LayoutAttribute.bottom, 1.0, 0));
		addConstraint(new LayoutConstraint(nextDueTable, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 0));
		addConstraint(new LayoutConstraint(nextDueTable, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, 0));
		addConstraint(new LayoutConstraint(nextDueTable, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.centerX, 1.0, 0));

		JLabel secondLabel = new JLabel("Courses");
		secondLabel.setFont(UIFont.displayHeavy.deriveFont(24f));
		add(secondLabel);
		addConstraint(new LayoutConstraint(secondLabel, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));
		addConstraint(new LayoutConstraint(secondLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.centerX, 1.0, 8));

		courseList = new ALJTable();
		courseList.dataSource = this;
		add(courseList);
		addConstraint(new LayoutConstraint(courseList, LayoutAttribute.top, LayoutRelation.equal, label, LayoutAttribute.bottom, 1.0, 0));
		addConstraint(new LayoutConstraint(courseList, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.centerX, 1.0, 0));
		addConstraint(new LayoutConstraint(courseList, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, 0));
		addConstraint(new LayoutConstraint(courseList, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, 0));

		TestAssignment assignment = new TestAssignment();
		assignment.numberOfPeopleCompleted = 24;
		assignment.title = "Homework 1";
		assignments.add(assignment);
		//courses.add(Course.testCourse);
	}

	@Override
	public void layoutSubviews()
	{
		super.layoutSubviews();

		courseList.reloadData();
		nextDueTable.reloadData();
	}

	@Override
	public int numberOfSectionsIn(ALJTable table)
	{
		return 1;
	}

	@Override
	public int numberOfRowsInSectionForTable(ALJTable table, int section)
	{
		if (table == nextDueTable)
		{
			return assignments.size();
		}
		else if (table == courseList)
		{
			return courses.size();
		}
		return 0;
	}

	@Override
	public int heightForRow(ALJTable table, int inSection)
	{
		return 150;
	}

	@Override
	public ALJTableCell cellForRowAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		if (table == nextDueTable)
		{
			return new NextDueCell(assignments.get(index.item));
		}
		else if (table == courseList)
		{
			ClassCell cell = new ClassCell(ALJTableCellAccessoryViewType.delete);
			cell.setCourse(courses.get(index.item));
			return cell;
		}

		return null;
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

class TestAssignment
{
	String title;
	int numberOfPeopleCompleted;
}

class NextDueCell extends ALJTableCell
{
	NextDueCell(TestAssignment assignment)
	{
		super(ALJTableCellAccessoryViewType.none);

		removeConstraintsFor(titleLabel);

		titleLabel.setText(assignment.title);
		titleLabel.setFont(UIFont.textBold.deriveFont(9f));

		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 24));
		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));
		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, -8));

		JLabel detailLabelTwo = new JLabel(assignment.numberOfPeopleCompleted + " people have submitted so far");
		detailLabelTwo.setFont(UIFont.textLight.deriveFont(9f));
		add(detailLabelTwo);
		addConstraint(new LayoutConstraint(detailLabelTwo, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 24));
		addConstraint(new LayoutConstraint(detailLabelTwo, LayoutAttribute.top, LayoutRelation.equal, titleLabel, LayoutAttribute.bottom, 1.0, 8));
		addConstraint(new LayoutConstraint(detailLabelTwo, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, -8));
	}
}