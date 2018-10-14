package client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;

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
			System.out.println(e.toString());
		} catch (IOException e) {
			System.out.println(e.toString());
		}
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
			} catch (SocketException e) {
				System.out.println(e.toString());
				return false;
			} catch (Exception e) {
				System.out.println(e.toString());
				return false;
			}
		}
		return false;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}