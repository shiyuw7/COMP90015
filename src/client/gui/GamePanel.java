package client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

import client.ClientConnection;
import client.common.Constants;
import client.common.GameBoard;
import client.common.JsonUtil;

@SuppressWarnings("serial")
public class GamePanel extends JPanel {

	private BoardPanel boardPanel;
	private JLabel currentPlayerLabel;
	private JComboBox<String> comboBox;
	private JButton passButton;
	private JPanel confirmPanel;
	private JButton logoutButton;

	private DefaultTableModel countTableModel;
	private JTable countTable;
	private JScrollPane scrollPane;

	public GamePanel() {
		boardPanel = new BoardPanel();
		initialize();
	}

	public GamePanel(GameBoard gameBoard) {
		boardPanel = new BoardPanel(gameBoard);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setPreferredSize(MainFrame.getInstance().getPreferredSize());
		GridBagConstraints c = new GridBagConstraints();

		// Board Panel
		boardPanel.setBackground(new Color(245, 245, 220));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 6;
		c.gridheight = 6;
		add(boardPanel, c);

		int width = MainFrame.getInstance().getWidth();
		int height = MainFrame.getInstance().getHeight();
		// Current player
		currentPlayerLabel = new JLabel();
		// Combo box
		comboBox = new JComboBox<>(Constants.CHARACTERS);
		comboBox.setPreferredSize(new Dimension(width / 11, height / 11));
		comboBox.setSelectedItem(null);
		comboBox.setBackground(Color.white);
		comboBox.setFont(new Font("Century", Font.PLAIN, 18));
		comboBox.addActionListener(boardPanel.new ComboBoxActionListener());
		// Pass Button
		passButton = new JButton(Constants.PASS);
		passButton.addActionListener(new PassButtonActionListener());
		passButton.setBackground(new Color(245, 255, 250));
		passButton.setFont(new Font("Calibri Light", Font.PLAIN, 16));
		passButton.setBounds(621, 392, 115, 48);
		// Confirm Panel
		confirmPanel = new JPanel();
		confirmPanel.setPreferredSize(new Dimension(width / 5, height / 4));
		confirmPanel.setBorder(new CompoundBorder(new TitledBorder("Confirm Window"),
				new EmptyBorder(2, 3, 2, 3)));
		confirmPanel.setBackground(new Color(245, 245, 220));
		// Count Table
		countTableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				// all cells false
				return false;
			}
		};
		countTable = new JTable(countTableModel);
		scrollPane = new JScrollPane(countTable);
		scrollPane.setPreferredSize(new Dimension(width / 5, height / 3));
		scrollPane.setBackground(new Color(245, 245, 220));
		// Logout Button
		logoutButton = new JButton("Log Out");
		logoutButton.addActionListener(new LogoutButtonActionListener());
		logoutButton.setBackground(new Color(245, 255, 250));
		logoutButton.setFont(new Font("Calibri Light", Font.PLAIN, 16));
		logoutButton.setBounds(621, 392, 115, 48);

		JPanel jPanel = new JPanel();
		jPanel.setPreferredSize(new Dimension(
				MainFrame.getInstance().getWidth() - MainFrame.getInstance().getHeight(),
				MainFrame.getInstance().getHeight() - 40));
		jPanel.setBackground(new Color(245, 245, 220));
		jPanel.add(currentPlayerLabel);
		jPanel.add(comboBox);
		jPanel.add(passButton);
		jPanel.add(confirmPanel);
		jPanel.add(scrollPane);
		jPanel.add(logoutButton);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 6;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 6;
		add(jPanel, c);
	}

	/**
	 * Every time player confirm a word
	 */
	public class ConfirmButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Remove all confirm buttons
			confirmPanel.removeAll();
			revalidate();
			repaint();
			// Send the status of every round to server
			JButton jButton = ((JButton) e.getSource());
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(Constants.PLACE_ROW, boardPanel.getPreviousRow());
			jsonObject.put(Constants.PLACE_COLUMN, boardPanel.getPreviousColumn());
			jsonObject.put(Constants.PLACE_VALUE, boardPanel.getPreviousValue());
			jsonObject.put(Constants.CHOSEN_WORD, jButton.getText());
			// Here should change to vote
			jsonObject = JsonUtil.parse(Constants.VOTE, jsonObject);
			if (!ClientConnection.getInstance().sendMsg(jsonObject.toString())) {
				MainFrame.getInstance().disconnect(this.getClass());
			}
			;
		}
	}

	/**
	 * Every time player cancel their choose
	 */
	public class CancelButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			boardPanel.clearCharacter();
			comboBox.setEnabled(true);
			passButton.setEnabled(true);
			// Remove all confirm buttons
			confirmPanel.removeAll();
			revalidate();
			repaint();
		}
	}

	/**
	 * Every time player logout
	 */
	public class LogoutButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JSONObject jsonObject = new JSONObject();
			jsonObject = JsonUtil.parse(Constants.LOGOUT, jsonObject);
			if (!ClientConnection.getInstance().sendMsg(jsonObject.toString())) {
				MainFrame.getInstance().disconnect(this.getClass());
			} else {
				MainFrame.getInstance().logout();
			}
		}
	}

	/**
	 * Initialize Count Table
	 */
	public void initializeCountTable(JSONArray data) {
		String[] usernames = JsonUtil.jsonArrayToStringArray(data, Constants.USER_NAME);
		String[] counts = JsonUtil.jsonArrayToIntArray(data, Constants.USER_COUNT);
		countTableModel.addColumn("Players", usernames);
		countTableModel.addColumn("Count", counts);
	}

	/**
	 * Update Count Table
	 */
	public void updateCountTable(String username, int usercount) {
		for (int i = 0; i < countTableModel.getRowCount(); i++) {
			if (countTableModel.getValueAt(i, 0).equals(username)) {
				countTableModel.setValueAt(usercount + "", i, 1);
			}
		}
	}

	/**
	 * Add to Count Table
	 */
	public void addToCountTable(String username) {
		String[] rowData = new String[2];
		rowData[0] = username;
		rowData[1] = "0";
		countTableModel.addRow(rowData);
	}

	/**
	 * Remove player from Count Table
	 */
	public void removePlayerFromTable(String username) {
		for (int i = 0; i < countTableModel.getRowCount(); i++) {
			if (countTableModel.getValueAt(i, 0).equals(username)) {
				countTableModel.removeRow(i);
			}
		}
	}

	/**
	 * Generate vote button
	 */
	public void generateVoteDialog(JSONObject data) {
		// Highlight

		String[] options = new String[2];
		options[0] = new String("Agree");
		options[1] = new String("Disagree");
		// time out
		int dialogResult = JOptionPane.showOptionDialog(MainFrame.getInstance(),
				data.getString(Constants.USER_NAME) + "'s chosen: "
						+ data.getString(Constants.CHOSEN_WORD),
				"Invitation", 0, JOptionPane.INFORMATION_MESSAGE, null, options, null);
		// time out
		JSONObject jsonObject = new JSONObject();
		if (dialogResult == 0) {
			jsonObject.put(Constants.IS_WORD, true);
		} else {
			jsonObject.put(Constants.IS_WORD, false);
		}
		jsonObject = JsonUtil.parse(Constants.VOTE_REPLY, jsonObject);
		if (!ClientConnection.getInstance().sendMsg(jsonObject.toString())) {
			MainFrame.getInstance().disconnect(this.getClass());
		}
		// Cancel Highlight
	}

	/**
	 * Every time player pass a round
	 */
	private class PassButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			comboBox.setEnabled(false);
			passButton.setEnabled(false);
			// Send the status of every round to server
			JSONObject jsonObject = new JSONObject();
			jsonObject = JsonUtil.parse(Constants.PASS, jsonObject);
			if (!ClientConnection.getInstance().sendMsg(jsonObject.toString())) {
				MainFrame.getInstance().disconnect(this.getClass());
			}
		}
	}

	public JPanel getConfirmPanel() {
		return confirmPanel;
	}

	public BoardPanel getBoardPanel() {
		return boardPanel;
	}

	public JComboBox<String> getComboBox() {
		return comboBox;
	}

	public JButton getPassButton() {
		return passButton;
	}

	public JTable getCountTable() {
		return countTable;
	}

	public void setCurrentPlayer(String currentPlayer) {
		this.currentPlayerLabel.setText("Current player: " + currentPlayer);
	}
}