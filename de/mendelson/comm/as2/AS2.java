//$Header: /mec_as2/de/mendelson/comm/as2/AS2.java 7     3/02/22 14:35 Heller $
package de.mendelson.comm.as2;

import de.mendelson.comm.as2.client.AS2Gui;
import de.mendelson.comm.as2.server.AS2Agent;
import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.comm.as2.server.ServerAlreadyRunningException;
import de.mendelson.comm.as2.server.UpgradeRequiredException;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.Splash;
import de.mendelson.util.font.FontUtil;
import de.mendelson.util.security.BCCryptoHelper;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.RescaleOp;
import java.net.BindException;
import java.util.Locale;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Start the AS2 server and the configuration GUI
 *
 * @author S.Heller
 * @version $Revision: 7 $
 */
public class AS2 {

    /**
     * Displays a usage of how to use this class
     */
    public static void printUsage() {
        System.out.println("java " + AS2.class.getName() + " <options>");
        System.out.println("Start up a " + AS2ServerVersion.getProductNameShortcut() + " server ");
        System.out.println("Options are:");
        System.out.println("-lang <String>: Language to use for the client/server, nonpersistent. Possible values are \"en\", \"fr\" and \"de\".");
        System.out.println("-country <String>: Country/region to use for the client/server, nonpersistent. Possible values are \"DE\", \"US\", \"FR\", \"GB\"...");
        System.out.println("-nohttpserver: Do not start the integrated HTTP server, only useful if you are integrating the product into an other web container");
        System.out.println("-mode <String>: Sets up the LIGHT or DARK mode for the client - default is LIGHT");
    }

    /**
     * Method to start the server on from the command line
     */
    public static void main(String args[]) {
        String language = null;
        String country = null;
        boolean startHTTP = true;
        String mode = "LIGHT";
        int optind;
        for (optind = 0; optind < args.length; optind++) {
            if (args[optind].toLowerCase().equals("-lang")) {
                language = args[++optind];
            } else if (args[optind].toLowerCase().equals("-country")) {
                country = args[++optind];
            }else if (args[optind].toLowerCase().equals("-nohttpserver")) {
                startHTTP = false;
            } else if (args[optind].toLowerCase().equals("-mode")) {
                mode = args[++optind];
            } else if (args[optind].toLowerCase().equals("-?")) {
                AS2.printUsage();
                System.exit(1);
            } else if (args[optind].toLowerCase().equals("-h")) {
                AS2.printUsage();
                System.exit(1);
            } else if (args[optind].toLowerCase().equals("-help")) {
                AS2.printUsage();
                System.exit(1);
            }
        }
        PreferencesAS2 clientPreferences = new PreferencesAS2();
        //load country from preferences
        if (country == null || language == null) {            
            if (language == null) {
                language = clientPreferences.get(PreferencesAS2.LANGUAGE);
            }
            if (country == null) {
                country = clientPreferences.get(PreferencesAS2.COUNTRY);
            }
        }
        if (language != null && country != null) {
            if (language.toLowerCase().equals("en")) {
                Locale.setDefault(new Locale(Locale.ENGLISH.getLanguage(), country));
            } else if (language.toLowerCase().equals("de")) {
                Locale.setDefault(new Locale(Locale.GERMAN.getLanguage(), country));
            }else if (language.toLowerCase().equals("fr")) {
                Locale.setDefault(new Locale(Locale.FRENCH.getLanguage(), country));
            } else {
                AS2.printUsage();
                System.out.println();
                System.out.println("Language " + language + " is not supported.");
                System.exit(1);
            }
        }
        if( mode != null && mode.equalsIgnoreCase("dark")){
            //darken all SVG generated images/icons by 10% (also the splash)
            MendelsonMultiResolutionImage.addSVGImageOperation(new RescaleOp(0.9f, 0, null));
        }
        //add colorblind overlays if required to the client icons
        if (clientPreferences.getBoolean(PreferencesAS2.COLOR_BLINDNESS)) {
            MendelsonMultiResolutionImage.addSVGOverlay("state_finished.svg", "/de/mendelson/util/colorblind/overlay_state_finished.svg");
            MendelsonMultiResolutionImage.addSVGOverlay("cert_valid.svg", "/de/mendelson/util/colorblind/overlay_state_finished.svg");
            MendelsonMultiResolutionImage.addSVGOverlay("state_stopped.svg", "/de/mendelson/util/colorblind/overlay_state_stopped.svg");
            MendelsonMultiResolutionImage.addSVGOverlay("cert_invalid.svg", "/de/mendelson/util/colorblind/overlay_state_stopped.svg");
            MendelsonMultiResolutionImage.addSVGOverlay("state_pending.svg", "/de/mendelson/util/colorblind/overlay_state_pending.svg");
            MendelsonMultiResolutionImage.addSVGOverlay("state_allselected.svg", "/de/mendelson/util/colorblind/overlay_state_allselected.svg");
            MendelsonMultiResolutionImage.addSVGOverlay("severity_info.svg", "/de/mendelson/util/colorblind/overlay_severity_info.svg");
        }
        Splash splash = new Splash("/de/mendelson/comm/as2/client/splash_mendelson_opensource_as2.svg", 330);
        splash.setTextAntiAliasing(false);
        //dark grey
        Color textColor = FontUtil.getFontColor(FontUtil.PRODUCT_OFTP2_COMMUNITY);
        splash.addDisplayString(FontUtil.getProductFont(FontUtil.STYLE_PRODUCT_BOLD, 10),
                12, 285, AS2ServerVersion.getFullProductName(),
                textColor);  
        splash.setVisible(true);
        splash.toFront();
        //start server
        try {            
            //initialize the security provider
            BCCryptoHelper helper = new BCCryptoHelper();
            helper.initialize();
            AS2Server as2Server = new AS2Server(startHTTP, false, false);
            AS2Agent agent = new AS2Agent(as2Server);
        } catch (UpgradeRequiredException e) {
            SystemEventManagerImplAS2.newEvent(
                    SystemEvent.SEVERITY_ERROR, 
                    SystemEvent.ORIGIN_SYSTEM, 
                    SystemEvent.TYPE_DATABASE_UPDATE, 
                    "Manual DB upgrade required", e.getMessage());
            //an upgrade to HSQLDB 2.x is required, delete the lock file
            Logger.getLogger(AS2Server.SERVER_LOGGER_NAME).warning(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getClass().getName() + ": " + e.getMessage());
            AS2Server.deleteLockFile();
            System.exit(1);
        } catch (ServerAlreadyRunningException | BindException e) {
            SystemEventManagerImplAS2.newEvent(
                    SystemEvent.SEVERITY_ERROR, 
                    SystemEvent.ORIGIN_SYSTEM, 
                    SystemEvent.TYPE_MAIN_SERVER_STARTUP_BEGIN, 
                    "[" + e.getClass().getSimpleName() + "]", 
                    e.getMessage());
            if (splash != null) {
                splash.destroy();
            }
            e.printStackTrace();
            String message = e.getMessage();
            if (message == null) {
                message = "[" + e.getClass().getName() + "]";
            }
            JOptionPane.showMessageDialog(null, message);
            System.exit(1);
        }catch (Throwable e) {
            SystemEventManagerImplAS2.newEvent(
                    SystemEvent.SEVERITY_ERROR, 
                    SystemEvent.ORIGIN_SYSTEM, 
                    SystemEvent.TYPE_MAIN_SERVER_STARTUP_BEGIN, 
                    "[" + e.getClass().getSimpleName() + "]", 
                    e.getMessage());
            if (splash != null) {
                splash.destroy();
            }
            e.printStackTrace();
            String message = e.getMessage();
            if (message == null) {
                message = "[" + e.getClass().getName() + "]";
            }
            JOptionPane.showMessageDialog(null, message);
            AS2Server.deleteLockFile();
            System.exit(1);
        }
        //start client
        AS2Gui gui = new AS2Gui(splash, "localhost", "admin", "admin", mode);
        gui.setVisible(true);
        splash.destroy();
        splash.dispose();
    }
}
