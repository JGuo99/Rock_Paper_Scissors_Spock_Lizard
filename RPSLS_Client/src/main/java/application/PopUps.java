package application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

// Class : PopUps
// Description :
// 		Define a class which contains static methods to display warnings for 
//		the user depending on the unexpected behaviour they illustrate.
public class PopUps {
	
	// Static Method : alertWithOption
	// Description :
	// 		Allows to alert the user with yes/no options in the Popup Box, 
	//		along with the appropriate choice and message to be displayed.
	public static void alertWithOption(String message, String yes, String no, Choice choice) {
		// Define a new stage
		Stage stage = new Stage(); 
		
		// Initialize the stage
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("RPSLS");
		stage.setWidth(300);
		stage.setHeight(150);
		
		// GUI Elements of the Popup Box
		Label text = new Label(message);
		
		Button cancelButton = new Button(no);
		cancelButton.setMinSize(80, 20);
		cancelButton.setOnAction(e->stage.close());
		
		Button okButton = new Button(yes);
		okButton.setMinSize(80, 20);
		
		// EventHandlers to Buttons using Lambda expressions
		okButton.setOnAction(e->{
			choice.setChoice(true);
			stage.close();
		});
		
		cancelButton.setOnAction(e->{
			choice.setChoice(false);
			stage.close();
		});
		
		HBox buttonLayout = new HBox(20,okButton,cancelButton);
		buttonLayout.setAlignment(Pos.BOTTOM_CENTER);
		
		VBox mainLayout = new VBox(20, text, buttonLayout);
		mainLayout.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(mainLayout);
		
		stage.setScene(scene);
		stage.showAndWait();
	}//end alertWithOption()...
	
	// Static Method : alertWithOption
	// Description :
	// 		Allows to alert the user with only the warning message 
	//      to be displayed, along with an inbuilt ok button.
	public static void alert(String s) {
		// Define a new stage
		Stage stage = new Stage();
		
		// Initialize the stage
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("RPSLS");
		stage.setWidth(300);
		stage.setHeight(150);
		
		// GUI Elements of the Popup Box
		Label text = new Label(s);
		
		Button ok = new Button("Ok");
		ok.setMinSize(80, 20);
		
		// EventHandlers to Ok Button using Lambda expression
		ok.setOnAction(e->{
			stage.close();
		});
		

		HBox buttonLayout = new HBox(20,ok);
		buttonLayout.setAlignment(Pos.BOTTOM_CENTER);
		
		VBox mainLayout = new VBox(20, text, buttonLayout);
		mainLayout.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(mainLayout);
		
		stage.setScene(scene);
		stage.showAndWait();
	}//end alert()...
}//end Popups{}...
