package ui.admin;

import ui.util.ALJTable.*;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("unused")
public class ManageGroup extends ALJPanel implements ALJTableDataSource, ALJTableDelegate
{
	private Group groupToManage = Group.none;
	private ALJTable manageTable;

	private final ArrayList<String> fakeData = new ArrayList<>();

	public ManageGroup(Group groupToManage)
	{
		fakeData.add("New " + groupToManage);
		fakeData.add("Blah");
		fakeData.add("Blah Blah");
		fakeData.add("Blah Blah Blah");
		if (groupToManage == Group.none)
		{
			System.out.println("You cannot manage a \"none\" group.");
			return;
		}
		this.groupToManage = groupToManage;

		setBackground(Color.white);
		setOpaque(true);

		manageTable = new ALJTable();
		manageTable.heightForRow = 66;
		manageTable.dataSource = this;
		manageTable.delegate = this;

		add(manageTable);

		addConstraint(new LayoutConstraint(manageTable, LayoutAttribute.leading,    LayoutRelation.equal, this, LayoutAttribute.leading,    1.0, 0));
		addConstraint(new LayoutConstraint(manageTable, LayoutAttribute.top,        LayoutRelation.equal, this, LayoutAttribute.top,        1.0, 0));
		addConstraint(new LayoutConstraint(manageTable, LayoutAttribute.trailing,   LayoutRelation.equal, this, LayoutAttribute.trailing,   1.0, 0));
		addConstraint(new LayoutConstraint(manageTable, LayoutAttribute.bottom,     LayoutRelation.equal, this, LayoutAttribute.bottom,     1.0, 0));
	}

	@Override
	public void layoutSubviews()
	{
		super.layoutSubviews();
		if (!manageTable.isLoaded())
		{
			manageTable.reloadData();
			manageTable.layoutSubviews();
			repaint();
		}
		else
		{
			manageTable.layoutSubviews();
			repaint();
		}
	}

	public Group currentGroup()
	{
		return groupToManage;
	}

	public void add()
	{
		fakeData.add("Fake " + groupToManage);
	}

	@Override
	public int numberOfSectionsIn(ALJTable table)
	{
		return 1;
	}

	@Override
	public int numberOfRowsInSectionForTable(ALJTable table, int section)
	{
		return fakeData.size();
	}

	@Override
	public ALJTableCell cellForRowAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		ALJTableCell newCell = new ALJTableCell(!Objects.equals(fakeData.get(index.item), "New " + groupToManage) ? ALJTableCellAccessoryViewType.delete : ALJTableCellAccessoryViewType.none);

		newCell.titleLabel.setText(fakeData.get(index.item));

		return newCell;
	}

	@Override
	public String titleForHeaderInSectionInTable(ALJTable table, int section)
	{
		return groupToManage.toString();
	}

	@Override
	public String titleForFooterInSectionInTable(ALJTable table, int section)
	{
		return "";
	}

	@Override
	public void tableView(ALJTable tableView, ALJTableCellEditingStyle commit, ALJTableIndex forRowAt)
	{
		switch (commit)
		{
			case delete:
			{
				fakeData.remove(forRowAt.item);
				break;
			}

			default: break;
		}
	}

	@Override
	public void didSelectItemAtIndexInTable(ALJTable table, ALJTableIndex index)
	{
		if (index.item == 0)
		{
			add();
			table.reloadData();
		}
	}
}
