package ui.common;

import objects.Course;
import objects.Instructor;
import objects.userType;
import ui.util.ALJTable.ALJTableCell;
import ui.util.ALJTable.ALJTableCellAccessoryViewType;
import ui.util.UIVariables;
import uikit.UIFont;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;

import javax.swing.*;
import java.awt.*;

public class CourseListClassCell extends ALJTableCell
{
	private final JLabel detailLabelOne;
	private final JLabel detailLabelTwo;
	private final JLabel detailLabelThree;
	private final JLabel detailLabelFour;

	public CourseListClassCell()
	{
		super(ALJTableCellAccessoryViewType.none);

		removeConstraintsFor(titleLabel);

		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));
		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));
		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, -8));

		detailLabelOne = new JLabel();
		detailLabelOne.setFont(UIFont.textLight.deriveFont(9f));
		add(detailLabelOne);
		addConstraint(new LayoutConstraint(detailLabelOne, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));
		addConstraint(new LayoutConstraint(detailLabelOne, LayoutAttribute.top, LayoutRelation.equal, titleLabel, LayoutAttribute.bottom, 1.0, 8));
		addConstraint(new LayoutConstraint(detailLabelOne, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.centerX, 1.0, 0));

		detailLabelTwo = new JLabel();
		detailLabelTwo.setFont(UIFont.textLight.deriveFont(9f));
		detailLabelTwo.setHorizontalAlignment(SwingConstants.RIGHT);
		add(detailLabelTwo);
		addConstraint(new LayoutConstraint(detailLabelTwo, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.centerX, 1.0, 0));
		addConstraint(new LayoutConstraint(detailLabelTwo, LayoutAttribute.top, LayoutRelation.equal, titleLabel, LayoutAttribute.bottom, 1.0, 8));
		addConstraint(new LayoutConstraint(detailLabelTwo, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, -8));

		detailLabelThree = new JLabel();
		detailLabelThree.setFont(UIFont.textLight.deriveFont(9f));
		add(detailLabelThree);
		addConstraint(new LayoutConstraint(detailLabelThree, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));
		addConstraint(new LayoutConstraint(detailLabelThree, LayoutAttribute.top, LayoutRelation.equal, detailLabelOne, LayoutAttribute.bottom, 1.0, 8));
		addConstraint(new LayoutConstraint(detailLabelThree, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.centerX, 1.0, 0));

		detailLabelFour = new JLabel();
		detailLabelFour.setFont(UIFont.textLight.deriveFont(9f));
		detailLabelFour.setHorizontalAlignment(SwingConstants.RIGHT);
		add(detailLabelFour);
		addConstraint(new LayoutConstraint(detailLabelFour, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.centerX, 1.0, 0));
		addConstraint(new LayoutConstraint(detailLabelFour, LayoutAttribute.top, LayoutRelation.equal, detailLabelOne, LayoutAttribute.bottom, 1.0, 8));
		addConstraint(new LayoutConstraint(detailLabelFour, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, -8));
	}

	public void setCourse(Course course)
	{
		titleLabel.setText(course.getTitle());
		detailLabelOne.setText("Hours: " + course.getMeetingTime());
		detailLabelTwo.setText("Room: " + course.getRoomNo());
		detailLabelThree.setText("Name: " + course.getCourseName());
		if (course.getTeachers() != null)
		{
			StringBuilder stringBuilder = new StringBuilder("Teacher: ");
			for (Instructor instructor : course.getTeachers())
			{
				stringBuilder.append(instructor.getFirstName()).append(" ").append(instructor.getLastName()).append(", ");
			}
			stringBuilder.delete(stringBuilder.toString().length() - 2, stringBuilder.toString().length());
			detailLabelFour.setText(stringBuilder.toString());
		}
		else if (course.getStudents() != null)
		{

			detailLabelFour.setText(course.getStudents().size() + (course.getStudents().size() == 1 ?  " student" : " students") + " in this course");
		}
		else
		{
			if (UIVariables.current.currentUser.getUserType() == userType.STUDENT)
			{
				detailLabelFour.setText("Loading teachers");
			}
			else
			{
				detailLabelFour.setText("Loading students");
			}
		}
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(super.getPreferredSize().width, titleLabel.getPreferredSize().height + 32 + detailLabelTwo.getPreferredSize().height + detailLabelOne.getPreferredSize().height);
	}
}
