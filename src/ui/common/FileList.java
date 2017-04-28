package ui.common;

import json.DocumentsQuery;
import objects.Course;
import objects.FileUpload;
import objects.userType;
import ui.Window;
import ui.util.ALJTable.*;
import ui.util.Alert;
import ui.util.ButtonType;
import ui.util.UIVariables;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJTablePanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
class FileList extends ALJTablePanel
{
	private final Map<String, ArrayList<Object>> fileListData = new HashMap<>();
	private final DocumentsQuery query = new DocumentsQuery();
	private File tempFile = null;
	private Course course;

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
			fileListData.put("Files", (ArrayList<Object>) UIVariables.current.globalUserData.get("files"));
		}

		reloadDocs();
	}

	public void reloadDocs()
	{
		query.getAllDocumentsIdsInCourse(course.getCourseID(), ((returnedData, error) -> {
			if (error != null)
			{
				if (error.code == 3)
				{
					return;
				}
				Alert errorAlert = new Alert("Error", "ABC could not load the files for this course.  Please try again");
				errorAlert.addButton("OK", ButtonType.defaultType, null);
				errorAlert.show(Window.current.mainScreen);
				return;
			}

			if (returnedData instanceof ArrayList)
			{
				ArrayList<Object> files = (ArrayList<Object>) returnedData;
				fileListData.put("Files", files);
				table.reloadData();
			}
			else
			{
				Alert errorAlert = new Alert("Error", "ABC could not load the files for this course.  Please try again");
				errorAlert.addButton("OK", ButtonType.defaultType, null);
				errorAlert.show(Window.current.mainScreen);
			}
		}));
	}

	private void updateSavedInfo()
	{
		UIVariables.current.globalUserData.put("files", fileListData.get("Files"));
	}

	private void addNewFile(File fileToShow)
	{
		Alert alert = new Alert("New File", "Choose file to upload");
		alert.addTextField("File Name", "fileName", false);

		if (fileToShow != null)
		{
			alert.textFieldForIdentifier("fileName").setText(fileToShow.getName());
		}

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
				addNewFile(tempFile);
			}
		});
		alert.addButton("Cancel", ButtonType.cancel, null);
		alert.addButton("Upload", ButtonType.defaultType, e ->
		{
			//FIXME: implement method after it's updated
			new DocumentsQuery().addDocument(tempFile, alert.textFieldForIdentifier("fileName").getText(), "A File", UIVariables.current.currentUser.getUserID(), "-1", String.valueOf(course.getCourseID()), alert.checkBoxForIdentifier("private").isSelected() ? 1 : 0, ((returnedData, error) ->
			{
				if (error != null)
				{
					Alert errorAlert = new Alert("Error", "ABC could not upload the file.  Please try again");
					errorAlert.addButton("OK", ButtonType.defaultType, null);
					errorAlert.show(Window.current.mainScreen);
					return;
				}
				if (returnedData instanceof Boolean)
				{
					if ((Boolean) returnedData)
					{
						reloadDocs();
					}
					else
					{
						Alert errorAlert = new Alert("Error", "ABC could not upload the file.  Please try again");
						errorAlert.addButton("OK", ButtonType.defaultType, null);
						errorAlert.show(Window.current.mainScreen);
					}
				}
				else
				{
					Alert errorAlert = new Alert("Error", "ABC could not upload the file.  Please try again");
					errorAlert.addButton("OK", ButtonType.defaultType, null);
					errorAlert.show(Window.current.mainScreen);
				}
			}));
			/*
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
			};*/
		});
		alert.show(Window.current.mainScreen);
	}

	@Override
	public void didSelectItemAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		if (index.section == 0 && index.item == 0 && isInstructor())
		{
			addNewFile(null);
		}
		else
		{
			FileUpload upload = (FileUpload) fileListData.get("Files").get(index.item);
			query.getDocument(upload.getDocumentid(), ((returnedData, error) -> {
				if (error != null)
				{
					Alert errorAlert = new Alert("Error", "ABC could not load the files for this course.  Please try again");
					errorAlert.addButton("OK", ButtonType.defaultType, null);
					errorAlert.show(Window.current.mainScreen);
					return;
				}
				if (returnedData instanceof File)
				{
					try
					{
						Desktop.getDesktop().open((File)returnedData);
					}
					catch (IOException e)
					{
						e.printStackTrace();
						Alert errorAlert = new Alert("Error", "ABC could not load the files for this course.  Please try again");
						errorAlert.addButton("OK", ButtonType.defaultType, null);
						errorAlert.show(Window.current.mainScreen);
					}
				}
			}));
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
			return new FileListCell(ALJTableCellAccessoryViewType.delete, (FileUpload) fileListData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item));
		}
		else
		{
			return new FileListCell(ALJTableCellAccessoryViewType.none, (FileUpload) fileListData.get(titleForHeaderInSectionInTable(table, index.section)).get(index.item));
		}
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
}

class FileListCell extends ALJTableCell
{
	FileListCell(ALJTableCellAccessoryViewType accessoryViewType, FileUpload info)
	{
		super(accessoryViewType);

		removeConstraintsFor(titleLabel);
		titleLabel.setText(info.getTitle());

		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));
		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));

		if (UIVariables.current.isInstructor())
		{
			JCheckBox checkBox = new JCheckBox("Private", info.getIsPrivate() == 1);
			checkBox.addActionListener(e -> {

				new DocumentsQuery().setDocumentPrivateField(checkBox.isSelected() ? 1 : 0, info.getDocumentid(), ((returnedData, error) -> {
					if (error != null)
					{
						Alert errorAlert = new Alert("Error", "ABC could not change the privacy status of this file.  Please try again");
						errorAlert.addButton("OK", ButtonType.defaultType, null);
						errorAlert.show(Window.current.mainScreen);
						return;
					}
					if (returnedData instanceof Boolean)
					{
						if (!(Boolean) returnedData)
						{
							Alert errorAlert = new Alert("Error", "ABC could not change the privacy status of this file.  Please try again");
							errorAlert.addButton("OK", ButtonType.defaultType, null);
							errorAlert.show(Window.current.mainScreen);
						}
					}
					else
					{
						Alert errorAlert = new Alert("Error", "ABC could not change the privacy status of this file.  Please try again");
						errorAlert.addButton("OK", ButtonType.defaultType, null);
						errorAlert.show(Window.current.mainScreen);
					}
				}));
			});
			add(checkBox);
			addConstraint(new LayoutConstraint(checkBox, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));
			addConstraint(new LayoutConstraint(checkBox, LayoutAttribute.top, LayoutRelation.equal, titleLabel, LayoutAttribute.bottom, 1.0, 8));
		}

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
