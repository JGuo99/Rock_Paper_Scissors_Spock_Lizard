package application;

import java.io.Serializable;
import application.GameInfo.Player;

// Class : GameInfo
// Description :
// 		Define a class implementing Serializable to be used as 
//		the match book-keeper containing important data on the 
//		status of the game. 
public class MatchInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	// Define ENUMS for possible match results
	//NOTOVER: at least one player has not chosen their hand
	//ERROR: returned by GameLogic.whoWon when sent an incomplete match
	public enum MatchStatus{
		NOTOVER, PLAYER1WON, PLAYER2WON, DRAW, ERROR;
	}

	// Data Members
	public MatchStatus matchStatus; 
	public Player player1;
	public Player player2;
	
	// Constructor with two arguments assigning them to the 
	// data members.
	public MatchInfo(Player p1, Player p2){
		matchStatus = MatchStatus.NOTOVER;
		player1 = p1;
		player2 = p2;
	}
	
	// Define a method to set the Match Status
	public void changeMatchStatus(MatchStatus source) {
		this.matchStatus = source;
	}

	
}
