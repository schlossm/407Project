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

class Login extends JPanel implements ActionListener, DocumentListener, MLMDelegate
{
	private JTextField      usernameField;
	private JPasswordField  passwordField;
	private JButton         loginButton;
	private JButton         quitButton;
	private Object          activeTextField;
	private boolean         typingPassword = true;

	private JFrame presentingFrame;

	Login(JFrame frame)
	{
		presentingFrame = frame;
		this.addMouseListener(new MouseListenerManager(this));
		this.setBackground(Color.WHITE);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new EmptyBorder((int) (frame.getHeight() * 0.2), 40, 20, 40));
		JPanel loginPan = this;

		frame.setFocusTraversalPolicy(new FocusTraversalPolicy() {
			@Override
			public Component getComponentAfter(Container aContainer, Component aComponent)
			{
				if (aComponent == frame || aComponent == loginPan)
				{
					if (Objects.equals(usernameField.getText(), "Username"))
					{
						usernameField.setForeground(Color.white);
						usernameField.setText("");
					}
					return usernameField;
				}
				else if (aComponent == usernameField)
				{
					if (Objects.equals(usernameField.getText(), ""))
					{
						usernameField.setText("Username");
						usernameField.setForeground(Color.lightGray);
					}
					passwordField.selectAll();
					return passwordField;
				}
				else if (aComponent == passwordField)
				{
					typingPassword = false;
					if (loginButton.isVisible())
					{
						return loginButton;
					}
					else
					{
						if (Objects.equals(usernameField.getText(), "Username"))
						{
							usernameField.setForeground(Color.white);
							usernameField.setText("");
						}
						return usernameField;
					}
				}
				else if (aComponent == loginButton)
				{
					typingPassword = false;
					if (Objects.equals(usernameField.getText(), "Username"))
					{
						usernameField.setForeground(Color.white);
						usernameField.setText("");
					}
					return usernameField;
				}

				return null;
			}

			@Override
			public Component getComponentBefore(Container aContainer, Component aComponent)
			{
				if (aComponent == frame || aComponent == loginPan)
				{
					if (Objects.equals(usernameField.getText(), "Username"))
					{
						usernameField.setForeground(Color.white);
						usernameField.setText("");
					}
					return usernameField;
				}
				else if (aComponent == usernameField)
				{
					if (Objects.equals(usernameField.getText(), ""))
					{
						usernameField.setText("Username");
						usernameField.setForeground(Color.lightGray);
					}
					typingPassword = false;
					if (loginButton.isVisible())
					{
						return loginButton;
					}
					passwordField.selectAll();
					return passwordField;
				}
				else if (aComponent == passwordField)
				{
					if (Objects.equals(usernameField.getText(), "Username"))
					{
						usernameField.setForeground(Color.white);
						usernameField.setText("");
					}
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
			public Component getFirstComponent(Container aContainer)
			{
				if (Objects.equals(usernameField.getText(), "Username"))
				{
					usernameField.setForeground(Color.white);
					usernameField.setText("");
				}
				typingPassword = false;
				return usernameField;
			}

			@Override
			public Component getLastComponent(Container aContainer)
			{
				if (Objects.equals(usernameField.getText(), ""))
				{
					usernameField.setText("Username");
					usernameField.setForeground(Color.lightGray);
				}
				return passwordField;
			}

			@Override
			public Component getDefaultComponent(Container aContainer) { typingPassword = false; return null; }
		});


		//Title

		JLabel title = new JLabel("ABC", JLabel.LEFT);
		title.setFont(UIFont.displayHeavy.deriveFont(48.0f));
		title.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(title);

		this.add(Box.createRigidArea(new Dimension(0, 30)));


		//Username

		JLabel usernameLabel = new JLabel("Username", JLabel.LEFT);
		usernameLabel.setFont(UIFont.textRegular.deriveFont(10.0f));
		usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(usernameLabel);

		this.add(Box.createRigidArea(new Dimension(0, 10)));

		usernameField = new JTextField("Username", 1);
		usernameField.setFont(UIFont.textLight.deriveFont(10.0f));
		usernameField.setMaximumSize(new Dimension(frame.getWidth(), 50));
		usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
		usernameField.addActionListener(this);
		usernameField.getDocument().addDocumentListener(this);
		usernameField.addMouseListener(new MouseListenerManager(this));
		usernameField.setForeground(Color.lightGray);
		usernameField.setBackground(Color.gray);
		usernameField.setBorder(new EmptyBorder(0, 20, 0, 20));
		this.add(usernameField);

		this.add(Box.createRigidArea(new Dimension(0, 10)));


		//Password

		JLabel passwordLabel = new JLabel("Password", JLabel.LEFT);
		passwordLabel.setFont(UIFont.textRegular.deriveFont(10.0f));
		passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(passwordLabel);

		this.add(Box.createRigidArea(new Dimension(0, 10)));

		passwordField = new JPasswordField("", 1);
		passwordField.setFont(UIFont.textLight.deriveFont(10.0f));
		passwordField.setMaximumSize(new Dimension(frame.getWidth(), 50));
		passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
		passwordField.addActionListener(this);
		passwordField.getDocument().addDocumentListener(this);
		passwordField.addMouseListener(new MouseListenerManager(this));
		passwordField.setBorder(new EmptyBorder(0, 20, 0, 20));
		passwordField.setBackground(Color.gray);
		passwordField.setForeground(Color.white);
		this.add(passwordField);

		this.add(Box.createRigidArea(new Dimension(0, 20)));


		//Login Button

		loginButton = new JButton("Login");
		loginButton.setFont(UIFont.textLight.deriveFont(9.0f));
		loginButton.addActionListener(this);
		loginButton.setMaximumSize(new Dimension(200, 44));
		loginButton.setVisible(false);
		this.add(loginButton);

		this.add(Box.createRigidArea(new Dimension(0, 20)));


		//Quit Button

		quitButton = new JButton("Quit");
		quitButton.setFont(UIFont.textLight.deriveFont(9.0f));
		quitButton.addActionListener(this);
		quitButton.setMaximumSize(new Dimension(200, 44));

		Action action = new AbstractAction("escape")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		};

		action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ESCAPE"));
		quitButton.getActionMap().put("escape", action);
		quitButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY), "escape");

		this.add(quitButton);
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
			this.requestFocus();
			Alert incorrectPassword = new Alert("Incorrect Credentials", "Your username or password were incorrect.\n\nPlease try again.");
			incorrectPassword.addButton("OK", ButtonType.defaultType, e1 ->
			{
				usernameField.requestFocus();
				usernameField.selectAll();
			});
			incorrectPassword.show(presentingFrame);
			//TODO: Request Verification of user/pass combination
		}
		else if (e.getSource() == loginButton)
		{
			this.requestFocus();
			Alert incorrectPassword = new Alert("Incorrect Credentials", "Your username or password were incorrect.\n\nPlease try again.");
			incorrectPassword.addButton("OK", ButtonType.defaultType, e12 ->
			{
				usernameField.requestFocus();
				usernameField.selectAll();
			});
			incorrectPassword.show(presentingFrame);
			//TODO: Request Verification of user/pass combination
		}
		else if (e.getSource() == quitButton)
		{
			System.exit(0);
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e)
	{
		if (activeTextField == usernameField)
		{
			usernameField.setForeground(Color.white);
		}
		else if (activeTextField == passwordField && !typingPassword)
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
					field.setForeground(Color.white);
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
					usernameField.setText("Username");
					usernameField.setForeground(Color.lightGray);
				}
				activeTextField = field;
			}
		}
		else if (eventType == MLMEventType.released)
		{
			if (action.getSource().getClass() == JPasswordField.class)
			{
				JPasswordField field = (JPasswordField) (action.getSource());
				if (field.getPassword().length != 0 && !typingPassword) { field.selectAll(); }
			}
			else if (action.getSource() == this && activeTextField != null)
			{
				typingPassword = false;
				this.requestFocus();
				if (Objects.equals(usernameField.getText(), ""))
				{
					usernameField.setText("Username");
					usernameField.setForeground(Color.lightGray);
				}
				activeTextField = null;
			}
		}
	}
}
