// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.IOException;
import java.net.SocketException;

import common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract superclass in order
 * to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer {

	// Instance variables **********************************************

	/**
	 * The interface type variable. It allows the implementation of the display
	 * method in the client.
	 */
	ChatIF serverUI;

	private static final String LOGIN_KEY = "loginID"; // login key for retrieving client's ID

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the echo server.
	 *
	 * @param port The port number to connect on.
	 */
	public EchoServer(int port, ServerConsole serverUI) {
		super(port);
		this.serverUI = serverUI;
	}

	// Instance methods ************************************************

	/**
	 * This method terminates the server gracefully.
	 */
	public void quit() {
		try {
			close();
		} catch (IOException e) {
		}
		System.exit(0);
	}

	/**
	 * This method overrides the one in the superclass. Called when the server
	 * starts listening for connections.
	 */
	@Override
	protected void serverStarted() {
		serverUI.display("Server listening for connections on port " + getPort());
	}

	/**
	 * This method overrides the one in the superclass. Called when the server stops
	 * listening for connections.
	 */
	@Override
	protected void serverStopped() {
		serverUI.display("Server has stopped listening for connections.");
	}

	/**
	 * Implements the hook method in OSCF called when the server is closed. The
	 * default implementation does nothing. This method may be overridden by
	 * subclasses. When the server is closed while still listening, serverStopped()
	 * will also be called.
	 */
	@Override
	protected void serverClosed() {
		serverUI.display("The server has shut down.");
	}

	/**
	 * This method handles any messages received from the client.
	 *
	 * @param msg    The message received from the client.
	 * @param client The connection from which the message originated.
	 */
	@Override
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String message = (String) msg;

		//Display client message on the server
		serverUI.display("Message received: " + message + " from " + client.getInfo(LOGIN_KEY));

		// Store login information for client if message starts with #login
		if (message.startsWith("#login")) {
			if (client.getInfo(LOGIN_KEY) == null) {

				client.setInfo(LOGIN_KEY, message.replaceAll("#login ", ""));
				String loginCommunication = client.getInfo(LOGIN_KEY) + " has logged on.";

				serverUI.display(loginCommunication);
				this.sendToAllClients(loginCommunication);

			} else {
				try {
					serverUI.display("Error, client already logged in, terminating connection...");
					client.close();
				} catch (IOException e) {
					serverUI.display("Unable to close connection while client misentered #login");
				}
			}
		} else { //Send normal client message to all other clients
			this.sendToAllClients(client.getInfo(LOGIN_KEY) + ": " + message);
		}
	}

	/**
	 * Implements the hook method in OSCF framework called each time a new client
	 * connection is accepted. The default implementation does nothing.
	 * 
	 * @param client the connection connected to the client.
	 */
	@Override
	protected void clientConnected(ConnectionToClient client) {
		serverUI.display("A new client has connected to the server.");
	}

	/**
	 * Implements the hook method in OSCF framework called each time a client
	 * disconnects. The default implementation does nothing. The method may be
	 * overridden by subclasses but should remains synchronized.
	 *
	 * @param client the connection with the client.
	 */
	@Override
	synchronized protected void clientDisconnected(ConnectionToClient client) {
		serverUI.display("Client '" + client.getInfo(LOGIN_KEY) + "' has disconnected.");
	}

	/**
	 * Implements the hook method in OSCF framework called each time an exception is
	 * thrown in a ConnectionToClient thread. The method may be overridden by
	 * subclasses but should remains synchronized.
	 *
	 * @param client    the client that raised the exception.
	 * @param Throwable the exception thrown.
	 */
	@Override
	synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
		// Verifying that the client indeed terminated their connection to the server,
		// causing a SocketException to be thrown
		if (exception.getClass() == SocketException.class) {
			clientDisconnected(client);
		}
	}

	/**
	 * This method handles any messages received from the server UI.
	 *
	 * @param msg The message received from the server
	 */
	public void handleMessageFromServerUI(String message) {

		if (message.startsWith("#")) { // Special command
			handleCommand(message);

		} else { // Echo message typed on the server's console by the end-user to server console
					// & all clients
			String serverCommunication = "SERVER MESSAGE> " + message;

			serverUI.display(serverCommunication);
			this.sendToAllClients(serverCommunication);

		}
	}

	/**
	 * This method handles all command statements entered by the user
	 *
	 * @param command The specific command statement from the UI.
	 */
	private void handleCommand(String command) {
		if (command.equals("#quit")) {
			quit();

		} else if (command.equals("#stop")) {
			stopListening();

		} else if (command.equals("#close")) {
			try {
				close();
			} catch (IOException e) {
				serverUI.display("Unable to close connection to clients and stop server, please try again");
			}

		} else if (command.startsWith("#setport")) {
			String newPort = command.replaceAll("#setport ", "");

			setPort(Integer.parseInt(newPort));
			serverUI.display("The port number has been set to " + newPort);

		} else if (command.startsWith("#start")) {
			try {
				listen();

			} catch (IOException e) {
				serverUI.display("Unable to start listening for new clients, please try again");
			}

		} else if (command.startsWith("#getport")) {
			serverUI.display("The port number for this server is " + String.valueOf(getPort()));

		} else {
			serverUI.display("You have entered an invalid command, please try again");
		}

	}
}
//End of EchoServer class
