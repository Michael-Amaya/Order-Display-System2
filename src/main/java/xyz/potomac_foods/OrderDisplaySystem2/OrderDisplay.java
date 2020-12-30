package xyz.potomac_foods.OrderDisplaySystem2;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
	
	boolean downloadedUpdater = false;

    public void start(Stage primaryStage) throws MalformedURLException {
    	// Get parameters
    	Parameters params = getParameters();
    	List<String> paramList = params.getRaw();
    	boolean downloadUpdater = true;
    	if (paramList.size() != 0)
    		downloadUpdater = Boolean.parseBoolean(paramList.get(0));

    	// Test something
    	// Updater
    	if (Utilities.hasInternetConnection()) {
    		updateUpdater(downloadUpdater);
    		try {
    			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
    		    Date date = new Date(); 
				Utilities.getHTML("https://potomac-foods.xyz/ods2/log.php?store_num=None" + "&program=OrderDisplaySystem2&log=Updated%20Updater%20Successfully%20On%20" + formatter.format(date) + "!");
			} catch (IOException e) {
				// Ignored
			}
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
		mainView.setCursor(Cursor.NONE);
        mainPane.getChildren().add(mainView);
		mainPane.setCursor(Cursor.NONE);
        Scene root = new Scene(mainPane, 1024, 720);
        root.setCursor(Cursor.NONE);
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
				downloadedUpdater = true;
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
