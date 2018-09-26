package server;

import java.util.ArrayList;
import java.util.List;

import common.GameBoard;

public class GameThread extends Thread {

	private static GameThread instance;
	private List<ClientConnection> clients;
	private GameBoard gameBoard;
	private int currentPlayer;

	private boolean status;

	private GameThread() {
		this.clients = new ArrayList<>();
		this.status = false;
	}

	public static synchronized GameThread getInstance() {
		if (instance == null) {
			instance = new GameThread();
		}
		return instance;
	}

	public synchronized void clientAdded(ClientConnection clientConnection) {
		// do something (broadcast)
		clients.add(clientConnection);
	}

	public synchronized void clientRemove(ClientConnection clientConnection) {
		// do something (broadcast)
		clients.remove(clientConnection);
	}

	@Override
	public synchronized void run() {
		this.status = true;
		// do something (Game start)
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public GameBoard getGameBoard() {
		return gameBoard;
	}

	public void setGameBoard(GameBoard gameBoard) {
		this.gameBoard = gameBoard;
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(int currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	/**
	 * Determine whether game is end or not
	 */
	public boolean isEnd() {
		if (isLastPlayer() || gameBoard.boardFull()) {
			return true;
		}
		return false;
	}

	/**
	 * Determine whether the player is the last one or not
	 */
	public boolean isLastPlayer() {
		if (clients.size() == 1) {
			return true;
		}
		return false;
	}

	/**
	 * Announce a winner
	 */
	public void announceWinner() {
		clients.get(currentPlayer).write("You are win. Balabala");
	}
}