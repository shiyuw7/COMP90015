package server;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import common.Constants;
import common.GameBoard;
import common.JsonUtil;

public class GameManager {

	private static GameManager instance;
	private GameBoard gameBoard;
	private List<ClientConnection> players;
	private int currentPlayer;
	private int passCount;

	private boolean status;

	private GameManager() {
		gameBoard = new GameBoard();
		players = new ArrayList<>();
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
	 * A player is added to game
	 */
	public synchronized void playerAdded(ClientConnection clientConnection) {
		players.add(clientConnection);
	}

	/**
	 * A player is removed from game
	 */
	public synchronized void playerRemoved(ClientConnection clientConnection) {
		players.remove(clientConnection);
		if (isEnd()) {
			announceWinner();
		}
	}

	/**
	 * Broadcast the player message to all players
	 */
	public synchronized void broadcastToAll(String msg) {
		for (ClientConnection player : players) {
			player.write(msg);
		}
	}

	/**
	 * Broadcast the start status of game to other players
	 */
	public synchronized void start() {
		status = true;
		// Random sort Player List
		// ...
		for (int i = 0; i < players.size(); i++) {
			ClientConnection player = players.get(i);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(Constants.USER_NAME, player.getClientName());
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
	 * Broadcast the status of every round to other players
	 */
	public synchronized void placeCharacter(int row, int column, String value,
			String currentPlayerName) {
		if (isEnd()) {
			announceWinner();
		}
		currentPlayer++;
		if (currentPlayer >= players.size()) {
			currentPlayer = 0;
		}
		gameBoard.setValue(row, column, value);
		// broadcast the character placed by player
		for (int i = 0; i < players.size(); i++) {
			ClientConnection player = players.get(i);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(Constants.PLACE_ROW, row);
			jsonObject.put(Constants.PLACE_COLUMN, column);
			jsonObject.put(Constants.PLACE_VALUE, value);
			jsonObject.put(Constants.USER_NAME, currentPlayerName);
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
	}

	/**
	 * Broadcast the pass status to next player
	 */
	public synchronized void pass() {
		passCount++;
		if (isEnd()) {
			announceWinner();
		}
		currentPlayer++;
		if (currentPlayer >= players.size()) {
			currentPlayer = 0;
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(Constants.IS_YOUR_ROUND, true);
		jsonObject = JsonUtil.parse(Constants.PASS, jsonObject);
		players.get(currentPlayer).write(jsonObject.toString());
	}

	/**
	 * Determine whether game is end or not
	 */
	public boolean isEnd() {
		if (isLastPlayer() || (passCount == players.size()) || gameBoard.boardFull()) {
			return true;
		}
		return false;
	}

	/**
	 * Determine whether the player is the last one or not
	 */
	public boolean isLastPlayer() {
		if (players.size() <= 1) {
			return true;
		}
		return false;
	}

	/**
	 * Announce a winner
	 */
	public void announceWinner() {
		status = false;
		if (players.size() == 0) {
			System.out.println("No player");
		} else if (players.size() == 1) {
			System.out.println(players.get(0).getClientName() + " win");
			// players.get(0).write("");
		} else {
			System.out.println(players.get(currentPlayer).getClientName() + " win");
			// players.get(currentPlayer).write("");
		}
	}

	public boolean getStatus() {
		return status;
	}

	public GameBoard getGameBoard() {
		return gameBoard;
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}

	public synchronized List<ClientConnection> getPlayers() {
		return players;
	}
}