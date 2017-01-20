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

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setBounds(screenSize.width/10, screenSize.height/10, screenSize.width/5 * 4, screenSize.height/5 * 4);
		frame.setBackground(Color.WHITE);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setUndecorated(true);

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

		Login loginPanel = new Login(frame);
		frame.add(loginPanel);

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

	static String convertToMultiline(String orig)
	{
		return "<html> <head> <style type=\"text/css\"> body { font-size: 14px; text-align: center; } </style> </head> <body>" + orig.replaceAll("\n", "<br>") + "</body> </html>";
	}
}
