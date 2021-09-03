import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.*;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import serverLogic.Server;

class RPLSTest {

	static Server s;
	GameInfo gameInfo;
	@BeforeAll
	static void init() {
		s = new Server(null, 0);
		
	}
	
	@Test
	void gameInfoTest() {
		myGInfo = new GameInfo();
		assertEquals("GameInfo", myGInfo.getClass().getName(), "GameInfo class consructor not working correctly");
	}

	@Test
	void MatchInfoTest() {
		MatchInfo c = new MatchInfo();
		assertEquals("MatchInfo", c.getClass().getName(), "MatchInfo class consructor not working correctly");
	}

	@Test
	void PopUpsTest() {
		PopUps c = new PopUps();
		assertEquals("PopUps", c.getClass().getName(), "PopUps class consructor not working correctly");
	}

	@Test
	void ServerAppTest() {
		ServerApp c = new ServerApp();
		assertEquals("ServerApp", c.getClass().getName(), "ServerApp class consructor not working correctly");
	}

	@Test
	void ServerMainControllerTest() {
		ServerMainController c = new ServerMainController();
		assertEquals("ServerMainController", c.getClass().getName(), "ServerMainController class consructor not working correctly");
	}

	@Test
	void ServerStartControllerTest() {
		ServerStartController c = new ServerStartController();
		assertEquals("ServerStartController", c.getClass().getName(), "ServerStartController class consructor not working correctly");
	}

	@Test
	void ServerConstructorTest() {
		assertEquals("Server", s.getClass().getName(), "Server class consructor not working correctly");
	}

}
