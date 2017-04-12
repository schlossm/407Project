package ui.common;

import json.DocumentsQuery;
import objects.Course;
import objects.userType;
import ui.Window;
import ui.util.ALJTable.*;
import ui.util.Alert;
import ui.util.ButtonType;
import ui.util.UIStrings;
import ui.util.UIVariables;
import uikit.DFNotificationCenter;
import uikit.DFNotificationCenterDelegate;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJTablePanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

@SuppressWarnings("unchecked")
class FileList extends ALJTablePanel implements DFNotificationCenterDelegate
{
	private Map<String, ArrayList<Object>> fileListData = new HashMap<>();

	private DocumentsQuery query = new DocumentsQuery();

	private Runnable workToDoOnSuccess = null;
	private Runnable workToDoOnFailure = null;

	Course course;

	FileList(Course course)
	{
		this.course = course;
		if (isInstructor())
		{
			ArrayList<Object> beginner = new ArrayList<>();
			beginner.add("Upload File");
			fileListData.put("", beginner);
		}

		if (UIVariables.current.globalUserData.get("files") != null)
		{
			fileListData.put("Files", (ArrayList<Object>)UIVariables.current.globalUserData.get("files"));
		}

		DFNotificationCenter.defaultCenter.register(this, UIStrings.returned);
		query.getAllDocumentsIdsInCourse(course.getCourseID());
	}

	@Override
	public void removeNotify()
	{
		super.removeNotify();
		DFNotificationCenter.defaultCenter.remove(this);
	}

	private void updateSavedInfo()
	{
		UIVariables.current.globalUserData.put("files", fileListData.get("Files"));
	}

	private File tempFile = null;

	private void add()
	{
		Alert alert = new Alert("New File", "Choose file to upload");
		alert.addTextField("File Name", "fileName", false);
		alert.addCheckBox("Private", "private");
		alert.addButton("Choose File", ButtonType.plain, e ->
		{
			final JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(Window.current.mainScreen);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = fc.getSelectedFile();
				alert.textFieldForIdentifier("fileName").setText(file.getName());
				tempFile = file;
			}
		}, true);
		alert.addButton("Cancel", ButtonType.cancel, null, false);
		alert.addButton("Upload", ButtonType.defaultType, e ->
		{
			//FIXME: implement method after it's updated
			query.addDocument();
			workToDoOnSuccess = () ->
			{
				if (fileListData.get("Files") != null)
				{
					FileListFileInfo fileInfo = new FileListFileInfo();
					fileInfo.file = tempFile.toPath();
					fileInfo.name = alert.textFieldForIdentifier("fileName").getText();
					fileInfo.isPrivate = alert.checkBoxForIdentifier("private").isSelected();
					fileListData.get("Files").add(fileInfo);
					updateSavedInfo();
				}
				else
				{
					ArrayList<Object> arrayList = new ArrayList<>();
					FileListFileInfo fileInfo = new FileListFileInfo();
					fileInfo.file = tempFile.toPath();
					fileInfo.name = alert.textFieldForIdentifier("fileName").getText();
					fileInfo.isPrivate = alert.checkBoxForIdentifier("private").isSelected();
					arrayList.add(fileInfo);
					fileListData.put("Files", arrayList);
					updateSavedInfo();
				}

				tempFile = null;
				table.reloadData();
				alert.dispose();
			};

			workToDoOnFailure = () ->
			{
				Alert errorAlert = new Alert("Error", "ABC could not add the file.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null, false);
				errorAlert.show(Window.current.mainScreen);
			};
			workToDoOnSuccess.run();
		}, true);
		alert.show(Window.current.mainScreen);
	}

	@Override
	public void didSelectItemAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		if (index.section == 0 && index.item == 0)
		{
			add();
		}
		else
		{
			//TODO: Fix this.  Will need to download the file instead of just copying.
			Path file = ((FileListFileInfo) fileListData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item)).file;

			File destinationCopy = new File(UIVariables.current.applicationDirectories.library + file.toFile().getName());

			try
			{
				Files.copy(file, destinationCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);
				Desktop.getDesktop().open(destinationCopy);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public int numberOfSectionsIn(ALJTable table)
	{
		return fileListData.keySet().size();
	}

	@Override
	public int numberOfRowsInSectionForTable(ALJTable table, int section)
	{
		return fileListData.get(titleForHeaderInSectionInTable(table, section)).size();
	}

	@Override
	public int heightForRow(ALJTable table, int inSection)
	{
		if (inSection == 0)
		{
			return 44;
		}
		else
		{
			return 80;
		}
	}

	@Override
	public ALJTableCell cellForRowAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		if (index.section == 0 && isInstructor())
		{
			ALJTableCell cell = new ALJTableCell(ALJTableCellAccessoryViewType.none);
			cell.titleLabel.setText((String) fileListData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item));
			return cell;
		}
		else if (isInstructor())
		{
			return new FileListCell(ALJTableCellAccessoryViewType.delete, (FileListFileInfo) fileListData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item));
		}
		return new ALJTableCell(ALJTableCellAccessoryViewType.none);
	}

	@Override
	public String titleForHeaderInSectionInTable(ALJTable table, int section)
	{
		return (String) fileListData.keySet().toArray()[section];
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

	@Override
	public void performActionFor(String notificationName, Object userData)
	{
		if (Objects.equals(notificationName, UIStrings.returned))
		{
			//TODO: Implement loading of files
		}
		else if (Objects.equals(notificationName, UIStrings.success))
		{
			workToDoOnSuccess.run();
		}
		else if (Objects.equals(notificationName, UIStrings.failure))
		{
			workToDoOnFailure.run();
		}
	}
}

class FileListCell extends ALJTableCell
{
	FileListCell(ALJTableCellAccessoryViewType accessoryViewType, FileListFileInfo info)
	{
		super(accessoryViewType);

		removeConstraintsFor(titleLabel);
		titleLabel.setText(info.file.toFile().getName());

		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));
		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));

		JCheckBox checkBox = new JCheckBox("Private", info.isPrivate);
		checkBox.addActionListener(e -> info.isPrivate = checkBox.isSelected());
		add(checkBox);
		addConstraint(new LayoutConstraint(checkBox, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));
		addConstraint(new LayoutConstraint(checkBox, LayoutAttribute.top, LayoutRelation.equal, titleLabel, LayoutAttribute.bottom, 1.0, 8));

		if (accessoryViewType != ALJTableCellAccessoryViewType.none)
		{
			addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.trailing, LayoutRelation.equal, accessoryView, LayoutAttribute.leading, 1.0, -8));
		}
		else
		{
			addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, -8));
		}



	}
}
