package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ClientMain extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception {
		// window
		primaryStage.setTitle("Client");
				
		Image icon = new Image("client.png");
		primaryStage.getIcons().add(icon);
				
		primaryStage.setWidth(456);
		primaryStage.setHeight(500);
		primaryStage.setResizable(true);
				
		// grid
		Parent root = FXMLLoader.load(getClass().getResource("client.fxml"));
				
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
