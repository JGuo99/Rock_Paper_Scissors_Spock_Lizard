package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import serverLogic.Server;

//Class: ServerMainController
//Description:
//	-Controls server start-up scene using corresponding fxml file
public class ServerStartController implements Initializable{
	public Server serverConnection;
	private FXMLLoader loader;
	private Parent newRoot;
	private ServerMainController mController;
	
	private Stage stage;
		
	@FXML
	private Button startServerButton;
	
	@FXML
	private Node root;
	
	@FXML
	private TextField serverPort_TextField;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}
	
	//	Function: startServer
	//	Description:
	//		-Setup scene. Take input, verify, start server if input is valid
	public void startServer(){
			try {
				int portNumber = Integer.parseInt(serverPort_TextField.getText());
				
				stage = (Stage) root.getScene().getWindow();
				loader = new FXMLLoader(getClass().getResource("/FXML/ServerMain.fxml"));
	            newRoot = loader.load();        
	            mController = loader.getController();
	            mController.setPortText(serverPort_TextField.getText());
	            
	            try {    
		            serverConnection = new Server(d-> {
		    			Platform.runLater(()->{
			    			PopUps.alert(d.toString());
		    			});
		    		}, portNumber);
		            mController.setServerConnection(serverConnection);
	            } catch(Exception e) {
	            	PopUps.alert("Failed to open port: " + portNumber);
	            	e.printStackTrace();
	            }
	            
	            stage.setScene(new Scene(newRoot));		
			} catch(Exception e) {
				PopUps.alert("Please write a number for the port.");
				e.printStackTrace();
			}
			
	}
	
}
