package client.gui;

import java.awt.Dimension;

import javax.swing.JPanel;

import client.common.Constants;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;

import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Color;
import javax.swing.JButton;
import java.awt.SystemColor;
import javax.swing.UIManager;

import client.ClientConnection;
import client.ServerListener;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class ConnectPanel extends JPanel {

	private JTextField serverIP;
	private JTextField serverPort;

	private String ip;
	private int port;

	public ConnectPanel() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		setPreferredSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));

		JLabel lblScrabbleGame = new JLabel("Scrabble Game");
		lblScrabbleGame.setForeground(Color.ORANGE);
		lblScrabbleGame.setFont(new Font("Calibri Light", Font.BOLD | Font.ITALIC, 50));
		add(lblScrabbleGame);

		JLabel lblServerIp = new JLabel("Server IP");
		JLabel lblNewLabel = new JLabel("Port Number");
		serverIP = new JTextField();
		serverIP.setColumns(10);
		serverPort = new JTextField();
		serverPort.setColumns(5);
		JButton connectButton = new JButton("Connect");
		connectButton.setBackground(UIManager.getColor("InternalFrame.inactiveTitleGradient"));
		connectButton.setForeground(SystemColor.textText);
		connectButton.setFont(new Font("Calibri Light", Font.PLAIN, 26));

		add(lblServerIp);
		add(serverIP);
		add(lblNewLabel);
		add(serverPort);
		add(connectButton);

		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectButton.setEnabled(false);
				ip = serverIP.getText();
				try {
					port = Integer.parseInt(serverPort.getText());
					Socket client;
					// Establish the connection
					client = new Socket();
					client.connect(new InetSocketAddress(ip, port), 10000);
					ClientConnection.getInstance().clientConnected(client);
					// Start new thread to listen message from server
					ServerListener serverListener = new ServerListener(client);
					serverListener.start();
					MainFrame.getInstance().connect();
				} catch (NumberFormatException numberFormatException) {
					showMessageDialog(MainFrame.getInstance(),
							"Unknown port: " + serverPort.getText());
					connectButton.setEnabled(true);
				} catch (UnknownHostException unknownHostException) {
					showMessageDialog(MainFrame.getInstance(),
							"Unknown port: " + "Unknown host: " + ip);
					connectButton.setEnabled(true);
				} catch (ConnectException connectException) {
					showMessageDialog(MainFrame.getInstance(),
							"Connection refused from " + ip + ":" + port);
					connectButton.setEnabled(true);
				} catch (SocketTimeoutException socketTimeoutException) {
					showMessageDialog(MainFrame.getInstance(), "Connect timed out");
					connectButton.setEnabled(true);
				} catch (IOException ioException) {
					showMessageDialog(MainFrame.getInstance(), "Connect lost");
					connectButton.setEnabled(true);
				}
			}
		});
	}
}
