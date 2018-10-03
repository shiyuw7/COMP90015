package client.gui;

import java.awt.Dimension;
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
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setPreferredSize(MainFrame.getInstance().getPreferredSize());
		GridBagConstraints c = new GridBagConstraints();

		// Board Panel
		boardPanel = new BoardPanel();
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
		comboBox.setPreferredSize(new Dimension(width / 11, height / 8));
		comboBox.setSelectedItem(null);
		comboBox.addActionListener(boardPanel.new ComboBoxActionListener());
		// Pass Button
		passButton = new JButton(Constants.PASS);
		passButton.addActionListener(new PassButtonActionListener());
		// Confirm Panel
		confirmPanel = new JPanel();
		confirmPanel.setPreferredSize(new Dimension(width / 5, height / 4));
		confirmPanel.setBorder(new CompoundBorder(new TitledBorder("Confirm Window"),
				new EmptyBorder(2, 3, 2, 3)));
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
		// Logout Button
		logoutButton = new JButton("Log Out");
		logoutButton.addActionListener(new LogoutButtonActionListener());

		JPanel jPanel = new JPanel();
		jPanel.setPreferredSize(new Dimension(
				MainFrame.getInstance().getWidth() - MainFrame.getInstance().getHeight(),
				MainFrame.getInstance().getHeight() - 40));
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
			ClientConnection.getInstance().sendMsg(jsonObject.toString());
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
			MainFrame.getInstance().logout();
			JSONObject jsonObject = new JSONObject();
			jsonObject = JsonUtil.parse(Constants.LOGOUT, jsonObject);
			ClientConnection.getInstance().sendMsg(jsonObject.toString());
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
		ClientConnection.getInstance().sendMsg(jsonObject.toString());

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
			ClientConnection.getInstance().sendMsg(jsonObject.toString());
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