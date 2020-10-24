package xyz.potomac_foods.OrderDisplaySystem2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.MalformedURLException;

/** Order Display System shows orders gotten from TCP Connections to a screen
 *  Inherits the Application class from the JavaFX package
 *  in order to open a webview
 *
 * @author  Michael Amaya
 * @version 1.0
 * @since   2020-08-09
 *
 */
public class OrderDisplay extends Application {

    public void start(Stage primaryStage) throws MalformedURLException {
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

    /** Launches the JavaFX Application */
    public static void main(String[] args) { launch(args); }
}
