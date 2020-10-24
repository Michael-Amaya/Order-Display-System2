package xyz.potomac_foods.OrderDisplaySystem2;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/** The Display Controller controls the WebView on
 *  the screen by parsing the XML data received
 *  from the TCP server
 *
 * @author  Michael Amaya
 * @version 1.0
 * @since   2020-08-09
 *
 */
public class DisplayController {
    /** The main WebEngine of the program */
    private WebEngine webEngine;

    /** The Program's Config */
    private Config config;

    /** The Program's updated config */
    private Config updateConfig;

    /** The program's current screen */
    private String currentScreen;

    /** The idle screen file name */
    private String idleScreenFileName;

    /** The order screen file name */
    private String orderScreenFileName;

    /** The totals screen file name */
    private String totalsScreenFileName;

    /** Constructor that sets the classes' main WebView
     *  and config
     *
     * @param mainView  The program's main WebView
     * @param config    The program's config
     */
    public DisplayController(WebView mainView, Config config, Config updateConfig) throws MalformedURLException {
        this.webEngine = mainView.getEngine();
        this.config = config;
        this.updateConfig = updateConfig;
        this.currentScreen = "NONE";
        this.idleScreenFileName = new File("layout/layout-idle-nobs.html").toURI().toURL().toString();
        this.orderScreenFileName = new File("layout/layout-order-nobs.html").toURI().toURL().toString();
        this.totalsScreenFileName = new File("layout/layout-total-nobs.html").toURI().toURL().toString();

        loadIdleScreen();
    }

    /** Loads the default title screen
     *  slideshow once the pay screen
     *  is finished or when the program
     *  start */
    public void loadIdleScreen() {
        // Action to set when page is loaded
        this.webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> event) {
                if("command:ready".equals(event.getData())){
                    loadIdleSettings();
                }
            }
        });

        loadToScreen(idleScreenFileName);
        this.currentScreen = "IDLE";

        // Wait for the alert then load settings, look above
    }

    /** Loads the order screen and puts all the necessary info
     *  so that the customer knows what they're ordering.
     *  Will try not to reload the page if the order is ongoing
     *  and not a new one, saves some time, makes it possible to
     *  add slideshows on this page too
     *
     * @param orderData     The order HTML with the condiments, already formatted
     * @param cornerPic     The pic to put in the top right corner
     * @param subTotal      The sub total of the order
     * @param tax           The order Tax
     * @param total         The order Total
     */
    public void loadOrderScreen(String orderData, String cornerPic, String subTotal, String tax, String total) {
        // Action to set when the page is loaded
        this.webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> event) {
                if("command:ready".equals(event.getData())){
                    loadOrderSettings(orderData, cornerPic, subTotal, tax, total);
                }
            }
        });

        if (this.currentScreen.equals("ORDER")) {
            loadOrderSettings(orderData, cornerPic, subTotal, tax, total);
        } else {
            loadToScreen(orderScreenFileName);
            this.currentScreen = "ORDER";
        }

        // Wait for alert then load settings, look above
    }

    /** Loads the totals screen and then later loads
     *  the idle screen after a few seconds. The seconds
     *  can be set in the config file.
     *
     * @param subTotal  The subtotal of the order
     * @param tax       The tax of the order
     * @param total     The order total
     */
    public void loadTotalsScreen(String subTotal, String tax, String total) {
        // Action to set when the page is loaded
        this.webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> event) {
                if("command:ready".equals(event.getData())){
                    loadTotalsSettings(subTotal, tax, total);
                }
            }
        });

        loadToScreen(totalsScreenFileName);
        this.currentScreen ="TOTALS";

        // Wait for the alert then load the settings, look above
    }

    /** Loads the pictures and slideshow delay from
     *  The config so the slideshow works correctly */
    @SuppressWarnings("unchecked")
	private void loadIdleSettings() {
        for (String picLink : (List<String>) updateConfig.getConfig().get("slideshow-images")) {
            runScript("addImage('" + picLink + "')");
        }

        runScript("setSlideTime(" + Integer.valueOf(config.getConfig().getOrDefault("slideshow-delay", 7).toString()) + ")");
    }

    /** Sets the order settings on the screen, by
     *  calling the JavaScript methods
     *
     * @param orderData The order data, already formatted
     * @param cornerPic The picture the user wants in the corner
     * @param subTotal  The subtotal of the order
     * @param tax       The order's tax
     * @param total     The order's total
     */
    private void loadOrderSettings(String orderData, String cornerPic, String subTotal, String tax, String total){

        String orderFontSize = config.getConfig().getOrDefault("order-main-font-size", 45).toString();
        String totalsFontSize =  config.getConfig().getOrDefault("order-totals-font-size", 45).toString();
        String descriptionFontSize = config.getConfig().getOrDefault("order-description-font-size", 36).toString();

        runScript("setOrder('" + orderData + "')");
        runScript("setSideImage('" + cornerPic + "')");
        runScript("setSubTotal('$" + subTotal + "')");
        runScript("setTax('$" + tax + "')");
        runScript("setTotal('$"+ total + "')");

        runScript("updateOrderFontSize('" + orderFontSize + "')");
        runScript("updateTotalsFontSize('" + totalsFontSize + "')");
        runScript("updateDescriptionFontSize('" + descriptionFontSize + "')");
    }

    /** Loads the totals screen settings, such as the
     *  delay until it goes to the next screen and sets
     *  the total, subtotal, and tax with the JavaScript
     *  methods. Also goes back to the idle screen after
     *  payDelay seconds (written in the config)
     *
     * @param subTotal  The order's subtotal
     * @param tax       The order's tax
     * @param total     The order's total
     */
    private void loadTotalsSettings(String subTotal, String tax, String total) {
        int payDelay = Integer.parseInt(config.getConfig().getOrDefault("pay-delay", 7).toString());

        runScript("setSubTotal('$" + subTotal + "')");
        runScript("setTax('$" + tax + "')");
        runScript("setTotal('$" + total + "')");

            // DELAY HERE
        Thread waitToDoStuff = new Thread(() ->{
            System.out.println("Delaying for " + payDelay + " seconds!");
            try {
                TimeUnit.SECONDS.sleep(payDelay);
                loadIdleScreen();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        waitToDoStuff.setDaemon(true);
        waitToDoStuff.start();
    }

    /** Run a JavaScript command on
     *  the WebView. There is an issue
     *  with running too many commands
     *  at once with the JavaFX thread,
     *  so Platform.runLater() is used
     *  so that the JavaFX can run the
     *  commands when it sees fit
     *
     * @param javaScriptMethod The JavaScript method to run
     */
    public void runScript(String javaScriptMethod) {
        try {
            Platform.runLater(()-> {
                this.webEngine.executeScript(javaScriptMethod);
            });
        } catch (Exception e) {
            System.out.println("Could not run script: " + javaScriptMethod);
            e.printStackTrace();
        }
    }

    /** Load a file to the screen. There is an
     *  issue with JavaFX Threads if you do not
     *  not use Platform.runLater().
     *  So this just makes it easier to read
     *  as the runLater is done here
     *
     * @param fileName The file to be displayed
     */
    public void loadToScreen(String fileName) {
        try {
            Platform.runLater(()-> {
                this.webEngine.load(fileName);
            });
        } catch (Exception e) {
            System.out.println("Could not put on screen: ");
            e.printStackTrace();
        }
    }

    /** The TCP Server is what sends the data
     *  To this class via this method. It is
     *  sending XML that must be parsed and
     *  the sent to the screen via the
     *  load methods such as loadOrderScreen,
     *  loadTotalsScreen, and loadIdleScreen
     *
     * @param data XML Data that has order information that needs to go on the screen, usually gotten from a TCP Server
     */
    public void parseAndSendData(String data){
        String cornerPic = updateConfig.getConfig().getOrDefault("corner-image", "images/corner.png").toString();

        Document orderDoc = convertStringToXMLDocument(data);

        NodeList orderHeaderNL = orderDoc.getElementsByTagName("OrderHeader");
        Node orderHeaderN = orderHeaderNL.item(0);
        Element orderHeaderE = (Element) orderHeaderN;
        String orderState = orderHeaderE.getElementsByTagName("OrderState").item(0).getTextContent();

        String subTotal = orderHeaderE.getElementsByTagName("Subtotal").item(0).getTextContent();
        String total = orderHeaderE.getElementsByTagName("Total").item(0).getTextContent();
        String tax = orderHeaderE.getElementsByTagName("Tax").item(0).getTextContent();
        StringBuilder orderInfo = new StringBuilder();

        if(orderState.equals("Open")) {
            NodeList items = orderDoc.getElementsByTagName("Item");

            for (int i = 0; i < items.getLength(); i++) {
                Node nNode = items.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String quantity = eElement.getElementsByTagName("Quantity").item(0).getTextContent();
                    String name = eElement.getElementsByTagName("Name").item(0).getTextContent();
                    String price = eElement.getElementsByTagName("Price").item(0).getTextContent();

                    orderInfo
                            .append(quantity)
                            .append("&emsp; ")
                            .append(name)
                            .append("<p class=\"price\">")
                            .append(price.equals("0.00") ? "" : "$" + price)
                            .append("&nbsp;&nbsp;")
                            .append("</p> <br />")
                    ;

                    try {
                        NodeList condis = eElement.getElementsByTagName("Condiments");
                        for(int x = 0; x < condis.getLength(); x++){
                            Node nCondi = condis.item(x);
                            if(nCondi.getNodeType() == Node.ELEMENT_NODE) {
                                Element nElement = (Element) nCondi;

                                orderInfo.append("<span class=\"condiments\">");
                                for(int z = 0; z < nElement.getElementsByTagName("Description").getLength(); z++){
                                    orderInfo
                                            .append("&emsp;&emsp; - ")
                                            .append(nElement.getElementsByTagName("Description").item(z).getTextContent())
                                            .append("<br />")
                                    ;
                                }

                                orderInfo.append("</span>");
                            }
                        }

                    } catch (Exception ignored) { }
                }
            }

            loadOrderScreen(orderInfo.toString(), cornerPic, subTotal, tax, total);
        } else {
            loadTotalsScreen(subTotal, tax, total);
        }

        orderInfo.delete(0,orderInfo.length());
    }

    /** Converts the data received from
     *  Ezra into an XML document that
     *  can be analyzed and checked for
     *  data
     *
     * @param xmlString The string to be turned into a document
     * @return An XML document that can be analyzed
     */
    private static Document convertStringToXMLDocument(String xmlString){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try{
            builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
