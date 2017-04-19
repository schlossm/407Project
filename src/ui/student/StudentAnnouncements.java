package ui.student;

import json.AnnouncementQuery;
import objects.Course;
import objects.Message;
import ui.Window;
import ui.util.ALJTable.ALJTable;
import ui.util.ALJTable.ALJTableCell;
import ui.util.ALJTable.ALJTableCellEditingStyle;
import ui.util.ALJTable.ALJTableIndex;
import ui.util.Alert;
import ui.util.AnnouncementCell;
import ui.util.ButtonType;
import ui.util.UIVariables;
import uikit.UIFont;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJTablePanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class StudentAnnouncements extends ALJTablePanel
{
	private final Map<String, ArrayList<Object>> announcementData = new HashMap<>();

	private final JLabel loadingLabel;

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
			announcementData.put("", savedAnnouncements);
		}

		AnnouncementQuery query = new AnnouncementQuery();
		query.getAllAnnouncementInCourse(course.getCourseID(), (returnedData, error) ->
		{
			if (error != null)
			{
				Alert errorAlert = new Alert("Error", "ABC could not load the announcements.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null, false);
				errorAlert.show(Window.current.mainScreen);
				return;
			}
			if (returnedData instanceof ArrayList)
			{
				ArrayList<Object> messages = (ArrayList<Object>) returnedData;
				announcementData.put("", messages);
				UIVariables.current.globalUserData.put("announcements" + course.getCourseID(), messages);
			}
		});
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

}
