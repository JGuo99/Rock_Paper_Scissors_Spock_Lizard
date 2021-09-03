package application;

import java.util.HashMap;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.GameInfo.Hand;
import application.GameInfo.MessageType;
import application.GameInfo.Player;
import application.GameInfo.PlayerStatus;
import application.MatchInfo.MatchStatus;
import clientLogic.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

// Class : GameMainController
// Description :
// 		Define a class implementing Initializable to control the 
//		Game's Main scene of the client program.
public class GameMainController implements Initializable {
	// Data Members
	MatchInfo mInfo;
	GameInfo.Hand handSelected;
	boolean somethingIsSelected;
	public Client clientConnection;
	
	// GUI elements
	private Stage stage;
	ChallengeController controller;
	Parent newRoot;
	FXMLLoader loader;
	
	// Hashmaps for allowing clickable imageViews
	HashMap<GameInfo.Hand, SetImage> setDefaultImages;
	HashMap<GameInfo.Hand, String> imageURLs;
	
	@FXML
	private Node root;
	
	@FXML
	private ImageView rock_ImageView;
	
	@FXML
	private ImageView paper_ImageView;
	
	@FXML
	private ImageView scissors_ImageView;
	
	@FXML
	private ImageView lizard_ImageView;
	
	@FXML
	private ImageView spock_ImageView;
	
	@FXML
	private Button confirm_Button;
	
	@FXML
	private ImageView playerPick_ImageView;

	@FXML
	private ImageView opponentPick_ImageView;
	
    @FXML
    private Label matchResult_Label;

    @FXML
    private Button backToChallengeScene_Button;
	
	// Method : initialize
	// Description :
	//		 Initializes and sets up the Game Scene of the client program.
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Initializing the data-members
		handSelected = GameInfo.Hand.NONE;
		somethingIsSelected = false;
		
		//Adapter Pattern implementation
		setDefaultImages = new HashMap<GameInfo.Hand, SetImage>();
		setDefaultImages.put(GameInfo.Hand.ROCK,    ()->mouseExitRock());
		setDefaultImages.put(GameInfo.Hand.PAPER,   ()->mouseExitPaper());
		setDefaultImages.put(GameInfo.Hand.SCISSORS,()->mouseExitScissors());
		setDefaultImages.put(GameInfo.Hand.LIZARD,  ()->mouseExitLizard());
		setDefaultImages.put(GameInfo.Hand.SPOCK,   ()->mouseExitSpock());
		
		imageURLs = new HashMap<GameInfo.Hand, String>();
		imageURLs.put(Hand.ROCK, "PlayImages/Rock.png");
		imageURLs.put(Hand.PAPER, "PlayImages/Paper.png");
		imageURLs.put(Hand.SCISSORS, "PlayImages/Scissors.png");
		imageURLs.put(Hand.LIZARD, "PlayImages/Lizard.png");
		imageURLs.put(Hand.SPOCK, "PlayImages/Spock.png");
	
		playerPick_ImageView.setVisible(false);
		backToChallengeScene_Button.setVisible(false);
	}//end intialize()...


	// Method : confirmSelection
	// Description :
	//		 Checks if one of the options of rock/paper/scissors/lizard/spock
	//		 is indeed selected and pops a message if none is selected.
	public void confirmSelection() {
		clientConnection.localGInfo.getPlayer(clientConnection.clientID).hand = handSelected;
		clientConnection.localGInfo.message = MessageType.PLAY;
		
		if(handSelected == Hand.NONE){ //Check if hand is selected
			PopUps.alert("Please Select a Gesture above!");
		}
		else { // If a hand is selected
			clientConnection.send();
			confirm_Button.setDisable(true);
			playerPick_ImageView.setImage(new Image(imageURLs.get(handSelected)));
			playerPick_ImageView.setVisible(true);
		}//end else
	}//end confirmSelection()...
	

	// Method : setClientConnection
	// Description :
	//		 Allows to set the client connection and the game scene for each client.
	public void setClientConnection(Client client) {
		clientConnection = client; // Set the client

		// Set callback()... 
		clientConnection.setGameCallback(data->{
			Platform.runLater(()->{
				GameInfo gInfo = (GameInfo) data;
				

				if(gInfo.message == MessageType.ERROR) { // if the opponent left amid a match
					//pop up to notify client
					PopUps.alert("Your Opponent Left the Match!");
					backToChallengeScene_Button.setVisible(true);
				}

				if(gInfo.message != MessageType.END_GAME) { // Error checking
					System.out.print("gameCallback received not end game message type");
					return;
				}
                
				// Variable Assignments
				Player player = gInfo.getPlayer(clientConnection.clientID);
				MatchInfo match = gInfo.getMatch(player.matchID);
				Player opponent;
				
				// Set up the opponent player
				if(match.player1.playerID == clientConnection.clientID) {
					opponent = match.player2;
				} else {
					opponent = match.player1;
				}
				// Set up the opponentPick ImageView and related GUI technicalities
				matchResult_Label.setText(roundResultToString(match.matchStatus,(player.playerID.equals(match.player1.playerID))));
				opponentPick_ImageView.setImage(new Image(imageURLs.get(opponent.hand)));
				backToChallengeScene_Button.setVisible(true);
				
				
			});//end Platform.runLater()...
		});//end callback()...
	}//end setClientConnection()...
	

	// Method : roundResultToString
	// Description :
	//		 Allows to set result of the game in the form of a string
	public String roundResultToString(MatchStatus s, boolean playerIsP1) { 
		// Initialization
		String result =""; 
		
		// Switch-case to parse result
		switch(s) {
		case DRAW:
			result = "Draw!";
			break;
		case PLAYER1WON:
			result = (playerIsP1) ? "You won!" : "You lost.";
			break;
		case PLAYER2WON:
			result = (playerIsP1) ? "You lost!" : "You won!";
			break;
		default:
			result = "error";
			break;
		}
		return result;
	}//end roundResultToString()...
	

	// Method : backToChallengeScene
	// Description :
	//		 Allows the let the user go back to the challenge screen to play 
	//		 the game again.
	public void backToChallengeScene() throws IOException {
		// Get Challenge scene
		stage = (Stage) root.getScene().getWindow();
		loader = new FXMLLoader(getClass().getResource("/FXML/ChallengeScene.fxml"));
		newRoot = loader.load();
		controller = loader.getController();
		controller.setClientConnection(clientConnection);	
		stage.setScene(new Scene(newRoot));
		
		// Send refill request to the server
		clientConnection.localGInfo.message = MessageType.REFILL_REQUEST;
		clientConnection.send();
	}//end backToChallengeScene()...

	// FUNCTIONS for each of the 5 options
	//1. Rock
	public void mouseEnteredRock() {
		rock_ImageView.setImage(new Image("PlayImages/RockSelected.png"));
	}
	
	public void mouseClickRock() {
		rock_ImageView.setImage(new Image("PlayImages/RockSelected.png"));
		select(GameInfo.Hand.ROCK);
	}
	
	public void mouseExitRock() {
		if(handSelected != GameInfo.Hand.ROCK)
			rock_ImageView.setImage(new Image("PlayImages/Rock.png"));
	}
	
	//2. Paper
	public void mouseEnteredPaper() {
		paper_ImageView.setImage(new Image("PlayImages/PaperSelected.png"));
	}
	
	public void mouseClickPaper() {
		paper_ImageView.setImage(new Image("PlayImages/PaperSelected.png"));
		select(GameInfo.Hand.PAPER);
	}
	
	public void mouseExitPaper() {
		if(handSelected != GameInfo.Hand.PAPER)
			paper_ImageView.setImage(new Image("PlayImages/Paper.png"));
	}

	//3. Scissors
	public void mouseEnteredScissors() {
		scissors_ImageView.setImage(new Image("PlayImages/ScissorsSelected.png"));
	}
	
	public void mouseClickScissors() {
		scissors_ImageView.setImage(new Image("PlayImages/ScissorsSelected.png"));
		select(GameInfo.Hand.SCISSORS);
	}
	
	public void mouseExitScissors() {
		if(handSelected != GameInfo.Hand.SCISSORS)
			scissors_ImageView.setImage(new Image("PlayImages/Scissors.png"));
	}
	
	//4. Lizard
	public void mouseEnteredLizard() {
		lizard_ImageView.setImage(new Image("PlayImages/LizardSelected.png"));
	}
	
	public void mouseClickLizard() {
		lizard_ImageView.setImage(new Image("PlayImages/LizardSelected.png"));
		select(GameInfo.Hand.LIZARD);
	}
	
	public void mouseExitLizard() {
		if(handSelected != GameInfo.Hand.LIZARD)
			lizard_ImageView.setImage(new Image("PlayImages/Lizard.png"));
	}
	
	//5. Spock
	public void mouseEnteredSpock() {
		spock_ImageView.setImage(new Image("PlayImages/SpockSelected.png"));
	}
	
	public void mouseClickSpock() {
		spock_ImageView.setImage(new Image("PlayImages/SpockSelected.png"));
		select(GameInfo.Hand.SPOCK);
	}
	
	public void mouseExitSpock() {
		if(handSelected != GameInfo.Hand.SPOCK)
			spock_ImageView.setImage(new Image("PlayImages/Spock.png"));
	}
	
	// Define an interface to set the image of each option
	interface SetImage{
		abstract void set();
	}//end SetImage{}...
	
	// Define a function to be used to quit the game.
	public void quit() {
		Platform.exit();
		System.exit(0);
	}//end quit()...
	
	// Define a method to select a hand and identify that 
	// an option has been selected
	public void select(GameInfo.Hand hand) {
		GameInfo.Hand oldHand = handSelected;
		handSelected = hand;
		if(oldHand != GameInfo.Hand.NONE) 
			setDefaultImages.get(oldHand).set();	
		
		somethingIsSelected = true;
	}//end select()...
}//end gameMainController{}...
