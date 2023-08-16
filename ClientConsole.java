// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import client.*;
import common.*;

/**
 * This class constructs the UI for a chat client. It implements the chat
 * interface in order to activate the display() method. Warning: Some of the
 * code here is cloned in ServerConsole
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @version September 2020
 */
public class ClientConsole implements ChatIF {
	// Class variables *************************************************

	/**
	 * The default port to connect on.
	 */
	final public static int DEFAULT_PORT = 5555;

	// Instance variables **********************************************

	/**
	 * The instance of the client that created this ConsoleChat.
	 */
	ChatClient client;

	/**
	 * Scanner to read from the console
	 */
	Scanner fromConsole;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the ClientConsole UI.
	 *
	 * @param loginID The login userID of the client
	 * @param host The host to connect to.
	 * @param port The port to connect on.
	 */
	public ClientConsole(String loginID, String host, int port) {
		try {
			client = new ChatClient(loginID, host, port, this);

		} catch (IOException exception) {
			this.display("Error: Can't setup connection!" + " Terminating client.");
			System.exit(1);
		}

		// Create scanner object to read from console
		fromConsole = new Scanner(System.in);
	}

	// Instance methods ************************************************

	/**
	 * This method waits for input from the console. Once it is received, it sends
	 * it to the client's message handler.
	 */
	public void accept() {
		try {

			String message;

			while (true) {
				message = fromConsole.nextLine();
				client.handleMessageFromClientUI(message);
			}
		} catch (Exception ex) {
			this.display("Unexpected error while reading from console!");
		}
	}

	/**
	 * This method overrides the method in the ChatIF interface. It displays a
	 * message onto the screen.
	 *
	 * @param message The string to be displayed.
	 */
	public void display(String message) {
		System.out.println("> " + message);
	}

	// Class methods ***************************************************

	/**
	 * This method is responsible for the creation of the Client UI.
	 *
	 * @param args[0] The host to connect to.
	 * @throws IOException 
	 */
	public static void main(String[] args) { 
		
		//Example login: #login client1 localhost 5555
				
		if(args.length == 0 || !args[0].startsWith("#login")) {
			System.out.println("ERROR - No login ID specified. Connection aborted");
			System.exit(1);
		}
		
		//Variables that will store the argument parameters entered by the user/client
		String loginID = "";
		String host = "";
		int port = 0;

		try { //Try to store argument parameters in respective variables
			loginID = args[0]+" "+args[1];
			host = args[2];
			port = Integer.parseInt(args[3]);
			
		} catch (ArrayIndexOutOfBoundsException e) { //Catch exception if user missed one of the arguments
			host = "localhost";
			port = DEFAULT_PORT;
			
		} catch (NumberFormatException e) { //Catch exception if user entered incorrect number format
			port = DEFAULT_PORT; 
		
		} catch (Exception e) {
			System.out.println("ERROR - Client could not parse arguments from console, terminating client"); //If the user enters invalid arguments altogether
			System.exit(1);
		}

		ClientConsole chat = new ClientConsole(loginID, host, port); //New Console declaration
		chat.accept(); // Wait for console data
	}
}
//End of ConsoleChat class
