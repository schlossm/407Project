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

@SuppressWarnings({"unused", "RedundantCast"})
public class Alert implements KeyListener, MLMDelegate
{
	private final JPanel alert;
	private JPanel buttonPanel;
	private JFrame dimView;
	private JDialog dialog;
	private int numButtons;
	private final Map<String, JTextField> textFields = new HashMap<>();
	private final Map<String, JCheckBox> checkBoxes = new HashMap<>();
	private final Map<String, JComboBox> dropDowns = new HashMap<>();
	private final ArrayList<JButton> buttons = new ArrayList<>();
	private boolean hasCancelAction = false;
	private boolean hasDefaultAction = false;

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
		textField.setPreferredSize(new Dimension(360, 44));
		textField.setAlignmentX(Component.CENTER_ALIGNMENT);
		textField.addMouseListener(new MouseListenerManager(this));
		textField.setForeground(Color.lightGray);
		textField.setBackground(Color.gray);
		textField.setBorder(new EmptyBorder(0, 20, 0, 20));

		textFields.put(identifier.toLowerCase(), textField);
		alert.add(textField);
		alert.add(Box.createRigidArea(new Dimension(0, 8)));
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

	public void addButton(String text, ButtonType type, ActionListener handler, boolean customEnterAction)
	{
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
		}

		JButton button = new JButton(text);
		button.setFont(UIFont.textLight.deriveFont(9.0f));
		button.setMaximumSize(new Dimension(400, 44));
		if (customEnterAction) { hasDefaultAction = true; }
		if (type == ButtonType.defaultType && !customEnterAction)
		{
			button.setFont(UIFont.textBold.deriveFont(9.0f));
			hasDefaultAction = true;
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
		else if (type == ButtonType.cancel)
		{
			button.setFont(UIFont.textRegular.deriveFont(9.0f));
			hasCancelAction = true;
			Action action = new AbstractAction("escape")
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					dimView.dispose();
					dialog.dispose();
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
		if (numButtons < 3) { button.setAlignmentY(Component.CENTER_ALIGNMENT); }
		else { button.setAlignmentX(Component.CENTER_ALIGNMENT); }
		button.addActionListener(e ->
		                         {
			                         if (handler != null) { handler.actionPerformed(e); }
			                         if (!customEnterAction)
			                         {
				                         dimView.dispose();
				                         dialog.dispose();
			                         }
		                         });
		buttonPanel.add(button);
		buttons.add(button);
	}

	public void show(JFrame aboveFrame)
	{
		//Show a Dim View

		dimView = new JFrame("ABC");

		dimView.setBounds(aboveFrame.getBounds());
		dimView.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		dimView.setUndecorated(true);
		dimView.setBackground(new Color(0f, 0f, 0f, 0.5f));
		dimView.setVisible(true);

		//Present the dialog

		dialog = new JDialog();
		dialog.addKeyListener(this);

		dialog.add(alert);
		if (buttonPanel != null)
		{
			alert.add(buttonPanel);
			if (!hasCancelAction)
			{
				JButton button = buttons.get(0);
				Action action = new AbstractAction("escape")
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						dimView.dispose();
						dialog.dispose();
					}
				};

				action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ESCAPE"));
				button.getActionMap().put("escape", action);
				button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put((KeyStroke) action.getValue(Action.ACCELERATOR_KEY), "escape");
			}
			if (!hasDefaultAction)
			{
				JButton button = buttons.get(0);
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
		}

		dialog.setFocusTraversalPolicy(new FocusTraversalPolicy()
		{
			@Override
			public Component getComponentAfter(Container aContainer, Component aComponent)
			{
				return buttons.get((buttons.indexOf((JButton) aComponent) + 1) % buttons.size());
			}

			@Override
			public Component getComponentBefore(Container aContainer, Component aComponent)
			{
				return buttons.get((buttons.indexOf((JButton) aComponent) - 1) % buttons.size());
			}

			@Override
			public Component getFirstComponent(Container aContainer)
			{
				buttons.get(0);
				return null;
			}

			@Override
			public Component getLastComponent(Container aContainer)
			{
				buttons.get(buttons.size() - 1);
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

			dialog.setBounds(screenSize.width / 2 - max(width + 44, 200), screenSize.height / 2 - dialog.getPreferredSize().height / 2, max(width * 2 + 88, 400), dialog.getPreferredSize().height);
		}
		else
		{
			dialog.setBounds(screenSize.width / 2 - 200, screenSize.height / 2 - dialog.getPreferredSize().height / 2, 400, dialog.getPreferredSize().height);
		}
		dialog.setVisible(true);
	}

	public void dispose()
	{
		dimView.dispose();
		dialog.dispose();
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
		return textFields;
	}

	public JTextField textFieldForIdentifier(String identifier)
	{
		return textFields.get(identifier.toLowerCase());
	}

	public Map<String, JCheckBox> getCheckBoxes()
	{
		return checkBoxes;
	}

	public JCheckBox getCheckBoxForIdentifier(String identifier)
	{
		return checkBoxes.get(identifier.toLowerCase());
	}
}
