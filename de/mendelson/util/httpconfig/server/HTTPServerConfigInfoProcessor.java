//$Header: /as2/de/mendelson/util/httpconfig/server/HTTPServerConfigInfoProcessor.java 14    17/01/23 11:55 Heller $
package de.mendelson.util.httpconfig.server;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.httpconfig.clientserver.DisplayHTTPServerConfigurationRequest;
import de.mendelson.util.httpconfig.clientserver.DisplayHTTPServerConfigurationResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.mina.core.session.IoSession;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Processes a http config request on the server side
 *
 * @author S.Heller
 * @version $Revision: 14 $
 */
public class HTTPServerConfigInfoProcessor {

    private HTTPServerConfigInfo httpServerConfigInfo;
    private MecResourceBundle rb;
    private String miscConfigurationText = "No HTTP server found";
    private String protocolConfigurationText = "No HTTP server found";
    private String cipherConfigurationText = "No HTTP server found";

    public HTTPServerConfigInfoProcessor(HTTPServerConfigInfo httpServerConfigInfo) {
        this.httpServerConfigInfo = httpServerConfigInfo;
        //Load default resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleHTTPServerConfigProcessor.class.getName());
        } //load up  resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        if( httpServerConfigInfo != null){
            this.miscConfigurationText = this.generateMiscConfigurationText();
            this.protocolConfigurationText = this.generateProtocolConfigurationText();
            this.cipherConfigurationText = this.generateCipherConfigurationText();
        }
    }

    private String generateMiscConfigurationText() {
        List<HTTPServerConfigInfo.Listener> listenerList = this.httpServerConfigInfo.getListener();
        List<String> receiptURLList = new ArrayList<String>();
        List<String> serverStateURLList = new ArrayList<String>();
        StringBuilder logBuilder = new StringBuilder();
        for (HTTPServerConfigInfo.Listener listener : listenerList) {
            String protocol = "NON-SSL";
            if (listener.getProtocol() != null && listener.getProtocol().toLowerCase().contains("ssl")) {
                protocol = "SSL";
                receiptURLList.add("https://<HOST>:" + listener.getPort() + this.httpServerConfigInfo.getReceiptURLPath());
                serverStateURLList.add("https://<HOST>:" + listener.getPort() + this.httpServerConfigInfo.getServerStatePath());
            } else {
                receiptURLList.add("http://<HOST>:" + listener.getPort() + this.httpServerConfigInfo.getReceiptURLPath());
                serverStateURLList.add("http://<HOST>:" + listener.getPort() + this.httpServerConfigInfo.getServerStatePath());
            }
            String adapterStr = "<unknown>";
            if (listener.getAdapter() != null) {
                adapterStr = listener.getAdapter();
            }
            logBuilder.append(
                    this.rb.getResourceString("http.server.config.listener",
                            new Object[]{String.valueOf(listener.getPort()),
                                protocol,
                                adapterStr
                            })
            );
            logBuilder.append("\n");
        }
        StringBuilder hostName = new StringBuilder();
        StringBuilder ip = new StringBuilder();
        //find out WAN IP
        logBuilder.append(this.generatePublicWANText(ip, hostName));
        //build receipt URLS to display
        List<String> tempList = new ArrayList<String>();
        if (hostName.length() > 0) {
            for (String receiptURL : receiptURLList) {
                String fullReceiptURL = receiptURL.replace("<HOST>", hostName.toString());
                tempList.add(fullReceiptURL);
            }
            receiptURLList.clear();
            receiptURLList.addAll(tempList);
        }
        logBuilder.append("\n");
        logBuilder.append("\n");
        logBuilder.append(this.rb.getResourceString("http.receipturls"));
        logBuilder.append("\n");
        for (String receiptURL : receiptURLList) {
            logBuilder.append(receiptURL);
            logBuilder.append("\n");
        }
        if (!serverStateURLList.isEmpty()) {
            logBuilder.append("\n");
            logBuilder.append(this.rb.getResourceString("http.serverstateurl"));
            logBuilder.append("\n");
            logBuilder.append(serverStateURLList.get(0).replace("<HOST>", hostName.toString()));
            logBuilder.append("\n");
            logBuilder.append("\n");
        }
        if (this.httpServerConfigInfo.isSSLEnabled()) {
            if (this.httpServerConfigInfo.getKeystorePath() != null) {
                Path keystoreFile = Paths.get(this.httpServerConfigInfo.getKeystorePath());
                logBuilder.append(this.rb.getResourceString("http.server.config.keystorepath",
                        keystoreFile.normalize().toAbsolutePath()));
                logBuilder.append("\n");
            }
            logBuilder.append(this.rb.getResourceString("http.server.config.clientauthentication",
                    String.valueOf(this.httpServerConfigInfo.needsClientAuthentication())));
        }
        //add the deployed WAR info
        logBuilder.append("\n");
        logBuilder.append("\n");
        logBuilder.append(this.rb.getResourceString("http.deployedwars"));
        logBuilder.append("\n");
        List<String> deployedWars = this.httpServerConfigInfo.getDeployedWars();
        if (deployedWars.isEmpty()) {
            logBuilder.append("--\n");
        }
        for (String deployedWARPath : deployedWars) {
            Path path = Paths.get(deployedWARPath);
            logBuilder.append("[");
            String filename = path.getFileName().toString();
            if( this.rb.containsResourceString("webapp." + filename)){
                logBuilder.append(this.rb.getResourceString("webapp." + filename));
            }else{
                logBuilder.append(this.rb.getResourceString("webapp._unknown"));
            }
            logBuilder.append("] ");
            logBuilder.append(deployedWARPath);
            logBuilder.append("\n");
        }
        return (logBuilder.toString());
    }

    private String generatePublicWANText(StringBuilder ipBuilder, StringBuilder hostNameBuilder) {
        StringBuilder logBuilder = new StringBuilder();
        //find out WAN IP
        String hostname = null;
        BufferedReader in = null;
        try {
            URL whatismyip = new URL("http://mendelson-e-c.com/mendelson_whatsmyip.php");
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ip = in.readLine(); //you get the IP as a String
            if (ip == null) {
                ip = "Unknown IP";
            }
            //try to get host name for the answer            
            hostname = "Unknown host";
            try {
                hostname = InetAddress.getByName(ip).getHostName();
            } catch (Exception e) {
                //nop
            }
            ipBuilder.append(ip);
            hostNameBuilder.append(hostname);
            logBuilder.append(this.rb.getResourceString("external.ip",
                    new Object[]{ip, hostname}));
        } catch (Exception e) {
            logBuilder.append(this.rb.getResourceString("external.ip.error"));
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    //nop
                }
            }
        }
        return (logBuilder.toString());
    }

    private String generateProtocolConfigurationText() {
        StringBuilder protocolBuilder = new StringBuilder();
        protocolBuilder.append(fold(this.rb.getResourceString("info.protocols",
                new Object[]{
                    this.httpServerConfigInfo.getHTTPServerConfigFile().normalize().toAbsolutePath().toString(),
                    this.httpServerConfigInfo.getJavaVersion(),
                    this.httpServerConfigInfo.getTLSSecurityProviderName()
                }), "\n", 80));
        protocolBuilder.append("\n\n");
        for (String protocol : this.httpServerConfigInfo.getPossibleProtocols()) {
            protocolBuilder.append(protocol);
            protocolBuilder.append("\n");
        }
        protocolBuilder.append("\n\n");        
        protocolBuilder.append(fold(this.rb.getResourceString("info.protocols.howtochange",
                new Object[]{
                    this.httpServerConfigInfo.getHTTPServerConfigFile().normalize().toAbsolutePath().toString()
                }), "\n", 80));
        protocolBuilder.append("\n");
        return (protocolBuilder.toString());
    }

    private String generateCipherConfigurationText() {
        StringBuilder cipherBuilder = new StringBuilder();
        cipherBuilder.append(fold(this.rb.getResourceString("info.cipher",
                new Object[]{
                    this.httpServerConfigInfo.getHTTPServerConfigFile().normalize().toAbsolutePath().toString(),
                    this.httpServerConfigInfo.getJavaVersion()
                }), "\n", 80));
        cipherBuilder.append("\n\n");
        for (String cipher : this.httpServerConfigInfo.getPossibleCipher()) {
            cipherBuilder.append(cipher);
            cipherBuilder.append("\n");
        }
        cipherBuilder.append("\n\n");
        cipherBuilder.append(fold(this.rb.getResourceString("info.cipher.howtochange",
                new Object[]{
                    this.httpServerConfigInfo.getHTTPServerConfigFile().normalize().toAbsolutePath().toString(),}), "\n", 80));
        return (cipherBuilder.toString());
    }

    /**
     * Async request from a client to display information about the server
     */
    public void processDisplayServerConfigurationRequest(IoSession session, DisplayHTTPServerConfigurationRequest request) {
        DisplayHTTPServerConfigurationResponse response = new DisplayHTTPServerConfigurationResponse(request);
        if (this.httpServerConfigInfo != null) {
            response.setHttpServerConfigFile(this.httpServerConfigInfo.getHTTPServerConfigFile().normalize().toAbsolutePath().toString());
            response.setHTTPServerUserConfigFile(this.httpServerConfigInfo.getHTTPServerUserConfigFile().normalize().toAbsolutePath().toString());
            response.setEmbeddedJettyServerVersion(this.httpServerConfigInfo.getJettyHTTPServerVersion());
            response.setEmbeddedHTTPServerStarted(this.httpServerConfigInfo.isEmbeddedHTTPServerStarted());
            response.setSSLEnabled(this.httpServerConfigInfo.isSSLEnabled());
            response.setJavaVersion(this.httpServerConfigInfo.getJavaVersion());
            if (this.httpServerConfigInfo.isSSLEnabled()) {
                for (String protocol : this.httpServerConfigInfo.getPossibleProtocols()) {
                    response.addProtocol(protocol);
                }
                for (String cipher : this.httpServerConfigInfo.getPossibleCipher()) {
                    response.addCipher(cipher);
                }
            }
            response.setMiscConfigurationText(this.getMiscConfigurationText());
            response.setProtocolConfigurationText(this.getProtocolConfigurationText());
            response.setCipherConfigurationText(this.getCipherConfigurationText());
        }
        //its a sync request
        session.write(response);
    }

    /**
     * @return the miscConfigurationText
     */
    public String getMiscConfigurationText() {
        return miscConfigurationText;
    }

    /**
     * @param miscConfigurationText the miscConfigurationText to set
     */
    public void setMiscConfigurationText(String miscConfigurationText) {
        this.miscConfigurationText = miscConfigurationText;
    }

    /**
     * @return the protocolConfigurationText
     */
    public String getProtocolConfigurationText() {
        return protocolConfigurationText;
    }

    /**
     * @return the cipherConfigurationText
     */
    public String getCipherConfigurationText() {
        return cipherConfigurationText;
    }

    /**
     * Folds a string using the passed delimiter where the max line length is
     * the passed lineLenght. Any included line delimiter is eaten up
     *
     * @param source Source string to use
     * @param delimiter Delimiter to add at the folding point
     * @param lineLength Max line length of the result
     */
    public static final String fold(String source, String delimiter, int lineLength) {
        List<String> lineList = new ArrayList<String>();
        String line = "";
        source = source.replace("\n", " ");
        String[] words = source.split(" ");
        for (String word : words) {
            if (line.length() + word.length() < lineLength) {
                if (line.length() > 0) {
                    line = line + " ";
                }
                line = line + word;
            } else {
                lineList.add(line);
                line = word;
            }
        }
        if (line.length() > 0) {
            lineList.add(line);
        }
        String result = String.join("\n", lineList);
        return (result);
    }

}
