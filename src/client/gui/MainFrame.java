package client.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private static MainFrame instance;
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
		this.setSize(800, 640);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int) ((screenSize.getWidth() - this.getWidth()) / 2),
				(int) ((screenSize.getHeight() - this.getHeight()) / 2));
		loginPanel = new LoginPanel();
		add(loginPanel);
	}

	/**
	 * From login to lobby
	 */
	public void login() {
		remove(loginPanel);
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