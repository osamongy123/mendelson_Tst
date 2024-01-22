//$Header: /as2/de/mendelson/util/clientserver/user/AddUser.java 1     17.01.11 15:46 Heller $
package de.mendelson.util.clientserver.user;

import java.util.logging.Logger;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Add a single user to the password file
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class AddUser {

    /**Displays a usage of how to use this class
     */
    public static void printUsage() {
        System.out.println("java " + AddUser.class.getName() + " <options>");
        System.out.println("Add a user to the client-server system");
        System.out.println("Options are:");
        System.out.println("-name <String>: User name");
        System.out.println("-passwd: Raw password");
    }

    /**Method to start the server on from the command line*/
    public static void main(String args[]) {
        String name = null;
        String passwd = null;
        int optind;
        for (optind = 0; optind < args.length; optind++) {
            if (args[optind].toLowerCase().equals("-name")) {
                name = args[++optind];
            } else if (args[optind].toLowerCase().equals("-passwd")) {
                passwd = args[++optind];
            } else if (args[optind].toLowerCase().equals("-?")) {
                AddUser.printUsage();
                System.exit(1);
            } else if (args[optind].toLowerCase().equals("-h")) {
                AddUser.printUsage();
                System.exit(1);
            } else if (args[optind].toLowerCase().equals("-help")) {
                AddUser.printUsage();
                System.exit(1);
            }
        }
        if (name == null) {
            AddUser.printUsage();
            System.out.println();
            System.out.println("Missing parameter \"name\".");
            System.exit(1);
        }
        if (passwd == null) {
            AddUser.printUsage();
            System.out.println();
            System.out.println("Missing parameter \"passwd\".");
            System.exit(1);
        }
        UserAccess userAccess = new UserAccess(Logger.getAnonymousLogger());
        try {
            userAccess.addUser(name, passwd.toCharArray());
            Logger.getAnonymousLogger().info("User \"" + name + "\" added successfully.");
        } catch (Exception e) {
            Logger.getAnonymousLogger().severe(e.getMessage());
        }
    }
}
