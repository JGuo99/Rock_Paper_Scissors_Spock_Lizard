package serverLogic;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import application.GameInfo;
import application.GameLogic;
import application.Observer;
import application.GameInfo.Hand;
import application.GameInfo.MessageType;
import application.GameInfo.Player;
import application.GameInfo.PlayerStatus;
import application.MatchInfo.MatchStatus;
import application.MatchInfo;
import javafx.application.Platform;
import javafx.scene.control.ListView;

//Server class contains logic for Server and Client threads
public class Server{
	
	//DATA MEMBERS
	volatile public GameInfo masterGInfo; // master gInfo is shared across all threads
	int count = 1;	
	HashMap<Integer, ClientThread> clients = new HashMap<Integer,ClientThread>();
	HashMap<Integer, WeakReference<Observer>> observerColl = new HashMap<>();
	TheServer server;
	private Consumer<Serializable> callback;
	private Consumer<Serializable> listViewCallBack; // accept() takes a string
	private int serverPortNumber;
	
	//CONSTRUCTOR
	public Server(Consumer<Serializable> call, int serverPortNumber){
		this.serverPortNumber = serverPortNumber;
		callback = call;
		server = new TheServer();
		masterGInfo = new GameInfo();
		server.start();
	}
	
	//FUNCTIONS
	
	//  Function: setCallback 
	// 	Description: 
	//		-Allows for callback to be set by the next controller taking over. This is
	//     necessary since elements from next scene are not instantiated on the original scene;
	//  Parameters:
	//      -call: functional interface containing the function to be called on .accept()
	public void setCallback(Consumer<Serializable> call) {
		this.callback = call;
	}

	//  Function: setListViewCallBack
	//  Description:
	//     -same functionality as setCallback, but specific for the status update listView on 
	//	   GUI
	public void setListViewCallBack(Consumer<Serializable> call) {
		this.listViewCallBack = call;
	}
	
	//  Function: notifyObservers Function
	//  Description:
	//     -Notify all observers that a client joined/disconnected from the server. Synchronized so that
	//     only one ClientThread is able to call this at a given time
	public synchronized void notifyObservers() { 
		synchronized(observerColl) { // In case if some clients join/disconnect amid notifying
			synchronized(masterGInfo) { // so that other threads can't make changes to it while this is running				
				
				masterGInfo.message = MessageType.NOTIFY;
				observerColl.forEach((k, wkR) -> {					
					Observer obs = (Observer) wkR.get();
					if (obs != null)
						obs.update(masterGInfo);					
				
				});					
			}//end synchronized for masterGInfo
		}//end synchronized for observerColl
	}

	//  Function: registerObserver Function
	//  Description:
	//		-Add an observer object, ClientThreads in this case, to the Observer list in Subject
	public void registerObserver(int clientNum, Observer obs) {	
		synchronized(observerColl) {
			observerColl.put(clientNum, new WeakReference<Observer>(obs));
		}		
	}

	
	//  Function: unregisterObserver
	//	Description: 
	//		-remove Observer from observerList, done when client disconnects so that an exception is
	//		not cause when trying to send to an unexisting client
	public void unregisterObserver(int clientNum) {
		synchronized(observerColl) {
			observerColl.remove(clientNum);
		}
	}
	
	//  Class: Server
	//  Description: 
	//		-Contains logic create the socket to receive client connections. 
	public class TheServer extends Thread{				

		//  Function: run
		//  Description:
		//		-Create server socket, start accepting connections 
		public void run() {		

			//Start socket
			try(ServerSocket mysocket = new ServerSocket(serverPortNumber);){

		    //Accept client connections
		    while(true) {
				ClientThread c = new ClientThread(mysocket.accept(), count);
				clients.put(count,c); // add to list of clients
				registerObserver(count,c); // add to observer list
				c.start(); // starts client thread

				listViewCallBack.accept("Client #" + count + " joined the server");
				count++;
				callback.accept(new GameInfo());				
			}

			} catch(Exception e) {
				System.out.println("Could not start server");
			}
		}		
	}
	
	//  Class: ClientThread
	//  Description:
	//		-Thread to hold in/out streams for each client
	class ClientThread extends Thread implements Observer {
		
		//DATA MEMBERS
		GameInfo localGameInfo;
		int clientNumber;
		Socket connection;
		ObjectInputStream in;
		ObjectOutputStream out;
		Boolean activeChallenge; //helps filter 1 challenge per player
		
		//CONSTRUCTOR
		ClientThread(Socket s, int clientNumber){
			this.connection = s;
			this.clientNumber  = clientNumber;
			this.localGameInfo = new GameInfo();
			this.activeChallenge = false;
		}
		
		//FUNCTIONS
		
		//  Function: update
		//	Description: 
		//		-Calls send to update client's status messages.
		public void update(GameInfo gInfo) {
			try {				
				masterGInfo.playerIdentifier = clientNumber; // associates the object with the clientID
				send();
			}
			catch(Exception e) {
				System.out.println("Failed to update client #" + clientNumber + ": " + e);
				e.printStackTrace();
			}
		}

		
		//  Function: run
		//	Description:
		//		-Sets in/out streams for this client. Executes all necessary actions for a Player
		//		in the GameInfo class
		public void run() {
			//Instantiate elements for this connection and Player
			try {
				in = new ObjectInputStream(connection.getInputStream());
				out = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);	
				localGameInfo.playerIdentifier = clientNumber; // set this clientThread gameInfo's ID
				masterGInfo.addPlayer(clientNumber, "Player" + clientNumber); // add client to arrayList of players in master gameInfo
				masterGInfo.playerIdentifier = clientNumber;
				masterGInfo.message = MessageType.NOTIFY;
				send(); // tell client its clientNumber
				masterGInfo.messageString = "Player" + clientNumber + " joined the server";
				notifyObservers(); // notify every client that a new client joined the server
			} catch(Exception e) {
				System.out.println("Streams not open");
			}
			
			//Read information from client and process it
			 while(true) {
				try {
					GameInfo data = (GameInfo) in.readObject();
					interpretMsg(data);
				} catch(Exception e) {
					listViewCallBack.accept("Client #" + clientNumber + " disconnected from the server");
					clients.remove(this); // remove from clients list						
					unregisterObserver(clientNumber); // remove from observers list
					synchronized(masterGInfo) { // remove from players list in master gameInfo
						try {
							masterGInfo.removeMatch(masterGInfo.getPlayer(clientNumber).matchID); // remove the match this player is in
						} catch(Exception ex) {
							System.out.println("No such match is ongoing");
						}
						masterGInfo.removePlayer(clientNumber);
						masterGInfo.messageString = "Client #" + clientNumber + " disconnected from the server";
					}
					notifyObservers(); // notify all observers that this client disconnected from the server
					callback.accept(masterGInfo);					
					break;
				}
			}
		}
		
		//  Function: interpretMsg
		//	Description:
		//		-Take GameInfo object sent by client and act accordingly. 
		public void interpretMsg(GameInfo gInfo) {
			switch(gInfo.message) {
				case CHALLENGE: //Send challenge onto the challenge target				
					sendChallenge(gInfo);
					break;
				case ACCEPT_CHALL://Confirm to the challenger that it can start the game
					startMatch(gInfo);
					break;
				case DECLINE_CHALL: 
					rejectChallenger(gInfo);
					break;
				case PLAY: //Receive specific hand picked by this client
					checkMatch(gInfo);
					break;
				case REFILL_REQUEST: //Send list of players on server to the client once they return to the challenge scene
					refillClient(gInfo);
					break;
				default: //Should be unreachable
				break;
			}
		}
		
		
		//Functions called by interpretMsg:
		
		//  Function: rejectChallenger
		//  Description:
		//		-Send to the challenger that their challenge was declined.
		public void rejectChallenger(GameInfo gInfo) {
			masterGInfo.message = MessageType.DECLINE_CHALL;
			clients.get(gInfo.challenger).send();
		}
		
		//  Function: refillClient
		//  Description:
		//		-Send list of players to client so they can fill their challenge list. Also resets information on this 
		//		client since this is called when player is no longer in game scene
		public void refillClient(GameInfo gInfo) {
			try {
				synchronized(masterGInfo) {
					masterGInfo.message = MessageType.REFILL_REQUEST;
					Player currPlayer = masterGInfo.getPlayer(gInfo.playerIdentifier); // get the player obj associated with this client
					currPlayer.status = PlayerStatus.AVAILABLE; // reset status
					currPlayer.hand = Hand.NONE; // reset hand
					send();
					notifyObservers(); // notify all players on the server that this client is available
					this.activeChallenge = false;
				}
			} catch(Exception e) {
				System.out.println("Failed to refill clients: " + e);
				e.printStackTrace();
			}
		}
		
		//  Function: sendChallenge
		//  Description:
		//		-Pass on challenge to challengee so they can answer to it. Automatically declines challenge if challengee
		// 		is being challenged at the time.
		public void sendChallenge(GameInfo gInfo) {
			try {
				synchronized(masterGInfo) {
					masterGInfo.challengee = gInfo.challengee; 
					masterGInfo.challenger = gInfo.challenger; 
					if(!clients.get(gInfo.challengee).activeChallenge) {	 
						masterGInfo.message = MessageType.CHALLENGE;
						clients.get(gInfo.challengee).send(); 
						clients.get(gInfo.challengee).activeChallenge = true;
					}else {
						masterGInfo.message = MessageType.DECLINE_CHALL;
						clients.get(gInfo.challenger).send();
					}
				}
			} catch (Exception e) {
				System.out.println("Failed to send challenge: " + e);
			}
		}
		
		//	Function: startMatch
		//	Description:
		//		-Tells challenger his challenge has been accepted. Creates match with these 2 players in masterGameInfo
		public void startMatch(GameInfo gInfo) {
			try {
				synchronized(masterGInfo) {
					Player p1 = masterGInfo.getPlayer(gInfo.challenger);
					Player p2 = masterGInfo.getPlayer(gInfo.challengee);
					p1.status = PlayerStatus.PLAYING;
					p2.status = PlayerStatus.PLAYING;
					masterGInfo.addMatch(p1, p2);
					masterGInfo.message = MessageType.ACCEPT_CHALL;
					clients.get(p1.playerID).send();
					listViewCallBack.accept(masterGInfo.getPlayer(masterGInfo.challenger).name + " and " + 
					masterGInfo.getPlayer(masterGInfo.challengee).name + " are playing in Match #"+ (p1.matchID+1) +"." ); // update listView with the string
					notifyObservers(); // notify all players on the server that these two players are in a match 
					callback.accept(masterGInfo); // update matchView to indicate that there's a new match ongoing

				}
			} catch(Exception e) {
				System.out.println("Failed to tell clients to start match: " + e);
				e.printStackTrace();
			}
		}
		
		//	Function: checkMatch
		//	Description:
		//		-check if both players in a match made their choice. Will be called when players in a match
		//		sent their choice to the server 
		public void checkMatch(GameInfo gInfo) {			
			synchronized(masterGInfo) {
				try {
					GameInfo.Player currPlayer = masterGInfo.getPlayer(gInfo.playerIdentifier); // get the player obj of the client who sent the GameInfo
					currPlayer.hand = gInfo.getPlayer(gInfo.playerIdentifier).hand; // set the client's choice in the master GameInfo
					
					MatchInfo currMatch = masterGInfo.getMatch(currPlayer.matchID); // get the match obj based on the client's matchID
					callback.accept(masterGInfo); // update this client's choice in tableView of matches
					
					if(currMatch.player1.hand != Hand.NONE && currMatch.player2.hand != Hand.NONE) { // check if both players in a match made their choices
						currMatch.matchStatus = GameLogic.whoWon(currMatch); // run game logic to determine who won
						masterGInfo.message = MessageType.END_GAME; // set message flag
						clients.get(currMatch.player1.playerID).send(); // notify player1
						clients.get(currMatch.player2.playerID).send(); // notify player2
						String resultStr = "";
						switch (currMatch.matchStatus) { // result string uses player's name rather than just match status
							case PLAYER1WON:
								resultStr = "Match #" + (currPlayer.matchID+1) + "'s Result: " + currMatch.player1.name  + " WON!";
								break;
							case PLAYER2WON:
								resultStr = "Match #" + (currPlayer.matchID+1) + "'s Result: " + currMatch.player2.name  + " WON!";
								break;
							case DRAW:
								resultStr = "Match #" + (currPlayer.matchID+1) + " is a " + currMatch.matchStatus;
							default:
						}
						listViewCallBack.accept(resultStr); // update listView to display the result
						callback.accept(masterGInfo); // update tableView of matches

						if(currMatch != null) { //must be uncommented or else GUI will be affected!!
							masterGInfo.matches.remove(currMatch);
							callback.accept(masterGInfo); // remove match from matchView
						}
					}
				}catch (Exception e) {
					System.out.println("Failed to check the current match: " + e);
					e.printStackTrace();
				}
			}			
		}

		//	Function: send
		//	Description:
		//		-Send masterGInfo to this client. playerIdentifier is set for instantiation and debuggin purposes
		public synchronized void send() {
			try {
				out.flush();
				out.reset();
				masterGInfo.playerIdentifier = this.clientNumber;
				out.writeObject(masterGInfo);	
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}//end of client thread
}


	
	

	
