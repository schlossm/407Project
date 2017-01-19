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
	private boolean typingPassword = true;

	Login(JFrame frame)
	{
		JPanel loginPanel = new JPanel();
		loginPanel.addMouseListener(new MouseListenerManager(this));
		loginPanel.setBackground(Color.WHITE);
		BoxLayout layout = new BoxLayout(loginPanel, BoxLayout.Y_AXIS);

		loginPanel.setLayout(layout);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		loginPanel.setBorder(new EmptyBorder((int) (screenSize.getHeight() * 0.2), 40, 20, 20));

		frame.setFocusTraversalPolicy(new FocusTraversalPolicy()
		{
			@Override
			public Component getComponentAfter(Container aContainer, Component aComponent)
			{
				if (aComponent == usernameField)
				{
					passwordField.selectAll();
					return passwordField;
				}
				else if (aComponent == passwordField)
				{
					if (loginButton.isVisible())
					{
						return loginButton;
					}
					typingPassword = false;
					return usernameField;
				}
				else if (aComponent == loginButton)
				{
					typingPassword = false;
					return usernameField;
				}

				return null;
			}

			@Override
			public Component getComponentBefore(Container aContainer, Component aComponent)
			{
				if (aComponent == usernameField)
				{
					if (loginButton.isVisible())
					{
						return loginButton;
					}
					passwordField.selectAll();
					return passwordField;
				}
				else if (aComponent == passwordField)
				{
					typingPassword = false;
					return usernameField;
				}
				else if (aComponent == loginButton)
				{
					typingPassword = false;
					return passwordField;
				}
				return null;
			}

			@Override
			public Component getFirstComponent(Container aContainer) { typingPassword = false; return usernameField; }

			@Override
			public Component getLastComponent(Container aContainer) { return passwordField; }

			@Override
			public Component getDefaultComponent(Container aContainer) { typingPassword = false; return null; }
		});


		//Title

		JLabel title = new JLabel("ABC", JLabel.LEFT);
		title.setFont(UIFont.displayHeavy.deriveFont(48.0f));
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
		usernameField.setMaximumSize(new Dimension(screenSize.width / 5 * 4, 50));
		usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
		usernameField.addActionListener(this);
		usernameField.getDocument().addDocumentListener(this);
		usernameField.addMouseListener(new MouseListenerManager(this));
		usernameField.setForeground(Color.lightGray);
		usernameField.setBackground(Color.gray);
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
		passwordField.setMaximumSize(new Dimension(screenSize.width / 5 * 4, 50));
		passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
		passwordField.addActionListener(this);
		passwordField.getDocument().addDocumentListener(this);
		passwordField.addMouseListener(new MouseListenerManager(this));
		passwordField.setBorder(new EmptyBorder(0, 20, 0, 20));
		passwordField.setBackground(Color.gray);
		loginPanel.add(passwordField);

		loginPanel.add(Box.createRigidArea(new Dimension(0, 20)));


		//Login Button

		loginButton = new JButton("Login");

		loginButton.setFont(UIFont.textLight.deriveFont(9.0f));
		loginButton.addActionListener(this);
		loginButton.setMaximumSize(new Dimension(200, 44));
		loginButton.setVisible(false);
		loginPanel.add(loginButton);


		//Administrative Stuff

		frame.add(loginPanel);
		loginPanel.setVisible(true);
		title.requestFocus();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == usernameField)
		{
			passwordField.requestFocus();
		}
		if (e.getSource() == passwordField)
		{
			typingPassword = false;
			//TODO: Request Verification of user/pass combination
		}
		else if (e.getSource() == loginButton)
		{
			System.out.println("Login Button Clicked");
			//TODO: Request Verification of user/pass combination
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e)
	{
		if (activeTextField == passwordField && !typingPassword)
		{
			typingPassword = true;
		}
		if (!Objects.equals(usernameField.getText(), "") && !Objects.equals(usernameField.getText(), "Username") && passwordField.getPassword().length != 0)
		{
			loginButton.setVisible(true);
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e)
	{
		if (activeTextField == passwordField && !typingPassword)
		{
			typingPassword = true;
		}
		if (Objects.equals(usernameField.getText(), "") || Objects.equals(usernameField.getText(), "Username") || passwordField.getPassword().length == 0)
		{
			loginButton.setVisible(false);
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) { }

	@Override
	public void mousePoint(MouseEvent action, MLMEventType eventType)
	{
		if (eventType == MLMEventType.pressed)
		{
			if (action.getSource().getClass() == JTextField.class)
			{
				typingPassword = false;
				JTextField field = ((JTextField) (action.getSource()));
				field.requestFocus();
				if (Objects.equals(field.getText(), "Username"))
				{
					field.setForeground(Color.black);
					field.setText("");
				}
				activeTextField = field;
			}
			else if (action.getSource().getClass() == JPasswordField.class)
			{
				JPasswordField field = (JPasswordField) (action.getSource());
				if (field.getPassword().length != 0 && !typingPassword) { field.selectAll(); }
				field.requestFocus();
				if (activeTextField == usernameField && Objects.equals(usernameField.getText(), ""))
				{
					usernameField.setForeground(Color.lightGray);
					usernameField.setText("Username");
				}
				activeTextField = field;
			}
			else if (action.getSource().getClass() == JPanel.class)
			{
				typingPassword = false;
				((JPanel) (action.getSource())).requestFocus();
				if (Objects.equals(usernameField.getText(), ""))
				{
					usernameField.setForeground(Color.lightGray);
					usernameField.setText("Username");
				}
				activeTextField = null;
			}
		}
		else if (eventType == MLMEventType.released)
		{
			if (action.getSource().getClass() == JPasswordField.class)
			{
				JPasswordField field = (JPasswordField) (action.getSource());
				if (field.getPassword().length != 0 && !typingPassword) { field.selectAll(); }
			}
			else if (action.getSource().getClass() == JPanel.class && activeTextField != null)
			{
				typingPassword = false;
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
