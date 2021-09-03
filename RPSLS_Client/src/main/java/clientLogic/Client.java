package clientLogic;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import application.GameInfo;
import application.MatchInfo;
import application.GameInfo.MessageType;
import application.GameInfo.PlayerStatus;	


// Class : Client
// Description :
// 		Define a class extending Thread to represent the Client
//		thread of the RPSLS program, which would be used to 
//      listen for client inouts and send messages to the 
//		server.
public class Client extends Thread{
	// Data Members
	String ipAddress;
	int portNumber;
	Socket socketClient;
	public int clientID;
	volatile public GameInfo localGInfo;
	
	// Object Streams to interact with the server
	ObjectOutputStream out;
	ObjectInputStream in;

	private Consumer<Serializable> callback; // update tableView
	private Consumer<Serializable> startUp;
	private Consumer<Serializable> updateMessagesCallback; // update listView
	private Consumer<Serializable> gameCallback; //TODO: maybe time to make a callback hashmap??
	
	// Client Constructor taking 3 arguments
	public Client(Consumer<Serializable> startUp, String ipAddress, int portNumber){
		this.startUp = startUp;
		this.ipAddress = ipAddress;
		this.portNumber = portNumber;
	}
	
	// SETTERS for Data Members
	public void setCallback(Consumer<Serializable> call) {
		this.callback = call;
	}
	
	public void setGameCallback(Consumer<Serializable> call) {
		this.gameCallback = call;
	}
	
	public void setUpdateMessagesCallback(Consumer<Serializable> call) {
		this.updateMessagesCallback = call;
	}
	
	// Method : run
	// Description :
	//		Defines the abstract method run from Thread Class.
	@Override
	public void run(){
		try {	
			//Connects to the port and ip of server
			socketClient= new Socket(ipAddress,portNumber);	
		    out = new ObjectOutputStream(socketClient.getOutputStream());
		    in = new ObjectInputStream(socketClient.getInputStream());
		    socketClient.setTcpNoDelay(true);
			
			//Gets info from server through Game Info
			GameInfo gInfo = (GameInfo) in.readObject(); // set playerID
			clientID = gInfo.playerIdentifier;
			localGInfo = gInfo;
			
			//Invalid Client
		    if(gInfo.playerIdentifier == -1) { //Error-Checking
		    	return;	
		    }
			
		} catch(Exception e){
			System.out.println("failed to initialize client: " + e);
			e.printStackTrace();
		}
		
		//Reads in information from the server and interprets message accordingly
		while(true) {
			try {
				GameInfo gInfo = (GameInfo) in.readObject();
				localGInfo = gInfo;
				interpretMessage();
			}
			catch(Exception e) {
				System.out.println("Failed to get input from server: " + e);
				e.printStackTrace();
				break;
			}//end catch block
		}//end while loop
    }//end run()...
	
	// Method : sendAccepted
	// Description :
	//		Allows to send the message to the server that the challenge to the player 
	//		has been accepted.
	public void sendAccepted(GameInfo gInfo){
		try{
			// Sends a message to server
			gInfo.message = GameInfo.MessageType.ACCEPT_CHALL;
			out.writeObject(gInfo);
		}catch (Exception e){
			System.out.println("Did not send accepting message to server!");
		}
	}//end sendAccepted()...

    // Method : receiveResult
	// Description :
	//		Interprets what the result the server sends to client is.
	public void receiveResult(GameInfo gInfo){
		try{
			updateMessagesCallback.accept(gInfo.messageString);
			callback.accept(gInfo);
		}catch(Exception e){
			System.out.println("Did not receive any Result from server");
		}
	}//end receiveResult()...

	// Method : updateMessage
	// Description :
	//		Defines the method listView of the clients.
	public void updateMessage(GameInfo gInfo) {
		updateMessagesCallback.accept(gInfo.messageString); // update listView 
		
		callback.accept(gInfo); // update more challenge scene gui elements... will interpret GameInfo msg type
		GameInfo.Player user = gInfo.getPlayer(gInfo.playerIdentifier);
		if (user.status == PlayerStatus.PLAYING) { // Get the match if the client is playing
				MatchInfo match = gInfo.getMatch(user.matchID);
				if (match == null) {
					gInfo.message = MessageType.ERROR;
					gameCallback.accept(gInfo);
				}
		}
	}//end updateMessage()...

	// Method : interpretMessage
	// Description :
	//		Defines the method to interpret message to be sent using switch-case
	public void interpretMessage(){
		// Switch-Case to parse message
		switch(localGInfo.message) {
			//Send to server who the Client is challenging
			case CHALLENGE:
				callback.accept(localGInfo);
				break;
			//Send to server if the Client accepted/declined the challenge
			case DECLINE_CHALL:
			case ACCEPT_CHALL:
				callback.accept(localGInfo);
				break;
			// Notify with the message
			case NOTIFY:
				updateMessage(localGInfo);
				break;
			// When client receives result from server, client will know
			// using this key-word.
			case END_GAME:
				gameCallback.accept(localGInfo);
				break;
			// Using the feature of playAgain
			case REFILL_REQUEST:
				callback.accept(localGInfo);
				break;
			default:
				System.out.println("Message did not interpret");
				break;
		}
	}//end interpretMessage()...
	
	// Method : send
	// Description :
	//		Defines the method send info to the server.
	public void send() {
		try {
			// Send information and reset the output stream
			localGInfo.playerIdentifier = clientID;
			out.flush(); 
			out.reset();
			out.writeObject(localGInfo);
		} catch (IOException e) {
			// catch block
			e.printStackTrace();
		}
	}//end send()...
}
