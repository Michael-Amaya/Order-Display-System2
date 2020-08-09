package xyz.potomac_foods.OrderDisplaySystem2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/** TCP Server that receives data from a TCP client and sends it over to the
 *  DisplayController so it can be shown on the screen
 *
 * @author  Michael Amaya
 * @version 1.0
 * @since 2020-08-09
 *
 */
public class TCPServer implements Runnable{
    /** The TCP Server */
    private ServerSocket tcpServer;

    /** The controller for the WebView */
    private DisplayController controller;

    /** The running state of the server */
    private boolean serverState;

    /** Constructor creates the TCP server itself and
     *  handles and problems with the port
     *
     * @param port          The port of the TCP Server
     * @param controller    The display controller
     */
    public TCPServer(int port, DisplayController controller) {
        System.out.println("Started TCP Server");
        this.controller = controller;

        try {
            this.tcpServer = new ServerSocket(port);
            this.serverState = true;
        } catch (Exception e) {
            this.serverState = false;
            e.printStackTrace();
        }
    }

    /** Sets the state of the TCP server
     *  When false, the server will stop
     *  When true, the server will keep going
     *
     * @param state The desired state of the server
     */
    public void setServerState(Boolean state) { this.serverState = state; }


    /** Start the server and accept data
     *  When data is received send it to
     *  the controller for processing and
     *  displaying
     *
     */
    @Override
    public void run() {
        while (this.serverState) {
            try {
                Socket socket;          // The connection between servers
                String msg;             // The next character received
                InputStream input;      // The input stream of the socket
                BufferedReader reader;  // The reader for the input stream

                StringBuilder data = new StringBuilder();

                System.out.println("Waiting for data...");
                socket = this.tcpServer.accept();
                System.out.println("Receiving data...");
                input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));

                while ((msg = reader.readLine()) != null) {
                    data.append(msg);
                }

                System.out.println("Sending");
                this.controller.parseAndSendData(data.toString());
                data.delete(0, data.length());
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
