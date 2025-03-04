package application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server {
	
	@FXML
    private ScrollPane sp_main;
	
	@FXML
    private VBox vb_messages;

    @FXML
    private Pane activeUsersPane;
    
    @FXML
    private Button shutdown_button;
    
    @FXML
    private AnchorPane scenePane;

    private ServerSocket serverSocket;
    private static final int PORT = 5000;
    private final Set<ClientHandler> clients = new HashSet<>();
    

    // Called automatically when the FXML is loaded.
    public void initialize() {
        startServer();
    }

    // start the server in the background thread
    private void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                System.out.println("Server started on port " + PORT);
                Platform.runLater(() -> {
                    updateMessages("Server started on port " + PORT);
                    updateMessages("Waiting for clients to connect...");
                });
                
                while (!serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    synchronized (clients) {
                    	clients.add(clientHandler);
					}
                    new Thread(clientHandler).start();
                    updateActiveUsers();
                }
            } catch (IOException e) {
            	if (serverSocket != null && !serverSocket.isClosed()) {
            		updateMessages("Server error");
            	}
            }
        }).start();
    }
    
    // Broadcasts a chat message to all connected clients.
    public void broadcast(String message) {
    	synchronized (clients) {
			for(ClientHandler c : clients) {
				c.sendMessage(message);
			}
		}
    }

    // Called by ClientHandler to display messages on the UI.
    public void updateMessages(String message) {
        Platform.runLater(() -> {
            Label messageLabel = new Label(message);
            messageLabel.setStyle("-fx-color: #000;"+"-fx-background-color: rgb(233, 233, 235);" + "-fx-background-radius: 20px;" + "-fx-padding: 7px;");
            vb_messages.getChildren().add(messageLabel);
            // Auto-scroll to the bottom of the scroll pane.
            sp_main.setVvalue(1.0);
        });
    }

    // Refreshes the active users list in the UI.
    public void updateActiveUsers() {
        Platform.runLater(() -> {
            activeUsersPane.getChildren().clear();
            int index = 0;
            int spacing = 20;
            synchronized (clients) {
            	for (ClientHandler client : clients) {                	
                    Label userLabel = new Label(" -> " +client.getClientName());
                    userLabel.setLayoutX(0);
                    userLabel.setLayoutY(spacing * index);
                    activeUsersPane.getChildren().add(userLabel);
                    index++;
                }
			}
        });
    }

    // Removes a client from the active list when they disconnect.
    public void removeClient(ClientHandler client) {
        clients.remove(client);
        updateActiveUsers();
    }

    // Method to gracefully stop the server (triggered by the shutdown button).
    public void stopServer() {
        try {
        	synchronized (clients) {
                for (ClientHandler client : clients) {
                    client.closeConnection();
                }
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            Platform.runLater(() -> {
                vb_messages.getChildren().clear();
                activeUsersPane.getChildren().clear();
            });
            System.out.println("Server is shutdown.");
        } catch (IOException e) {
        	updateMessages("Error stopping server");
        }
    }
    
    public void shutdown(ActionEvent event) {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("shutdown");
    	alert.setHeaderText("You're about to logout");
    	alert.setContentText("are you sure you want to shutdown the server ?");
    	
    	if(alert.showAndWait().get() == ButtonType.OK) {
	    	Stage stage = (Stage) scenePane.getScene().getWindow();
	    	stage.close();
	    	stopServer();
    	}
    }
}
