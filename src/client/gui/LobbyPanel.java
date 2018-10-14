package client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
		setLayout(null);
		setBackground(new Color(245, 255, 250));

		// Create component
		JButton startButton = new JButton("Start Game");
		startButton.setBackground(new Color(245, 255, 250));
		startButton.setFont(new Font("Arial Black", Font.PLAIN, 18));
		startButton.setBounds(576, 199, 160, 50);

		JButton joinGameButton = new JButton("Join Game");
		joinGameButton.setBackground(new Color(245, 255, 250));
		joinGameButton.setFont(new Font("Arial Black", Font.PLAIN, 18));
		joinGameButton.setBounds(576, 264, 160, 45);

		JButton joinButton = new JButton("Join Room");
		joinButton.setBackground(new Color(245, 255, 250));
		joinButton.setFont(new Font("Arial Black", Font.PLAIN, 18));
		joinButton.setBounds(576, 324, 160, 45);

		JButton leaveButton = new JButton("Leave Room");
		leaveButton.setBackground(new Color(245, 255, 250));
		leaveButton.setFont(new Font("Arial Black", Font.PLAIN, 18));
		leaveButton.setBounds(576, 392, 160, 51);

		JButton inviteButton = new JButton("Invite");
		inviteButton.setBackground(new Color(245, 255, 250));
		inviteButton.setFont(new Font("Arial Black", Font.PLAIN, 18));
		inviteButton.setBounds(621, 455, 115, 48);

		JButton refreshButton = new JButton("Refresh");
		refreshButton.setBackground(new Color(245, 255, 250));
		refreshButton.setFont(new Font("Arial Black", Font.PLAIN, 18));
		refreshButton.setBounds(621, 515, 115, 45);

		lobbyList = new JList<>();
		roomList = new JList<>();

		lobbyScroll = new JScrollPane(lobbyList);
		lobbyScroll.setBounds(299, 68, 184, 432);
		lobbyScroll.setPreferredSize(new Dimension(400, 200));
		JPanel lobbyScrollHeader = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) lobbyScrollHeader.getLayout();
		flowLayout_1.setVgap(15);
		JLabel label_1 = new JLabel("Lobby List");
		label_1.setFont(new Font("Century", Font.BOLD, 25));
		label_1.setForeground(new Color(255, 255, 255));
		lobbyScrollHeader.add(label_1);
		lobbyScrollHeader.setBackground(Color.GRAY);
		lobbyScroll.setColumnHeaderView(lobbyScrollHeader);

		roomScroll = new JScrollPane(roomList);
		roomScroll.setBounds(51, 68, 184, 432);
		roomScroll.setPreferredSize(new Dimension(400, 200));
		JPanel roomScrollHeader = new JPanel();
		FlowLayout flowLayout = (FlowLayout) roomScrollHeader.getLayout();
		flowLayout.setVgap(15);
		roomScrollHeader.setForeground(new Color(240, 255, 240));
		JLabel label = new JLabel("Room List");
		label.setFont(new Font("Century", Font.BOLD, 25));
		label.setForeground(new Color(255, 255, 255));
		roomScrollHeader.add(label);
		roomScrollHeader.setBackground(new Color(112, 128, 144));
		roomScroll.setColumnHeaderView(roomScrollHeader);

		JLabel lblScrabbleGame = new JLabel("Scrabble");
		lblScrabbleGame.setForeground(new Color(112, 128, 144));
		lblScrabbleGame.setFont(new Font("Century Gothic", Font.BOLD | Font.ITALIC, 42));
		lblScrabbleGame.setBounds(552, 58, 184, 68);

		JLabel lblNewLabel = new JLabel("Game");
		lblNewLabel.setForeground(new Color(112, 128, 144));
		lblNewLabel.setBackground(new Color(112, 128, 144));
		lblNewLabel.setFont(new Font("Century Gothic", Font.BOLD | Font.ITALIC, 40));
		lblNewLabel.setBounds(607, 124, 129, 60);

		// Add component
		add(startButton);
		add(joinGameButton);
		add(joinButton);
		add(leaveButton);
		add(inviteButton);
		add(refreshButton);
		add(lobbyScroll);
		add(roomScroll);
		add(lblScrabbleGame);
		add(lblNewLabel);

		// Add listener
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!roomListModel.contains(ClientConnection.getInstance().getUserName())) {
					showMessageDialog(MainFrame.getInstance(), "You are not in the room");
				} else if (roomListModel.size() < 2) {
					showMessageDialog(MainFrame.getInstance(),
							"Users in the room should larger than 1");
				} else {
					// broadcast Game Start to other players
					JSONObject jsonObject = new JSONObject();
					jsonObject.put(Constants.USER_NAME,
							ClientConnection.getInstance().getUserName());
					jsonObject = JsonUtil.parse(Constants.START_GAME, jsonObject);
					if (!ClientConnection.getInstance().sendMsg(jsonObject.toString())) {
						MainFrame.getInstance().disconnect(this.getClass());
					}
				}
			}
		});

		joinGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(Constants.USER_NAME, ClientConnection.getInstance().getUserName());
				jsonObject = JsonUtil.parse(Constants.JOIN_GAME, jsonObject);
				if (!ClientConnection.getInstance().sendMsg(jsonObject.toString())) {
					MainFrame.getInstance().disconnect(this.getClass());
				}
			}
		});

		joinButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (roomListModel.contains(ClientConnection.getInstance().getUserName())) {
					showMessageDialog(MainFrame.getInstance(), "You are already in the room");
				} else {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put(Constants.USER_NAME,
							ClientConnection.getInstance().getUserName());
					jsonObject = JsonUtil.parse(Constants.ADD_USER_TO_ROOM, jsonObject);
					if (!ClientConnection.getInstance().sendMsg(jsonObject.toString())) {
						MainFrame.getInstance().disconnect(this.getClass());
					}
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
					if (!ClientConnection.getInstance().sendMsg(jsonObject.toString())) {
						MainFrame.getInstance().disconnect(this.getClass());
					}
				} else {
					showMessageDialog(MainFrame.getInstance(), "You are not in the room");
				}
			}
		});

		inviteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lobbyList.getSelectedIndex() < 0) {
					showMessageDialog(MainFrame.getInstance(), "Please select a user");
				} else if (!roomListModel.contains(ClientConnection.getInstance().getUserName())) {
					showMessageDialog(MainFrame.getInstance(), "You are not in the room");
				} else {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put(Constants.USER_NAME, lobbyList.getSelectedValue());
					jsonObject = JsonUtil.parse(Constants.INVITE, jsonObject);
					if (!ClientConnection.getInstance().sendMsg(jsonObject.toString())) {
						MainFrame.getInstance().disconnect(this.getClass());
					}
				}
			}
		});

		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JSONObject jsonObject = new JSONObject();
				jsonObject = JsonUtil.parse(Constants.REFRESH, jsonObject);
				if (!ClientConnection.getInstance().sendMsg(jsonObject.toString())) {
					MainFrame.getInstance().disconnect(this.getClass());
				}
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
		this.lobbyList.setFont(new Font("Century", Font.PLAIN, 28));
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
		this.roomList.setFont(new Font("Century", Font.PLAIN, 28));
		roomScroll.setViewportView(this.roomList);
	}

	/**
	 * Add user to lobby list
	 */
	public void addToLobbyList(String userName) {
		lobbyListModel.addElement(userName);
		lobbyList = new JList<>(lobbyListModel);
		this.lobbyList.setFont(new Font("Century", Font.PLAIN, 28));
		lobbyScroll.setViewportView(lobbyList);
	}

	/**
	 * Add user to room list
	 */
	public void addToRoomList(String userName) {
		roomListModel.addElement(userName);
		roomList = new JList<>(roomListModel);
		this.roomList.setFont(new Font("Century", Font.PLAIN, 28));
		roomScroll.setViewportView(roomList);
		removeFromLobbyList(userName);
	}

	/**
	 * Remove user from lobby list
	 */
	public void removeFromLobbyList(String userName) {
		lobbyListModel.removeElement(userName);
		lobbyList = new JList<>(lobbyListModel);
		this.lobbyList.setFont(new Font("Century", Font.PLAIN, 28));
		lobbyScroll.setViewportView(lobbyList);
	}

	/**
	 * Remove user from room list
	 */
	public void removeFromRoomList(String userName) {
		roomListModel.removeElement(userName);
		roomList = new JList<>(roomListModel);
		this.roomList.setFont(new Font("Century", Font.PLAIN, 28));
		roomScroll.setViewportView(roomList);
	}

	/**
	 * Clear room list
	 */
	public void clearRoom() {
		roomListModel.removeAllElements();
		roomList = new JList<>(roomListModel);
		this.roomList.setFont(new Font("Century", Font.PLAIN, 28));
		roomScroll.setViewportView(roomList);
	}

	/**
	 * Pop up a window to ask user whether accept others' invitation
	 */
	public void invite(String inviter) {
		int dialogButton = JOptionPane.YES_NO_OPTION;
		int dialogResult = JOptionPane.showConfirmDialog(MainFrame.getInstance(),
				inviter + " invite you to join room?", "Invitation", dialogButton);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(Constants.USER_NAME, inviter);
		if (dialogResult == 0) {
			jsonObject.put(Constants.IS_ACCEPTED, true);
		} else {
			jsonObject.put(Constants.IS_ACCEPTED, false);
		}
		jsonObject = JsonUtil.parse(Constants.INVITE_REPLY, jsonObject);
		if (!ClientConnection.getInstance().sendMsg(jsonObject.toString())) {
			MainFrame.getInstance().disconnect(this.getClass());
		}
	}

	/**
	 * Pop up a window that show a refuse message
	 */
	public void inviteRefused(String invitee) {
		showMessageDialog(MainFrame.getInstance(), invitee + " refused your invitation");
	}

	/**
	 * Pop up a window that show a reply message
	 */
	public void gameStarted() {
		showMessageDialog(MainFrame.getInstance(), "Game has already started");
	}

	/**
	 * Pop up a window that show a reply message
	 */
	public void gameNotStarted() {
		showMessageDialog(MainFrame.getInstance(), "Game hasn't started");
	}
}