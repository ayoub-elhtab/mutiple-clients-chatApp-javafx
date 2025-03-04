package application;

import java.io.*;
import java.net.Socket;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class Client {
    
    @FXML
    private TextField tf_message;
    
    @FXML
    private Button button_send;
    
    @FXML
    private VBox vb_messages;
    
    @FXML
    private ScrollPane sp_main; 
    
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;
    private String clientName;
    
    @FXML
    public void initialize() {
    	Platform.runLater(() -> appendMessage("Enter your name for the group chat!"));    	    	
    }
    
    private void connectToServer() {         
         try {
             clientSocket = new Socket("localhost", 5000);
             appendMessage("Connected to the server.");
             in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
             
             // Send client name to the server as the first message.
             out.write(clientName);
             out.newLine();
             out.flush();
             
             // Start reading messages from the server.
             readMessage();
         } catch (IOException e) {
        	 Platform.runLater(() -> appendMessage("Error connecting to server: " + e.getMessage()));
         }
    }
    
    
    public void readMessage() {
        Thread readerThread = new Thread(() -> {
            String msgFromServer;
            try {
                while ((msgFromServer = in.readLine()) != null) {
                    final String message = msgFromServer;
                    Platform.runLater(() -> appendMessage(message));
                }
            } catch (IOException e) {
                Platform.runLater(() -> appendMessage("Connection closed : " + e.getMessage()));
                closeEverything();
            }
        });
        readerThread.setDaemon(true);
        readerThread.start();
    }
    
     
    @FXML
    public void send(ActionEvent event) {
        // If the client hasn't connected yet, use the text field as the name input.
        if (clientSocket == null) {
            String name = tf_message.getText();
            if (name == null || name.trim().isEmpty()) {
                appendMessage("Please enter a valid name.");
                return;
            }
            clientName = name;
            connectToServer();
            tf_message.clear();
        } else {
            // Otherwise, send the chat message.
            String message = tf_message.getText();
            if (message == null || message.trim().isEmpty()) return;
            try {
                out.write(clientName + " : " + message);
                out.newLine();
                out.flush();
                tf_message.clear();
            } catch (IOException e) {
            	Platform.runLater(() -> appendMessage("Error sending message" + e.getMessage()));
                closeEverything();
            }
        }
    }
    
    private void appendMessage(String message) {
        Label messageLabel = new Label(message);
        vb_messages.getChildren().add(messageLabel);
        messageLabel.setStyle("-fx-color: #000;"+"-fx-background-color: rgb(233, 233, 235);" + "-fx-background-radius: 20px;" + "-fx-padding: 7px;");
        // Auto-scroll to the bottom of the scroll pane.
        sp_main.setVvalue(1.0);
    }
    
    
    
    private void closeEverything() {
        try {
            if (in != null) { in.close(); }
            if (out != null) { out.close(); }
            if (clientSocket != null && !clientSocket.isClosed()) { clientSocket.close(); }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
