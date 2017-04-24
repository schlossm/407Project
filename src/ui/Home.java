package ui;

import objects.userType;
import ui.homepages.AdminPanel;
import ui.homepages.InstructorPanel;
import ui.homepages.StudentPanel;
import ui.util.UIVariables;
import uikit.UIFont;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class Home extends ALJPanel
{
	private AnnouncementPanel announcementPanel;

	Home()
	{
		setBackground(Color.white);

		ALJPanel currentPanel = null;
		switch (UIVariables.current.currentUser.getUserType())
		{
			case ADMIN:
			{
				AdminPanel adminPanel = new AdminPanel();
				add(adminPanel);
				currentPanel = adminPanel;
				break;
			}

			case STUDENT:
			{
				StudentPanel studentPanel = new StudentPanel();
				add(studentPanel);
				currentPanel = studentPanel;
				break;
			}

			case TEACHER:
			{
				InstructorPanel instructorPanel = new InstructorPanel();
				add(instructorPanel);
				currentPanel = instructorPanel;
				break;
			}

			case TA:
			{
				InstructorPanel instructorPanel = new InstructorPanel();
				add(instructorPanel);
				currentPanel = instructorPanel;
				break;
			}
		}

		addConstraint(new LayoutConstraint(currentPanel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 0));
		addConstraint(new LayoutConstraint(currentPanel, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 0));
		addConstraint(new LayoutConstraint(currentPanel, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, 0));
		addConstraint(new LayoutConstraint(currentPanel, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, 0));

		loadAnnouncements();
	}

	private void loadAnnouncements()
	{
		if (UIVariables.current.currentUser.getUserType() != userType.ADMIN)
		{
			//FIXME: See if there is a real announcement

			//FIXME: Remove after debugging
			Home current = this;

			announcementPanel = new AnnouncementPanel();
			announcementPanel.current = current;
			//ADD PANEL

			add(announcementPanel, 0);
			addConstraint(new LayoutConstraint(announcementPanel, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 0));
			addConstraint(new LayoutConstraint(announcementPanel, LayoutAttribute.centerX, LayoutRelation.equal, this, LayoutAttribute.centerX, 1.0, 0));
			addConstraint(new LayoutConstraint(announcementPanel, LayoutAttribute.width, LayoutRelation.equal, null, LayoutAttribute.width, 1.0, 600));
			addConstraint(new LayoutConstraint(announcementPanel, LayoutAttribute.height, LayoutRelation.equal, null, LayoutAttribute.height, 1.0, 150));
		}
	}

	@Override
	public void layoutSubviews()
	{
		super.layoutSubviews();

		if (announcementPanel != null)
		{
			announcementPanel.layoutSubviews();
		}
	}
}

class AnnouncementPanel extends ALJPanel
{
	Home current;

	AnnouncementPanel()
	{
		setOpaque(true);
		setBackground(new Color(0, 0, 0, 0.5f));
		AnnouncementPanel panel = this;
		addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e) { }

			@Override
			public void mousePressed(MouseEvent e) { }

			@Override
			public void mouseReleased(MouseEvent e)
			{
				if (new Rectangle(0, 0, getWidth(), getHeight()).contains(e.getPoint()))
				{
					current.remove(panel);
					current.repaint();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) { }

			@Override
			public void mouseExited(MouseEvent e) { }
		});

		//ANNOUNCEMENT INFO

		JLabel title = new JLabel("Test Announcement Title");
		title.setFont(UIFont.textHeavy.deriveFont(12.0f));
		title.setForeground(Color.white);
		add(title);

		addConstraint(new LayoutConstraint(title, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));
		addConstraint(new LayoutConstraint(title, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));

		JLabel detail = new JLabel("Test Announcement Detail");
		detail.setFont(UIFont.textLight.deriveFont(9.0f));
		detail.setForeground(Color.white);
		add(detail);

		addConstraint(new LayoutConstraint(detail, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));
		addConstraint(new LayoutConstraint(detail, LayoutAttribute.top, LayoutRelation.equal, title, LayoutAttribute.bottom, 1.0, 0));

		//CLICK TO DISMISS

		JLabel clickDismiss = new JLabel("(Click to Dismiss)");
		clickDismiss.setFont(UIFont.textLightItalic.deriveFont(5.0f));
		clickDismiss.setForeground(Color.white);
		add(clickDismiss);

		addConstraint(new LayoutConstraint(clickDismiss, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, -2));
		addConstraint(new LayoutConstraint(clickDismiss, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, -2));
	}
}