package ui.common;

import json.InstructorQuery;
import json.StudentQuery;
import json.UserQuery;
import objects.Course;
import objects.Grade;
import objects.User;
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
import java.util.Objects;

@SuppressWarnings("unchecked")
public class GradesView extends ALJTablePanel
{
	private ArrayList<GradeData> gradeData = new ArrayList<>();

	public GradesView(Course course)
	{
		//A Teacher is looking
		if (UIVariables.current.isInstructor())
		{
			new InstructorQuery().getGradeOfAllStudentsInCourse(course.getCourseID(), (returnedData, error) -> {
				if (error != null)
				{
					if (error.code == 3)
					{
						return;
					}
					Alert errorAlert = new Alert("Error", "ABC could not load the grades for this course.  Please try again");
					errorAlert.addButton("OK", ButtonType.defaultType, null);
					errorAlert.show(Window.current.mainScreen);
					return;
				}
				if (returnedData instanceof ArrayList)
				{
					ArrayList<Grade> grades = (ArrayList<Grade>) returnedData;

					for (Grade grade : grades)
					{
						new UserQuery().getUser(grade.getUserID(), ((returnedData1, error1) -> {
							if (error1 != null)
							{
								Alert errorAlert = new Alert("Error", "ABC could not load the grades for this course.  Please try again");
								errorAlert.addButton("OK", ButtonType.defaultType, null);
								errorAlert.show(Window.current.mainScreen);
								return;
							}
							if (returnedData1 instanceof User)
							{
								User user = (User)returnedData1;
								boolean contains = false;
								for (GradeData data : gradeData)
								{
									if (Objects.equals(data.title, user.getFirstName() + " " + user.getLastName()))
									{
										contains = true;
										data.grade += Double.valueOf(grade.getScore());
									}
								}

								if (!contains)
								{
									GradeData data = new GradeData();
									data.title = user.getFirstName() + " " + user.getLastName();
									data.grade = Double.valueOf(grade.getScore());
									gradeData.add(data);

									if (grades.indexOf(grade) == grades.size() - 1) //We are at the last one
									{
										table.reloadData();
									}
								}
							}
						}));
					}
				}
				else
				{
					Alert errorAlert = new Alert("Error", "ABC could not load the grades for this course.  Please try again");
					errorAlert.addButton("OK", ButtonType.defaultType, null);
					errorAlert.show(Window.current.mainScreen);
				}
			});
		}
		else
		{
			new StudentQuery().getAllGradeInCourse(UIVariables.current.currentUser.getUserID(), course.getCourseID(), (returnedData, error) -> {
				if (error != null)
				{
					if (error.code == 3)
					{
						return;
					}
					Alert errorAlert = new Alert("Error", "ABC could not load your grades for this course.  Please try again");
					errorAlert.addButton("OK", ButtonType.defaultType, null);
					errorAlert.show(Window.current.mainScreen);
					return;
				}
				if (returnedData instanceof ArrayList)
				{
					ArrayList<Grade> grades = (ArrayList<Grade>) returnedData;

					for (Grade grade : grades)
					{
						boolean contains = false;
						for (GradeData data : gradeData)
						{
							if (Objects.equals(data.title, grade.getAssignmentName()))
							{
								contains = true;
								data.grade += Double.valueOf(grade.getScore());
							}
						}

						if (!contains)
						{
							GradeData data = new GradeData();
							data.title = grade.getAssignmentName();
							data.grade = Double.valueOf(grade.getScore());
							gradeData.add(data);

							if (grades.indexOf(grade) == grades.size() - 1) //We are at the last one
							{
								table.reloadData();
							}
						}
					}
				}
				else
				{
					Alert errorAlert = new Alert("Error", "ABC could not load your grades for this course.  Please try again");
					errorAlert.addButton("OK", ButtonType.defaultType, null);
					errorAlert.show(Window.current.mainScreen);
				}
			});
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
		return gradeData.size();
	}

	@Override
	public int heightForRow(ALJTable table, int inSection)
	{
		return 66;
	}

	@Override
	public ALJTableCell cellForRowAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		return new GradeCell(gradeData.get(index.item).title, gradeData.get(index.item).grade);
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

class GradeCell extends ALJTableCell
{
	GradeCell(String text, double points)
	{
		super(ALJTableCellAccessoryViewType.none);

		removeConstraintsFor(titleLabel);
		titleLabel.setText(text);
		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));
		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));
		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, -8));

		JLabel pointsLabel = new JLabel(String.valueOf(points));
		pointsLabel.setFont(UIFont.textLight.deriveFont(14f));
		pointsLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		add(pointsLabel);
		addConstraint(new LayoutConstraint(pointsLabel, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, -8));
		addConstraint(new LayoutConstraint(pointsLabel, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));
		addConstraint(new LayoutConstraint(pointsLabel, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, -8));
		addConstraint(new LayoutConstraint(pointsLabel, LayoutAttribute.width, LayoutRelation.equal, null, LayoutAttribute.width, 1.0, 100));
		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.trailing, LayoutRelation.equal, pointsLabel, LayoutAttribute.leading, 1.0, -8));
	}
}

class GradeData
{
	public String title;
	public double grade;
}