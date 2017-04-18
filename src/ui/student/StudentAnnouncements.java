package ui.student;

import json.AnnouncementQuery;
import objects.Course;
import objects.Message;
import ui.util.ALJTable.ALJTable;
import ui.util.ALJTable.ALJTableCell;
import ui.util.ALJTable.ALJTableCellEditingStyle;
import ui.util.ALJTable.ALJTableIndex;
import ui.util.AnnouncementCell;
import ui.util.UIStrings;
import ui.util.UIVariables;
import uikit.DFNotificationCenter;
import uikit.DFNotificationCenterDelegate;
import uikit.UIFont;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJTablePanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class StudentAnnouncements extends ALJTablePanel implements DFNotificationCenterDelegate
{
	private final Map<String, ArrayList<Object>> announcementData = new HashMap<>();

	private JLabel loadingLabel;

	public StudentAnnouncements(Course course)
	{
		loadingLabel = new JLabel("Loading");
		loadingLabel.setFont(UIFont.textLight.deriveFont(30f));
		loadingLabel.setHorizontalTextPosition(JLabel.CENTER);
		add(loadingLabel, 2);
		addConstraint(new LayoutConstraint(loadingLabel, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 0));
		addConstraint(new LayoutConstraint(loadingLabel, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, 0));
		addConstraint(new LayoutConstraint(loadingLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 0));
		addConstraint(new LayoutConstraint(loadingLabel, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, 0));

		if (UIVariables.current.globalUserData.get("announcements" + course.getCourseID()) != null)
		{
			remove(loadingLabel);
			ArrayList<Object> savedAnnouncements = (ArrayList<Object>) UIVariables.current.globalUserData.get("announcements" + course.getCourseID());
			announcementData.put("Announcements", savedAnnouncements);
		}

		DFNotificationCenter.defaultCenter.register(this, UIStrings.returned);
		AnnouncementQuery query = new AnnouncementQuery();
		query.getAllAnnouncementInCourse(course.getCourseID());
	}

	@Override
	public void removeNotify()
	{
		super.removeNotify();
		DFNotificationCenter.defaultCenter.remove(this);
	}

	@Override
	public void didSelectItemAtIndexInTable(ALJTable table, ALJTableIndex index) { }

	@Override
	public int numberOfSectionsIn(ALJTable table)
	{
		return announcementData.keySet().size();
	}

	@Override
	public int numberOfRowsInSectionForTable(ALJTable table, int section)
	{
		return announcementData.get(titleForHeaderInSectionInTable(table, section)).size();
	}

	@Override
	public int heightForRow(ALJTable table, int inSection)
	{
		return 106;
	}

	@Override
	public ALJTableCell cellForRowAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		return new AnnouncementCell((Message) announcementData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item));
	}

	@Override
	public String titleForHeaderInSectionInTable(ALJTable table, int section)
	{
		return (String) announcementData.keySet().toArray()[section];
	}

	@Override
	public String titleForFooterInSectionInTable(ALJTable table, int section)
	{
		return null;
	}

	@Override
	public void tableView(ALJTable tableView, ALJTableCellEditingStyle commit, ALJTableIndex forRowAt) { }

	@Override
	public void performActionFor(String notificationName, Object userData)
	{
		if (Objects.equals(notificationName, UIStrings.returned))
		{
			if (userData != null)
			{
				//remove(loadingLabel);
			}
		}
	}

}
