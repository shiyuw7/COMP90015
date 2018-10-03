package server;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import server.common.Constants;
import server.common.JsonUtil;

public class LobbyManager {

	private static LobbyManager instance;
	private List<ClientConnection> connectedClients;

	private LobbyManager() {
		connectedClients = new ArrayList<>();
	}

	public static synchronized LobbyManager getInstance() {
		if (instance == null) {
			instance = new LobbyManager();
		}
		return instance;
	}

	/**
	 * A client is connected to lobby
	 */
	public synchronized void clientConnected(ClientConnection clientConnection) {
		connectedClients.add(clientConnection);
	}

	/**
	 * A client is disconnected from lobby
	 */
	public synchronized void clientDisconnected(ClientConnection clientConnection) {
		connectedClients.remove(clientConnection);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(Constants.USER_NAME, clientConnection.getClientName());
		jsonObject = JsonUtil.parse(Constants.REMOVE_USER_FROM_LOBBY, jsonObject);
		broadcastToAll(jsonObject.toString());
	}

	/**
	 * Broadcast the message to one client
	 */
	public synchronized void broadcastToOne(String msg, String username) {
		for (ClientConnection clientConnection : connectedClients) {
			if (clientConnection.getClientName().equals(username)) {
				clientConnection.write(msg);
			}
		}
	}

	/**
	 * Broadcast the message to the given clients
	 */
	public synchronized void broadcastToList(String msg, List<ClientConnection> clients) {
		for (ClientConnection clientConnection : clients) {
			clientConnection.write(msg);
		}
	}

	/**
	 * Broadcast the message to all the clients
	 */
	public synchronized void broadcastToAll(String msg) {
		for (ClientConnection clientConnection : connectedClients) {
			clientConnection.write(msg);
		}
	}

	/**
	 * Check whether user name is unique or not
	 */
	public synchronized boolean isUnique(String userName) {
		for (ClientConnection oldClient : connectedClients) {
			if (oldClient.getClientName().equals(userName)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Get all connected clients with JSON array format
	 */
	public JSONArray getConnectedClientsArray() {
		JSONArray connectedClientsArray = new JSONArray();
		for (ClientConnection client : connectedClients) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(Constants.USER_NAME, client.getClientName());
			jsonObject.put(Constants.USER_STATUS, client.getClientStatus());
			connectedClientsArray.put(jsonObject);
		}
		return connectedClientsArray;
	}

	/**
	 * Transfer invite message
	 */
	public synchronized void invite(String inviter, String invitee) {
		for (ClientConnection clientConnection : connectedClients) {
			if (clientConnection.getClientName().equals(invitee)) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(Constants.USER_NAME, inviter);
				jsonObject = JsonUtil.parse(Constants.INVITE, jsonObject);
				clientConnection.write(jsonObject.toString());
			}
		}
	}

	/**
	 * Clear the clients in room (When game starts)
	 */
	public synchronized void clearRoom(List<ClientConnection> inRoomClients) {
		for (ClientConnection client : inRoomClients) {
			connectedClients.remove(client);
		}
	}

	public synchronized List<ClientConnection> getConnectedClients() {
		return connectedClients;
	}

}