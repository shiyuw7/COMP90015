package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

import org.json.JSONException;
import org.json.JSONObject;

import common.Constants;
import common.JsonUtil;

public class ClientConnection extends Thread {

	private Socket clientSocket;
	private int clientNum;
	private BufferedReader reader;
	private BufferedWriter writer;

	// Received message
	private String clientMsg;
	// Unique identification
	private String clientName;
	// 1. inLobby; 2. inRoom; 3. inGame
	private int clientStatus;
	// Current Count
	private int clientCount;

	public ClientConnection(Socket clientSocket, int clientNum) {
		try {
			this.clientSocket = clientSocket;
			this.clientNum = clientNum;
			this.reader = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
			this.writer = new BufferedWriter(
					new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			System.out.println(Thread.currentThread().getName()
					+ " - Reading messages from client's " + clientNum + " connection");
			while ((clientMsg = reader.readLine()) != null) {
				System.out.println(Thread.currentThread().getName() + " - Message from client "
						+ clientNum + " received: " + clientMsg);
				// Logical processing based on request type
				JSONObject msg = new JSONObject(clientMsg);
				String type = msg.getString(Constants.TYPE);
				JSONObject data = (JSONObject) msg.get(Constants.DATA);
				// Used for return message
				JSONObject jsonObject = new JSONObject();
				// Different request type
				switch (type) {
				case Constants.LOGIN:
					String userName = data.getString(Constants.USER_NAME);
					if (LobbyManager.getInstance().isUnique(userName)) {
						clientName = userName;
						clientStatus = 1;
						// Broadcast to other
						jsonObject.put(Constants.USER_NAME, clientName);
						jsonObject = JsonUtil.parse(Constants.ADD_USER_TO_LOBBY, jsonObject);
						LobbyManager.getInstance().broadcastToAll(jsonObject.toString());
						LobbyManager.getInstance().clientConnected(this);
						// Return all info of other clients to this client
						jsonObject = new JSONObject();
						jsonObject.put(Constants.IS_UNIQUE, true);
						jsonObject.put(Constants.LOBBY_LIST,
								LobbyManager.getInstance().getConnectedClientsArray());
						jsonObject.put(Constants.ROOM_LIST,
								RoomManager.getInstance().getConnectedClientsArray());
						jsonObject = JsonUtil.parse(Constants.LOGIN, jsonObject);
						write(jsonObject.toString());
					} else {
						jsonObject.put(Constants.IS_UNIQUE, false);
						jsonObject = JsonUtil.parse(Constants.LOGIN, jsonObject);
						write(jsonObject.toString());
					}
					break;
				case Constants.REFRESH:
					jsonObject.put(Constants.LOBBY_LIST,
							LobbyManager.getInstance().getConnectedClientsArray());
					jsonObject.put(Constants.ROOM_LIST,
							RoomManager.getInstance().getConnectedClientsArray());
					jsonObject = JsonUtil.parse(Constants.REFRESH, jsonObject);
					write(jsonObject.toString());
					break;
				case Constants.ADD_USER_TO_ROOM:
					clientStatus = 2;
					RoomManager.getInstance().clientConnected(this);
					// Broadcast to other
					jsonObject.put(Constants.USER_NAME, clientName);
					jsonObject = JsonUtil.parse(Constants.ADD_USER_TO_ROOM, jsonObject);
					LobbyManager.getInstance().broadcastToAll(jsonObject.toString());
					break;
				case Constants.REMOVE_USER_FROM_ROOM:
					clientStatus = 1;
					RoomManager.getInstance().clientDisconnected(this);
					// Broadcast to other
					jsonObject.put(Constants.USER_NAME, clientName);
					jsonObject = JsonUtil.parse(Constants.ADD_USER_TO_LOBBY, jsonObject);
					LobbyManager.getInstance().broadcastToAll(jsonObject.toString());
					break;
				case Constants.INVITE:
					LobbyManager.getInstance().invite(clientName,
							data.getString(Constants.USER_NAME));
					break;
				case Constants.INVITE_REPLY:
					jsonObject.put(Constants.USER_NAME, clientName);
					if (data.getBoolean(Constants.IS_ACCEPTED)) {
						// Accepted
						clientStatus = 2;
						RoomManager.getInstance().clientConnected(this);
						// Broadcast to other
						jsonObject = JsonUtil.parse(Constants.ADD_USER_TO_ROOM, jsonObject);
						LobbyManager.getInstance().broadcastToAll(jsonObject.toString());
					} else {
						// Not Accepted
						jsonObject.put(Constants.IS_ACCEPTED, false);
						jsonObject = JsonUtil.parse(Constants.INVITE_REPLY, jsonObject);
						LobbyManager.getInstance().broadcastToOne(jsonObject.toString(),
								data.getString(Constants.USER_NAME));
					}
					break;
				case Constants.START_GAME:
					if (GameManager.getInstance().getStatus()) {
						// if game has started
						jsonObject.put(Constants.IS_STARTED, true);
						jsonObject = JsonUtil.parse(Constants.REPLY_START_GAME, jsonObject);
						write(jsonObject.toString());
					} else {
						GameManager.getInstance().setStatus(true);
						for (ClientConnection clientConnection : RoomManager.getInstance()
								.getConnectedClients()) {
							clientConnection.setClientStatus(3);
							clientConnection.setClientCount(0);
							GameManager.getInstance().clientConnected(clientConnection);
						}
						// notify
						jsonObject = JsonUtil.parse(Constants.CLEAR_ROOM, jsonObject);
						LobbyManager.getInstance()
								.clearRoom(RoomManager.getInstance().getConnectedClients());
						LobbyManager.getInstance().broadcastToAll(jsonObject.toString());
						RoomManager.getInstance().clearRoom();
						GameManager.getInstance().start();
					}
					break;
				case Constants.VOTE:
					GameManager.getInstance().vote(data.getInt(Constants.PLACE_ROW),
							data.getInt(Constants.PLACE_COLUMN),
							data.getString(Constants.PLACE_VALUE),
							data.getString(Constants.CHOSEN_WORD), clientName);
					break;
				case Constants.VOTE_REPLY:
					GameManager.getInstance().voteReply(data.getBoolean(Constants.IS_WORD));
					break;
				case Constants.PASS:
					GameManager.getInstance().pass();
					break;
				case Constants.LOGOUT:
					GameManager.getInstance().clientDisconnected(this);
					break;
				}
			}
			clientSocket.close();
			ClientManager.getInstance().clientDisconnected(this);
			System.out.println(
					Thread.currentThread().getName() + " - Client " + clientNum + " disconnected");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			// System.out.println( Thread.currentThread().getName() + " - Client " +
			// clientNum + " disconnected");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getClientName() {
		return clientName;
	}

	public int getClientStatus() {
		return clientStatus;
	}

	public void setClientStatus(int clientStatus) {
		this.clientStatus = clientStatus;
	}

	public int getClientCount() {
		return clientCount;
	}

	public void setClientCount(int clientCount) {
		this.clientCount = clientCount;
	}

	public void count(int count) {
		this.clientCount = this.clientCount + count;
	}

	/**
	 * Write message to client from server
	 */
	public void write(String msg) {
		try {
			writer.write(msg + "\n");
			writer.flush();
			System.out.println(
					Thread.currentThread().getName() + " - Message sent to client " + clientNum);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}