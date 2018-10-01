package client.gui;

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
import common.Constants;
import common.JsonUtil;

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

		// Create component
		JLabel userNameLabel = new JLabel("Enter username:");
		userNameLabel.setFont(new Font("Times New Roman", Font.PLAIN, 14));
		userNameLabel.setPreferredSize(new Dimension(100, 24));
		userNameField = new JTextField();
		userNameField.setPreferredSize(new Dimension(100, 24));
		JButton loginButton = new JButton("Login");
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
					showMessageDialog(null, "Username cannot null");
				} else if (isValidUsername(username)) {
					ClientConnection.getInstance().setUserName(username);
					JSONObject jsonObject = new JSONObject();
					jsonObject.put(Constants.USER_NAME, username);
					jsonObject = JsonUtil.parse(Constants.LOGIN, jsonObject);
					ClientConnection.getInstance().sendMsg(jsonObject.toString());
				} else {
					showMessageDialog(null, "Username invalid");
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
		showMessageDialog(null, "Login Failed: Username is already being used");
	}
}