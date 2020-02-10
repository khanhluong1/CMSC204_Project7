import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * ChatServer runs in a separate thread.
 * Open socket and wait for client connection.
 * When a client connects, ask for screen name.
 * 	Check screen name duplicated. 
 * 		If yes, return NAMEDUPPLICATED code. 
 * 		If not, return NAMEACCEPTED code and start new thread to listen client message
 * When receive MESSAGE, populate message to all clients
 * when receive QUIT, disconnect client.
 * 
 * @author Derek Luong
 *
 */
public class ChatServer implements Runnable {
	
	public static final String CODE_SUBMIT_NAME = "SUBMITNAME";
	public static final String CODE_NAME_ACCEPTED = "NAMEACCEPTED";
	public static final String CODE_NAME_DUPPLICATED = "NAMEDUPPLICATED";
	public static final String CODE_MESSAGE = "MESSAGE";
	public static final String CODE_QUIT = "QUIT";
	public static final int DEFAULT_CHAT_PORT = 6000;

	private int chatRoomPort;
	private ArrayList<String> clientNames;
	private ArrayList<PrintWriter> clientOutputs;
	
	public ChatServer() {
		chatRoomPort = DEFAULT_CHAT_PORT;
	}
	
	public ChatServer(int port) {
		chatRoomPort = port;
	}
	
	/**
	 * populate message to all clients
	 * 
	 * @param msg - message to be populated
	 */
	public void echoMessage(String msg) {
		msg = CODE_MESSAGE + " " + msg;
		for (PrintWriter out : clientOutputs) {
			out.println(msg);
		}
	}
	
	/**
	 * remove client from list
	 * 
	 * @param clientName - removing client name
	 * @param clientOutput - removing client output
	 */
	public void removeClient(String clientName, PrintWriter clientOutput) {
		clientNames.remove(clientName);
		clientOutputs.remove(clientOutput);
	}
	
	@Override
	public void run() {
		try {
			ServerSocket server = new ServerSocket(chatRoomPort);
			clientNames = new ArrayList<String>();
			clientOutputs = new ArrayList<PrintWriter>();
			System.out.println("Waiting for clients to connect . . .");
			
			while (true) {
		        Socket client = server.accept();
		        System.out.println("Client connected.");

		        BufferedReader clientIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
		        PrintWriter clientOut = new PrintWriter(client.getOutputStream(), true);
		        
		        clientOut.println(CODE_SUBMIT_NAME);
		        String submitName = clientIn.readLine();
		        if (clientNames.contains(submitName)) {
		        	clientOut.println(CODE_NAME_DUPPLICATED);
		        	continue;
		        }
		        clientOut.println(CODE_NAME_ACCEPTED);
		        System.out.println(submitName + " is accepted.");
		        clientNames.add(submitName);
		        clientOutputs.add(clientOut);
		        
		        ChatServerListener serverListener = new ChatServerListener(this, client, submitName, clientOut);
		        Thread serverListenerThread = new Thread(serverListener);
		        serverListenerThread.start();
		        
			}
			
			
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

}
