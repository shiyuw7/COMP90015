package client;

import java.io.BufferedReader;
import java.io.IOException;

import org.json.JSONObject;

import client.gui.GamePanel;
import client.gui.MainFrame;
import common.Constants;
import common.JsonUtil;

public class ListeningThread extends Thread {

	private BufferedReader reader;
	private GamePanel gamePanel;

	public ListeningThread() {
		reader = ClientConnection.getInstance().getReader();
	}

	@Override
	public void run() {
		if (reader != null) {
			try {
				String serverMsg = null;
				while ((serverMsg = reader.readLine()) != null) {
					// Logical processing based on request type
					JSONObject msg = new JSONObject(serverMsg);
					String type = msg.getString(Constants.TYPE);
					JSONObject data = (JSONObject) msg.get(Constants.DATA);
					// Different request type
					switch (type) {
					case Constants.START_GAME:
						MainFrame.getInstance().startGame();
						gamePanel = MainFrame.getInstance().getGamePanel();
						if (!JsonUtil.getBooleanDataByType(serverMsg, Constants.IS_YOUR_ROUND)) {
							gamePanel.getComboBox().setEnabled(false);
							gamePanel.getPassButton().setEnabled(false);
						}
						break;
					case Constants.PLACE_CHARACTER:
						gamePanel.getBoardPanel().setValue(data.getInt(Constants.PLACE_ROW),
								data.getInt(Constants.PLACE_COLUMN),
								data.getString(Constants.PLACE_VALUE));
						if (JsonUtil.getBooleanDataByType(serverMsg, Constants.IS_YOUR_ROUND)) {
							gamePanel.getComboBox().setEnabled(true);
							gamePanel.getPassButton().setEnabled(true);
						} else {
							gamePanel.getComboBox().setEnabled(false);
							gamePanel.getPassButton().setEnabled(false);
						}
						break;
					case Constants.PASS:
						if (JsonUtil.getBooleanDataByType(serverMsg, Constants.IS_YOUR_ROUND)) {
							gamePanel.getComboBox().setEnabled(true);
							gamePanel.getPassButton().setEnabled(true);
						} else {
							gamePanel.getComboBox().setEnabled(false);
							gamePanel.getPassButton().setEnabled(false);
						}
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}