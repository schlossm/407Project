package ui.student;

import json.InstructorQuery;
import objects.Question;
import objects.QuizAssignment;
import ui.Window;
import ui.util.ABCButton;
import ui.util.ALJTable.*;
import ui.util.Alert;
import ui.util.ButtonType;
import ui.util.UIVariables;
import uikit.UIFont;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJPanel;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Objects;

public class TakeQuiz extends ALJPanel implements ALJTableDataSource, ALJTableDelegate
{
	private QuizAssignment assignment;

	public TakeQuiz(QuizAssignment assignment)
	{
		this.assignment = assignment;

		JLabel title = new JLabel(assignment.getTitle());
		title.setFont(UIFont.textSemibold.deriveFont(14f));
		add(title);
		addConstraint(new LayoutConstraint(title, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 20));
		addConstraint(new LayoutConstraint(title, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 20));

		ALJTable table = new ALJTable();
		table.dataSource = this;
		table.delegate = this;
		add(table);
		addConstraint(new LayoutConstraint(table, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 0));
		addConstraint(new LayoutConstraint(table, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, 0));
		addConstraint(new LayoutConstraint(table, LayoutAttribute.top, LayoutRelation.equal, title, LayoutAttribute.bottom, 1.0, 0));
		addConstraint(new LayoutConstraint(table, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, 0));
		table.reloadData();
	}

	private void submitQuiz()
	{
		int sum = 0;
		for (Question question : assignment.getQuestions())
		{
			if (Objects.equals(question.getSelectedChoice(), question.getCorrectChoice()))
			{
				sum += question.getPoints();
			}
		}

		int finalSum = sum;
		new InstructorQuery().enterGrade(assignment.getAssignmentId(), UIVariables.current.currentUser.getUserID(), sum, ((returnedData, error) ->
		{
			if (error != null)
			{
				Alert errorAlert = new Alert("Error", "ABC could not submit your quiz.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, e -> Window.current.destroyQuiz());
				errorAlert.show(Window.current.quizFrame);
				return;
			}
			if (returnedData instanceof Boolean)
			{
				if ((Boolean) returnedData)
				{
					Alert points = new Alert("Grade", "You scored " + finalSum + " points out of " + assignment.getMaxPoints() + "!");
					points.addButton("OK", ButtonType.defaultType, e -> Window.current.destroyQuiz());
					points.show(Window.current.quizFrame);
				}
				else
				{
					Alert errorAlert = new Alert("Error", "ABC could not submit your quiz.  Please try again.");
					errorAlert.addButton("OK", ButtonType.defaultType, e -> Window.current.destroyQuiz());
					errorAlert.show(Window.current.quizFrame);
				}
			}
			else
			{
				Alert errorAlert = new Alert("Error", "ABC could not submit your quiz.  Please try again.");
				errorAlert.addButton("OK", ButtonType.defaultType, e -> Window.current.destroyQuiz());
				errorAlert.show(Window.current.quizFrame);
			}
		}));
	}

	@Override
	public void didSelectItemAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		Question question = assignment.getQuestions().get(index.section);
		question.setSelectedChoice(question.getChoices().get(index.item - 1));
		table.reloadData();
	}

	@Override
	public int numberOfSectionsIn(ALJTable table)
	{
		return assignment.getQuestions().size() + 1;
	}

	@Override
	public int numberOfRowsInSectionForTable(ALJTable table, int section)
	{
		if (section == assignment.getQuestions().size())
		{
			return 1;
		}
		return assignment.getQuestions().get(section).getChoices().size() + 1;
	}

	@Override
	public int heightForRow(ALJTable table, int inSection)
	{
		return 66;
	}

	@Override
	public ALJTableCell cellForRowAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		if (index.section == assignment.getQuestions().size())
		{
			return new NewQuestionCell(e ->
			                           {
				                           boolean hasUnanswered = false;
				                           for (Question question : assignment.getQuestions())
				                           {
					                           if (Objects.equals(question.getSelectedChoice(), ""))
					                           {
						                           hasUnanswered = true;
						                           break;
					                           }
				                           }

				                           if (hasUnanswered)
				                           {
					                           Alert unanswered = new Alert("Unanswered Questions", "You have unanswered questions!");
					                           unanswered.addButton("OK", ButtonType.cancel, null);
					                           unanswered.addButton("Submit Anyways", ButtonType.destructive, e2 -> submitQuiz());
					                           unanswered.show(Window.current.quizFrame);
				                           }
				                           else
				                           {
					                           submitQuiz();
				                           }
			                           });
		}
		if (index.item == 0)
		{
			ALJTableCell cell = new ALJTableCell(ALJTableCellAccessoryViewType.none);
			cell.titleLabel.setText(assignment.getQuestions().get(index.section).getQuestion());
			return cell;
		}
		return new QuestionChoiceCell(assignment.getQuestions().get(index.section), index.item - 1);
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

class NewQuestionCell extends ALJTableCell
{
	NewQuestionCell(ActionListener action)
	{
		super(ALJTableCellAccessoryViewType.none);

		ABCButton button = new ABCButton("Submit Quiz");
		button.addActionListener(action);
		add(button);
		addConstraint(new LayoutConstraint(button, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));
		addConstraint(new LayoutConstraint(button, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));
		addConstraint(new LayoutConstraint(button, LayoutAttribute.height, LayoutRelation.equal, null, LayoutAttribute.height, 1.0, 50));
		addConstraint(new LayoutConstraint(button, LayoutAttribute.width, LayoutRelation.greaterThanOrEqual, null, LayoutAttribute.width, 1.0, 44));
	}
}

class QuestionChoiceCell extends ALJTableCell
{
	QuestionChoiceCell(Question question, int pos)
	{
		super(ALJTableCellAccessoryViewType.none);

		removeConstraintsFor(titleLabel);
		titleLabel.setFont(UIFont.textRegular.deriveFont(9f));

		titleLabel.setText(question.getChoices().get(pos));

		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));
		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, -8));
		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, 0));

		JRadioButton button = new JRadioButton();
		button.setSelected(Objects.equals(question.getChoices().get(pos), question.getSelectedChoice()));
		add(button);
		addConstraint(new LayoutConstraint(button, LayoutAttribute.centerY, LayoutRelation.equal, this, LayoutAttribute.centerY, 1.0, 0));
		addConstraint(new LayoutConstraint(button, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 20));
		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.leading, LayoutRelation.equal, button, LayoutAttribute.trailing, 1.0, 20));
		registerComponentForClicking(button);
	}
}