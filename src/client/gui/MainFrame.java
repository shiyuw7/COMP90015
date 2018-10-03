package client.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import client.ClientConnection;
import client.common.Constants;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private static MainFrame instance;
	private ConnectPanel connectPanel;
	private LoginPanel loginPanel;
	private LobbyPanel lobbyPanel;
	private GamePanel gamePanel;

	private MainFrame() {
		initialize();
	}

	public static MainFrame getInstance() {
		if (instance == null) {
			instance = new MainFrame();
		}
		return instance;
	}

	/**
	 * Initialize the contents of the frame
	 */
	private void initialize() {
		this.setTitle("Scrabble");
		this.setSize(Constants.WIDTH, Constants.HEIGHT);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int) ((screenSize.getWidth() - this.getWidth()) / 2),
				(int) ((screenSize.getHeight() - this.getHeight()) / 2));
		connectPanel = new ConnectPanel();
		add(connectPanel);
	}

	/**
	 * From connect to login
	 */
	public void connect() {
		remove(connectPanel);
		loginPanel = new LoginPanel();
		add(loginPanel);
		revalidate();
		repaint();
	}

	/**
	 * From login to lobby
	 */
	public void login() {
		remove(loginPanel);
		setTitle("Scrabble (Your Name: " + ClientConnection.getInstance().getUserName() + ")");
		lobbyPanel = new LobbyPanel();
		add(lobbyPanel);
		revalidate();
		repaint();
	}

	/**
	 * From lobby to game
	 */
	public void startGame() {
		remove(lobbyPanel);
		gamePanel = new GamePanel();
		add(gamePanel);
		revalidate();
		repaint();
	}

	/**
	 * From game to login
	 */
	public void logout() {
		remove(gamePanel);
		add(loginPanel);
		revalidate();
		repaint();
	}

	/**
	 * From game to login
	 */
	public void gameOver(boolean isTie, String winner, JSONArray countList) {
		String msg;
		if (isTie) {
			msg = "The game ended in a draw";
		} else {
			msg = "Winner: " + winner;
		}
		for (int i = 0; i < countList.length(); i++) {
			JSONObject jsonObject = countList.getJSONObject(i);
			msg = msg + "\n" + jsonObject.getString(Constants.USER_NAME) + " Count: "
					+ jsonObject.getInt(Constants.USER_COUNT);
		}
		JOptionPane.showMessageDialog(this, msg, "Game Over", JOptionPane.INFORMATION_MESSAGE);
		remove(gamePanel);
		add(loginPanel);
		revalidate();
		repaint();
	}

	/**
	 * show vote reply
	 */
	public void showVoteReply() {
		JOptionPane.showMessageDialog(this, "Someone disagree your chosen", "Vote Reply",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Every time player place a character, confirm button will be generated
	 */
	public void generateButton(List<String> words) {
		gamePanel.getComboBox().setEnabled(false);
		gamePanel.getPassButton().setEnabled(false);
		for (String word : words) {
			JButton jButton = new JButton(word);
			jButton.addActionListener(gamePanel.new ConfirmButtonActionListener());
			gamePanel.getConfirmPanel().add(jButton);
			JLabel jLabel = new JLabel("Score: " + word.length());
			gamePanel.getConfirmPanel().add(jLabel);
		}
		JButton jButton = new JButton("Cancel");
		jButton.addActionListener(gamePanel.new CancelButtonActionListener());
		gamePanel.getConfirmPanel().add(jButton);
		revalidate();
		repaint();
	}

	public LoginPanel getLoginPanel() {
		return loginPanel;
	}

	public LobbyPanel getLobbyPanel() {
		return lobbyPanel;
	}

	public GamePanel getGamePanel() {
		return gamePanel;
	}
}