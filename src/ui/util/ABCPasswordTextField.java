package ui.util;

import uikit.UIFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by michaelschloss on 4/25/17.
 */
public class ABCPasswordTextField extends JPasswordField implements FocusListener
{
	public final String placeholder;

	public ABCPasswordTextField(String placeholder, String text)
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
		if (Arrays.equals(getPassword(), placeholder.toCharArray()))
		{
			setText("");
			setForeground(Color.white);
		}
		else
		{
			selectAll();
		}
	}

	@Override
	public void focusLost(FocusEvent e)
	{
		if (Arrays.equals(getPassword(), "".toCharArray()))
		{
			setText(placeholder);
			setForeground(Color.lightGray);
		}
	}
}
