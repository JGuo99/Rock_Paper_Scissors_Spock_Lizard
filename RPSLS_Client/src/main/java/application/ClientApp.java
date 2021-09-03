package application;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

// Class : ClientApp
// Description :
// 		Define a class which extends Application to enable GUI features
//      and contains the inherited method of 'Start' conatining the code
//		to set up the first scene of the client program.
public class ClientApp extends Application {
    
	// Static Main Method of the program
	public static void main(String[] args) {
		launch(args); // Launch the game
	}

	// Define the inherited Start Method to set up the intital
	// stage of the game.
	@Override
	public void start(Stage primaryStage) throws Exception {
		 // Try-Catch to handle exceptions
		 try {
	            // Read file fxml and draw interface.
	            Parent root = FXMLLoader.load(getClass().getResource("/FXML/ConnectScene.fxml"));
	         
				// Initialize the primary stage
				primaryStage.setTitle("RPSLS");
	            Scene s1 = new Scene(root);
	            primaryStage.setScene(s1);
	            primaryStage.show();
	         
	        } catch(Exception e) {
	            e.printStackTrace();
	            System.exit(1);
	        }
		 
		 // Allows the proper closure of primary stage
		 primaryStage.setOnCloseRequest(e->{
				Platform.exit();
				System.exit(0);
		 });//end lambda expression

	}//end start()...

}//end ClientApp{}...
