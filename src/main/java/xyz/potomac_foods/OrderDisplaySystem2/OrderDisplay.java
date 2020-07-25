package xyz.potomac_foods.OrderDisplaySystem2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/** Order Display System shows orders gotten from TCP Connections to a screen
 *  Inherits the Application class from the JavaFX package
 *  in order to open a webview
 *
 * @author  Michael Amaya
 * @version 1.0
 * @since   2020-7-25
 *
 */
public class OrderDisplay extends Application {

    public void start(Stage primaryStage) {
        StackPane mainPane = new StackPane();   // StackPane so the webview takes the whole page
        WebView mainPage = new WebView();       // What the program will load up

        Config config = new Config("config.yml"); // The program config

        // Add the WebView to the application
        mainPane.getChildren().add(mainPage);

        Scene root = new Scene(mainPane, 1024, 720);
        primaryStage.setScene(root);
        primaryStage.setTitle("Order Display System 2 by Michael Amaya");
        primaryStage.show();

        // Set the program to Full screen
        // primaryStage.setFullScreen(true);


    }

    /** Launches the JavaFX Application */
    public static void main(String[] args) { launch(args); }
}
