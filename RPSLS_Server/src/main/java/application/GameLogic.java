package application;

import java.util.ArrayList;
import java.util.HashMap;

import application.GameInfo.Hand;
import application.MatchInfo.MatchStatus;

//	Class: GameLogic
// 	Description:
//		-Container for staticfunction whoWon
public class GameLogic {
	
	//	Function: whoWon
	// 	Description:
	//		-Returns which player won based on their hand selections and the rules of RPSLS
	static public MatchStatus whoWon(MatchInfo mInfo) {
		if( mInfo.player1.hand == null || 
		    mInfo.player2.hand == null || 
		    mInfo.player1.hand == Hand.NONE || 
		    mInfo.player2.hand == Hand.NONE) {
			return MatchStatus.ERROR;
		}
		
		if(mInfo.player1.hand == mInfo.player2.hand) {
			return MatchStatus.DRAW;
		}
		 
		switch(mInfo.player1.hand) {
			case LIZARD:
				if(mInfo.player2.hand == Hand.SPOCK || mInfo.player2.hand == Hand.PAPER)
					return MatchStatus.PLAYER1WON;
				break;
			case PAPER:
				if(mInfo.player2.hand == Hand.SPOCK || mInfo.player2.hand == Hand.ROCK)
					return MatchStatus.PLAYER1WON;
				break;
			case ROCK:
				if(mInfo.player2.hand == Hand.LIZARD || mInfo.player2.hand == Hand.SCISSORS)
					return MatchStatus.PLAYER1WON;
				break;
			case SCISSORS:
				if(mInfo.player2.hand == Hand.LIZARD || mInfo.player2.hand == Hand.PAPER)
					return MatchStatus.PLAYER1WON;
				break;
			case SPOCK:
				if(mInfo.player2.hand == Hand.ROCK || mInfo.player2.hand == Hand.SCISSORS)
					return MatchStatus.PLAYER1WON;
				break;
			default:
				return MatchStatus.ERROR;
		}
		
		return MatchStatus.PLAYER2WON;
	}
}
