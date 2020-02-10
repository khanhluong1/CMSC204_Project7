import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * ChatClientExec connect to server and send screen name.
 * Display chat form gui to handle sending message and receiving message.
 * 
 * @author Derek Luong
 *
 */
public class ChatClientExec implements ChatClientExecInterface {
	
	private ChatClient client;
	private TextArea outputTxt;
	private String screenName;
	
	public ChatClientExec(String screenName) {
		this.screenName = screenName;
	}

	@Override
	public void startClient() throws Exception {
		Socket clientSocket = new Socket("127.0.0.1", ChatServer.DEFAULT_CHAT_PORT);
       	
		BufferedReader clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		PrintWriter clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
        
        String serverMsg = clientIn.readLine();
        if (serverMsg.equals(ChatServer.CODE_SUBMIT_NAME)) {
        	clientOut.println(screenName);
        }
        
        serverMsg = clientIn.readLine();
        if (serverMsg != null && serverMsg.equals(ChatServer.CODE_NAME_DUPPLICATED)) {
        	clientSocket.close();
        	throw new Exception("Screen name has been existing.");
        }
        
        client = new ChatClient(screenName, clientSocket, this);
		
		Thread clientThread = new Thread(client);
		clientThread.start();
	}
	
	/**
	 * display received message to textarea output
	 * 
	 * @param serverMsg - received message
	 */
	public void printMsg(String serverMsg) {
		String output = outputTxt.getText();
        output = output + "\n" + serverMsg;
        outputTxt.setText(output);
	}
	
	/**
	 * display Chat Form Gui
	 * 
	 */
	public void showClientChatForm() {
		Stage stage = new Stage();
		VBox box = new VBox();
		box.setPadding(new Insets(10));
		box.setAlignment(Pos.CENTER);
		
		TextField textMsg = new TextField();
		textMsg.setPromptText("Enter a message");
		box.getChildren().add(textMsg);
		
		outputTxt = new TextArea();
		outputTxt.setPrefRowCount(15);
		outputTxt.setPrefColumnCount(100);
		outputTxt.setWrapText(true);
		outputTxt.setPrefWidth(150);
		outputTxt.setEditable(false);
		box.getChildren().add(outputTxt);
		
		textMsg.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				String msg = textMsg.getText();
				client.sendMsg(msg);
				textMsg.setText("");
            }
        });
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				client.disconnect();
			}
			
        });
		
        Scene scene = new Scene(box, 500, 300);
        stage.setScene(scene);
        stage.setTitle("Chat Room (" + screenName + ")");
        stage.show();
	}
}
