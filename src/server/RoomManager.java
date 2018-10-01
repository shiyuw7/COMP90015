package server;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import common.Constants;
import common.JsonUtil;

public class RoomManager {

	private static RoomManager instance;
	private List<ClientConnection> connectedClients;

	private RoomManager() {
		connectedClients = new ArrayList<>();
	}

	public static synchronized RoomManager getInstance() {
		if (instance == null) {
			instance = new RoomManager();
		}
		return instance;
	}

	/**
	 * A client is connected to room
	 */
	public synchronized void clientConnected(ClientConnection clientConnection) {
		connectedClients.add(clientConnection);
	}

	/**
	 * A client is disconnected from room
	 */
	public synchronized void clientDisconnected(ClientConnection clientConnection) {
		connectedClients.remove(clientConnection);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(Constants.USER_NAME, clientConnection.getClientName());
		jsonObject = JsonUtil.parse(Constants.REMOVE_USER_FROM_ROOM, jsonObject);
		LobbyManager.getInstance().broadcastToAll(jsonObject.toString());
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

	public synchronized void clearRoom() {
		connectedClients.clear();
	}

	public synchronized List<ClientConnection> getConnectedClients() {
		return connectedClients;
	}

}