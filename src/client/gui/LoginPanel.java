package client.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.json.JSONObject;

import client.ClientConnection;
import client.ListeningThread;
import common.Constants;
import common.JsonUtil;

@SuppressWarnings("serial")
public class LoginPanel extends JPanel {

	public LoginPanel() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setPreferredSize(new Dimension(800, 640));
		
		// Create component
		JButton loginButton = new JButton("Login");
		loginButton.setPreferredSize(new Dimension(100, 24));

		// Add component
		add(loginButton);

		// Add listener
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// User name check
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(Constants.USER_NAME, ClientConnection.getInstance().getUsername());
				jsonObject = JsonUtil.parse(Constants.LOGIN, jsonObject);
				ClientConnection.getInstance().sendMsg(jsonObject.toString());
				
				// Start new thread to listen message from server
				ListeningThread listenThread = new ListeningThread();
				listenThread.start();
				
				MainFrame.getInstance().login();
			}
		});
	}
}