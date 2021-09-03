package application;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import application.GameInfo.Hand;
import application.GameInfo.MessageType;
import application.GameInfo.Player;
import application.GameInfo.PlayerStatus;
import application.MatchInfo.MatchStatus;
import clientLogic.Client;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

// Class : ChallengeController
// Description :
// 		Define a class which takes care of the challenge scene
// 		on the client side and allows its effecient mechanism.
public class ChallengeController implements Initializable {
	public Client clientConnection; // Client object
	//Observalble Lists to be used for selection of a challengee
	ObservableList<Information> playerOList; 
	ObservableList<Information> selection;
	
	//Objects to load game scene
	Parent newRoot;
	FXMLLoader loader;
	Stage stage;
	GameMainController controller;

    //FXML elements
	@FXML
	private Node root;
	
    @FXML
    private TableColumn<Information, String> column1; //Name of Player

    @FXML
    private TableColumn<Information, String> column2; //Availability of Player
	
    @FXML
    private TableView<Information> client_TableView; //Stores the player statuses together

    @FXML
    private ListView<String> serverMessages_ListView;// Displays the server messages

    @FXML
    private Button challenge_Button;//Allows to challenge a player
    
	// Define the method from interface Initializable to initialize the setup for 
	// the challenge scene.
	//@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		playerOList = FXCollections.observableArrayList();
		
		column1.setCellValueFactory(new PropertyValueFactory<Information,String>("name"));
		column2.setCellValueFactory(new PropertyValueFactory<Information,String>("data"));
	
	}//end initialize()...
	
//-------------------------------------------------------------------------------------------------------

	//---------------------------------------------------------------------------------------------------
	// METHODS not attached to GUI elements
	
	// Method : setClientConnection
	// Description :
	//		 Sets the callback method for the Server class to update this Scene.
	//@SuppressWarnings("unchecked")
	public void setClientConnection(Client client) {
		
		clientConnection = client;
		//Server Messages
		clientConnection.setUpdateMessagesCallback(data ->{
			Platform.runLater(()->{
				String message = (String) data;
				serverMessages_ListView.getItems().add(message);
			});
		}); //End of setUpdateMessageCallback

		//Tableview mechanisms using Platform.runLater()..
		clientConnection.setCallback(data ->{ // takes in a GameInfo
			Platform.runLater(()->{				
				GameInfo gInfo = (GameInfo) data;
				
				// Switch-case to parse the message 
				switch(gInfo.message) { // challenge scene gui will change depending on the GameInfo msg type
					case NOTIFY:           //Update tableView for both 
					case REFILL_REQUEST:   //these cases.
						playerOList.clear();
						for(Player p : gInfo.players) {							
							if (p.playerID != clientConnection.clientID)
								playerOList.add(new Information(p.name,p.status.toString(),p.playerID));
						}
						client_TableView.setItems(playerOList);
						break;
					case CHALLENGE: // if the message is to challenge a player
						Choice choice = new Choice();
						PopUps.alertWithOption(gInfo.getPlayer(gInfo.challenger).name + " has challenged you!", "Accept", "Decline", choice);
						try {
							if(choice.getChoice()) {
							   stage = (Stage) root.getScene().getWindow();
							   loader = new FXMLLoader(getClass().getResource("/FXML/GameMainScene.fxml"));
							   newRoot = loader.load();
							   controller = loader.getController();
							   controller.setClientConnection(clientConnection);			
							   stage.setScene(new Scene(newRoot));
							  clientConnection.localGInfo.message = MessageType.ACCEPT_CHALL;													
							} else {
							  clientConnection.localGInfo.message = MessageType.DECLINE_CHALL;							
							}
							clientConnection.send();										
						} catch(Exception e) {
								PopUps.alert("Could not load game scene.");
								e.printStackTrace();
						}
						break;
						
					case ACCEPT_CHALL: // if you accept the challenge of a player
						try {
							stage = (Stage) root.getScene().getWindow();
							loader = new FXMLLoader(getClass().getResource("/FXML/GameMainScene.fxml"));
					        newRoot = loader.load();
							controller = loader.getController();
							controller.setClientConnection(clientConnection);	
							stage.setScene(new Scene(newRoot));
							
						} catch(Exception e) {
							PopUps.alert("Could not load game scene.");
							e.printStackTrace();
						}
						break;

					case DECLINE_CHALL: // if you decline the challenge of a player
						PopUps.alert("Your challenge has been declined.");
						challenge_Button.setDisable(false);
						break;						
					
					default: // Default case triggered
						break;
				}//End switch-case
				
			});//End of Platform.runLater()...
		}); //End of setCallback()...

	}//end setClientConnection()...

	//---------------------------------------------------------------------------------------------------
	// METHODS not attached to GUI elements
	
	// Method : challengeAction
	// Description :
	//		 Define the action to be taken when the 'Challenge Button' on the
	//		 challenge scene is pressed.
	public void challengeAction() {
	
		if (client_TableView.getSelectionModel().getSelectedItem() != null){
			Player selectedPlayer = clientConnection.localGInfo.getPlayer(client_TableView.getSelectionModel().getSelectedItem().playerID);
			try{
				if(selectedPlayer.status != PlayerStatus.AVAILABLE) {
					PopUps.alert("Selected player isn't available");
				} 
				else { // If challenged Player is available then send the details to the server
					clientConnection.localGInfo.challengee = selectedPlayer.playerID;
					clientConnection.localGInfo.challenger = clientConnection.clientID;
					clientConnection.localGInfo.message = GameInfo.MessageType.CHALLENGE;
					clientConnection.send();
					challenge_Button.setDisable(true);
				}
			}catch (Exception e){
			}
		}
		else {
            PopUps.alert("Please select a player to challenge");
		}//end else
	}//end challengeAction()...
	
	public void quit() {
		Platform.exit();
		System.exit(0);
	}
	
//-------------------------------------------------------------------------------------------------------
	// HELPER CLASS : information
	// Description :
	//				Information inner class which feeds the Player-Availabilty Table View on the Challenge Scene. 
	public class Information{
		//Data Members
		private String name;
		private String data;
		public int playerID;
		
		//Constructor
		Information(String name, String data, int playerID){
			this.name = name;
			this.data = data;
			this.playerID = playerID;
		}
		//Setters
		public void setName(String s) { this.name = s; }
		public void setData(String s) { data = s; }
		//Getters
		public String getName() { return name; }
		public String getData() { return data; }
	}//end inner class Information{}...
}//end ChallengeController{}...
