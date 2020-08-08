package xyz.potomac_foods.OrderDisplaySystem2;

import javafx.scene.web.WebView;

/** The Display Controller controls the WebView on
 *  the screen by parsing the XML data received
 *  from the TCP server
 *
 * @author  Michael Amaya
 * @version 1.0
 * @since   2020-07-25
 *
 */
public class DisplayController {
    /** The main Webview of the program */
    private WebView mainView;

    /** The Program's Config */
    private Config config;

    /** Constructor that sets the classes' main WebView
     *  and config
     *
     * @param mainView  The program's main WebView
     * @param config    The program's config
     */
    public DisplayController(WebView mainView, Config config) {
        this.mainView = mainView;
        this.config = config;
    }

    /** Load the HTML data sent to the
     *  Webview page
     *
     * @param data The HTML to load to the page
     */
    private void displayToScreen(String data) { mainView.getEngine().loadContent(data); }

    /** Loads the default title screen
     *  slideshow once the pay-screen
     *  is finished
     *
     */
    private void loadDefaultSlideShow() {

    }

    /** The TCP Server is what sends the data
     *  To this class via this method. It is
     *  sending XML that must be parsed and
     *  the sent to the screen via the
     *  displayToScreen method.
     *
     * @param data The XML data received from the TCP Server
     */
    public void sendData(String data){
        System.out.println(data);
    }
}
