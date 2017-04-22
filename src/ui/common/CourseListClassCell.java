package ui.common;

import objects.Course;
import ui.Window;
import ui.util.ALJTable.ALJTableCell;
import ui.util.ALJTable.ALJTableCellAccessoryViewType;
import ui.util.Alert;
import ui.util.ButtonType;
import ui.util.MLMEventType;
import ui.util.MouseListenerManager;
import uikit.UIFont;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class CourseListClassCell extends ALJTableCell
{
	private final JLabel detailLabelOne;
	private final JLabel detailLabelTwo;
	private final JLabel detailLabelThree;
	private final JLabel detailLabelFour;
	private Course course;

	public CourseListClassCell()
	{
		super(ALJTableCellAccessoryViewType.delete);

		removeConstraintsFor(titleLabel);

		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));
		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));
		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.trailing, LayoutRelation.equal, accessoryView, LayoutAttribute.leading, 1.0, -8));

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
		addConstraint(new LayoutConstraint(detailLabelTwo, LayoutAttribute.trailing, LayoutRelation.equal, accessoryView, LayoutAttribute.leading, 1.0, -8));

		detailLabelThree = new JLabel();
		detailLabelThree.setFont(UIFont.textLight.deriveFont(9f));
		add(detailLabelThree);
		addConstraint(new LayoutConstraint(detailLabelThree, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));
		addConstraint(new LayoutConstraint(detailLabelThree, LayoutAttribute.top, LayoutRelation.equal, detailLabelOne, LayoutAttribute.bottom, 1.0, 8));
		addConstraint(new LayoutConstraint(detailLabelThree, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.centerX, 1.0, 0));

		detailLabelFour = new JLabel();
		detailLabelFour.setFont(UIFont.textLight.deriveFont(9f));
		detailLabelFour.addMouseListener(new MouseListenerManager(this));
		detailLabelFour.setHorizontalAlignment(SwingConstants.RIGHT);
		add(detailLabelFour);
		addConstraint(new LayoutConstraint(detailLabelFour, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.centerX, 1.0, 0));
		addConstraint(new LayoutConstraint(detailLabelFour, LayoutAttribute.top, LayoutRelation.equal, detailLabelOne, LayoutAttribute.bottom, 1.0, 8));
		addConstraint(new LayoutConstraint(detailLabelFour, LayoutAttribute.trailing, LayoutRelation.equal, accessoryView, LayoutAttribute.leading, 1.0, -8));
	}

	public void setCourse(Course course)
	{
		titleLabel.setText(course.getTitle());
		detailLabelOne.setText("Hours: " + course.getMeetingTime());
		detailLabelTwo.setText("Room: " + course.getRoomNo());
		detailLabelThree.setText("Name: " + course.getCourseName());
		detailLabelFour.setText("Teacher: " + course.getTeachers().get(0).getFirstName() + course.getTeachers().get(0).getLastName());
		this.course = course;
	}

	@Override
	public void mousePoint(MouseEvent action, MLMEventType eventType)
	{
		super.mousePoint(action, eventType);
		if (action.getSource() == detailLabelFour && eventType == MLMEventType.released)
		{
			Alert changeQuota = new Alert("Change Quota", "Please input the new quota in megabytes for\n" + course.getCourseName());
			changeQuota.addTextField("Quota size", "ClassCell.quota", false);
			changeQuota.addButton("OK", ButtonType.plain, e ->
			{
				try
				{
					int newQuota = Integer.valueOf(changeQuota.textFieldForIdentifier("ClassCell.quota").getText());
					course.setMaxStorage(newQuota * 1024 * 1024);
					detailLabelFour.setText("Quota: " + course.getMaxStorage() / 1024 / 1024 + " MB");
					changeQuota.dispose();
				}
				catch (Exception ignored)
				{
					changeQuota.textFieldForIdentifier("ClassCell.quota").setBackground(Color.red);
				}
			}, true);
			changeQuota.addButton("Cancel", ButtonType.defaultType, null, false);
			changeQuota.show(Window.current.mainScreen);
		}
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(super.getPreferredSize().width, titleLabel.getPreferredSize().height + 32 + detailLabelTwo.getPreferredSize().height + detailLabelOne.getPreferredSize().height);
	}
}
