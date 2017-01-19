package ui;

import uikit.UIFont;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Window implements ActionListener, ItemListener
{
	private Window()
	{
		JFrame frame = new JFrame("ABC");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setBounds(0, 0, screenSize.width, screenSize.height);
		frame.setBackground(Color.WHITE);

		/*
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		menuBar.add(file);

		JMenuItem newMenuItem = new JMenuItem("Quit", KeyEvent.VK_Q);
		newMenuItem.setMnemonic(KeyEvent.VK_Q);
		newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
		file.addActionListener(this);
		file.addItemListener(this);
		file.add(newMenuItem);

		frame.setJMenuBar(menuBar);*/

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

	@Override
	public void actionPerformed(ActionEvent e)
	{
		System.out.println(e.getSource());
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		System.out.println(e.getSource());
	}
}
