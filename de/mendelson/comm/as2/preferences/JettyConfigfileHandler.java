//$Header: /as2/de/mendelson/comm/as2/preferences/JettyConfigfileHandler.java 1     2/08/22 15:37 Heller $
package de.mendelson.comm.as2.preferences;

import de.mendelson.util.httpconfig.server.HTTPServerConfigInfo;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Handler that deals with changes of the config file of the embedded jetty and
 * reads the properties from it
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class JettyConfigfileHandler {

    /**
     * keeps this as singleton
     */
    private static JettyConfigfileHandler instance;

    /**
     * Singleton for the whole application
     */
    public static synchronized JettyConfigfileHandler instance() {
        if (instance == null) {
            instance = new JettyConfigfileHandler();
        }
        return instance;
    }

    private JettyConfigfileHandler() {
    }

    /**
     * Checks if the config file is r/w and does exist etc
     */
    public synchronized boolean configFileAccessible() {
        Path configPath = Paths.get( HTTPServerConfigInfo.FILENAME_HTTP_SERVER_CONFIG_USER);
        return (Files.exists(configPath)
                && Files.isRegularFile(configPath)
                && Files.isWritable(configPath)
                && Files.isReadable(configPath));
    }

    /**
     * Reads a value from the jetty config file
     */
    public synchronized String getValue(String preferencesKey, String defaultValue) {
        Properties properties = new Properties();
        InputStream inStream = null;
        try {
            inStream = Files.newInputStream(Paths.get(HTTPServerConfigInfo.FILENAME_HTTP_SERVER_CONFIG_USER));
            properties.load(inStream);
            if (properties.containsKey(preferencesKey)) {
                return (properties.getProperty(preferencesKey));
            } else {
                return (defaultValue);
            }
        } catch (Exception e) {
            return (defaultValue);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (Exception e) {
                    //nop
                }
            }
        }
    }

    /**
     * Writes a value to the jetty config file by keeping the file structure. If
     * the key does not exist in the file it is added at the end
     */
    public synchronized void setValue(String preferencesKey, String value) {
        //Load the file structure
        List<String> lines = null;
        BufferedWriter writer = null;
        try {
            //read the config file line by line
            try {
                lines = Files.readAllLines(Paths.get(HTTPServerConfigInfo.FILENAME_HTTP_SERVER_CONFIG_USER));
                //replace the single value
                boolean replaced = false;
                for( int i = 0; i < lines.size(); i++ ){
                    if( lines.get(i).toLowerCase().startsWith(preferencesKey.toLowerCase())){
                        lines.set(i, preferencesKey + "=" + value);
                        replaced = true;
                    }
                }
                if( !replaced ){
                    lines.add(preferencesKey + "=" + value);
                }
            } catch (Exception e) {
                return;
            }
            writer = Files.newBufferedWriter(Paths.get(HTTPServerConfigInfo.FILENAME_HTTP_SERVER_CONFIG_USER));
            //write back the config file
            for( String line:lines){
                writer.write( line );
                writer.newLine();
            }
        } catch (Exception e) {
        } finally {
            if( writer != null ){
                try{
                    writer.flush();
                    writer.close();
                }
                catch( Exception e ){                    
                }
            }
        }

    }

}
