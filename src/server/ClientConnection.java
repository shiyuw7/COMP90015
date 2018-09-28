package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

import org.json.JSONException;
import org.json.JSONObject;

import common.Constants;

public class ClientConnection extends Thread {

	private Socket clientSocket;
	private int clientNum;
	private BufferedReader reader;
	private BufferedWriter writer;

	// Received message
	private String clientMsg;
	// Unique identification
	private String clientName;
	// Whether in a game or not
	private boolean status;
	// Current Count
	private int clientCount;

	public ClientConnection(Socket clientSocket, int clientNum) {
		try {
			this.clientSocket = clientSocket;
			this.clientNum = clientNum;
			this.reader = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
			this.writer = new BufferedWriter(
					new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			System.out.println(Thread.currentThread().getName()
					+ " - Reading messages from client's " + clientNum + " connection");
			while ((clientMsg = reader.readLine()) != null) {
				System.out.println(Thread.currentThread().getName() + " - Message from client "
						+ clientNum + " received: " + clientMsg);
				// Logical processing based on request type
				JSONObject msg = new JSONObject(clientMsg);
				String type = msg.getString(Constants.TYPE);
				JSONObject data = (JSONObject) msg.get(Constants.DATA);
				// Different request type
				switch (type) {
				case Constants.LOGIN:
					this.setClientName(data.getString(Constants.USER_NAME));
					GameManager.getInstance().playerAdded(this);
					break;
				case Constants.START_GAME:
					GameManager.getInstance().start();
					break;
				case Constants.PLACE_CHARACTER:
					GameManager.getInstance().placeCharacter(data.getInt(Constants.PLACE_ROW),
							data.getInt(Constants.PLACE_COLUMN),
							data.getString(Constants.PLACE_VALUE), clientName);
					break;
				case Constants.PASS:
					GameManager.getInstance().pass();
					break;
				}
			}
			clientSocket.close();
			ClientManager.getInstance().clientDisconnected(this);
			System.out.println(
					Thread.currentThread().getName() + " - Client " + clientNum + " disconnected");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			// System.out.println( Thread.currentThread().getName() + " - Client " +
			// clientNum + " disconnected");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getClientName() {
		return clientName;
	}

	private void setClientName(String clientName) {
		// check name, keep unique
		this.clientName = clientName;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public int getClientCount() {
		return clientCount;
	}

	public void setClientCount(int clientCount) {
		this.clientCount = clientCount;
	}

	/**
	 * Write message to client from server
	 */
	public void write(String msg) {
		try {
			writer.write(msg + "\n");
			writer.flush();
			System.out.println(
					Thread.currentThread().getName() + " - Message sent to client " + clientNum);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}