package ui;

import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

class Home extends ALJPanel
{
	Home()
	{
		setBackground(Color.white);
		setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(new EmptyBorder(40,40,40,40));
		scrollPane.setBackground(Color.red);
		scrollPane.setForeground(Color.black);
		add(scrollPane);

		addConstraint(new LayoutConstraint(scrollPane, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 0));
		addConstraint(new LayoutConstraint(scrollPane, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 0));
		addConstraint(new LayoutConstraint(scrollPane, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, 0));
		addConstraint(new LayoutConstraint(scrollPane, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, 0));

		layoutSubviews();
	}
}
