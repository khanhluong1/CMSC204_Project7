import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Display Chat Room Controller GUI to interact with user.
 * 
 * @author Derek Luong
 *
 */
public class ChatRoomControllerGui extends Application {
	
	private ChatServerExec chatServerExec;

	@Override
	public void start(Stage stage) throws Exception {
		VBox mainPane = new VBox(10);
		
		Label chatRoomLabel = new Label("Chat Room Controller");
		mainPane.getChildren().add(chatRoomLabel);
		
		String instructionText = "1. Start the server.\n" + 
								"2. Start the client.\n" +
								"3. Enter a screen name in the client's GUI.\n" +
								"4. Start more clients.\n" +
								"5. Enter a message in a client's GUI.";
		Label instruction = new Label(instructionText);
		mainPane.getChildren().add(instruction);
		
		// button pane
		GridPane buttonPane = new GridPane();
		buttonPane.setHgap(5);
		buttonPane.setVgap(5);
		
		Button startServerBtn = new Button("Start the Server");
		startServerBtn.setTooltip(new Tooltip("Start server socket to catch client's message."));
		startServerBtn.setMnemonicParsing(true);
		buttonPane.add(startServerBtn, 1, 1);
		
		Button startClientBtn = new Button("Start each Client");
		startClientBtn.setTooltip(new Tooltip("Start server socket to catch client's message."));
		startClientBtn.setMnemonicParsing(true);
		buttonPane.add(startClientBtn, 2, 1);
		
		Button exitBtn = new Button("E_xit");
		exitBtn.setTooltip(new Tooltip("Exit"));
		exitBtn.setMnemonicParsing(true);
		buttonPane.add(exitBtn, 3, 1);
		
		mainPane.getChildren().add(buttonPane);
		
		startServerBtn.setOnAction(new EventHandler<ActionEvent>() {
    		@Override
            public void handle(ActionEvent e) {
    			if (chatServerExec != null) {
    				showAlertMessage(AlertType.ERROR, "Start Server Error", "Server is already started.");
    				return;
    			}
    			chatServerExec = new ChatServerExec(ChatServer.DEFAULT_CHAT_PORT);
    			chatServerExec.startServer(ChatServer.DEFAULT_CHAT_PORT);
    		}
        });
		
		startClientBtn.setOnAction(new EventHandler<ActionEvent>() {
    		@Override
            public void handle(ActionEvent e) {
    			if (chatServerExec == null) {
    				showAlertMessage(AlertType.ERROR, "Start Client Error", "Server has not been started.");
    				return;
    			}
    			showClientLoginForm();
    		}
        });
		
		exitBtn.setOnAction(new EventHandler<ActionEvent>() {
    		@Override
            public void handle(ActionEvent e) {
    			Platform.exit();
             	System.exit(0);
    		}
        });
		
		Scene scene = new Scene(mainPane, 500, 300);
		stage.setScene(scene);
		// Set stage title and show the stage.
		stage.setTitle("Chat Room Controller");
		stage.show();
	}
	
	public void showClientLoginForm() {
		Stage stage = new Stage();
		VBox box = new VBox();
		box.setPadding(new Insets(10));
		box.setAlignment(Pos.CENTER);
		
		Label label = new Label("Choose a screen name:");
		box.getChildren().add(label);
		
		TextField textUser = new TextField();
		textUser.setPromptText("Enter a screen name");
		box.getChildren().add(textUser);
		
		GridPane buttonPane = new GridPane();
		buttonPane.setHgap(5);
		buttonPane.setVgap(5);
		
		Button okBtn = new Button();
		okBtn.setText("OK");
		buttonPane.add(okBtn, 1, 1);
		
		Button cancelBtn = new Button();
		cancelBtn.setText("Cancel");
		buttonPane.add(cancelBtn, 2, 1);
		
		box.getChildren().add(buttonPane);
		
		okBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				String screenName = textUser.getText();
				ChatClientExec clientExec = new ChatClientExec(screenName);
				try {
					clientExec.startClient();
					clientExec.showClientChatForm();
				} catch (Exception ex) {
					showAlertMessage(AlertType.ERROR, "Start Client Error", ex.getMessage());
				}
                stage.close(); // return to main window
            }
        });
		
		cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
                stage.close(); // return to main window
            }
        });
        
        Scene scene = new Scene(box, 400, 150);
        stage.setScene(scene);
        stage.setTitle("Screen Name Selection");
        stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private void showAlertMessage(AlertType alertType, String title, String content) {
    	Alert alert = new Alert(alertType);
    	alert.setResizable(true);
    	alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
    }
}
