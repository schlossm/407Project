package ui.common;

import objects.Course;
import objects.userType;
import ui.instructor.InstructorAnnouncements;
import ui.student.StudentAnnouncements;
import ui.util.ALJTable.*;
import ui.util.UIVariables;
import uikit.UIFont;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJPanel;
import uikit.autolayout.uiobjects.ALJTablePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static javax.imageio.ImageIO.read;

public class CourseView extends ALJPanel
{
	private final CourseViewTabBar tabBar;

	private final Course courseToView;

	private final JLabel title;
	private ALJPanel activePanel;

	public CourseView(Course course)
	{
		courseToView = course;

		title = new JLabel(course.getTitle());
		title.setFont(UIFont.displayBlack.deriveFont(14f));
		add(title);
		addConstraint(new LayoutConstraint(title, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 20));
		addConstraint(new LayoutConstraint(title, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));

		tabBar = new CourseViewTabBar();
		tabBar.parent = this;
		add(tabBar);
		addConstraint(new LayoutConstraint(tabBar, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1, 20));
		addConstraint(new LayoutConstraint(tabBar, LayoutAttribute.top, LayoutRelation.equal, title, LayoutAttribute.bottom, 1, 0));
		addConstraint(new LayoutConstraint(tabBar, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1, 0));
		addConstraint(new LayoutConstraint(tabBar, LayoutAttribute.width, LayoutRelation.equal, null, LayoutAttribute.width, 1, 200));

		if (UIVariables.current.currentUser.getUserType() == userType.TEACHER)
		{
			activePanel = new InstructorAnnouncements(courseToView);
		}
		else
		{
			try
			{
				BufferedImage img = read(this.getClass().getResource("/uikit/images/" + "close-envelope@2x" + ".png"));
				Image scaledInstance = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
				JLabel email = new JLabel(new ImageIcon(scaledInstance));
				email.addMouseListener(new MouseListener()
				{
					@Override
					public void mouseClicked(MouseEvent e)
					{

					}

					@Override
					public void mousePressed(MouseEvent e)
					{

					}

					@Override
					public void mouseReleased(MouseEvent e)
					{
						if (e.getSource() == email)
						{
							URI mailURI;
							try
							{
								mailURI = new URI("mailto:" + course.getTeachers().get(0).getEmail() +"?subject=" + course.getCourseName().replace(" ", "%20") + "%20[[INSERT%20QUESTION%20HERE]]");
							}
							catch (URISyntaxException e1)
							{
								e1.printStackTrace();
								return;
							}
							try
							{
								Desktop.getDesktop().mail(mailURI);
							}
							catch (IOException e1)
							{
								e1.printStackTrace();
							}
						}
					}

					@Override
					public void mouseEntered(MouseEvent e)
					{

					}

					@Override
					public void mouseExited(MouseEvent e)
					{

					}
				});
				add(email);
				addConstraint(new LayoutConstraint(email, LayoutAttribute.leading, LayoutRelation.equal, title, LayoutAttribute.trailing, 1.0, 20));
				addConstraint(new LayoutConstraint(email, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));
				addConstraint(new LayoutConstraint(email, LayoutAttribute.width, LayoutRelation.equal, null, LayoutAttribute.width, 1.0, 30));
				addConstraint(new LayoutConstraint(email, LayoutAttribute.height, LayoutRelation.equal, null, LayoutAttribute.height, 1.0, 30));
			}
			catch (Exception e) { e.printStackTrace();}
			activePanel = new StudentAnnouncements(courseToView);
		}

		add(activePanel);
		addConstraint(new LayoutConstraint(activePanel, LayoutAttribute.leading, LayoutRelation.equal, tabBar, LayoutAttribute.trailing, 1, 8));
		addConstraint(new LayoutConstraint(activePanel, LayoutAttribute.top, LayoutRelation.equal, title, LayoutAttribute.bottom, 1, 20));
		addConstraint(new LayoutConstraint(activePanel, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1, 0));
		addConstraint(new LayoutConstraint(activePanel, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1, 0));
	}

	void changeTab(String newTab)
	{
		ALJPanel newPanel;
		switch (newTab)
		{
			case "Announcements":
			{
				if (!(activePanel instanceof InstructorAnnouncements) && !(activePanel instanceof StudentAnnouncements))
				{
					if (UIVariables.current.currentUser.getUserType() == userType.TEACHER)
					{
						newPanel = new InstructorAnnouncements(courseToView);
					}
					else
					{
						newPanel = new StudentAnnouncements(courseToView);
					}
				}
				else { return; }
				break;
			}

			case "Files":
			{
				if (!(activePanel instanceof FileList))
				{
					newPanel = new FileList(courseToView);
				}
				else { return; }
				break;
			}

			default:
			{
				System.err.println(newTab + " has not been implemented yet.  I feel like this is dêjá vu.");
				return;
			}
		}

		remove(activePanel);
		add(newPanel);
		addConstraint(new LayoutConstraint(newPanel, LayoutAttribute.leading, LayoutRelation.equal, tabBar, LayoutAttribute.trailing, 1, 8));
		addConstraint(new LayoutConstraint(newPanel, LayoutAttribute.top, LayoutRelation.equal, title, LayoutAttribute.bottom, 1, 20));
		addConstraint(new LayoutConstraint(newPanel, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1, 0));
		addConstraint(new LayoutConstraint(newPanel, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1, 0));
		activePanel = newPanel;

		layoutSubviews();
		activePanel.layoutSubviews();
		activePanel.repaint();
		activePanel.layoutSubviews();
	}
}

class CourseViewTabBar extends ALJTablePanel
{
	private final String[] options = new String[]{"Announcements", "Files", "Assignments", "Grades"};
	CourseView parent;

	@Override
	public void didSelectItemAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		parent.changeTab(options[index.item]);
	}

	@Override
	public int numberOfSectionsIn(ALJTable table)
	{
		return 1;
	}

	@Override
	public int numberOfRowsInSectionForTable(ALJTable table, int section)
	{
		return options.length;
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
		cell.titleLabel.setFont(UIFont.textLight.deriveFont(9f));
		cell.titleLabel.setText(options[index.item]);
		return cell;
	}

	@Override
	public String titleForHeaderInSectionInTable(ALJTable table, int section)
	{
		return "Course Links";
	}

	@Override
	public String titleForFooterInSectionInTable(ALJTable table, int section)
	{
		return null;
	}

	@Override
	public void tableView(ALJTable table, ALJTableCellEditingStyle commit, ALJTableIndex forRowAt) { }
}