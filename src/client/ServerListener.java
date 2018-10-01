package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import client.gui.GamePanel;
import client.gui.MainFrame;
import common.Constants;
import common.JsonUtil;

public class ServerListener extends Thread {

	private Socket client;
	private BufferedReader reader;
	private GamePanel gamePanel;

	public ServerListener(Socket client) {
		try {
			this.client = client;
			this.reader = new BufferedReader(
					new InputStreamReader(client.getInputStream(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		if (reader != null && client.isConnected()) {
			try {
				String serverMsg = null;
				while ((serverMsg = reader.readLine()) != null) {
					// Logical processing based on request type
					JSONObject msg = new JSONObject(serverMsg);
					String type = msg.getString(Constants.TYPE);
					JSONObject data = (JSONObject) msg.get(Constants.DATA);
					// Different request type
					switch (type) {
					case Constants.LOGIN:
						if (data.getBoolean(Constants.IS_UNIQUE)) {
							MainFrame.getInstance().login();
							refreshLobby(data);
						} else {
							MainFrame.getInstance().getLoginPanel().clear();
						}
						break;
					case Constants.ADD_USER_TO_LOBBY:
						MainFrame.getInstance().getLobbyPanel()
								.addToLobbyList(data.getString(Constants.USER_NAME));
						break;
					case Constants.REMOVE_USER_FROM_LOBBY:
						MainFrame.getInstance().getLobbyPanel()
								.removeFromLobbyList(data.getString(Constants.USER_NAME));
						break;
					case Constants.ADD_USER_TO_ROOM:
						MainFrame.getInstance().getLobbyPanel()
								.addToRoomList(data.getString(Constants.USER_NAME));
						break;
					case Constants.REMOVE_USER_FROM_ROOM:
						MainFrame.getInstance().getLobbyPanel()
								.removeFromRoomList(data.getString(Constants.USER_NAME));
						break;
					case Constants.REFRESH:
						refreshLobby(data);
						break;
					case Constants.INVITE:
						MainFrame.getInstance().getLobbyPanel()
								.inviteConfirmation(data.getString(Constants.USER_NAME));
						break;
					case Constants.INVITE_REPLY:
						MainFrame.getInstance().getLobbyPanel()
								.inviteRefused(data.getString(Constants.USER_NAME));
						break;
					case Constants.START_GAME:
						MainFrame.getInstance().startGame();
						gamePanel = MainFrame.getInstance().getGamePanel();
						if (!JsonUtil.getBooleanDataByType(serverMsg, Constants.IS_YOUR_ROUND)) {
							gamePanel.getComboBox().setEnabled(false);
							gamePanel.getPassButton().setEnabled(false);
						}
						break;
					case Constants.CLEAR_ROOM:
						MainFrame.getInstance().getLobbyPanel().clearRoom();
						break;
					case Constants.PLACE_CHARACTER:
						gamePanel.getBoardPanel().setValue(data.getInt(Constants.PLACE_ROW),
								data.getInt(Constants.PLACE_COLUMN),
								data.getString(Constants.PLACE_VALUE));
						if (JsonUtil.getBooleanDataByType(serverMsg, Constants.IS_YOUR_ROUND)) {
							gamePanel.getComboBox().setEnabled(true);
							gamePanel.getPassButton().setEnabled(true);
						} else {
							gamePanel.getComboBox().setEnabled(false);
							gamePanel.getPassButton().setEnabled(false);
						}
						break;
					case Constants.PASS:
						if (JsonUtil.getBooleanDataByType(serverMsg, Constants.IS_YOUR_ROUND)) {
							gamePanel.getComboBox().setEnabled(true);
							gamePanel.getPassButton().setEnabled(true);
						} else {
							gamePanel.getComboBox().setEnabled(false);
							gamePanel.getPassButton().setEnabled(false);
						}
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void refreshLobby(JSONObject data) {
		// Refresh the lobby list
		String[] lobbyArray = JsonUtil.jsonArrayToStringArray(
				data.getJSONArray(Constants.LOBBY_LIST), Constants.USER_NAME);
		// Refresh the room list
		String[] roomArray = JsonUtil.jsonArrayToStringArray(
				data.getJSONArray(Constants.ROOM_LIST), Constants.USER_NAME);
		MainFrame.getInstance().getLobbyPanel().refreshRoomList(roomArray);
		// Get not in room list
		Set<String> s1 = new HashSet<String>(Arrays.asList(lobbyArray));
		Set<String> s2 = new HashSet<String>(Arrays.asList(roomArray));
		s1.removeAll(s2);
		String[] notInRoomArray = s1.toArray(new String[s1.size()]);
		MainFrame.getInstance().getLobbyPanel().refreshLobbyList(notInRoomArray);
	}

	public Socket getClient() {
		return client;
	}
}