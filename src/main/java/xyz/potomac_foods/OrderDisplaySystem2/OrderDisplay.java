package xyz.potomac_foods.OrderDisplaySystem2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

/** Order Display System shows orders gotten from TCP Connections to a screen
 *  Inherits the Application class from the JavaFX package
 *  in order to open a WebView
 *
 * @author  Michael Amaya
 * @version 1.0
 * @since   2020-08-09
 *
 */
public class OrderDisplay extends Application {

    public void start(Stage primaryStage) throws MalformedURLException {
    	// Routines to do before opening anything
    	
    	// Get parameters
    	Parameters params = getParameters();
    	List<String> paramList = params.getRaw();
    	boolean downloadUpdater = true;
    	if (paramList.size() != 0)
    		downloadUpdater = Boolean.parseBoolean(paramList.get(0));
    	
    	// Updater
    	if (Utilities.hasInternetConnection()) {
    		updateUpdater(downloadUpdater);
    	} else {
    		System.out.println("No Internet connection! Can't update updater");
    	}
    	
    	// End Updater
    	
    	
        StackPane mainPane = new StackPane();   // StackPane so the WebView takes the whole page
        WebView mainView = new WebView();       // What the program will load up

        Config config = new Config("config.yml");                       // The program config
        Config updateConfig = new Config("updateConfig.yml");
        DisplayController controller = new DisplayController(mainView, config, updateConfig);  // Controller for display

        // Add the WebView to the application
        mainPane.getChildren().add(mainView);

        Scene root = new Scene(mainPane, 1024, 720);
        primaryStage.setScene(root);

        // Make it so the application is always on the top
        primaryStage.setAlwaysOnTop(true);

        primaryStage.setTitle("Order Display System 2 by Michael Amaya");
        primaryStage.show();

        // Set the program to Full screen
        primaryStage.setFullScreen(true);
        primaryStage.requestFocus();

        TCPServer server = new TCPServer(Integer.parseInt(config.getConfig().getOrDefault("port", 5901).toString()), controller);
        Thread serverThread = new Thread(server);
        serverThread.setDaemon(true);
        serverThread.start();
    }

    private void updateUpdater(boolean downloadUpdater) {
    	// Need to download https://www.potomac-foods.xyz/ods2/downloads/ODSUpdater2.jar
    	if (downloadUpdater) {
			System.out.println("Beginning to update Updater....");
			Downloader downloader = new Downloader("https://www.potomac-foods.xyz/ods2/downloads/");
			
			downloader.add("/ODSUpdater2.jar");
			
			try {
				downloader.downloadAll();
				System.out.println("Finished updating Updater!");
			} catch (IOException e) {
				System.err.println("There was a fatal error downloading the updater.. " + e);
			}
    	} else {
    		System.out.println("Chose not to download the updater");
    	}
	}

	/** Launches the JavaFX Application */
    public static void main(String[] args) { launch(args); }
}
