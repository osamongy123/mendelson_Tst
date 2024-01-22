//$Header: /as2/de/mendelson/comm/as2/AS2Shutdown.java 3     23.08.21 13:48 Heller $
package de.mendelson.comm.as2;

import de.mendelson.comm.as2.clientserver.message.ServerShutdown;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.clientserver.TextClient;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Shutdown the AS2 server
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class AS2Shutdown {

    /**
     * Displays a usage of how to use this class
     */
    public static void printUsage() {
        System.out.println(AS2Shutdown.class.getName() + ": Shutdown a local mendelson AS2 server by command line");
        System.out.println("Usage:");
        System.out.println("java " + AS2Shutdown.class.getName() + " <parameter>");
        System.out.println("Conditional parameter:");
        System.out.println("-user <String>: User name for the login process, default to \"admin\"");
        System.out.println("-password <String>: Password for the login process, defaults to \"admin\"");
    }

    /**
     * Method to start the server on from the command line
     */
    public static void main(String args[]) {
        String user = "admin";
        String password = "admin";
        int optind;
        for (optind = 0; optind < args.length; optind++) {
            if (args[optind].toLowerCase().equals("-user")) {
                user = args[++optind];
            } else if (args[optind].toLowerCase().equals("-password")) {
                password = args[++optind];
            } else if (args[optind].toLowerCase().equals("-?")) {
                AS2Shutdown.printUsage();
                System.exit(1);
            } else if (args[optind].toLowerCase().equals("-h")) {
                AS2Shutdown.printUsage();
                System.exit(1);
            } else if (args[optind].toLowerCase().equals("-help")) {
                AS2Shutdown.printUsage();
                System.exit(1);
            }
        }
        PreferencesAS2 preferences = new PreferencesAS2();
        try {
            TextClient client = new TextClient();
            client.connectAndLogin("localhost", AS2Server.CLIENTSERVER_COMM_PORT, 
                    AS2ServerVersion.getFullProductName(), user, password.toCharArray(), 15000);
            client.sendAsync(new ServerShutdown());
            client.logout();
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }
}