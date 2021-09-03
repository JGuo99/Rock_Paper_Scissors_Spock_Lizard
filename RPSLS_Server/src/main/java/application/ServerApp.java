package application;
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import serverLogic.Server;

//	Class: ServerApp
//	Description:
//		-Initializes Server application, passes of stage to ServerStartController
public class ServerApp extends Application {
	GameInfo gInfo;
	HashMap<String, Scene> sceneMap;
	Server serverConnection;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		gInfo = new GameInfo();
		sceneMap = new HashMap<String,Scene>();
		
		sceneMap.put("start", createStartScene());
		
		primaryStage.setTitle("RPSLS");
		primaryStage.setScene(sceneMap.get("start"));
		primaryStage.show();
		// TODO Auto-generated method stub
		
		primaryStage.setOnCloseRequest(e->{
			Platform.exit();
			System.exit(0);
		});

	}

	//	Function: createStartScene
	//	Description:
	//		-Returns scene with loaded StartServerScene
	public Scene createStartScene() {
		try {
            // Read file fxml and draw interface.
            Parent root = FXMLLoader.load(getClass()
                    .getResource("/FXML/StartServerScene.fxml"));
 
            Scene scene = new Scene(root);
            return scene;
        } catch(Exception e) {
            e.printStackTrace();
    		return null;
        }
	}
	
	
}
