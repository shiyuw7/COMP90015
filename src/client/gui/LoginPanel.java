package client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.JSONObject;

import client.ClientConnection;
import client.common.Constants;
import client.common.JsonUtil;

import static javax.swing.JOptionPane.showMessageDialog;

@SuppressWarnings("serial")
public class LoginPanel extends JPanel {

	private JTextField userNameField;

	public LoginPanel() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setPreferredSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));
		setLayout(null);
		setBackground(new Color(245, 255, 250));

		// Create component
		JLabel userNameLabel = new JLabel("Enter username:");
		userNameLabel.setBounds(44, 185, 272, 100);
		userNameLabel.setFont(new Font("Century", Font.PLAIN, 31));
		userNameLabel.setPreferredSize(new Dimension(100, 24));

		userNameField = new JTextField();
		userNameField.setFont(new Font("Calibri Light", Font.PLAIN, 31));
		userNameField.setBounds(331, 214, 198, 52);
		userNameField.setPreferredSize(new Dimension(100, 24));

		JButton loginButton = new JButton("Login");
		loginButton.setBackground(new Color(245, 255, 250));
		loginButton.setFont(new Font("Arial Black", Font.PLAIN, 25));
		loginButton.setBounds(331, 308, 152, 61);
		loginButton.setPreferredSize(new Dimension(100, 24));

		// Add component
		add(userNameLabel);
		add(userNameField);
		add(loginButton);

		// Add listener
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String username = userNameField.getText();
				if (username.equals("")) {
					showMessageDialog(MainFrame.getInstance(), "Username cannot null");
				} else if (isValidUsername(username)) {
					ClientConnection.getInstance().setUserName(username);
					JSONObject jsonObject = new JSONObject();
					jsonObject.put(Constants.USER_NAME, username);
					jsonObject = JsonUtil.parse(Constants.LOGIN, jsonObject);
					ClientConnection.getInstance().sendMsg(jsonObject.toString());
				} else {
					showMessageDialog(MainFrame.getInstance(), "Username invalid");
				}
			}
		});
	}

	/**
	 * check the format of user name
	 */
	public boolean isValidUsername(String username) {
		for (int i = 0; i < username.length(); i++) {
			if ((Character.isDigit(username.charAt(i))
					|| Character.isLetter(username.charAt(i))) == false)
				return false;
		}
		return true;
	}

	/**
	 * clear user name field
	 */
	public void clear() {
		userNameField.setText("");
		showMessageDialog(MainFrame.getInstance(), "Login Failed: Username is already being used");
	}
}