package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.ResourceBundle;

import application.GameInfo.Hand;
import application.MatchInfo.MatchStatus;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import serverLogic.Server;

//	Class: ServerMainController
//	Description:
//		-Controls server using corresponding fxml file
public class ServerMainController implements Initializable {
	public Server serverConnection;
	ObservableList<Information> listFormat;
	ArrayList<ObservableList<Information>> matchViews;
	ArrayList<String> matchesAList;
	
	@FXML
	private ListView server_listView;

    @FXML
    private Label serverPortNumber_Label;

    @FXML
    private ComboBox matchSelector_ComboBox;

    @FXML
    private TableView matchStatus_TableView;

    @FXML
    private TableColumn column1;
    
    @FXML
    private TableColumn column2;
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		//initialize all lists
		matchesAList = new ArrayList<String>();
		matchViews = new ArrayList<ObservableList<Information>>();
		
		column1.setCellValueFactory(new PropertyValueFactory<Information,String>("name"));
		column2.setCellValueFactory(new PropertyValueFactory<Information,String>("data"));
	}

//-------------------------------------------------------------------------------------------------------
	//METHODS
	
	//---------------------------------------------------------------------------------------------------
	//Methods not attached to GUI elements
	
	//setServerConnection method. Sets the callback method for the Server class to update this Scene
	//@SuppressWarnings("unchecked") 
	public void setServerConnection(Server server) {
		serverConnection = server; 
		
		serverConnection.setListViewCallBack(data-> {
			Platform.runLater(() -> {
				server_listView.getItems().add(data);
				server_listView.scrollTo(server_listView.getItems().size()-1);
			});
		});

		serverConnection.setCallback(data ->{
			Platform.runLater(()->{
				GameInfo gInfo = (GameInfo) data;
				
				matchViews.clear();
				matchesAList.clear();
				matchSelector_ComboBox.getItems().clear();
				//TODO: replace placeholder strings with respective information from GameInfo object
				//  using the matches ArrayList like so:
				for(MatchInfo m : gInfo.matches) {
					matchViews.add(getNewFormatList()); // add to MatchView
					matchesAList.add("Match " + (m.player1.matchID+1)); // add to dropdown list
					//Fill the table views
						//Update each row data
					ObservableList<Information> o = matchViews.get(matchViews.size() - 1);
					o.get(0).setName(m.player1.name);
					o.get(0).setData(m.player1.hand.toString());
					o.get(1).setName(m.player2.name);
					o.get(1).setData(m.player2.hand.toString());
					o.get(2).setData(m.matchStatus.toString());
				}												
				
				if(matchesAList.size() != 0){
					matchSelector_ComboBox.setItems(FXCollections.observableArrayList(matchesAList)); // set the dropdown list in comboBox
					
					//Set table view to selected information, defaulting on match 1
					for (String s : matchesAList) {
						matchSelector_ComboBox.setValue(s);
					}
					matchStatus_TableView.setItems(matchViews.get(0));
				} else {
					matchStatus_TableView.getItems().clear();
					matchSelector_ComboBox.getItems().clear();
				}
				
			});
		});
	}
	
	//helper function to create new formatted tableview content
	public ObservableList<Information> getNewFormatList(){
		return FXCollections.observableArrayList(
				/*0*/new Information("Player 1 Hand", Hand.NONE.toString()),
				/*1*/new Information("Player 2 Hand", Hand.NONE.toString()),
				/*2*/new Information("Result", MatchStatus.NOTOVER.toString())
				);
	}
	
	//setPortText method. Called by old controller to be able to pass port number
	public void setPortText(String s) {
		serverPortNumber_Label.setText(s);
	}
	
	//---------------------------------------------------------------------------------------------------
	//Methods attached to GUI elements

	//setMatchInstance method. Changes table view depending on on comboBox Selection
	//FXML element: matchSelector_ComboBox
	public void setMatchInstance() {
		 int indexOfSelection = matchesAList.indexOf(matchSelector_ComboBox.getValue());	
		 if(indexOfSelection != -1)
			 matchStatus_TableView.setItems(matchViews.get(indexOfSelection));
	}
	
//-------------------------------------------------------------------------------------------------------
	//HELPER CLASSES
	
	//Information inner class. Feeds table view 
	public class Information{
		//Data Members
		private String name;
		private String data;
		
		Information(){
			
		}
		
		//Constructor
		Information(String name, String data){
			this.name = name;
			this.data = data;
		}
		//Setters
		public void setName(String s) { this.name = s; }
		public void setData(String s) { data = s; }
		//Getters
		public String getName() { return name; }
		public String getData() { return data; }
	}
}
