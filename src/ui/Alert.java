package ui;

import com.sun.istack.internal.Nullable;
import uikit.UIFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import static java.lang.Integer.max;

@SuppressWarnings("unused")
enum ButtonType
{
	defaultType, cancel, plain, destructive
}

@SuppressWarnings("SuspiciousMethodCalls")
class Alert implements KeyListener
{
	private JPanel              alert;
	private JPanel              buttonPanel;
	private JFrame              dimView;
	private JDialog             dialog;
	private int                 numButtons;
	private ArrayList<JButton>  buttons             = new ArrayList<>();
	private boolean             hasCancelAction     = false;
	private boolean             hasDefaultAction    = false;

	Alert(@Nullable String title, @Nullable String message)
	{
		alert = new JPanel();

		alert.setLayout(new BoxLayout(alert, BoxLayout.Y_AXIS));
		alert.setBorder(new EmptyBorder(40, 40, 40, 40));

		if (title != null)
		{
			JLabel titleLabel = new JLabel(title, JLabel.CENTER);
			titleLabel.setFont(UIFont.textBold.deriveFont(12.0f));
			titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			alert.add(titleLabel);
			alert.add(Box.createRigidArea(new Dimension(0, 30)));
		}
		if (message != null)
		{
			JLabel messageLabel = new JLabel(Window.convertToMultiline(message), JLabel.CENTER);
			messageLabel.setFont(UIFont.textRegular.deriveFont(12.0f));
			messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			alert.add(messageLabel);
			alert.add(Box.createRigidArea(new Dimension(0, 30)));
		}

		alert.setMinimumSize(new Dimension(400, 0));
	}

	void addButton(String text, ButtonType type, ActionListener handler)
	{
		if (buttonPanel == null)
		{
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
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
		if (type == ButtonType.defaultType)
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
			                         dimView.dispose();
			                         dialog.dispose();
		                         });
		buttonPanel.add(button);
		buttonPanel.setMinimumSize(new Dimension(44, 44));
		buttons.add(button);
	}

	void show(JFrame aboveFrame)
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

		dialog.setFocusTraversalPolicy(new FocusTraversalPolicy() {
			@Override
			public Component getComponentAfter(Container aContainer, Component aComponent)
			{
				return buttons.get((buttons.indexOf(aComponent) + 1) % buttons.size());
			}

			@Override
			public Component getComponentBefore(Container aContainer, Component aComponent)
			{
				return buttons.get((buttons.indexOf(aComponent) - 1) % buttons.size());
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

			dialog.setBounds(screenSize.width / 2 - max(width + 44, 200), screenSize.height / 2 - dialog.getPreferredSize().height / 2, max(width*2 + 88, 400), dialog.getPreferredSize().height);
		}
		else
		{
			dialog.setBounds(screenSize.width / 2 - 200, screenSize.height / 2 - dialog.getPreferredSize().height / 2, 400, dialog.getPreferredSize().height);
		}
		dialog.setVisible(true);
	}

	@Override
	public void keyTyped(KeyEvent e) { }

	@Override
	public void keyPressed(KeyEvent e) { }

	@Override
	public void keyReleased(KeyEvent e) { }
}
