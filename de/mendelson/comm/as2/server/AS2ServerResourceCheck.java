//$Header: /as2/de/mendelson/comm/as2/server/AS2ServerResourceCheck.java 5     23.08.21 13:48 Heller $
package de.mendelson.comm.as2.server;

import de.mendelson.util.AS2Tools;
import de.mendelson.util.MecResourceBundle;
import java.io.IOException;
import java.net.Socket;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Class that checks resources of the host before starting the server
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class AS2ServerResourceCheck {

    private MecResourceBundle rb = null;

    public AS2ServerResourceCheck() {
        //Load default resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleAS2ServerResourceCheck.class.getName());
        } //load up  resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    /**Checks if a required port is used by another process*/
    public void performPortCheck() throws Exception {
        int[] ports = new int[]{
            AS2Server.CLIENTSERVER_COMM_PORT
        };
        for (int port : ports) {
            this.checkPort(port);
        }
    }

    private void checkPort(int port) throws Exception {
        try {
            Socket socket = new Socket("localhost", port);
            socket.close();
            throw new Exception(this.rb.getResourceString("port.in.use", String.valueOf(port)));
        } catch (IOException ex) {
            // The host is not listening on this port
        }
    }
    
    public void checkCPUCores(Logger logger){
        //check the number of processor cores
        int cores = Runtime.getRuntime().availableProcessors();
        if( cores < 4){
            logger.warning(this.rb.getResourceString("warning.few.cpucores", String.valueOf(cores)));
        }        
    }
    
    public void checkHeap( Logger logger ){
        //check the max heap memory assigned to the java process
        long maxMemory = Runtime.getRuntime().maxMemory();
        if( maxMemory < 950000000L){
            logger.warning(this.rb.getResourceString("warning.low.maxheap", AS2Tools.getDataSizeDisplay(maxMemory)));
        }
    }
    
    
    

}
