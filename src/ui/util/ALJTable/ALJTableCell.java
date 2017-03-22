package ui.util.ALJTable;

import ui.util.MLMDelegate;
import ui.util.MLMEventType;
import ui.util.MouseListenerManager;
import uikit.UIFont;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static javax.imageio.ImageIO.read;

interface ALJTableCellDelegate
{
	void accessoryViewClicked(ALJTableCellAccessoryViewType accessoryViewType, ALJTableIndex atIndex);
}

@SuppressWarnings("WeakerAccess")
public class ALJTableCell extends ALJPanel implements MLMDelegate
{
	public final JLabel titleLabel;
	protected JLabel accessoryView;
	private ALJTableCellAccessoryViewType _accessoryViewType = ALJTableCellAccessoryViewType.none;
	ALJTableIndex currentIndex;

	ALJTableCellDelegate delegate;

	public ALJTableCell(ALJTableCellAccessoryViewType accessoryViewType)
	{
		setBackground(Color.white);
		titleLabel = new JLabel();
		titleLabel.setFont(UIFont.textSemibold.deriveFont(14f));
		titleLabel.setFocusable(false);
		titleLabel.setVerticalAlignment(SwingConstants.CENTER);
		add(titleLabel);

		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.leading, LayoutRelation.equal,   this, LayoutAttribute.leading,  1.0, 8));
		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.top,     LayoutRelation.equal,   this, LayoutAttribute.top,      1.0, 8));
		addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.bottom,  LayoutRelation.equal,   this, LayoutAttribute.bottom,   1.0, -8));

		setAccessoryType(accessoryViewType);
		_accessoryViewType = accessoryViewType;
	}

	public void setAccessoryType(ALJTableCellAccessoryViewType accessoryViewType)
	{
		switch (accessoryViewType)
		{
			case delete:
			{
				if (accessoryView != null)
				{
					remove(accessoryView);
					accessoryView = null;
				}
				accessoryView = new JLabel(new ImageIcon(new ALJTableCellAccessoryViewImage("delete").image));
				accessoryView.addMouseListener(new MouseListenerManager(this));
				add(accessoryView);
				break;
			}

			case detail:
			{
				if (accessoryView != null)
				{
					remove(accessoryView);
					accessoryView = null;
				}
				accessoryView = new JLabel(new ImageIcon(new ALJTableCellAccessoryViewImage("detail").image));
				accessoryView.addMouseListener(new MouseListenerManager(this));
				add(accessoryView);
				break;
			}

			case info:
			{
				if (accessoryView != null)
				{
					remove(accessoryView);
					accessoryView = null;
				}
				accessoryView = new JLabel(new ImageIcon(new ALJTableCellAccessoryViewImage("info").image));
				accessoryView.addMouseListener(new MouseListenerManager(this));
				add(accessoryView);
				break;
			}

			case move:
			{
				if (accessoryView != null)
				{
					remove(accessoryView);
					accessoryView = null;
				}
				accessoryView = new JLabel(new ImageIcon(new ALJTableCellAccessoryViewImage("move").image));
				accessoryView.addMouseListener(new MouseListenerManager(this));
				add(accessoryView);
				break;
			}

			case none:
			{
				if (accessoryView != null)
				{
					remove(accessoryView);
					accessoryView = null;
				}
				addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, 0));
				break;
			}
		}

		if (accessoryView != null)
		{
			addConstraint(new LayoutConstraint(titleLabel, LayoutAttribute.trailing, LayoutRelation.equal, accessoryView, LayoutAttribute.leading, 1.0, -8));
			addConstraint(new LayoutConstraint(accessoryView, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, 0));
			addConstraint(new LayoutConstraint(accessoryView, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 0));
			addConstraint(new LayoutConstraint(accessoryView, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, 0));
			addConstraint(new LayoutConstraint(accessoryView, LayoutAttribute.width, LayoutRelation.equal, null, LayoutAttribute.width, 1.0, 44));
		}
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(super.getPreferredSize().width, titleLabel.getPreferredSize().height + 16);
	}

	private boolean isClicked = false;

	@Override
	public void mousePoint(MouseEvent action, MLMEventType eventType)
	{
		if (eventType == MLMEventType.pressed)
		{
			if (action.getSource() != accessoryView) return ;
			isClicked = true;
			accessoryView.setOpaque(true);
			accessoryView.setBackground(Color.lightGray);
		}
		else if (eventType == MLMEventType.draggedIn)
		{
			if (!isClicked) return;
			if (action.getSource() != accessoryView) return;
			accessoryView.setOpaque(true);
			accessoryView.setBackground(Color.lightGray);
		}
		else if (eventType == MLMEventType.released)
		{
			if (!isClicked) return;
			isClicked = false;
			if (action.getSource() != accessoryView && !accessoryView.isOpaque()) return;
			accessoryView.setOpaque(false);
			accessoryView.setBackground(new Color(0,0,0,0));
			delegate.accessoryViewClicked(_accessoryViewType, currentIndex);
		}
		else if (eventType == MLMEventType.draggedOut)
		{
			if (!isClicked) return;
			if (action.getSource() != accessoryView) return;
			accessoryView.setOpaque(false);
			accessoryView.setBackground(new Color(0,0,0,0));
		}
	}
/*
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		g.setColor(Color.lightGray);
		g.drawRect(0, this.getHeight() - 1, this.getWidth(), 1);
	}*/
}

class ALJTableCellAccessoryViewImage
{
	BufferedImage image;

	ALJTableCellAccessoryViewImage(String imageTitle)
	{
		String path = "images/";
		try
		{
			image = read(this.getClass().getResource(path + imageTitle + ".png"));
		}
		catch (IOException ignored) { }
	}
}