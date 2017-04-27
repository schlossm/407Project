package ui.student;

import objects.QuizAssignment;
import ui.util.ALJTable.*;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJPanel;

import javax.swing.*;

public class TakeQuiz extends ALJPanel implements ALJTableDataSource, ALJTableDelegate
{
	QuizAssignment assignment;
	ALJTable table;


	public TakeQuiz(QuizAssignment assignment)
	{
		this.assignment = assignment;

		JLabel title = new JLabel(assignment.getTitle());

		table = new ALJTable();
		table.dataSource = this;
		table.delegate = this;
		add(table);
		addConstraint(new LayoutConstraint(table, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 0));
		addConstraint(new LayoutConstraint(table, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 0));
	}

	@Override
	public void didSelectItemAtIndexInTable(ALJTable table, ALJTableIndex index)
	{

	}

	@Override
	public int numberOfSectionsIn(ALJTable table)
	{
		return 0;
	}

	@Override
	public int numberOfRowsInSectionForTable(ALJTable table, int section)
	{
		return 0;
	}

	@Override
	public int heightForRow(ALJTable table, int inSection)
	{
		return 0;
	}

	@Override
	public ALJTableCell cellForRowAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		return null;
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
