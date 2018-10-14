package client.gui;

import java.awt.Dimension;

import javax.swing.JPanel;

import common.Constants;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;

import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Color;
import javax.swing.JButton;

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
	private void initialize() {
		setPreferredSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));
		setLayout(null);
		setBackground(new Color(255, 255, 255));

		JLabel lblScrabbleGame = new JLabel("Scrabble Game");
		lblScrabbleGame.setBounds(88, 81, 597, 110);
		lblScrabbleGame.setForeground(new Color(112, 128, 144));
		lblScrabbleGame.setFont(new Font("Century Gothic", Font.BOLD | Font.ITALIC, 76));

		JLabel lblServerIp = new JLabel("Server IP");
		lblServerIp.setBounds(155, 235, 140, 45);
		lblServerIp.setForeground(Color.GRAY);
		lblServerIp.setFont(new Font("Century", Font.PLAIN, 29));

		serverIP = new JTextField();
		serverIP.setFont(new Font("Calibri Light", Font.PLAIN, 26));
		serverIP.setBounds(356, 236, 203, 45);
		serverIP.setColumns(10);

		JLabel lblNewLabel = new JLabel("Port Number");
		lblNewLabel.setBounds(144, 313, 182, 45);
		lblNewLabel.setForeground(Color.GRAY);
		lblNewLabel.setFont(new Font("Century", Font.PLAIN, 29));

		serverPort = new JTextField();
		serverPort.setFont(new Font("Calibri Light", Font.PLAIN, 26));
		serverPort.setBounds(356, 314, 203, 45);
		serverPort.setColumns(5);

		JButton connectButton = new JButton("Connect");
		connectButton.setBounds(356, 398, 182, 51);
		connectButton.setBackground(new Color(245, 255, 250));
		connectButton.setForeground(new Color(112, 128, 144));
		connectButton.setFont(new Font("Arial Black", Font.PLAIN, 23));

		add(lblScrabbleGame);
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