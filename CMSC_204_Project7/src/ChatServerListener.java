import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ChatServerListener listen a specific client connection.
 * When receiving a message, call parent thread ChatServer to execute message.
 * 
 * @author Derek Luong
 *
 */
public class ChatServerListener implements Runnable {
	
	private Socket client;
	private ChatServer chatServer;
	private String clientName;
	private PrintWriter clientOutput;
	
	public ChatServerListener() {}
	
	public ChatServerListener(ChatServer chatServer, Socket client, String clientName, PrintWriter clientOutput) {
		this.chatServer = chatServer;
		this.client = client;
		this.clientName = clientName;
		this.clientOutput = clientOutput;
	}

	@Override
	public void run() {
		try {
			BufferedReader clientIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
			
			while (true) {
		        String serverMsg = clientIn.readLine();
		        if (serverMsg.equals(ChatServer.CODE_QUIT)) {
		        	System.out.println(clientName + " QUIT.");
		        	chatServer.removeClient(clientName, clientOutput);
		        	break;
		        }
		        
		        System.out.println(clientName + ": " + serverMsg);
	        	chatServer.echoMessage(clientName + ": " + serverMsg);
		        
			}
			
			
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

}
