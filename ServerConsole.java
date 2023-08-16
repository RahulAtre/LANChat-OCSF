import java.io.*;
import java.util.Scanner;
import client.*;
import common.*;

/**
 * This class constructs the UI for a server. It implements the chat interface
 * in order to activate the display() method.
 *
 * @author Rahul Atre
 * @version November 2022
 */

public class ServerConsole implements ChatIF {

	// Class variables *************************************************

	/**
	 * The default port to listen on.
	 */
	final public static int DEFAULT_PORT = 5555;

	// Instance variables **********************************************

	/**
	 * The instance of the server that created this Server console.
	 */
	EchoServer server;

	/**
	 * Scanner to read from the console
	 */
	Scanner fromConsole;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the ClientConsole UI.
	 *
	 * @param host The host to connect to.
	 * @param port The port to connect on.
	 */
	public ServerConsole(int port) {
		server = new EchoServer(port, this);

		// Create scanner object to read from console
		fromConsole = new Scanner(System.in);
	}

	// Instance methods ************************************************

	/**
	 * This method overrides the method in the ChatIF interface. It displays a
	 * message onto the screen.
	 *
	 * @param message The string to be displayed.
	 */
	public void display(String message) {
		System.out.println("> " + message);
	}

	/**
	 * This method waits for input from the console. Once it is received, it sends
	 * it to the client's message handler.
	 */

	public void accept() {
		try {

			String message;

			while (true) {
				message = fromConsole.nextLine();
				server.handleMessageFromServerUI(message);
			}
		} catch (Exception ex) {
			this.display("Unexpected error while reading from console!");
		}
	}

	/**
	 * Calls EchoServer superclass method for the thread that waits for new clients.
	 * If the server is already in listening mode, this call has no effect.
	 *
	 * @exception IOException if an I/O error occurs when creating the server
	 *                        socket.
	 */
	private void listen() throws IOException {
		server.listen();
	}

	// Class methods ***************************************************

	/**
	 * This method is responsible for the creation of the server instance
	 *
	 * @param args[0] The port number to listen on. Defaults to 5555 if no argument
	 *                is entered.
	 */
	public static void main(String[] args) {
		int port = 0; // Port to listen on

		try {
			port = Integer.parseInt(args[0]); // Get port from command line
		} catch (Throwable t) {
			port = DEFAULT_PORT; // Set port to 5555
		}

		ServerConsole sv = new ServerConsole(port);

		try {
			sv.listen(); // Start listening for connections
			sv.accept(); // Start listening for server side messages
		} catch (Exception ex) {
			sv.display("ERROR - Could not listen for clients!");
		}
	}

}
//End of ServerConsole class