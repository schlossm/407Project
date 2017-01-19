package ui;

import uikit.UIFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Objects;

/**
 * The Login Panel
 */
class Login implements ActionListener, DocumentListener, MLMDelegate
{
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JButton loginButton;

	private Object activeTextField;

	Login(JFrame frame)
	{
		JPanel loginPanel = new JPanel();
		loginPanel.addMouseListener(new MouseListenerManager(this));
		BoxLayout layout = new BoxLayout(loginPanel, BoxLayout.Y_AXIS);

		loginPanel.setLayout(layout);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		loginPanel.setBorder(new EmptyBorder((int)(screenSize.getHeight() * 0.2), 40, 20, 20));


		//Title

		JLabel title = new JLabel("ABC", JLabel.LEFT);
		title.setFont(UIFont.displayHeavy.deriveFont(10.0f));
		title.setAlignmentX(Component.LEFT_ALIGNMENT);
		loginPanel.add(title);

		loginPanel.add(Box.createRigidArea(new Dimension(0, 30)));


		//Username

		JLabel usernameLabel = new JLabel("Username", JLabel.LEFT);
		usernameLabel.setFont(UIFont.textRegular.deriveFont(10.0f));
		usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		loginPanel.add(usernameLabel);

		loginPanel.add(Box.createRigidArea(new Dimension(0, 10)));

		usernameField = new JTextField("Username", 1);
		usernameField.setFont(UIFont.textLight.deriveFont(10.0f));
		usernameField.setMaximumSize(new Dimension(screenSize.width/5 * 4, 50));
		usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
		usernameField.addActionListener(this);
		usernameField.getDocument().addDocumentListener(this);
		usernameField.addMouseListener(new MouseListenerManager(this));
		usernameField.setForeground(Color.lightGray);
		usernameField.setBorder(new EmptyBorder(0, 20, 0, 20));
		loginPanel.add(usernameField);

		loginPanel.add(Box.createRigidArea(new Dimension(0, 10)));


		//Password

		JLabel passwordLabel = new JLabel("Password", JLabel.LEFT);
		passwordLabel.setFont(UIFont.textRegular.deriveFont(10.0f));
		passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		loginPanel.add(passwordLabel);

		loginPanel.add(Box.createRigidArea(new Dimension(0, 10)));

		passwordField = new JPasswordField("", 1);
		passwordField.setFont(UIFont.textLight.deriveFont(10.0f));
		passwordField.setMaximumSize(new Dimension(screenSize.width/5 * 4, 50));
		passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
		passwordField.addActionListener(this);
		passwordField.getDocument().addDocumentListener(this);
		passwordField.addMouseListener(new MouseListenerManager(this));
		passwordField.setBorder(new EmptyBorder(0, 20, 0, 20));
		loginPanel.add(passwordField);

		loginPanel.add(Box.createRigidArea(new Dimension(0, 20)));


		//Login Button

		loginButton = new JButton("Login");

		loginButton.setFont(UIFont.textLight.deriveFont(9.0f));
		loginButton.addActionListener(this);
		loginButton.setMaximumSize(new Dimension(200, 44));
		loginButton.setVisible(false);
		loginButton.setFocusable(false);
		loginPanel.add(loginButton);


		//Administrative Stuff

		frame.add(loginPanel);
		loginPanel.setVisible(true);
		usernameField.setFocusable(false);
		passwordField.setFocusable(false);
		title.requestFocus();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == usernameField)
		{
			passwordField.requestFocus();
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e)
	{
		if (!Objects.equals(usernameField.getText(), "") && !Objects.equals(usernameField.getText(), "Username") && passwordField.getPassword().length != 0)
		{
			loginButton.setVisible(true);
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e)
	{
		if (Objects.equals(usernameField.getText(), "") || Objects.equals(usernameField.getText(), "Username") || passwordField.getPassword().length == 0)
		{
			loginButton.setVisible(false);
		}
	}

	@Override public void changedUpdate(DocumentEvent e) { }

	@Override
	public void mousePoint(MouseEvent action, MLMEventType eventType)
	{
		if (eventType == MLMEventType.pressed)
		{
			if (action.getSource().getClass() == JTextField.class)
			{
				JTextField field = ((JTextField)(action.getSource()));
				field.setFocusable(true);
				field.requestFocus();
				field.setFocusable(true);
				if (Objects.equals(field.getText(), "Username"))
				{
					field.setForeground(Color.black);
					field.setText("");
				}
				activeTextField = field;
			}
			else if (action.getSource().getClass() == JPasswordField.class)
			{
				JPasswordField field = (JPasswordField)(action.getSource());
				if (field.getPassword().length != 0) field.selectAll();
				field.setFocusable(true);
				field.requestFocus();
				if (activeTextField == usernameField && Objects.equals(usernameField.getText(), ""))
				{
					usernameField.setForeground(Color.lightGray);
					usernameField.setText("Username");
				}
				activeTextField = action.getSource();
			}
			else if (action.getSource().getClass() == JPanel.class)
			{
				usernameField.setFocusable(false);
				passwordField.setFocusable(false);
				if (Objects.equals(usernameField.getText(), ""))
				{
					usernameField.setForeground(Color.lightGray);
					usernameField.setText("Username");
				}
				activeTextField = null;
			}
			else if (action.getSource().getClass() == JButton.class)
			{
				loginButton.setFocusable(true);
			}
		}
		else if (eventType == MLMEventType.released)
		{
			loginButton.setFocusable(false);
			if (action.getSource().getClass() == JPasswordField.class)
			{
				JPasswordField field = (JPasswordField)(action.getSource());
				if (field.getPassword().length != 0) field.selectAll();
			}
			else if (action.getSource().getClass() == JPanel.class && activeTextField != null)
			{
				usernameField.setFocusable(false);
				passwordField.setFocusable(false);
				if (Objects.equals(usernameField.getText(), ""))
				{
					usernameField.setForeground(Color.lightGray);
					usernameField.setText("Username");
				}
				activeTextField = null;
			}
		}
	}
}
