package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

// Class : GameInfo
// Description :
// 		Define a class implementing Serializable to be used as 
//		the only means to send information between the server and 
//		the client.
public class GameInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	//ENUMS: Different message options
	//NOTIFY: Server -> Clients : new client connections and general updates. 
	//CHALLENGE: Client -> Server: takes on player's challenge and tries to make match. USES: message, matchID, challenger, challengee
	//PLAY: Client -> Server: notify server what clients played. USES: message, matchID, respective player hands
	//RESULT: Server -> Client: notify clients the result of a match. USES: message, matchID, matchResult
	//ERROR: default
	public static enum MessageType{
		NOTIFY, CHALLENGE, ACCEPT_CHALL, DECLINE_CHALL, START_GAME, PLAY, END_GAME, REFILL_REQUEST, ERROR;
	}
	
	public static enum NotifyType{
		NEWPLAYER, DISCONNECTEDPLAYER;
	}

	//Enum for possible hands
	public enum Hand 
	{ 
	    ROCK, PAPER, SCISSORS, LIZARD, SPOCK, NONE;  
	} 
    
	//Enum for possible player statuses
	public enum PlayerStatus
	{
		DISCONNECTED, PLAYING, AVAILABLE, ERROR;
	}


	// Data Members
	public ArrayList<Player> players; 
	public ArrayList<MatchInfo> matches;
	public HashMap<Integer,Player> playerFinder;
	public HashMap<Integer, MatchInfo> matchFinder;

	// Communication stuff
	public MessageType message;
	public Integer challenger;
	public Integer challengee;
	public Integer playerIdentifier; 
	public String messageString;

	
	// Define the Constructor of GameInfo class
	public GameInfo(){
		players = new ArrayList<Player>();
		matches = new ArrayList<MatchInfo>();
		playerFinder = new HashMap<Integer,Player>();
		matchFinder = new HashMap<Integer, MatchInfo>();
		message = MessageType.ERROR;
	}

	// METHODS
	
	//addPlayer: adds a new player to the Array of players
	public void addPlayer(int number, String name){
		Player newPlayer = new Player(number,name);
		players.add(newPlayer);
		playerFinder.put(number, newPlayer);
	}

	//getPlayer: gets player, or returns null if there is no such player
	public Player getPlayer(int id){
		return playerFinder.get(id);
	}
	
	//removePlayer: removes player from the Array of players
	public void removePlayer(int id) {
		players.remove(playerFinder.get(id));
		playerFinder.remove(id);
	}

	//addMatch: creates new MatchInfo instance for both players, adds it to array of matches
	public void addMatch(Player player1, Player player2){
		MatchInfo mInfo = new MatchInfo(player1, player2);		
		matches.add(mInfo);
		int matchID = matches.size() - 1;
		matchFinder.put(matchID, matches.get(matchID));
		player1.matchID = matchID;
		player2.matchID = matchID;
	}
	
	//getMatch: gets match, or returns null if there is no such match
	public MatchInfo getMatch(int matchID){
		return matchFinder.get(matchID);
	}
    
	//removeMatch: removes match from the Array of matches
	public void removeMatch(int matchID) {
		try {
			matches.remove(matchFinder.get(matchID));
			matchFinder.remove(matchID);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	// Inner Class : Player
	// Description : 
	//		Serves as the helper class to represent each player in the
	//		game, and also allows an effecient management of the game
	//		mechanisms.
	public class Player implements Serializable {
		// Data members
		private static final long serialVersionUID = 1L;
		public Integer playerID;
		public String name;
		public PlayerStatus status;
		public Hand hand;
		public Integer matchID;
        
		// Constructor
		Player( Integer number,  String name){
			this.playerID = number;
			this.name = name;
			this.status = PlayerStatus.AVAILABLE;
			this.hand = Hand.NONE;
			this.matchID = -1;
		}//end Player()...
	}//end inner class Player{}...
}//end GameInfo{}...
