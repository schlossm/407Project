package ui.student;

import objects.QuizAssignment;
import ui.util.ALJTable.*;
import uikit.autolayout.uiobjects.ALJPanel;

public class TakeQuiz extends ALJPanel implements ALJTableDataSource, ALJTableDelegate
{



	public TakeQuiz(QuizAssignment assignment)
	{

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
