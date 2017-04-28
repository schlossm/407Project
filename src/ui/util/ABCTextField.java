package ui.util;

import uikit.UIFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Objects;

/**
 * Created by michaelschloss on 4/25/17.
 */
public class ABCTextField extends JTextField implements FocusListener
{
	public final String placeholder;

	public ABCTextField(String placeholder, String text)
	{
		super(text != null && !Objects.equals(text, "") ? text : placeholder, 1);
		this.placeholder = placeholder;

		setFont(UIFont.textLight.deriveFont(10.0f));
		setAlignmentX(Component.LEFT_ALIGNMENT);
		if (text == null || Objects.equals(text, ""))
		{
			setForeground(Color.lightGray);
		}
		else
		{
			setForeground(Color.white);
		}
		setBackground(Color.gray);
		setBorder(new EmptyBorder(0, 20, 0, 20));
		addFocusListener(this);
	}

	@Override
	public void focusGained(FocusEvent e)
	{
		if (Objects.equals(getText(), placeholder))
		{
			setText("");
			setForeground(Color.white);
		}
	}

	@Override
	public void focusLost(FocusEvent e)
	{
		if (Objects.equals(getText(), ""))
		{
			setText(placeholder);
			setForeground(Color.lightGray);
		}
	}
}
