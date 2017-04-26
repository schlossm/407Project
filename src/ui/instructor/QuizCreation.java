package ui.instructor;

import objects.Course;
import objects.Question;
import objects.QuizAssignment;
import ui.Window;
import ui.util.*;
import ui.util.ALJTable.*;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Objects;

public class QuizCreation extends ALJPanel implements MLMDelegate, ALJTableDataSource, ALJTableDelegate
{
	private QuizAssignment quizAssignment = new QuizAssignment();

	private JTextField quizName;
	private Course course;
	ALJTable quizTable;

	private ActionListener saveListener = e -> {
		System.out.println(quizAssignment);
	};

	public QuizCreation(Course course)
	{
		this.course = course;

		addMouseListener(new MouseListenerManager(this));
		setFocusTraversalPolicy(new FocusTraversalPolicy()
		{
			@Override
			public Component getComponentAfter(Container aContainer, Component aComponent)
			{
				return null;
			}

			@Override
			public Component getComponentBefore(Container aContainer, Component aComponent)
			{
				return null;
			}

			@Override
			public Component getFirstComponent(Container aContainer)
			{
				return null;
			}

			@Override
			public Component getLastComponent(Container aContainer)
			{
				return null;
			}

			@Override
			public Component getDefaultComponent(Container aContainer)
			{
				return null;
			}
		});

		quizName = new ABCTextField("Quiz Title", "");
		quizName.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e)
			{

			}

			@Override
			public void keyPressed(KeyEvent e)
			{

			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				quizAssignment.setTitle(quizName.getText());
			}
		});
		add(quizName);
		addConstraint(new LayoutConstraint(quizName, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));
		addConstraint(new LayoutConstraint(quizName, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));
		addConstraint(new LayoutConstraint(quizName, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.centerX, 1.0, -8));
		addConstraint(new LayoutConstraint(quizName, LayoutAttribute.height, LayoutRelation.equal, null, LayoutAttribute.height, 1.0, 50));

		ABCButton cancel = new ABCButton("Cancel");
		cancel.addActionListener(e -> Window.current.closeQuizCreation());
		add(cancel);
		addConstraint(new LayoutConstraint(cancel, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));
		addConstraint(new LayoutConstraint(cancel, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, -8));
		addConstraint(new LayoutConstraint(cancel, LayoutAttribute.height, LayoutRelation.equal, null, LayoutAttribute.height, 1.0, 50));
		addConstraint(new LayoutConstraint(cancel, LayoutAttribute.width, LayoutRelation.greaterThanOrEqual, null, LayoutAttribute.width, 1.0, 44));

		ABCButton save = new ABCButton("Save");
		save.addActionListener(saveListener);
		add(save);
		addConstraint(new LayoutConstraint(save, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));
		addConstraint(new LayoutConstraint(save, LayoutAttribute.leading, LayoutRelation.equal, quizName, LayoutAttribute.trailing, 1.0, 8));
		addConstraint(new LayoutConstraint(save, LayoutAttribute.height, LayoutRelation.equal, null, LayoutAttribute.height, 1.0, 50));
		addConstraint(new LayoutConstraint(save, LayoutAttribute.width, LayoutRelation.greaterThanOrEqual, null, LayoutAttribute.width, 1.0, 44));

		quizTable = new ALJTable();
		quizTable.delegate = this;
		quizTable.dataSource = this;
		add(quizTable);
		addConstraint(new LayoutConstraint(quizTable, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 0));
		addConstraint(new LayoutConstraint(quizTable, LayoutAttribute.top, LayoutRelation.equal, quizName, LayoutAttribute.bottom, 1.0, 8));
		addConstraint(new LayoutConstraint(quizTable, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, 0));
		addConstraint(new LayoutConstraint(quizTable, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, 0));
		quizTable.reloadData();
	}

	@Override
	public void layoutSubviews()
	{
		super.layoutSubviews();
	}

	@Override
	public void mousePoint(MouseEvent action, MLMEventType eventType)
	{
		if (eventType == MLMEventType.released)
		{
			if (action.getSource() == this || action.getSource() == quizTable)
			{
				requestFocus();
			}
		}
	}

	@Override
	public void didSelectItemAtIndexInTable(ALJTable table, ALJTableIndex index)
	{

	}

	@Override
	public int numberOfSectionsIn(ALJTable table)
	{
		return quizAssignment.getQuestions().size() + 1;
	}

	@Override
	public int numberOfRowsInSectionForTable(ALJTable table, int section)
	{
		if (section == numberOfSectionsIn(table) - 1)
		{
			return 1;
		}
		return quizAssignment.getQuestions().get(section).getChoices().size() + 2;
	}

	@Override
	public int heightForRow(ALJTable table, int inSection)
	{
		return 66;
	}

	@Override
	public ALJTableCell cellForRowAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		if (index.section == numberOfSectionsIn(table) - 1) //We are at the final cell
		{
			return new NewQuestionCell(e ->
			                           {
				                           ArrayList<Question> questions = quizAssignment.getQuestions();
				                           questions.add(new Question());
				                           quizAssignment.setQuestions(questions);
				                           table.reloadData();
			                           });
		}
		//We have a question
		if (index.item == 0)
		{
			if (!Objects.equals(quizAssignment.getQuestions().get(index.section).getQuestion(), ""))
			{
				return new QuestionTitleCell(quizAssignment.getQuestions().get(index.section).getQuestion(), quizAssignment.getQuestions().get(index.section));
			}
			return new QuestionTitleCell(null, quizAssignment.getQuestions().get(index.section));
		}
		else if (index.item == numberOfRowsInSectionForTable(table, index.section) - 1)
		{
			return new NewChoiceCell(e ->
			                         {
				                         ArrayList<String> choices = quizAssignment.getQuestions().get(index.section).getChoices();
				                         choices.add("");
				                         quizAssignment.getQuestions().get(index.section).setChoices(choices);
				                         table.reloadData();
			                         });
		}
		else //Choice
		{
			if (!Objects.equals(quizAssignment.getQuestions().get(index.section).getChoices().get(index.item - 1), ""))
			{
				return new QuestionChoiceCell(quizAssignment.getQuestions().get(index.section).getChoices().get(index.item), quizAssignment.getQuestions().get(index.section), index.item - 1);
			}
			return new QuestionChoiceCell(null, quizAssignment.getQuestions().get(index.section), index.item - 1);
		}
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

		ABCButton button = new ABCButton("Add Question");
		button.addActionListener(action);
		add(button);
		addConstraint(new LayoutConstraint(button, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));
		addConstraint(new LayoutConstraint(button, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));
		addConstraint(new LayoutConstraint(button, LayoutAttribute.height, LayoutRelation.equal, null, LayoutAttribute.height, 1.0, 50));
		addConstraint(new LayoutConstraint(button, LayoutAttribute.width, LayoutRelation.greaterThanOrEqual, null, LayoutAttribute.width, 1.0, 44));
	}
}

class NewChoiceCell extends ALJTableCell
{
	NewChoiceCell(ActionListener action)
	{
		super(ALJTableCellAccessoryViewType.none);

		ABCButton button = new ABCButton("Add Choice");
		button.addActionListener(action);
		add(button);
		addConstraint(new LayoutConstraint(button, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));
		addConstraint(new LayoutConstraint(button, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));
		addConstraint(new LayoutConstraint(button, LayoutAttribute.height, LayoutRelation.equal, null, LayoutAttribute.height, 1.0, 50));
		addConstraint(new LayoutConstraint(button, LayoutAttribute.width, LayoutRelation.greaterThanOrEqual, null, LayoutAttribute.width, 1.0, 44));
	}
}

class QuestionTitleCell extends ALJTableCell
{
	private ABCTextField textField;

	QuestionTitleCell(String text, Question question)
	{
		super(ALJTableCellAccessoryViewType.delete);

		textField = new ABCTextField("Question Title", text);
		textField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e)
			{
				question.setQuestion(textField.getText());
			}

			@Override
			public void keyPressed(KeyEvent e)
			{

			}

			@Override
			public void keyReleased(KeyEvent e)
			{

			}
		});
		add(textField);
		addConstraint(new LayoutConstraint(textField, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));
		addConstraint(new LayoutConstraint(textField, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));
		addConstraint(new LayoutConstraint(textField, LayoutAttribute.trailing, LayoutRelation.equal, accessoryView, LayoutAttribute.leading, 1.0, -8));
		addConstraint(new LayoutConstraint(textField, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, -8));
	}
}

class QuestionChoiceCell extends ALJTableCell
{
	private ABCTextField textField;

	QuestionChoiceCell(String text, Question question, int position)
	{
		super(ALJTableCellAccessoryViewType.delete);

		textField = new ABCTextField("Choice Text", text);
		textField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e)
			{

			}

			@Override
			public void keyPressed(KeyEvent e)
			{

			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				ArrayList<String> choices = question.getChoices();
				choices.set(position, textField.getText());
				question.setChoices(choices);
			}
		});
		add(textField);
		addConstraint(new LayoutConstraint(textField, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));
		addConstraint(new LayoutConstraint(textField, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 58));
		addConstraint(new LayoutConstraint(textField, LayoutAttribute.trailing, LayoutRelation.equal, accessoryView, LayoutAttribute.leading, 1.0, -8));
		addConstraint(new LayoutConstraint(textField, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, -8));
	}
}