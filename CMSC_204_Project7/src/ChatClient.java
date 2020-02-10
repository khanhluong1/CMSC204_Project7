import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ChatClient will open create input to listen message from server and
 * output to send message to server.
 * Call parent thread ChatClientExec to update GUI
 * 
 * @author Derek Luong
 *
 */
public class ChatClient implements ChatClientInterface {
	
	private String name;
	private Socket server;
	private PrintWriter clientOut;
	private ChatClientExec clientExec;
	private boolean isFormOpen = false;
	
	public ChatClient(String name, Socket server, ChatClientExec clientExec) throws Exception {
		this.name = name;
		this.server = server;
		this.clientExec = clientExec;
		clientOut = new PrintWriter(server.getOutputStream(), true);
		isFormOpen = true;
	}
	
	@Override
	public void run() {
		try {
			BufferedReader clientIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
			
			while (isFormOpen) {
		        String serverMsg = clientIn.readLine();
		        serverMsg = serverMsg.substring(ChatServer.CODE_MESSAGE.length());
		        clientExec.printMsg(serverMsg);
		        
			}
			
			
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
	}
	
	/**
	 * send message to server
	 * 
	 * @param msg - message
	 */
	public void sendMsg(String msg) {
		clientOut.println(msg);
	}
	
	/**
	 * disconnect server and kill thread.
	 * 
	 */
	public void disconnect() {
		clientOut.println(ChatServer.CODE_QUIT);
		isFormOpen = false;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getServerPort() {
		return server.getPort();
	}

}
