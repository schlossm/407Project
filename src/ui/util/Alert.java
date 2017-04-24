package ui.util;

import uikit.UIFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.Integer.max;

class AlertTextField extends JTextField
{
	String placeholder;

	AlertTextField(String text)
	{
		super(text, 1);
	}
}

class AlertPasswordField extends JPasswordField
{
	String placeholder;

	AlertPasswordField(String text)
	{
		super(text, 1);
	}
}

class TextFieldArrayClass
{
	final JTextField textField;
	final String identifier;

	TextFieldArrayClass(String identifier, JTextField textField)
	{
		this.identifier = identifier;
		this.textField = textField;
	}
}

@SuppressWarnings({"unused", "RedundantCast", "unchecked", "ConstantConditions"})
public class Alert implements KeyListener, MLMDelegate
{
	private static ArrayList<JDialog> alertsThatArePresentOnScreenShowingToTheUserSoTheUserCanInteractWithThemAndMakeABCDoSomething = new ArrayList<>();
	private final JPanel alert;
	private final ArrayList<TextFieldArrayClass> textFields = new ArrayList<>();
	private final Map<String, JCheckBox> checkBoxes = new HashMap<>();
	private final Map<String, JComboBox> dropDowns = new HashMap<>();
	private final ArrayList<JButton> buttons = new ArrayList<>();
	private JPanel buttonPanel;
	private JFrame dimView;
	private int numButtons;
	private JTextField activeTextField;

	public Alert(String title, String message)
	{
		alert = new JPanel();

		alert.setLayout(new BoxLayout(alert, BoxLayout.Y_AXIS));
		alert.setBorder(new EmptyBorder(40, 40, 40, 40));
		alert.setMinimumSize(new Dimension(400, 80));

		if (title != null)
		{
			JLabel titleLabel = new JLabel(title, JLabel.CENTER);
			titleLabel.setFont(UIFont.textBold.deriveFont(12.0f));
			titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			alert.add(titleLabel);
			alert.add(Box.createRigidArea(new Dimension(0, 30)));
		}
		if (message != null && !Objects.equals(message, ""))
		{
			JLabel messageLabel = new JLabel(convertToMultiline(message), JLabel.CENTER);
			messageLabel.setFont(UIFont.textRegular.deriveFont(12.0f));
			messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			alert.add(messageLabel);
			alert.add(Box.createRigidArea(new Dimension(0, 20)));
		}
	}

	public void addTextField(String placeholder, String identifier, boolean isSecure)
	{
		JTextField textField;
		if (!isSecure)
		{
			textField = new AlertTextField(placeholder);
			((AlertTextField) textField).placeholder = placeholder;
		}
		else
		{
			textField = new AlertPasswordField(placeholder);
			((JPasswordField) textField).setEchoChar((char) 0);
			((AlertPasswordField) textField).placeholder = placeholder;
		}
		textField.setFont(UIFont.textLight.deriveFont(10.0f));
		textField.setMinimumSize(new Dimension(100, 44));
		textField.setPreferredSize(new Dimension(320, 44));
		textField.setAlignmentX(Component.CENTER_ALIGNMENT);
		textField.addMouseListener(new MouseListenerManager(this));
		textField.setForeground(Color.lightGray);
		textField.setBackground(Color.gray);
		textField.setBorder(new EmptyBorder(0, 20, 0, 20));

		textFields.add(new TextFieldArrayClass(identifier.toLowerCase(), textField));
		alert.add(textField);
		alert.add(Box.createRigidArea(new Dimension(0, 8)));
	}

	public void showError(String error, JTextField onTextField)
	{
		JLabel errorLabel = new JLabel(error);
		errorLabel.setHorizontalTextPosition(JLabel.LEFT);
		errorLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		errorLabel.setFont(UIFont.textSemibold.deriveFont(8f));
		errorLabel.setForeground(Color.red);
		errorLabel.setMinimumSize(new Dimension(100, 32));
		errorLabel.setPreferredSize(new Dimension(320, 32));

		JPanel panel = (JPanel) alertsThatArePresentOnScreenShowingToTheUserSoTheUserCanInteractWithThemAndMakeABCDoSomething.get(alertsThatArePresentOnScreenShowingToTheUserSoTheUserCanInteractWithThemAndMakeABCDoSomething.size() - 1).getContentPane().getComponent(0);

		Component[] components = panel.getComponents();
		int count = 0;
		for (Component component : components)
		{
			if (component instanceof JTextField)
			{
				if ((JTextField) component == onTextField)
				{
					((JTextField) component).setBackground(Color.red);
					panel.add(errorLabel, count + 1);
				}
			}
			count++;
		}

		JDialog dialog = alertsThatArePresentOnScreenShowingToTheUserSoTheUserCanInteractWithThemAndMakeABCDoSomething.get(alertsThatArePresentOnScreenShowingToTheUserSoTheUserCanInteractWithThemAndMakeABCDoSomething.size() - 1);
		dialog.revalidate();
		dialog.repaint();
		dialog.pack();
		dialog.setBounds(dimView.getBounds().x + dimView.getBounds().width / 2 - dialog.getWidth(), dimView.getBounds().y + dimView.getBounds().height / 2 - dialog.getPreferredSize().height / 2, dialog.getWidth(), dialog.getPreferredSize().height);
	}

	public void addCheckBox(String question, String identifier)
	{
		JCheckBox checkBox = new JCheckBox(question);
		checkBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		checkBoxes.put(identifier, checkBox);
		alert.add(checkBox);
		alert.add(Box.createRigidArea(new Dimension(0, 8)));
	}

	public void addDropDown(String[] options, int defaultOption, String identifier)
	{
		JComboBox<String> dropDown = new JComboBox<>(options);
		dropDown.setSelectedIndex(defaultOption);
		alert.add(dropDown);
		alert.add(Box.createRigidArea(new Dimension(0, 8)));
		dropDowns.put(identifier, dropDown);
	}

	@Deprecated
	public void addButton(String text, ButtonType type, ActionListener handler, boolean e)
	{
		addButton(text, type, handler);
	}

	public void addButton(String text, ButtonType type, ActionListener handler)
	{
		//Make sure the button panel is done properly
		if (buttonPanel == null)
		{
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
			buttonPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
			buttonPanel.setMinimumSize(new Dimension(44, 44));
		}
		numButtons++;
		if (numButtons == 3)
		{
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

			for (JButton button : buttons)
			{
				button.setAlignmentX(Component.CENTER_ALIGNMENT);
			}

			buttonPanel.setMinimumSize(new Dimension(320, 44 * buttons.size()));
		}

		//Initialize the button
		JButton button = new JButton(text);
		button.setFont(UIFont.textLight.deriveFont(9.0f));
		button.setMinimumSize(new Dimension(44, 44));
		button.setMaximumSize(new Dimension(400, 44));

		//Add the handler (if there's one)
		if (handler != null)
		{
			button.addActionListener(handler);
		}

		//Add the dismiss handler.  Every button will do this
		button.addActionListener(e -> dispose());

		//Default type button
		if (type == ButtonType.defaultType)
		{
			button.setFont(UIFont.textBold.deriveFont(9.0f));
			Action action = new AbstractAction("complete")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					for (ActionListener listener : button.getActionListeners())
					{
						listener.actionPerformed(e);
					}
				}
			};

			action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ENTER"));

			button.getActionMap().put("complete", action);
			button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY), "complete");
		}
		else if (type == ButtonType.cancel) //Cancel type button
		{
			button.setFont(UIFont.textRegular.deriveFont(9.0f));
			Action action = new AbstractAction("escape")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					for (ActionListener listener : button.getActionListeners())
					{
						listener.actionPerformed(e);
					}
				}
			};

			action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ESCAPE"));
			button.getActionMap().put("escape", action);
			button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY), "escape");
		}
		else if (type == ButtonType.destructive)
		{
			button.setForeground(Color.RED);
		}

		//Align the buttons
		if (numButtons < 3) { button.setAlignmentY(Component.CENTER_ALIGNMENT); }
		else { button.setAlignmentX(Component.CENTER_ALIGNMENT); }

		//Add the button to the proper lists
		buttonPanel.add(button);
		buttons.add(button);
	}

	public void show(JFrame aboveFrame)
	{
		if (!alertsThatArePresentOnScreenShowingToTheUserSoTheUserCanInteractWithThemAndMakeABCDoSomething.isEmpty())
		{
			JDialog presentOne = alertsThatArePresentOnScreenShowingToTheUserSoTheUserCanInteractWithThemAndMakeABCDoSomething.get(alertsThatArePresentOnScreenShowingToTheUserSoTheUserCanInteractWithThemAndMakeABCDoSomething.size() - 1);

			presentOne.setVisible(false);
		}
		else
		{
			//Show a Dim View

			dimView = new JFrame("ABC");

			dimView.setBounds(aboveFrame.getBounds());
			dimView.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			dimView.setUndecorated(true);
			dimView.setBackground(new Color(0f, 0f, 0f, 0.5f));
			dimView.setVisible(true);
		}

		//Present the dialog

		JDialog dialog = new JDialog();
		dialog.addKeyListener(this);

		dialog.add(alert);
		if (buttonPanel != null)
		{
			alert.add(buttonPanel);
		}

		dialog.setFocusTraversalPolicy(new FocusTraversalPolicy()
		{
			@Override
			public Component getComponentAfter(Container aContainer, Component aComponent)
			{
				if (aComponent instanceof JDialog)
				{
					if (textFields.get(0).textField.getText().contains(((AlertTextField) textFields.get(0).textField).placeholder))
					{
						textFields.get(0).textField.setForeground(Color.white);
						textFields.get(0).textField.setText("");
					}
					return textFields.get(0).textField;
				}
				if (aComponent instanceof JTextField)
				{
					if (!textFields.isEmpty())
					{
						boolean shouldReturnNext = false;
						for (TextFieldArrayClass textFieldArrayClass : textFields)
						{
							if (textFieldArrayClass.textField == (JTextField) aComponent)
							{
								if (Objects.equals(textFieldArrayClass.textField.getText(), ""))
								{
									if (textFieldArrayClass.textField instanceof JPasswordField)
									{
										((JPasswordField) textFieldArrayClass.textField).setEchoChar((char) 0);
										textFieldArrayClass.textField.setText(((AlertPasswordField) textFieldArrayClass.textField).placeholder);
									}
									else if (textFieldArrayClass.textField instanceof JTextField)
									{
										textFieldArrayClass.textField.setText(((AlertTextField) textFieldArrayClass.textField).placeholder);
									}

									textFieldArrayClass.textField.setForeground(Color.lightGray);
								}
								shouldReturnNext = true;
								continue;
							}
							if (shouldReturnNext)
							{
								if (textFieldArrayClass.textField.getText().contains(((AlertTextField) textFieldArrayClass.textField).placeholder))
								{
									textFieldArrayClass.textField.setForeground(Color.white);
									textFieldArrayClass.textField.setText("");
								}
								return textFieldArrayClass.textField;
							}
						}
					}
				}
				else if (aComponent == buttons.get(buttons.size() - 1))
				{
					if (textFields.get(0).textField.getText().contains(((AlertTextField) textFields.get(0).textField).placeholder))
					{
						textFields.get(0).textField.setForeground(Color.white);
						textFields.get(0).textField.setText("");
					}
					return textFields.get(0).textField;
				}
				if (aComponent instanceof JTextField)
				{
					return buttons.get(0);
				}
				if (!buttons.isEmpty())
				{
					if ((buttons.indexOf((JButton) aComponent) + 1) == buttons.size())
					{
						if (textFields.get(0).textField.getText().contains(((AlertTextField) textFields.get(0).textField).placeholder))
						{
							textFields.get(0).textField.setForeground(Color.white);
							textFields.get(0).textField.setText("");
						}
						return textFields.get(0).textField;
					}
					return buttons.get((buttons.indexOf((JButton) aComponent) + 1));
				}
				return null;
			}

			private void checkEmpty(int i)
			{
				if (Objects.equals(textFields.get(i).textField.getText(), ""))
				{
					if (textFields.get(i).textField instanceof JPasswordField)
					{
						((JPasswordField) textFields.get(i).textField).setEchoChar((char) 0);
						textFields.get(i).textField.setText(((AlertPasswordField) textFields.get(i).textField).placeholder);
					}
					else
					{
						textFields.get(i).textField.setText(((AlertTextField) textFields.get(i).textField).placeholder);
					}

					textFields.get(i).textField.setForeground(Color.lightGray);
				}
			}

			@Override
			public Component getComponentBefore(Container aContainer, Component aComponent)
			{
				if (aComponent instanceof JDialog)
				{
					return buttons.get(buttons.size() - 1);
				}
				if (aComponent instanceof JTextField)
				{
					boolean shouldReturnPrevious = false;
					for (int i = 0; i < textFields.size(); i++)
					{
						if (textFields.get(i).textField == (JTextField) aComponent)
						{
							if (i == 0)
							{
								checkEmpty(i);
								return buttons.get(buttons.size() - 1);
							}
							checkEmpty(i);
							shouldReturnPrevious = true;
							i -= 2;

							continue;
						}
						if (shouldReturnPrevious)
						{
							if (textFields.get(i).textField.getText().contains(((AlertTextField) textFields.get(i).textField).placeholder))
							{
								textFields.get(i).textField.setForeground(Color.white);
								textFields.get(i).textField.setText("");
							}
							return textFields.get(i).textField;
						}
					}
				}
				if ((buttons.indexOf((JButton) aComponent) - 1) < 0)
				{
					if (textFields.get(textFields.size() - 1).textField.getText().contains(((AlertTextField) textFields.get(textFields.size() - 1).textField).placeholder))
					{
						textFields.get(textFields.size() - 1).textField.setForeground(Color.white);
						textFields.get(textFields.size() - 1).textField.setText("");
					}
					return textFields.get(textFields.size() - 1).textField;
				}
				return buttons.get((buttons.indexOf((JButton) aComponent) - 1));
			}

			@Override
			public Component getFirstComponent(Container aContainer)
			{
				if (!textFields.isEmpty())
				{
					if (textFields.get(0).textField.getText().contains(((AlertTextField) textFields.get(0).textField).placeholder))
					{
						textFields.get(0).textField.setForeground(Color.white);
						textFields.get(0).textField.setText("");
					}
					return textFields.get(0).textField;
				}
				if (!buttons.isEmpty()) { return buttons.get(0); }
				return null;
			}

			@Override
			public Component getLastComponent(Container aContainer)
			{
				if (!buttons.isEmpty()) { buttons.get(buttons.size() - 1); }
				if (!textFields.isEmpty())
				{
					if (textFields.get(textFields.size() - 1).textField.getText().contains(((AlertTextField) textFields.get(textFields.size() - 1).textField).placeholder))
					{
						textFields.get(textFields.size() - 1).textField.setForeground(Color.white);
						textFields.get(textFields.size() - 1).textField.setText("");
					}
					return textFields.get(textFields.size() - 1).textField;
				}
				return null;
			}

			@Override
			public Component getDefaultComponent(Container aContainer)
			{
				return null;
			}
		});

		dialog.setUndecorated(true);
		dialog.setBackground(Color.white);
		dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (buttons.size() == 2)
		{
			int width = 0;

			for (JButton button : buttons)
			{
				if (button.getPreferredSize().width > width)
				{
					width = button.getPreferredSize().width;
				}
			}

			for (JButton button : buttons)
			{
				button.setPreferredSize(new Dimension(width, 44));
			}

			dialog.setBounds(aboveFrame.getBounds().x + aboveFrame.getBounds().width / 2 - max(width + 44, 200), aboveFrame.getBounds().y + aboveFrame.getBounds().height / 2 - dialog.getPreferredSize().height / 2, max(width * 2 + 88, 400), dialog.getPreferredSize().height);
		}
		else
		{
			dialog.setBounds(screenSize.width / 2 - 200, screenSize.height / 2 - dialog.getPreferredSize().height / 2, 400, dialog.getPreferredSize().height);
		}
		alertsThatArePresentOnScreenShowingToTheUserSoTheUserCanInteractWithThemAndMakeABCDoSomething.add(dialog);
		dialog.setVisible(true);
	}

	public void dispose()
	{
		if (alertsThatArePresentOnScreenShowingToTheUserSoTheUserCanInteractWithThemAndMakeABCDoSomething.isEmpty())
		{ return; }

		JDialog presentOne = alertsThatArePresentOnScreenShowingToTheUserSoTheUserCanInteractWithThemAndMakeABCDoSomething.get(alertsThatArePresentOnScreenShowingToTheUserSoTheUserCanInteractWithThemAndMakeABCDoSomething.size() - 1);
		alertsThatArePresentOnScreenShowingToTheUserSoTheUserCanInteractWithThemAndMakeABCDoSomething.remove(presentOne);
		presentOne.dispose();

		if (alertsThatArePresentOnScreenShowingToTheUserSoTheUserCanInteractWithThemAndMakeABCDoSomething.isEmpty())
		{
			dimView.dispose();
		}
		else
		{
			JDialog nextOneInLine = alertsThatArePresentOnScreenShowingToTheUserSoTheUserCanInteractWithThemAndMakeABCDoSomething.get(alertsThatArePresentOnScreenShowingToTheUserSoTheUserCanInteractWithThemAndMakeABCDoSomething.size() - 1);
			nextOneInLine.setVisible(true);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) { }

	@Override
	public void keyPressed(KeyEvent e) { }

	@Override
	public void keyReleased(KeyEvent e) { }

	private String convertToMultiline(String orig)
	{
		return "<html> <head> <style type=\"text/css\"> body { font-family: SF UI Text Regular; font-size: 12px; text-align: center; } </style> </head> <body>" + orig.replaceAll("\n", "<br>") + "</body> </html>";
	}

	@Override
	public void mousePoint(MouseEvent action, MLMEventType eventType)
	{
		switch (eventType)
		{
			case clicked:
			{

				break;
			}

			case pressed:
			{
				if (action.getSource() instanceof JPasswordField)
				{
					JPasswordField field = (JPasswordField) (action.getSource());

					if (Objects.equals(String.valueOf(field.getPassword()), ((AlertPasswordField) field).placeholder))
					{
						field.setForeground(Color.white);
						field.setText("");
						field.setEchoChar('•');
					}

					if (field.getPassword().length != 0) { field.selectAll(); }
					field.requestFocus();
					if (activeTextField != null && Objects.equals(activeTextField.getText(), ""))
					{

						activeTextField.setText(((AlertTextField) activeTextField).placeholder);
						activeTextField.setForeground(Color.lightGray);
					}
					activeTextField = field;
				}
				else if (action.getSource() instanceof JTextField)
				{
					JTextField field = ((JTextField) (action.getSource()));
					field.requestFocus();
					if (field.getText().contains(((AlertTextField) field).placeholder))
					{
						field.setForeground(Color.white);
						field.setText("");
					}
					if (activeTextField != null)
					{
						if (Objects.equals(activeTextField.getText(), ""))
						{
							if (activeTextField instanceof JPasswordField)
							{
								((JPasswordField) activeTextField).setEchoChar((char) 0);
								activeTextField.setText(((AlertPasswordField) activeTextField).placeholder);
							}
							else if (activeTextField instanceof JTextField)
							{
								activeTextField.setText(((AlertTextField) activeTextField).placeholder);
							}

							activeTextField.setForeground(Color.lightGray);
						}
					}
					activeTextField = field;
				}
				break;
			}

			case released:
			{
				break;
			}

			case draggedIn:
			{
				break;
			}

			case draggedOut:
			{
				break;
			}
		}
	}

	public Map<String, JComboBox> getDropDowns()
	{
		return dropDowns;
	}

	public JComboBox dropDownForIdentifier(String identifier)
	{
		return dropDowns.get(identifier.toLowerCase());
	}

	public Map<String, JTextField> getTextFields()
	{
		Map<String, JTextField> returnInfo = new HashMap<>();
		for (TextFieldArrayClass textFieldArrayClass : textFields)
		{
			returnInfo.put(textFieldArrayClass.identifier, textFieldArrayClass.textField);
		}
		return returnInfo;
	}

	public JTextField textFieldForIdentifier(String identifier)
	{

		for (TextFieldArrayClass textFieldArrayClass : textFields)
		{
			if (Objects.equals(textFieldArrayClass.identifier, identifier.toLowerCase()))
			{
				return textFieldArrayClass.textField;
			}
		}
		return null;
	}

	public Map<String, JCheckBox> getCheckBoxes()
	{
		return checkBoxes;
	}

	public JCheckBox checkBoxForIdentifier(String identifier)
	{
		return checkBoxes.get(identifier.toLowerCase());
	}
}
