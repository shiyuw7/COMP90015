package server.common;

import java.util.ArrayList;
import java.util.List;

public class GameBoard {

	protected String[][] board;
	private final int ROWS;
	private final int COLUMNS;
	private int round;

	public GameBoard() {
		this.ROWS = 20;
		this.COLUMNS = 20;
		this.round = 0;
		this.board = new String[ROWS][COLUMNS];
		initializeBoard();
	}

	public GameBoard(int rows, int columns) {
		this.ROWS = rows;
		this.COLUMNS = columns;
		this.round = 0;
		this.board = new String[ROWS][COLUMNS];
		initializeBoard();
	}

	public GameBoard(GameBoard gameBoard) {
		this.ROWS = gameBoard.ROWS;
		this.COLUMNS = gameBoard.COLUMNS;
		this.round = gameBoard.round;
		this.board = new String[ROWS][COLUMNS];
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLUMNS; c++) {
				board[r][c] = gameBoard.board[r][c];
			}
		}
	}

	/**
	 * Initialize the data in the board
	 */
	private void initializeBoard() {
		for (int r = 0; r < this.ROWS; r++) {
			for (int c = 0; c < this.COLUMNS; c++) {
				this.board[r][c] = "";
			}
		}
	}

	public int getROWS() {
		return ROWS;
	}

	public int getCOLUMNS() {
		return COLUMNS;
	}

	public int getRound() {
		return round;
	}

	public String[][] getBoard() {
		return this.board;
	}

	public String getValue(int row, int col) {
		if (this.inRange(row, col)) {
			return this.board[row][col];
		}
		return "";
	}

	public void setValue(int row, int col, String value) {
		this.board[row][col] = value;
		round++;
	}

	public void clearValue(int row, int col) {
		this.board[row][col] = "";
		round--;
	}

	/**
	 * Get all the word which can be made.
	 * 
	 * @return In first round (round = 1), return only one character. In other round
	 *         (round > 1), return words that are more than one character.
	 */
	public List<String> getWord(int row, int col) {
		List<String> words = new ArrayList<>();
//		if (round == 1) {
		if (!this.isTouch(row, col)) {
			words.add(this.board[row][col]);
		} else {
			StringBuilder wordInRow = new StringBuilder();
			StringBuilder wordInCol = new StringBuilder();
			// Calculate start row and column
			int startRow = row - 1;
			int startCol = col - 1;
			while (startRow >= 0 && isNotEmpty(startRow, col)) {
				startRow--;
			}
			startRow++;
			while (startCol >= 0 && isNotEmpty(row, startCol)) {
				startCol--;
			}
			startCol++;
			// Make words
			while (startRow < this.ROWS && isNotEmpty(startRow, col)) {
				wordInRow.append(this.board[startRow][col]);
				startRow++;
			}
			while (startCol < this.COLUMNS && isNotEmpty(row, startCol)) {
				wordInCol.append(this.board[row][startCol]);
				startCol++;
			}
			if (wordInRow.length() > 1) {
				words.add(wordInRow.toString());
			}
			if (wordInCol.length() > 1) {
				words.add(wordInCol.toString());
			}
		}
		return words;
	}

	/**
	 * Determine whether the board is full or not
	 */
	public boolean boardFull() {
		for (int r = 0; r < this.ROWS; r++) {
			for (int c = 0; c < this.COLUMNS; c++) {
				if (isEmpty(r, c))
					return false;
			}
		}
		return true;
	}

	/**
	 * Determine whether the operation is success or not
	 * 
	 * @return Return false if not touch any other character except first round
	 */
	public boolean placeCharacter(int row, int col, String value) {
		if (this.isTileAvailable(row, col)) {
			// if (this.round != 0 && !this.isTouch(row, col)) {
			// return false;
			// }
			this.board[row][col] = value;
			this.round++;
			return true;
		}
		return false;
	}

	/**
	 * Determine whether the tile is available or not (In range and Is empty)
	 */
	public boolean isTileAvailable(int row, int col) {
		return (this.inRange(row, col) && isEmpty(row, col));
	}

	/**
	 * Determine whether the tile is empty or not
	 */
	private boolean isEmpty(int row, int col) {
		return this.board[row][col].equals("");
	}

	/**
	 * Determine whether the tile is empty or not
	 */
	private boolean isNotEmpty(int row, int col) {
		return !this.board[row][col].equals("");
	}

	/**
	 * Determine whether the tile is in the range of board or not
	 */
	private boolean inRange(int row, int col) {
		return row >= 0 && col >= 0 && row <= this.ROWS && col <= this.COLUMNS;
	}

	/**
	 * Determine whether the tile is on the edge or not
	 */
	private boolean onEdge(int row, int col) {
		return row == 0 || col == 0 || row == this.ROWS - 1 || col == this.COLUMNS - 1;
	}

	/**
	 * Determine whether the tile is in the corner or not
	 */
	private boolean inCorner(int row, int col) {
		return (row == 0 && col == 0) || (row == this.ROWS - 1 && col == this.COLUMNS - 1)
				|| (row == 0 && col == this.COLUMNS - 1) || (row == this.ROWS - 1 && col == 0);
	}

	/**
	 * Determine whether the tile touch any other character
	 */
	private boolean isTouch(int row, int col) {
		if (onEdge(row, col)) {
			// On edge
			if (inCorner(row, col)) {
				// In corner
				if (row == 0 && col == 0
						&& (isNotEmpty(row + 1, col) || isNotEmpty(row, col + 1))) {
					return true;
				} else if (row == (this.ROWS - 1) && col == (this.COLUMNS - 1)
						&& (isNotEmpty(row - 1, col) || isNotEmpty(row, col - 1))) {
					return true;
				} else if (row == 0 && col == (this.COLUMNS - 1)
						&& (isNotEmpty(row + 1, col) || isNotEmpty(row, col - 1))) {
					return true;
				} else if (row == (this.ROWS - 1) && col == 0
						&& (isNotEmpty(row - 1, col) || isNotEmpty(row, col + 1))) {
					return true;
				}
			} else {
				// Not in corner
				if (row == 0 && (isNotEmpty(row, col - 1) || isNotEmpty(row, col + 1)
						|| isNotEmpty(row + 1, col))) {
					return true;
				} else if (col == 0 && (isNotEmpty(row - 1, col) || isNotEmpty(row + 1, col)
						|| isNotEmpty(row, col + 1))) {
					return true;
				} else if (row == (this.ROWS - 1) && (isNotEmpty(row, col - 1)
						|| isNotEmpty(row, col + 1) || isNotEmpty(row - 1, col))) {
					return true;
				} else if (col == (this.COLUMNS - 1) && (isNotEmpty(row - 1, col)
						|| isNotEmpty(row + 1, col) || isNotEmpty(row, col - 1))) {
					return true;
				}
			}
		} else {
			// Not on edge
			if (isNotEmpty(row - 1, col) || isNotEmpty(row + 1, col) || isNotEmpty(row, col - 1)
					|| isNotEmpty(row, col + 1)) {
				return true;
			}
		}
		return false;
	}
}