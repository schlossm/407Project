package ui.common;

import json.AssignmentQuery;
import json.DocumentsQuery;
import objects.*;
import ui.Window;
import ui.util.ALJTable.*;
import ui.util.Alert;
import ui.util.ButtonType;
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
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class AssignmentsList extends ALJTablePanel implements DFNotificationCenterDelegate
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
		add(loadingLabel);
		addConstraint(new LayoutConstraint(loadingLabel, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 0));
		addConstraint(new LayoutConstraint(loadingLabel, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, 0));
		addConstraint(new LayoutConstraint(loadingLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 0));
		addConstraint(new LayoutConstraint(loadingLabel, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, 0));

		DFNotificationCenter.defaultCenter.register(this, UIStrings.reloadDataNotification);
		new AssignmentQuery().getAllAssignmentsInCourse(course.getCourseID(), (returnedData, error) ->
		{
			if (error != null)
			{
				if (error.code == 3)
				{
					loadingLabel.setText("No Assignments Yet!");
					return;
				}
				Alert errorAlert = new Alert("Error", "ABC could not load the assignments for this course.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null);
				errorAlert.show(Window.current.mainScreen);
				return;
			}
			if (returnedData instanceof ArrayList)
			{
				assignments = (ArrayList<Assignment>) returnedData;
				remove(loadingLabel);
				table.reloadData();
			}
			else
			{
				Alert errorAlert = new Alert("Error", "ABC could not load the assignments for this course.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null);
				errorAlert.show(Window.current.mainScreen);
			}
		});
	}

	private void add()
	{
		Alert assignmentType = new Alert("", "");
		assignmentType.addButton("Quiz / Test", ButtonType.plain, e -> Window.current.openQuizCreationFor(course));
		assignmentType.addButton("File Submission", ButtonType.plain, e ->
		{
			Alert alert = new Alert("Add New Assignment", null);
			alert.addButton("Cancel", ButtonType.defaultType, null);
			alert.addTextField("Title", "assignment.title", false);
			alert.addTextField("Due Date (MM/DD/YYYY HH:MM A/P)", "assignment.dueDate", false);
			alert.addTextField("Number of points", "assignment.points", false);
			alert.addButton("Submit", ButtonType.plain, e2 ->
			{
				new AssignmentQuery().addAssignment(course.getCourseID(), alert.textFieldForIdentifier("assignment.title").getText(), UIVariables.dateToSQLDATETIME(alert.textFieldForIdentifier("assignment.dueDate").getText()), Integer.valueOf(alert.textFieldForIdentifier("assignment.points").getText()), "file", ((returnedData, error) ->
				{
					if (error != null)
					{
						Alert errorAlert = new Alert("Error", "ABC could not submit the assignment.  Please try again.");
						errorAlert.addButton("OK", ButtonType.defaultType, null);
						errorAlert.show(Window.current.mainScreen);
						return;
					}

					if (returnedData instanceof Boolean)
					{
						if ((Boolean) returnedData)
						{
							DFNotificationCenter.defaultCenter.post(UIStrings.reloadDataNotification, null);
						}
						else
						{
							Alert errorAlert = new Alert("Error", "ABC could not submit the assignment.  Please try again.");
							errorAlert.addButton("OK", ButtonType.defaultType, null);
							errorAlert.show(Window.current.mainScreen);
						}
					}
					else
					{
						Alert errorAlert = new Alert("Error", "ABC could not submit the assignment.  Please try again.");
						errorAlert.addButton("OK", ButtonType.defaultType, null);
						errorAlert.show(Window.current.mainScreen);
					}
				}));
			});
			alert.show(Window.current.mainScreen);
		});
		assignmentType.addButton("Cancel", ButtonType.cancel, null);
		assignmentType.show(Window.current.mainScreen);
	}

	private File tempFile = null;

	@Override
	public void didSelectItemAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		if (UIVariables.current.isInstructor())
		{
			if (index.section == 0)
			{
				add();
			}
			else
			{
				Assignment assignmentClicked = assignments.get(index.item);
				if (!Objects.equals(assignmentClicked.getType(), "quiz"))
				{
					new DocumentsQuery().getAllDocumentsIdsGivenAssignment(assignmentClicked.getAssignmentID(), ((returnedData, error) ->
					{
						if (error != null)
						{
							if (error.code == 3)
							{
								return;
							}
							Alert errorAlert = new Alert("Error", "ABC could not download the files for this assignment.  Please try again");
							errorAlert.addButton("OK", ButtonType.defaultType, null);
							errorAlert.show(Window.current.mainScreen);
							return;
						}
						if (returnedData instanceof ArrayList)
						{
							ArrayList<FileUpload> uploads = (ArrayList<FileUpload>) returnedData;
							Alert fileChoices = new Alert("Choose student to view", null);
							for (FileUpload upload : uploads)
							{
								fileChoices.addButton(upload.getAuthoruserid(), ButtonType.plain, e ->
								{
									Alert userOptions = new Alert("Choose Action", null);
									userOptions.addButton("Download Submission", ButtonType.plain, e1 -> new DocumentsQuery().getDocument(upload.getDocumentid(), ((returnedData1, error1) ->
									{
										if (error1 != null)
										{
											Alert errorAlert = new Alert("Error", "ABC could not download the file.  Please try again");
											errorAlert.addButton("OK", ButtonType.defaultType, null);
											errorAlert.show(Window.current.mainScreen);
											return;
										}
										if (returnedData1 instanceof File)
										{
											try
											{
												Desktop.getDesktop().open((File) returnedData1);
											}
											catch (IOException e2)
											{
												Alert errorAlert = new Alert("Error", "ABC could not download the file.  Please try again");
												errorAlert.addButton("OK", ButtonType.defaultType, null);
												errorAlert.show(Window.current.mainScreen);
											}
										}
										else
										{
											Alert errorAlert = new Alert("Error", "ABC could not download the file.  Please try again");
											errorAlert.addButton("OK", ButtonType.defaultType, null);
											errorAlert.show(Window.current.mainScreen);
										}
									})));
									userOptions.addButton("Enter Grade", ButtonType.plain, e1 ->
									{
										Alert enterGrade = new Alert("Enter Grade", "Enter " + upload.getAuthoruserid() + "'s grade");
										enterGrade.addTextField("Grade", "grade", false);
										enterGrade.addButton("Cancel", ButtonType.cancel, null);
										enterGrade.addButton("Submit Grade", ButtonType.plain, null);
										enterGrade.show(Window.current.mainScreen);

									});
									userOptions.addButton("Cancel", ButtonType.cancel, null);
									userOptions.show(Window.current.mainScreen);
								});
							}
							fileChoices.addButton("Cancel", ButtonType.cancel, null);
							fileChoices.show(Window.current.mainScreen);
						}
						else
						{
							Alert errorAlert = new Alert("Error", "ABC could not download the files for this assignment.  Please try again");
							errorAlert.addButton("OK", ButtonType.defaultType, null);
							errorAlert.show(Window.current.mainScreen);
						}
					}));
				}
			}
		}
		else
		{
			Assignment assignmentClicked = assignments.get(index.item);

			if (Objects.equals(assignmentClicked.getType(), "quiz"))
			{
				new AssignmentQuery().getQuiz(new QuizAssignment(assignmentClicked), ((returnedData, error) ->
				{
					if (error != null)
					{
						Alert errorAlert = new Alert("Error", "ABC could not load the quiz.  Please try again.");
						errorAlert.addButton("OK", ButtonType.defaultType, null);
						errorAlert.show(Window.current.mainScreen);
						return;
					}
					if (returnedData instanceof QuizAssignment)
					{
						QuizAssignment quizAssignment = (QuizAssignment) returnedData;
						Window.current.showQuiz(quizAssignment);
					}
					else
					{
						Alert errorAlert = new Alert("Error", "ABC could not load the quiz.  Please try again.");
						errorAlert.addButton("OK", ButtonType.defaultType, null);
						errorAlert.show(Window.current.mainScreen);
					}
				}));
			}
			else
			{
				Alert alert = new Alert("New File", "Choose file to upload for " + assignmentClicked.getTitle());
				alert.addTextField("File Name", "fileName", false);

				if (tempFile != null)
				{
					alert.textFieldForIdentifier("fileName").setText(tempFile.getName());
				}

				alert.addButton("Choose File", ButtonType.plain, e ->
				{
					final JFileChooser fc = new JFileChooser();
					int returnVal = fc.showOpenDialog(Window.current.mainScreen);

					if (returnVal == JFileChooser.APPROVE_OPTION)
					{
						File file = fc.getSelectedFile();
						alert.textFieldForIdentifier("fileName").setText(file.getName());
						tempFile = file;
						didSelectItemAtIndexInTable(table, index);
					}
				});
				alert.addButton("Cancel", ButtonType.cancel, null);
				alert.addButton("Upload", ButtonType.defaultType, e ->
				{
					new DocumentsQuery().addDocument(tempFile, alert.textFieldForIdentifier("fileName").getText(), "A File", UIVariables.current.currentUser.getUserID(), String.valueOf(assignmentClicked.getAssignmentID()), String.valueOf(course.getCourseID()), 0, ((returnedData, error) ->
					{
						if (error != null)
						{
							Alert errorAlert = new Alert("Error", "ABC could not submit the file.  Please try again.");
							errorAlert.addButton("OK", ButtonType.defaultType, null);
							errorAlert.show(Window.current.mainScreen);
							return;
						}
						if (returnedData instanceof Boolean)
						{
							if ((Boolean) returnedData)
							{
								Alert success = new Alert("Success", "Your file has been successfully submitted!");
								success.addButton("OK", ButtonType.defaultType, null);
								success.show(Window.current.mainScreen);
							}
						}
					}));
				});
				alert.show(Window.current.mainScreen);
			}
		}
	}

	@Override
	public int numberOfSectionsIn(ALJTable table)
	{
		if (UIVariables.current.isInstructor()) { return 2; }
		return 1;
	}

	@Override
	public int numberOfRowsInSectionForTable(ALJTable table, int section)
	{
		if (UIVariables.current.isInstructor())
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
		return 90;
	}

	@Override
	public ALJTableCell cellForRowAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		if (UIVariables.current.isInstructor())
		{
			if (index.section == 0)
			{
				ALJTableCell cell = new ALJTableCell(ALJTableCellAccessoryViewType.none);
				cell.titleLabel.setText("New Assignment");
				return cell;
			}
			return new AssignmentCell(ALJTableCellAccessoryViewType.delete, assignments.get(index.item));
		}
		return new AssignmentCell(ALJTableCellAccessoryViewType.none, assignments.get(index.item));
	}

	@Override
	public String titleForHeaderInSectionInTable(ALJTable table, int section)
	{
		if (UIVariables.current.isInstructor())
		{
			if (section == 0)
			{
				return "";
			}
			else if (assignments.size() > 0)
			{
				return "Assignments";
			}
			return null;
		}
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
		Alert confirmDelete = new Alert("Confirm Delete", null);
		confirmDelete.addButton("Delete", ButtonType.destructive, e ->
		{
			Assignment assignmentToRemove = assignments.get(forRowAt.item);
			new AssignmentQuery().removeAssignment(assignmentToRemove.getAssignmentID(), ((returnedData, error) ->
			{
				assignments.remove(assignmentToRemove);
				table.reloadData();
			}));
		});
		confirmDelete.addButton("Cancel", ButtonType.cancel, null);
	}

	/**
	 * @return false if user is not instructor, true otherwise
	 *
	 * @see UIVariables#isInstructor()
	 * @deprecated This method is deprecated.  Use UIVariables().isInstructor() instead.
	 */
	@Deprecated(forRemoval = true)
	public static boolean isInstructor()
	{
		return EnumSet.of(userType.TA, userType.TEACHER).contains(UIVariables.current.currentUser.getUserType());
	}

	@Override
	public void performActionFor(String notificationName, Object userData)
	{
		new AssignmentQuery().getAllAssignmentsInCourse(course.getCourseID(), (returnedData, error) ->
		{
			if (error != null)
			{
				if (error.code == 3)
				{
					loadingLabel.setText("No Assignments Yet!");
					return;
				}
				Alert errorAlert = new Alert("Error", "ABC could not load the assignments for this course.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null);
				errorAlert.show(Window.current.mainScreen);
				return;
			}
			if (returnedData instanceof ArrayList)
			{
				assignments = (ArrayList<Assignment>) returnedData;
				remove(loadingLabel);
				table.reloadData();
			}
			else
			{
				Alert errorAlert = new Alert("Error", "ABC could not load the assignments for this course.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, null);
				errorAlert.show(Window.current.mainScreen);
			}
		});
	}
}

class AssignmentCell extends ALJTableCell
{
	AssignmentCell(ALJTableCellAccessoryViewType accessoryViewType, Assignment assignment)
	{
		super(accessoryViewType);

		removeConstraintsFor(titleLabel);

		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));
		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));
		if (UIVariables.current.isInstructor())
		{
			addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.trailing, LayoutRelation.equal, accessoryView, LayoutAttribute.leading, 1.0, -8));
		}
		else
		{
			addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, -8));
		}

		JLabel detailLabelOne = new JLabel();
		detailLabelOne.setFont(UIFont.textLight.deriveFont(9f));
		add(detailLabelOne);
		addConstraint(new LayoutConstraint(detailLabelOne, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));
		addConstraint(new LayoutConstraint(detailLabelOne, LayoutAttribute.top, LayoutRelation.equal, titleLabel, LayoutAttribute.bottom, 1.0, 8));
		if (UIVariables.current.isInstructor())
		{
			addConstraint(new LayoutConstraint(detailLabelOne, LayoutAttribute.trailing, LayoutRelation.equal, accessoryView, LayoutAttribute.leading, 1.0, -8));
		}
		else
		{
			addConstraint(new LayoutConstraint(detailLabelOne, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, -8));
		}

		JLabel detailLabelTwo = new JLabel();
		detailLabelTwo.setFont(UIFont.textLight.deriveFont(9f));
		detailLabelTwo.setHorizontalAlignment(SwingConstants.RIGHT);
		add(detailLabelTwo);
		addConstraint(new LayoutConstraint(detailLabelTwo, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));
		addConstraint(new LayoutConstraint(detailLabelTwo, LayoutAttribute.top, LayoutRelation.equal, detailLabelOne, LayoutAttribute.bottom, 1.0, 8));
		if (UIVariables.current.isInstructor())
		{
			addConstraint(new LayoutConstraint(detailLabelTwo, LayoutAttribute.trailing, LayoutRelation.equal, accessoryView, LayoutAttribute.leading, 1.0, -8));
		}
		else
		{
			addConstraint(new LayoutConstraint(detailLabelTwo, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, -8));
		}

		titleLabel.setText(assignment.getTitle());
		detailLabelOne.setText(assignment.getInstructions());
		detailLabelTwo.setText(UIVariables.delimitSQLDATETIME(assignment.getDueDate()));
	}

	@Override
	public void layoutSubviews()
	{
		super.layoutSubviews();

	}
}