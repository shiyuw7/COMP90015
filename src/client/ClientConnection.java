package client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class ClientConnection {

	private static ClientConnection instance;
	private Socket client;
	private BufferedWriter writer;

	private String userName;

	private ClientConnection() {
	}

	public static ClientConnection getInstance() {
		if (instance == null) {
			instance = new ClientConnection();
		}
		return instance;
	}

	public void clientConnected(Socket client) {
		try {
			this.client = client;
			this.writer = new BufferedWriter(
					new OutputStreamWriter(client.getOutputStream(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Socket getClient() {
		return client;
	}

	public BufferedWriter getWriter() {
		return writer;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Write message to server
	 */
	public boolean sendMsg(String msg) {
		if (writer != null && client.isConnected()) {
			try {
				writer.write(msg + "\n");
				writer.flush();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
}