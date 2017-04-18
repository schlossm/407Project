package ui;

import net.sf.plist.NSBoolean;
import ui.util.UIVariables;
import uikit.UIFont;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJPanel;

import javax.swing.*;

class Settings extends ALJPanel
{
	private final JLabel title;

	Settings()
	{
		JLabel settings = new JLabel("Settings");
		settings.setFont(UIFont.textHeavy.deriveFont(24.0f));
		add(settings);

		addConstraint(new LayoutConstraint(settings, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 8));
		addConstraint(new LayoutConstraint(settings, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));

		title = settings;

		insertGenericSettings();
	}

	private void insertGenericSettings()
	{
		JLabel saveState = new JLabel("Save position on close");
		saveState.setFont(UIFont.textLight.deriveFont(11.0f));
		add(saveState);

		boolean savesState = true;

		try
		{
			savesState = ((NSBoolean)UIVariables.current.valueFor("savesState")).bool();
		}
		catch (Exception ignored)
		{
			UIVariables.current.writeValue("savesState", new NSBoolean(true));
			UIVariables.current.synchronize();
		}

		JCheckBox saveStateBox = new JCheckBox("", savesState);
		saveStateBox.addActionListener(e -> { UIVariables.current.writeValue("savesState", new NSBoolean(saveStateBox.isSelected())); });
		add(saveStateBox);

		addConstraint(new LayoutConstraint(saveState, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 32));
		addConstraint(new LayoutConstraint(saveState, LayoutAttribute.top, LayoutRelation.equal, title, LayoutAttribute.bottom, 1.0, 32));

		addConstraint(new LayoutConstraint(saveStateBox, LayoutAttribute.centerY, LayoutRelation.equal, saveState, LayoutAttribute.centerY, 1.0, 0));
		addConstraint(new LayoutConstraint(saveStateBox, LayoutAttribute.leading, LayoutRelation.equal, saveState, LayoutAttribute.trailing, 1.0, 16));
	}
}
