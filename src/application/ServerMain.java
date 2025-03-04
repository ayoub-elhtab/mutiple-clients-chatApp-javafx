package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;


public class ServerMain extends Application {
	@Override
	public void start(Stage primaryStage) throws IOException {
		// window
		primaryStage.setTitle("Server");
		
		Image icon = new Image("server.png");
		primaryStage.getIcons().add(icon);
		
		primaryStage.setWidth(670);
		primaryStage.setHeight(500);
		primaryStage.setResizable(false);
		
		// grid
		Parent root = FXMLLoader.load(getClass().getResource("server.fxml"));
		
		// scene
		Scene scene = new Scene(root);
		
		// CSS
		scene.getStylesheets().add(getClass().getResource("css/application.css").toExternalForm());
		
		// finish window
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
