package ui.util.ALJTable;

public interface ALJTableDataSource
{
	int numberOfSectionsIn(ALJTable table);

	int numberOfRowsInSectionForTable(ALJTable table, int section);

	ALJTableCell cellForRowAtIndexInTable(ALJTable table, ALJTableIndex index);

	String titleForHeaderInSectionInTable(ALJTable table, int section);

	String titleForFooterInSectionInTable(ALJTable table, int section);

	void tableView(ALJTable tableView, ALJTableCellEditingStyle commit, ALJTableIndex forRowAt);
}
