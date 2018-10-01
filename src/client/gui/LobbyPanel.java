package client.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.json.JSONObject;

import client.ClientConnection;
import common.Constants;
import common.JsonUtil;

import static javax.swing.JOptionPane.showMessageDialog;

@SuppressWarnings("serial")
public class LobbyPanel extends JPanel {

	private JList<String> lobbyList;
	private JList<String> roomList;
	private DefaultListModel<String> lobbyListModel;
	private DefaultListModel<String> roomListModel;
	private JScrollPane lobbyScroll;
	private JScrollPane roomScroll;

	public LobbyPanel() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setPreferredSize(MainFrame.getInstance().getPreferredSize());

		// Create component
		JButton startButton = new JButton("Start Game");
		JButton joinButton = new JButton("Join Room");
		JButton leaveButton = new JButton("Leave Room");
		JButton inviteButton = new JButton("Invite");
		JButton refreshButton = new JButton("Refresh");
		JLabel yourName = new JLabel("Your name: " + ClientConnection.getInstance().getUserName());

		lobbyList = new JList<>();
		roomList = new JList<>();
		lobbyScroll = new JScrollPane(lobbyList);
		lobbyScroll.setPreferredSize(new Dimension(400, 200));
		roomScroll = new JScrollPane(roomList);
		roomScroll.setPreferredSize(new Dimension(400, 200));

		// Add component
		add(startButton);
		add(joinButton);
		add(leaveButton);
		add(inviteButton);
		add(refreshButton);
		add(yourName);

		add(lobbyScroll);
		add(roomScroll);

		// Add listener
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!roomListModel.contains(ClientConnection.getInstance().getUserName())) {
					showMessageDialog(null, "You are not in the room");
				} else if (roomListModel.size() < 2) {
					showMessageDialog(null, "Users in the room should larger than 1");
				} else {
					// broadcast Game Start to other players
					JSONObject jsonObject = new JSONObject();
					jsonObject.put(Constants.USER_NAME,
							ClientConnection.getInstance().getUserName());
					jsonObject = JsonUtil.parse(Constants.START_GAME, jsonObject);
					ClientConnection.getInstance().sendMsg(jsonObject.toString());
				}
			}
		});

		joinButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (roomListModel.contains(ClientConnection.getInstance().getUserName())) {
					showMessageDialog(null, "You are already in the room");
				} else {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put(Constants.USER_NAME,
							ClientConnection.getInstance().getUserName());
					jsonObject = JsonUtil.parse(Constants.ADD_USER_TO_ROOM, jsonObject);
					ClientConnection.getInstance().sendMsg(jsonObject.toString());
				}
			}
		});

		leaveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (roomListModel.contains(ClientConnection.getInstance().getUserName())) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put(Constants.USER_NAME,
							ClientConnection.getInstance().getUserName());
					jsonObject = JsonUtil.parse(Constants.REMOVE_USER_FROM_ROOM, jsonObject);
					ClientConnection.getInstance().sendMsg(jsonObject.toString());
				} else {
					showMessageDialog(null, "You are not in the room");
				}
			}
		});

		inviteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lobbyList.getSelectedIndex() < 0) {
					showMessageDialog(null, "Please select a user");
				} else if (!roomListModel.contains(ClientConnection.getInstance().getUserName())) {
					showMessageDialog(null, "You are not in the room");
				} else {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put(Constants.USER_NAME, lobbyList.getSelectedValue());
					jsonObject = JsonUtil.parse(Constants.INVITE, jsonObject);
					ClientConnection.getInstance().sendMsg(jsonObject.toString());
				}
			}
		});

		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JSONObject jsonObject = new JSONObject();
				jsonObject = JsonUtil.parse(Constants.REFRESH, jsonObject);
				ClientConnection.getInstance().sendMsg(jsonObject.toString());
			}
		});
	}

	/**
	 * Refresh all information of lobby list
	 */
	public void refreshLobbyList(String[] lobbyList) {
		lobbyListModel = new DefaultListModel<>();
		for (String userName : lobbyList) {
			lobbyListModel.addElement(userName);
		}
		this.lobbyList = new JList<>(lobbyListModel);
		lobbyScroll.setViewportView(this.lobbyList);
	}

	/**
	 * Refresh all information of room list
	 */
	public void refreshRoomList(String[] roomList) {
		roomListModel = new DefaultListModel<>();
		for (String userName : roomList) {
			roomListModel.addElement(userName);
		}
		this.roomList = new JList<>(roomListModel);
		roomScroll.setViewportView(this.roomList);
	}

	/**
	 * Add user to lobby list
	 */
	public void addToLobbyList(String userName) {
		lobbyListModel.addElement(userName);
		lobbyList = new JList<>(lobbyListModel);
		lobbyScroll.setViewportView(lobbyList);
	}

	/**
	 * Add user to room list
	 */
	public void addToRoomList(String userName) {
		roomListModel.addElement(userName);
		roomList = new JList<>(roomListModel);
		roomScroll.setViewportView(roomList);
		removeFromLobbyList(userName);
	}

	/**
	 * Remove user from lobby list
	 */
	public void removeFromLobbyList(String userName) {
		lobbyListModel.removeElement(userName);
		lobbyList = new JList<>(lobbyListModel);
		lobbyScroll.setViewportView(lobbyList);
	}

	/**
	 * Remove user from room list
	 */
	public void removeFromRoomList(String userName) {
		roomListModel.removeElement(userName);
		roomList = new JList<>(roomListModel);
		roomScroll.setViewportView(roomList);
	}

	/**
	 * Clear room list
	 */
	public void clearRoom() {
		roomListModel.removeAllElements();
		roomList = new JList<>(roomListModel);
		roomScroll.setViewportView(roomList);
	}

	/**
	 * Pop up a window to ask user whether accept others' invitation
	 */
	public void inviteConfirmation(String inviter) {
		int dialogButton = JOptionPane.YES_NO_OPTION;
		int dialogResult = JOptionPane.showConfirmDialog(null,
				inviter + " invite you to join room?", "Invitation", dialogButton);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(Constants.USER_NAME, inviter);
		if (dialogResult == 0) {
			jsonObject.put(Constants.IS_ACCEPTED, true);
		} else {
			jsonObject.put(Constants.IS_ACCEPTED, false);
		}
		jsonObject = JsonUtil.parse(Constants.INVITE_REPLY, jsonObject);
		ClientConnection.getInstance().sendMsg(jsonObject.toString());
	}

	/**
	 * Pop up a window that show a refuse message
	 */
	public void inviteRefused(String invitee) {
		showMessageDialog(null, invitee + " refused your invitation");
	}
}