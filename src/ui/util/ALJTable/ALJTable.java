package ui.util.ALJTable;

import uikit.UIFont;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Objects;

import static uikit.autolayout.LayoutEngine.getClassAndHashCode;

public class ALJTable extends JScrollPane implements ComponentListener, ALJTableCellDelegate
{
	public ALJTableDataSource dataSource;
	public int heightForRow = -1;
	private boolean _isLoaded = false;

	private ALJPanel tableView;

	public ALJTable()
	{
		setBackground(Color.white);
		setBorder(new EmptyBorder(0,0,0,0));
		tableView = new ALJPanel();
		tableView.setBackground(Color.white);
		ScrollPaneLayout layout = (ScrollPaneLayout)(getLayout());
		layout.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		layout.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		getVerticalScrollBar().setVisible(false);
		setWheelScrollingEnabled(true);

		getViewport().add(tableView, ScrollPaneConstants.VIEWPORT);
	}

	public boolean isLoaded()
	{
		return _isLoaded;
	}

	public void layoutSubviews()
	{
		tableView.setBounds(0,0,getBounds().width, 1000000);
		tableView.layoutSubviews();
		tableView.setBounds(0,0,getBounds().width, tableView.calculatedHeight());
		tableView.layoutSubviews();
		tableView.setBounds(0,0,getBounds().width, tableView.calculatedHeight());
	}

	public void reloadData()
	{
		if (dataSource == null)
		{
			System.out.println(getClassAndHashCode(this) + " does not have a dataSource set yet.");
			return;
		}

		int totalNumSections = dataSource.numberOfSectionsIn(this);

		JComponent previous = null;

		for (int section = 0; section < totalNumSections; section++)
		{
			if (!Objects.equals(dataSource.titleForHeaderInSectionInTable(this, section), ""))
			{
				JLabel sectionTitle = new JLabel(dataSource.titleForHeaderInSectionInTable(this, section).toUpperCase());
				sectionTitle.setForeground(Color.lightGray);
				sectionTitle.setFont(UIFont.textLight.deriveFont(9f));
				tableView.add(sectionTitle);

				tableView.addConstraint(new LayoutConstraint(sectionTitle, LayoutAttribute.leading, LayoutRelation.equal, tableView, LayoutAttribute.leading, 1.0, 40));
				if (previous == null)
				{
					tableView.addConstraint(new LayoutConstraint(sectionTitle, LayoutAttribute.top, LayoutRelation.equal, tableView, LayoutAttribute.top, 1.0, 80));
				}
				else
				{
					tableView.addConstraint(new LayoutConstraint(sectionTitle, LayoutAttribute.top, LayoutRelation.equal, previous, LayoutAttribute.bottom, 1.0, 80));
				}

				previous = sectionTitle;
			}

			int totalNumRowsInSection = dataSource.numberOfRowsInSectionForTable(this, section);
			for (int item = 0; item < totalNumRowsInSection; item++)
			{
				ALJTableCell cell = dataSource.cellForRowAtIndexInTable(this, new ALJTableIndex(section, item));
				cell.delegate = this;
				tableView.add(cell);

				tableView.addConstraint(new LayoutConstraint(cell, LayoutAttribute.leading,  LayoutRelation.equal, tableView,   LayoutAttribute.leading,  1.0, 0));
				tableView.addConstraint(new LayoutConstraint(cell, LayoutAttribute.trailing, LayoutRelation.equal, tableView,   LayoutAttribute.trailing, 1.0, 0));
				if (heightForRow > 44)
				{
					tableView.addConstraint(new LayoutConstraint(cell, LayoutAttribute.height, LayoutRelation.equal, null,   LayoutAttribute.height, 1.0, heightForRow));
				}
				if (previous == null)
				{
					tableView.addConstraint(new LayoutConstraint(cell, LayoutAttribute.top, LayoutRelation.equal, tableView, LayoutAttribute.top, 1.0, 0));
				}
				else
				{
					tableView.addConstraint(new LayoutConstraint(cell, LayoutAttribute.top, LayoutRelation.equal, previous, LayoutAttribute.bottom, 1.0, ((previous instanceof JLabel) ? 8 : 0)));
				}

				previous = cell;
			}
		}

		if (previous != null) setPreferredSize(new Dimension(tableView.getPreferredSize().width, previous.getBounds().y + previous.getPreferredSize().height));
		_isLoaded = true;
		tableView.setBounds(0,0,getBounds().width, 1000000);
		tableView.layoutSubviews();
		tableView.setBounds(0,0,getBounds().width, tableView.calculatedHeight());
		tableView.layoutSubviews();
		tableView.setBounds(0,0,getBounds().width, tableView.calculatedHeight());
	}

	@Override
	public void componentResized(ComponentEvent e)
	{
		tableView.layoutSubviews();
	}

	@Override
	public void componentMoved(ComponentEvent e) { }

	@Override
	public void componentShown(ComponentEvent e)
	{
		tableView.layoutSubviews();
	}

	@Override
	public void componentHidden(ComponentEvent e) { }

	@Override
	public void accessoryViewClicked(ALJTableCellAccessoryViewType accessoryViewType)
	{
		System.out.println(accessoryViewType);
	}
}
