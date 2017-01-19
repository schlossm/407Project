package ui;

import uikit.UIFont;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class Window
{
	private Window()
	{
		JFrame frame = new JFrame("ABC");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setBounds(0, 0, screenSize.width, screenSize.height);

		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		menuBar.add(file);

		JMenuItem newMenuItem = new JMenuItem("New", KeyEvent.VK_N);
		file.add(newMenuItem);

		frame.setJMenuBar(menuBar);

		new Login(frame);
		frame.setVisible(true);
	}

	public static void main(String[] args)
	{
		if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("mac"))
		{
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		UIFont.loadIntoGE();
		new Window();

	}
}
