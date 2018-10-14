package server;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import common.Constants;
import common.GameBoard;
import common.JsonUtil;

public class GameManager {

	private static GameManager instance;
	private List<ClientConnection> connectedClients;
	private boolean status;

	private GameBoard gameBoard;
	private int passCount;
	private int voteCount;
	private int agreeCount;

	private int currentPlayer;
	private String currentPlayerName;
	private int row;
	private int column;
	private String value;
	private String word;

	private GameManager() {
		gameBoard = new GameBoard();
		connectedClients = new ArrayList<>();
		currentPlayer = 0;
		passCount = 0;
		status = false;
	}

	public static synchronized GameManager getInstance() {
		if (instance == null) {
			instance = new GameManager();
		}
		return instance;
	}

	/**
	 * A client is connected to game
	 */
	public synchronized void clientConnected(ClientConnection clientConnection) {
		connectedClients.add(clientConnection);
	}

	/**
	 * A client is disconnected from game
	 */
	public synchronized void clientDisconnected(ClientConnection clientConnection) {
		if (clientConnection == connectedClients.get(currentPlayer)) {
			connectedClients.remove(clientConnection);
			// Next player
			if (currentPlayer >= connectedClients.size()) {
				currentPlayer = 0;
			}
			for (int i = 0; i < connectedClients.size(); i++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(Constants.NEXT_USER_NAME,
						connectedClients.get(currentPlayer).getClientName());
				if (i == currentPlayer) {
					jsonObject.put(Constants.IS_YOUR_ROUND, true);
				} else {
					jsonObject.put(Constants.IS_YOUR_ROUND, false);
				}
				jsonObject = JsonUtil.parse(Constants.PASS, jsonObject);
				connectedClients.get(i).write(jsonObject.toString());
			}
		} else {
			if (connectedClients.indexOf(clientConnection) < currentPlayer) {
				currentPlayer--;
			}
			connectedClients.remove(clientConnection);
		}
		// Broadcast Remove user
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(Constants.USER_NAME, clientConnection.getClientName());
		jsonObject = JsonUtil.parse(Constants.REMOVE_USER_FROM_GAME, jsonObject);
		broadcastToAll(jsonObject.toString());
		if (isEnd()) {
			announceWinner();
			clearGameStatus();
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
	 * Broadcast the start status of game to other players
	 */
	public synchronized void start() {
		status = true;
		// Random sort Player List
		// ...
		JSONArray countList = new JSONArray();
		for (ClientConnection clientConnection : connectedClients) {
			JSONObject user = new JSONObject();
			user.put(Constants.USER_NAME, clientConnection.getClientName());
			user.put(Constants.USER_COUNT, clientConnection.getClientCount());
			countList.put(user);
		}
		for (int i = 0; i < connectedClients.size(); i++) {
			ClientConnection player = connectedClients.get(i);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(Constants.COUNT_LIST, countList);
			jsonObject.put(Constants.USER_NAME, player.getClientName());
			jsonObject.put(Constants.NEXT_USER_NAME,
					connectedClients.get(currentPlayer).getClientName());
			// First player's round
			if (i == currentPlayer) {
				jsonObject.put(Constants.IS_YOUR_ROUND, true);
			} else {
				jsonObject.put(Constants.IS_YOUR_ROUND, false);
			}
			jsonObject = JsonUtil.parse(Constants.START_GAME, jsonObject);
			player.write(jsonObject.toString());
		}
	}

	/**
	 * A client join to game
	 */
	public synchronized void clientJoined(ClientConnection clientConnection) {
		JSONObject broadcastMsg = new JSONObject();
		broadcastMsg.put(Constants.USER_NAME, clientConnection.getClientName());
		broadcastMsg = JsonUtil.parse(Constants.JOIN_GAME, broadcastMsg);
		broadcastToAll(broadcastMsg.toString());
		connectedClients.add(clientConnection);

		JSONObject gameData = new JSONObject();
		gameData.put(Constants.IS_STARTED, true);
		JSONArray board = JsonUtil.stringArrayToJsonArrayTwo(gameBoard.getBoard());
		gameData.put(Constants.BOARD, board);

		JSONArray countList = new JSONArray();
		for (int i = 0; i < connectedClients.size(); i++) {
			if (i == currentPlayer) {
				gameData.put(Constants.NEXT_USER_NAME, connectedClients.get(i).getClientName());
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(Constants.USER_NAME, connectedClients.get(i).getClientName());
			jsonObject.put(Constants.USER_COUNT, connectedClients.get(i).getClientCount());
			countList.put(jsonObject);
		}
		gameData.put(Constants.COUNT_LIST, countList);
		gameData.put(Constants.ROUND, gameBoard.getRound());
		gameData = JsonUtil.parse(Constants.JOIN_GAME_REPLY, gameData);
		clientConnection.write(gameData.toString());
	}
	
	/**
	 * A client want to refresh
	 */
	public synchronized void refreshGame(ClientConnection clientConnection) {
		JSONObject gameData = new JSONObject();
		gameData.put(Constants.IS_STARTED, true);
		JSONArray board = JsonUtil.stringArrayToJsonArrayTwo(gameBoard.getBoard());
		gameData.put(Constants.BOARD, board);

		JSONArray countList = new JSONArray();
		for (int i = 0; i < connectedClients.size(); i++) {
			if (i == currentPlayer) {
				gameData.put(Constants.NEXT_USER_NAME, connectedClients.get(i).getClientName());
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(Constants.USER_NAME, connectedClients.get(i).getClientName());
			jsonObject.put(Constants.USER_COUNT, connectedClients.get(i).getClientCount());
			countList.put(jsonObject);
		}
		gameData.put(Constants.COUNT_LIST, countList);
		gameData.put(Constants.ROUND, gameBoard.getRound());
		gameData = JsonUtil.parse(Constants.REFRESH_GAME, gameData);
		clientConnection.write(gameData.toString());
	}

	/**
	 * Vote
	 */
	public synchronized void vote(int row, int column, String value, String word,
			String currentPlayerName) {
		if (isEnd()) {
			announceWinner();
			clearGameStatus();
		} else {
			this.voteCount = 0;
			this.agreeCount = 0;
			this.row = row;
			this.column = column;
			this.value = value;
			this.word = word;
			this.currentPlayerName = currentPlayerName;
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(Constants.PLACE_ROW, row);
			jsonObject.put(Constants.PLACE_COLUMN, column);
			jsonObject.put(Constants.PLACE_VALUE, value);
			jsonObject.put(Constants.CHOSEN_WORD, word);
			jsonObject.put(Constants.USER_NAME, currentPlayerName);
			jsonObject = JsonUtil.parse(Constants.VOTE, jsonObject);
			for (int i = 0; i < connectedClients.size(); i++) {
				if (i != currentPlayer) {
					ClientConnection clientConnection = connectedClients.get(i);
					clientConnection.write(jsonObject.toString());
				}
			}
		}
	}

	/**
	 * Vote Reply
	 */
	public synchronized void voteReply(boolean isWord) {
		if (isWord) {
			agreeCount++;
		}
		voteCount++;
		if (voteCount == connectedClients.size() - 1) {
			if (agreeCount == voteCount) {
				connectedClients.get(currentPlayer).count(word.length());
				int currentCount = connectedClients.get(currentPlayer).getClientCount();
				currentPlayer++;
				if (currentPlayer >= connectedClients.size()) {
					currentPlayer = 0;
				}
				gameBoard.setValue(row, column, value);
				// broadcast the character placed by player
				for (int i = 0; i < connectedClients.size(); i++) {
					ClientConnection player = connectedClients.get(i);
					JSONObject jsonObject = new JSONObject();
					jsonObject.put(Constants.PLACE_ROW, row);
					jsonObject.put(Constants.PLACE_COLUMN, column);
					jsonObject.put(Constants.PLACE_VALUE, value);
					jsonObject.put(Constants.USER_NAME, currentPlayerName);
					jsonObject.put(Constants.USER_COUNT, currentCount);
					jsonObject.put(Constants.NEXT_USER_NAME,
							connectedClients.get(currentPlayer).getClientName());
					// Next player's round
					if (i == currentPlayer) {
						jsonObject.put(Constants.IS_YOUR_ROUND, true);
					} else {
						jsonObject.put(Constants.IS_YOUR_ROUND, false);
					}
					jsonObject = JsonUtil.parse(Constants.PLACE_CHARACTER, jsonObject);
					player.write(jsonObject.toString());
				}
				passCount = 0;
			} else {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(Constants.USER_NAME,
						connectedClients.get(currentPlayer).getClientName());
				jsonObject.put(Constants.IS_WORD, false);
				jsonObject = JsonUtil.parse(Constants.VOTE_REPLY, jsonObject);
				broadcastToAll(jsonObject.toString());

				currentPlayer++;
				if (currentPlayer >= connectedClients.size()) {
					currentPlayer = 0;
				}
				for (int i = 0; i < connectedClients.size(); i++) {
					jsonObject = new JSONObject();
					jsonObject.put(Constants.NEXT_USER_NAME,
							connectedClients.get(currentPlayer).getClientName());
					if (i == currentPlayer) {
						jsonObject.put(Constants.IS_YOUR_ROUND, true);
					} else {
						jsonObject.put(Constants.IS_YOUR_ROUND, false);
					}
					jsonObject = JsonUtil.parse(Constants.PASS, jsonObject);
					connectedClients.get(i).write(jsonObject.toString());
				}
			}
		}
	}

	/**
	 * Broadcast the pass status to next player
	 */
	public synchronized void pass() {
		passCount++;
		if (isEnd()) {
			announceWinner();
			clearGameStatus();
		} else {
			currentPlayer++;
			if (currentPlayer >= connectedClients.size()) {
				currentPlayer = 0;
			}
			for (int i = 0; i < connectedClients.size(); i++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(Constants.NEXT_USER_NAME,
						connectedClients.get(currentPlayer).getClientName());
				if (i == currentPlayer) {
					jsonObject.put(Constants.IS_YOUR_ROUND, true);
				} else {
					jsonObject.put(Constants.IS_YOUR_ROUND, false);
				}
				jsonObject = JsonUtil.parse(Constants.PASS, jsonObject);
				connectedClients.get(i).write(jsonObject.toString());
			}
		}
	}

	/**
	 * Determine whether game is end or not
	 */
	private boolean isEnd() {
		if (isLastPlayer() || (passCount == connectedClients.size()) || gameBoard.boardFull()) {
			return true;
		}
		return false;
	}

	/**
	 * Determine whether the player is the last one or not
	 */
	private boolean isLastPlayer() {
		if (connectedClients.size() <= 1) {
			return true;
		}
		return false;
	}

	/**
	 * Announce a winner
	 */
	private void announceWinner() {
		status = false;
		if (connectedClients.size() == 0) {
			// No player
			System.out.println("No player");
		} else if (connectedClients.size() == 1) {
			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < connectedClients.size(); i++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(Constants.USER_NAME, connectedClients.get(i).getClientName());
				jsonObject.put(Constants.USER_COUNT, connectedClients.get(i).getClientCount());
				jsonArray.put(jsonObject);
			}
			ClientConnection winner = connectedClients.get(0);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(Constants.WINNER, winner.getClientName());
			jsonObject.put(Constants.IS_TIE, false);
			jsonObject.put(Constants.COUNT_LIST, jsonArray);
			jsonObject = JsonUtil.parse(Constants.GAME_OVER, jsonObject);
			winner.write(jsonObject.toString());
		} else {
			int maxCount = 0;
			String winner = "";
			boolean isTie = false;
			JSONArray countList = new JSONArray();
			for (int i = 0; i < connectedClients.size(); i++) {
				JSONObject user = new JSONObject();
				int usercount = connectedClients.get(i).getClientCount();
				String username = connectedClients.get(i).getClientName();
				if (usercount > maxCount) {
					isTie = false;
					maxCount = usercount;
					winner = username;
				} else if (usercount == maxCount) {
					isTie = true;
				}
				user.put(Constants.USER_NAME, username);
				user.put(Constants.USER_COUNT, usercount);
				countList.put(user);
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(Constants.WINNER, winner);
			jsonObject.put(Constants.IS_TIE, isTie);
			jsonObject.put(Constants.COUNT_LIST, countList);
			jsonObject = JsonUtil.parse(Constants.GAME_OVER, jsonObject);
			broadcastToAll(jsonObject.toString());
		}
	}

	/**
	 * Clear
	 */
	private void clearGameStatus() {
		for (ClientConnection clientConnection : connectedClients) {
			clientConnection.setClientStatus(0);
		}
		gameBoard = new GameBoard();
		connectedClients = new ArrayList<>();
		currentPlayer = 0;
		passCount = 0;
		status = false;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public synchronized List<ClientConnection> getConnectedClients() {
		return connectedClients;
	}
}