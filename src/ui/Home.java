package ui;

import ui.homepages.AdminPanel;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

class Home extends ALJPanel
{
	private ALJPanel currentPanel;
	private JScrollPane scrollPane;

	Home()
	{
		setBackground(Color.white);
		setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		scrollPane = new JScrollPane();
		scrollPane.setBorder(new EmptyBorder(0,0,0,0));
		ScrollPaneLayout layout = (ScrollPaneLayout)(scrollPane.getLayout());
		layout.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		layout.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		scrollPane.getVerticalScrollBar().setVisible(false);
		scrollPane.setWheelScrollingEnabled(true);
		add(scrollPane);

		//FIXME: after debugging
		//switch (UIVariables.current.currentUser.getUserType())
		//{
			//case ADMIN:
			//{
				AdminPanel adminPanel = new AdminPanel();
				scrollPane.getViewport().add(adminPanel);
				currentPanel = adminPanel;
			//}
			//case STUDENT:
			//{

			//}
			//case TEACHER:
			//{

			//}
		//}

		addConstraint(new LayoutConstraint(scrollPane, LayoutAttribute.leading,     LayoutRelation.equal, this, LayoutAttribute.leading,    1.0, 0));
		addConstraint(new LayoutConstraint(scrollPane, LayoutAttribute.top,         LayoutRelation.equal, this, LayoutAttribute.top,        1.0, 0));
		addConstraint(new LayoutConstraint(scrollPane, LayoutAttribute.trailing,    LayoutRelation.equal, this, LayoutAttribute.trailing,   1.0, 0));
		addConstraint(new LayoutConstraint(scrollPane, LayoutAttribute.bottom,      LayoutRelation.equal, this, LayoutAttribute.bottom,     1.0, 0));

		layoutSubviews();
	}

	@Override
	public void layoutSubviews()
	{
		super.layoutSubviews();

		//FIXME: after debugging
		currentPanel.setPreferredSize(new Dimension(scrollPane.getBounds().width, currentPanel.getPreferredSize().height));
		currentPanel.layoutSubviews();
	}
}
