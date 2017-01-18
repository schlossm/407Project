package ui;

import uikit.UIFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by michaelschloss on 1/14/17.
 */
class Login
{
	private JPanel loginPanel;
	private JTextField usernameField;
	private JTextField passwordField;

	Login(JFrame frame)
	{
		loginPanel = new JPanel();
		BoxLayout layout = new BoxLayout(loginPanel, BoxLayout.Y_AXIS);
		loginPanel.setLayout(layout);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		loginPanel.setBorder(new EmptyBorder((int)(screenSize.getHeight() * 0.2),0,0,(int)(screenSize.getHeight() * 0.2)));

		//Title

		JLabel title = new JLabel("ABC", JLabel.LEFT);
		title.setFont(UIFont.displayHeavy.deriveFont(new Float(48.0)));
		title.setAlignmentX(Component.LEFT_ALIGNMENT);
		loginPanel.add(title);


		//Username

		JLabel usernameLabel = new JLabel("Username", JLabel.LEFT);
		usernameLabel.setFont(UIFont.textSemibold.deriveFont(new Float(18.0)));
		usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		loginPanel.add(usernameLabel);
		/*
		usernameField = new JTextField("");
		usernameField.setFont(UIFont.textLight.deriveFont(new Float(18.0)));
		loginPanel.add(usernameField);

		//Password

		JLabel passwordLabel = new JLabel("Password", JLabel.LEFT);
		passwordLabel.setFont(UIFont.textSemibold.deriveFont(new Float(18.0)));
		loginPanel.add(usernameLabel);

		passwordField = new JTextField("");
		passwordField.setFont(UIFont.textLight.deriveFont(new Float(18.0)));
		loginPanel.add(passwordField);*/
		
		frame.add(loginPanel);
	}
}
