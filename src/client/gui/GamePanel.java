package client.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import common.Constants;

@SuppressWarnings("serial")
public class GamePanel extends JPanel {

	private BoardPanel boardPanel;
	private JComboBox<String> comboBox;
	private JPanel confirmPanel;
	private JPanel votePanel;

	public GamePanel() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		setPreferredSize(new Dimension(800, 640));
		setLayout(new GridBagLayout());
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

		// Combo box
		comboBox = new JComboBox<>(Constants.CHARACTERS);
		comboBox.setPreferredSize(new Dimension(85, 80));
		comboBox.setSelectedItem(null);
		comboBox.addActionListener(boardPanel.new ComboBoxActionListener());
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 6;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 2;
		add(comboBox, c);

		// Confirm Panel
		confirmPanel = new JPanel();
		confirmPanel.setPreferredSize(new Dimension(150, 250));
		confirmPanel.setBorder(new CompoundBorder(new TitledBorder("Confirm Window"),
				new EmptyBorder(40, 50, 40, 50)));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 6;
		c.gridy = 2;
		c.gridwidth = 2;
		c.gridheight = 2;
		add(confirmPanel, c);

		// Vote Panel
		votePanel = new JPanel();
		votePanel.setPreferredSize(new Dimension(150, 250));
		votePanel.setBorder(new CompoundBorder(new TitledBorder("Vote Window"),
				new EmptyBorder(40, 50, 40, 50)));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 6;
		c.gridy = 4;
		c.gridwidth = 2;
		c.gridheight = 2;
		add(votePanel, c);
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

	public class ButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			confirmPanel.removeAll();
			comboBox.setEnabled(true);
			revalidate();
			repaint();
		}
	}

	// private void generateButton(char[] randomAlphabet) {
	// for (char alphabet : randomAlphabet) {
	// JButton jButton = new JButton(Character.toString(alphabet));
	// jButton.addActionListener(boardPanel.new ButtonActionListener());
	// buttonPanel.add(jButton);
	// }
	// }
}