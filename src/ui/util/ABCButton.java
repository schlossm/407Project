package ui.util;

import uikit.UIFont;

import javax.swing.*;

/**
 * Created by michaelschloss on 4/25/17.
 */
public class ABCButton extends JButton
{
	public ABCButton(String text)
	{
		super(text);
		setFont(UIFont.textLight.deriveFont(9.0f));
	}
}
