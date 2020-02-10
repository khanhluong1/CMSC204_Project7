
/**
 * ChatServerExec handle request to start ChatServer in a given port
 * 
 * @author Derek Luong
 *
 */
public class ChatServerExec implements ChatServerExecInterface {
	private ChatServer server;
	
	public ChatServerExec(int port) {
		server = new ChatServer(port);
	}

	@Override
	public void startServer(int port) {
		Thread serverThread = new Thread(server);
		serverThread.start();
	}

}
