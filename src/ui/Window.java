package ui;

import uikit.UIFont;

import javax.swing.*;
import java.awt.*;

/**
 * Created by michaelschloss on 1/12/17.
 */
public class Window
{
	private Window()
	{
		JFrame frame = new JFrame("ABC");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setBounds(0, 0, screenSize.width, screenSize.height);

		new Login(frame);
		frame.setVisible(true);
	}

	public static void main(String[] args)
	{
		UIFont.loadIntoGE();
		new Window();

	}
}
