import static org.junit.jupiter.api.Assertions.*;

import application.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import clientLogic.Client;

class RPLSTest {
	static Client c;
	static GameInfo myGInfo;

	@BeforeAll
	static void init() {
		c = new Client(data->{}, "0.0.0.0", 1);
		myGInfo = new GameInfo();
	}
	@Test
	void constructorTest() {
		assertEquals(c.getClass(), Client.class);
	}

	@Test
	void clientTest() {
		assertFalse(c.isAlive());
	}

	@Test
	void sendAcceptedTest() {
		c.sendAccepted(myGInfo);
		assertEquals(GameInfo.MessageType.ACCEPT_CHALL, myGInfo.message, "Failed to set message type for sending accept req");
	}

	@Test
	void gameInfoConstructorTest() {
		GameInfo newGInfo = new GameInfo();
		assertEquals("application.GameInfo", newGInfo.getClass().getName(), "Failed to initialize from GameInfo");
	}

	@Test
	void getPlayerNameTest() {
		myGInfo.addPlayer(1, "Ben");
		assertEquals("Ben", myGInfo.getPlayer(1).name, "Failed to get player in Gameinfo");
	}

	@Test
	void getMatchTest() {
		myGInfo.addPlayer(1, "Ben");
		myGInfo.addPlayer(2, "Ben");
		myGInfo.addMatch(myGInfo.getPlayer(1), myGInfo.getPlayer(2));
		assertEquals(myGInfo.matches.get(0), myGInfo.getMatch(myGInfo.getPlayer(1).matchID), "Failed to get match in Gameinfo");
	}
	@Test
	void getPlayerIDTest(){
		myGInfo.addPlayer(1, "Franklin");
		assertEquals(1, myGInfo.getPlayer(1).playerID, "Fail to add player!");
	}
	@Test
	void removePlayerTest(){
		myGInfo.addPlayer(1,"Ben");
		myGInfo.removePlayer(1);
		myGInfo.addPlayer(1, "Franklin");
		assertEquals("Franklin",myGInfo.getPlayer(1).name, "Did not remove player");
	}


	@Test
	void removeMatchTest(){
		myGInfo.addPlayer(1, "Ben");
		myGInfo.addPlayer(2, "Frank");
		myGInfo.addMatch(myGInfo.getPlayer(1), myGInfo.getPlayer(2));
		myGInfo.removeMatch(myGInfo.getPlayer(1).matchID);
		assertEquals(0, myGInfo.matches.size(), "Match was not removed!");
	}

	@Test
	void ChallengeControllerTest() {
		ChallengeController c = new ChallengeController();
		assertEquals("ChallengeController", c.getClass().getName(), "ChallengeController class consructor not working correctly");
	}

	@Test
	void ChoiceTest() {
		Choice c = new Choice();
		assertEquals("Choice", c.getClass().getName(), "Choice class consructor not working correctly");
	}

	@Test
	void ClientAppTest() {
		ClientApp c = new ClientApp();
		assertEquals("ClientApp", c.getClass().getName(), "ChallengeController class consructor not working correctly");
	}

	@Test
	void ConnectControllerTest() {
		ConnectController c = new ConnectController();
		assertEquals("ConnectController", c.getClass().getName(), "ConnectController class consructor not working correctly");
	}

	@Test
	void GameMainControllerTest() {
		GameMainController c = new GameMainController();
		assertEquals("GameMainController", c.getClass().getName(), "GameMainController class consructor not working correctly");
	}
//	@Test
//	void MatchInfoTest() {
//		MatchInfo c = new MatchInfo();
//		assertEquals("MatchInfo", c.getClass().getName(), "MatchInfo class consructor not working correctly");
//	}

	@Test
	void PopUpsTest() {
		PopUps c = new PopUps();
		assertEquals("PopUps", c.getClass().getName(), "PopUps class consructor not working correctly");
	}


}
