package ui;

import com.sun.istack.internal.Nullable;
import uikit.UIFont;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;

class Alert
{
	private JPanel alert;
	private JPanel buttonPanel;
	private JFrame dimView;
	private JDialog dialog;
	private int numButtons;
	private ArrayList<JButton> buttons = new ArrayList<>();

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

		alert.setMinimumSize(new Dimension (400, 0));
	}

	void addButton(String text, ActionListener handler)
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
		button.setPreferredSize(new Dimension(200, 44));
		button.setMaximumSize(new Dimension(400, 44));
		if (numButtons < 3) button.setAlignmentY(Component.CENTER_ALIGNMENT);
		else button.setAlignmentX(Component.CENTER_ALIGNMENT);
			button.addActionListener(e ->
			                         {
				                         if (handler != null) handler.actionPerformed(e);
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
		dimView.setBackground(new Color(0f,0f,0f,0.5f));
		dimView.setVisible(true);

		//Present the dialog

		dialog = new JDialog();

		dialog.add(alert);
		if (buttonPanel != null) alert.add(buttonPanel);

		dialog.setUndecorated(true);
		dialog.setBackground(Color.white);
		dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setBounds(screenSize.width/2 - 200,screenSize.height/2 - dialog.getPreferredSize().height/2,400, dialog.getPreferredSize().height);
		dialog.setVisible(true);
	}
}
