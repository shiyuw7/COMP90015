package client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import client.config.Config;
import client.config.ConfigLoader;
import client.gui.MainFrame;

public class Client {

	private static int port;
	private static String ip;

	public static void main(String[] args) {
		Socket client;
		if (args.length == 0) {
			Config config = ConfigLoader.load();
			ip = config.getServerAddress();
			port = config.getServerPort();
		} else if (args.length == 2) {
			ip = args[0];
			try {
				port = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.out.println("Unknown port: " + args[1]);
				System.exit(0);
			}
		} else {
			System.out.println("Unknown parameters");
			System.exit(0);
		}
		try {
			// Establish the connection
			client = new Socket();
			client.connect(new InetSocketAddress(ip, port), 10000);
			ClientConnection.getInstance().clientConnected(client);
			// Start new thread to listen message from server
			ServerListener serverListener = new ServerListener(client);
			serverListener.start();
			// Open UI
			MainFrame.getInstance().setVisible(true);
		} catch (UnknownHostException e) {
			System.out.println("Unknown host: " + ip);
			System.exit(0);
		} catch (ConnectException e) {
			System.out.println("Connection refused from " + ip + ":" + port);
			System.exit(0);
		} catch (SocketTimeoutException e) {
			System.out.println("Connect timed out");
			System.exit(0);
		} catch (IOException e) {
			System.out.println(e.toString());
			System.exit(0);
		}
	}
}