package server;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	private static int port;
	private static ServerSocket serverSocket;

	public static void main(String[] args) {
		// Bind a port
		if (args.length == 0) {
			port = 8888;
		} else if (args.length == 1) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.out.println("Unknown port: " + args[0]);
				System.exit(0);
			}
		} else {
			System.out.println("Unknown parameters");
			System.exit(0);
		}
		// Keep accepting the client socket, start a new thread for each
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server is running on port " + port + " for a connection");
			int count = 0;
			while (true) {
				Socket clientSocket = serverSocket.accept();
				count++;
				ClientConnection clientConnection = new ClientConnection(clientSocket, count);
				clientConnection.setName("Thread" + count);
				clientConnection.start();
				ClientManager.getInstance().clientConnected(clientConnection);
			}
		} catch (BindException e) {
			System.out.println("Port " + port + " already in use (Bind failed)");
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					System.out.println(e.toString());
				}
			}
		}
	}
}