// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract superclass
 * in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient {
	// Instance variables **********************************************

	/**
	 * The interface type variable. It allows the implementation of the display
	 * method in the client.
	 */
	ChatIF clientUI;

	/*
	 * Login ID for Client
	 */
	String loginID;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the chat client.
	 *
	 * @param host     The server to connect to.
	 * @param port     The port number to connect on.
	 * @param clientUI The interface type variable.
	 */

	public ChatClient(String loginID, String host, int port, ChatIF clientUI) throws IOException {
		super(host, port); // Call the superclass constructor
		this.loginID = loginID.replaceAll("#login", ""); 
		this.clientUI = clientUI;
		openConnection();
	}

	// Instance methods ************************************************

	/**
	 * This method handles all data that comes in from the server.
	 *
	 * @param msg The message from the server.
	 */
	public void handleMessageFromServer(Object msg) {
		clientUI.display(msg.toString());
	}

	/**
	 * This method handles all data coming from the UI
	 *
	 * @param message The message from the UI.
	 */
	public void handleMessageFromClientUI(String message) {
		try {

			if (message.startsWith("#")) {
				handleCommand(message);

			} else {
				sendToServer(message);

			}
		} catch (IOException e) {
			clientUI.display("Could not send message to server.  Terminating client.");
			quit();
		}
	}

	/**
	 * This method handles all command statements entered by the user
	 *
	 * @param command The specific command statement from the UI.
	 */
	public void handleCommand(String command) throws IOException {
		if (command.equals("#quit")) {
			quit();

		} else if (command.equals("#logoff")) {
			try {
				closeConnection();
			} catch (IOException e) {
				clientUI.display("Unable to close connection, please check network configuration again");
			}

		} else if (command.startsWith("#sethost")) {
			String newHost = command.replaceAll("#sethost ", "");

			setHost(newHost);
			clientUI.display("The host name has been set to " + newHost);

		} else if (command.startsWith("#setport")) {
			String newPort = command.replaceAll("#setport ", "");

			setPort(Integer.parseInt(newPort));
			clientUI.display("The port number has been set to " + newPort);

		} else if (command.startsWith("#login")) {
			if (!isConnected()) {

				try {
					openConnection();
				} catch (IOException e) {
					clientUI.display("Unable to connect to server, please try again");
				}
			} else {
				sendToServer("#login");
			}

		} else if (command.startsWith("#gethost")) {
			clientUI.display("The host name is " + getHost());

		} else if (command.startsWith("#getport")) {
			clientUI.display("The port number for this server is " + String.valueOf(getPort()));

		} else {
			clientUI.display("You have entered an invalid command, please try again");
		}
	}

	/**
	 * This method terminates the client.
	 */
	public void quit() {
		try {
			closeConnection();
		} catch (IOException e) {
		}
		System.exit(0);
	}

	/**
	 * Implements the hook method called after the connection has been closed. The
	 * default implementation does nothing. The method may be overridden by
	 * subclasses to perform special processing such as cleaning up and terminating,
	 * or attempting to reconnect.
	 */
	@Override
	protected void connectionClosed() {
		clientUI.display("Connection closed");
	}

	/**
	 * Implements the hook method in the OSCF framework called each time an
	 * exception is thrown by the client's thread that is waiting for messages from
	 * the server. The method may be overridden by subclasses.
	 * 
	 * @param exception the exception raised.
	 */
	@Override
	protected void connectionException(Exception exception) {
		clientUI.display("The server has shut down.");
		quit();
	}

	/**
	 * Implements the hook method in OSCF framework called after a connection has
	 * been established. The default implementation does nothing. It may be
	 * overridden by subclasses to do anything they wish.
	 */
	@Override
	protected void connectionEstablished() {
		try {
			sendToServer("#login" + this.loginID);
		} catch (IOException e) {
			clientUI.display("Unable to send loginID to the server, please try again");
		}
	}

}
