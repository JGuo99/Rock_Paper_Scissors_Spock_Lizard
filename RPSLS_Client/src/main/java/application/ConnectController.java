package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import clientLogic.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

// Class : ConnectController
// Description :
// 		Define a class implementing Initializable to control the 
//		first scene of the client program.
public class ConnectController implements Initializable{
	//Data Members
	GameInfo gInfo;
	public Client clientConnection;
	public Stage stage;
	ChallengeController controller;
	Parent newRoot;
	FXMLLoader loader;
	
	//IP Address string pattern for matching
	//Credit to mkyong at: https://www.mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression/
	private static final String IPADDRESS_PATTERN =
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	private Pattern ip_Pattern;
	
	@FXML
	private Node root;
	
	@FXML
	private TextField ipAddress_TextField;
	
	@FXML
	private TextField serverPort_TextField;
	
	@FXML
	private Button connect_Button;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}
	
	// Define the Constructor of the class
	public ConnectController(){
		ip_Pattern = Pattern.compile(IPADDRESS_PATTERN);
	}
	
	// Define a method to connect to the first scene of the
	// client side.
	public void connect() {
		try {
			//Check the IP-Address and PortNumber eneterd by the user. 
			String address = ipAddress_TextField.getText();
			if(!ip_Pattern.matcher(address).matches())
				throw new WrongAddressFormatException("Address is not in a valid format");
			
			int portNumber = Integer.parseInt(serverPort_TextField.getText());

// UNCOMMENT THE DEFAULTS
			// String address= "127.0.0.1";
			// int portNumber = 5555; 
			
			// Instantiate a new client
			clientConnection = new Client( data -> {				
					Platform.runLater(()->{		
						System.out.println(data);
					});
			}, address, portNumber);
            
			// Load the Challenge scene and handle exceptions using try-catch block
			try {
				stage = (Stage) root.getScene().getWindow();
				loader = new FXMLLoader(getClass().getResource("/FXML/ChallengeScene.fxml"));
				newRoot = loader.load();
				controller = loader.getController();
				controller.setClientConnection(clientConnection);	
				stage.setScene(new Scene(newRoot));

			} catch(Exception e) {
				PopUps.alert("Could not load challenge scene.");
				e.printStackTrace();
			}//end inner catch
			
			clientConnection.start(); // Start the Client Thread	

	 } catch(WrongAddressFormatException e) {
    			PopUps.alert(e.getMessage());
		} catch(NumberFormatException e) {
			PopUps.alert("Server port is not valid.");
		} catch(Exception e) {
			e.printStackTrace();
			PopUps.alert("Could not connect to server.");
		}//end outer catch 
			
	}//end connect()...
	
	public void quit() {
		Platform.exit();
		System.exit(0);
	} 
	
	//@SuppressWarnings("serial")
	private class WrongAddressFormatException extends Exception{
		WrongAddressFormatException(String message){
			super(message);
		}
	}//end WrongAddressFormatException{}...
	
}//end ConnectController{}...
