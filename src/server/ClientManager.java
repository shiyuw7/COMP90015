package server;

import java.util.ArrayList;
import java.util.List;

public class ClientManager {

	private static ClientManager instance;
	private List<ClientConnection> connectedClients;

	private ClientManager() {
		connectedClients = new ArrayList<>();
	}

	public static synchronized ClientManager getInstance() {
		if (instance == null) {
			instance = new ClientManager();
		}
		return instance;
	}

	/**
	 * A client is connected to server
	 */
	public synchronized void clientConnected(ClientConnection clientConnection) {
		connectedClients.add(clientConnection);
	}

	/**
	 * A client is disconnected to server
	 */
	public synchronized void clientDisconnected(ClientConnection clientConnection) {
		int status = clientConnection.getClientStatus();
		if (status == 1) {
			LobbyManager.getInstance().clientDisconnected(clientConnection);
		} else if (status == 2) {
			LobbyManager.getInstance().clientDisconnected(clientConnection);
			RoomManager.getInstance().clientDisconnected(clientConnection);
		} else if (status == 3) {
			GameManager.getInstance().clientDisconnected(clientConnection);
		}
		connectedClients.remove(clientConnection);
	}

	/**
	 * Broadcast the client message to the given clients
	 */
	public synchronized void broadcast(String msg, List<ClientConnection> clients) {
		for (ClientConnection clientConnection : clients) {
			clientConnection.write(msg);
		}
	}

	/**
	 * Broadcast the client message to all the clients connected to the server
	 */
	public synchronized void broadcastToAll(String msg) {
		for (ClientConnection clientConnection : connectedClients) {
			clientConnection.write(msg);
		}
	}

	public synchronized List<ClientConnection> getConnectedClients() {
		return connectedClients;
	}
}