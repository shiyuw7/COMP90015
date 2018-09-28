package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class ClientConnection {

	private static ClientConnection instance;
	private Socket client;
	private BufferedReader reader;
	private BufferedWriter writer;

	private String username;

	private ClientConnection() {
		username = "someone";
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
			this.reader = new BufferedReader(
					new InputStreamReader(client.getInputStream(), "UTF-8"));
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

	public BufferedReader getReader() {
		return reader;
	}

	public BufferedWriter getWriter() {
		return writer;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Write message to server from client
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