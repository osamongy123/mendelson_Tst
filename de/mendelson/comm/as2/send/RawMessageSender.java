//$Header: /as2/de/mendelson/comm/as2/send/RawMessageSender.java 26    13.12.21 8:39 Heller $
package de.mendelson.comm.as2.send;

import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.clientserver.message.IncomingMessageRequest;
import de.mendelson.comm.as2.clientserver.message.IncomingMessageResponse;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.clientserver.AnonymousTextClient;
import de.mendelson.util.security.BCCryptoHelper;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Raw data uploader, for test purpose. Sends a already fully prepared
 * AS2 message to a specified sender
 *
 * @author S.Heller
 * @version $Revision: 26 $
 */
public class RawMessageSender {

    private Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);

    /**
     * Creates new raw message sender
     */
    public RawMessageSender() {
    }

    private IncomingMessageResponse send(Path rawDataFile, Path headerFile) throws Throwable {
        Properties header = new Properties();
        InputStream headerStream = null;
        try {
            headerStream = Files.newInputStream(headerFile);
            header.load(headerStream);
        } finally {
            if (headerStream != null) {
                headerStream.close();
            }
        }
        AnonymousTextClient client = null;
        client = new AnonymousTextClient();
        client.connect("localhost", AS2Server.CLIENTSERVER_COMM_PORT, 30000);        
        IncomingMessageRequest messageRequest = new IncomingMessageRequest();
        messageRequest.setMessageDataFilename(rawDataFile.toAbsolutePath().toString());
        messageRequest.setHeader(header);
        messageRequest.setContentType(header.getProperty("content-type"));
        messageRequest.setRemoteHost("localhost");
        IncomingMessageResponse response = (IncomingMessageResponse) client.sendSyncWaitInfinite(messageRequest);
        if (response.getException() != null) {
            throw (response.getException());
        }
        return (response);
    }

    /**
     * Displays a usage of how to use this class
     */
    public static void printUsage() {
        System.out.println("java " + RawMessageSender.class.getName() + " <options>");
        System.out.println("Start up a " + AS2ServerVersion.getProductNameShortcut() + " server ");
        System.out.println("Options are:");
        System.out.println("-datafile <String>: File that contains the AS2 message, fully packed");
        System.out.println("-headerfile <String>: File that contains the AS2 message header");
        System.out.println("-repeat <int>: Repeat the send process n times, defaults to 1 (single send)");
    }

    public static final void main(String[] args) {
        String filenameStr = null;
        String headerFilenameStr = null;
        int repeatCount = 1;
        int optind;
        for (optind = 0; optind < args.length; optind++) {
            if (args[optind].toLowerCase().equals("-datafile")) {
                filenameStr = args[++optind];
            } else if (args[optind].toLowerCase().equals("-headerfile")) {
                headerFilenameStr = args[++optind];
            } else if (args[optind].toLowerCase().equals("-repeat")) {
                repeatCount = Integer.valueOf(args[++optind]).intValue();
            } else if (args[optind].toLowerCase().equals("-?")) {
                RawMessageSender.printUsage();
                System.exit(1);
            } else if (args[optind].toLowerCase().equals("-h")) {
                RawMessageSender.printUsage();
                System.exit(1);
            } else if (args[optind].toLowerCase().equals("-help")) {
                RawMessageSender.printUsage();
                System.exit(1);
            }
        }
        if (filenameStr == null) {
            System.err.println("Parameter missing: " + "datafile");
            printUsage();
            System.exit(1);
        }
        if (headerFilenameStr == null) {
            System.err.println("Parameter missing: " + "headerfile");
            printUsage();
            System.exit(1);
        }
        RawMessageSender sender = new RawMessageSender();
        try {
            //initialize the security provider
            BCCryptoHelper helper = new BCCryptoHelper();
            helper.initialize();
            for (int i = 0; i < repeatCount; i++) {
                if (repeatCount > 0) {
                    Logger.getLogger(AS2Server.SERVER_LOGGER_NAME).info("Send attempt " + (i + 1) + "/" + (repeatCount + 1));
                }
                IncomingMessageResponse response = sender.send(Paths.get(filenameStr), Paths.get(headerFilenameStr));
                if (response.getMDNData() != null) {
                    Logger.getLogger(AS2Server.SERVER_LOGGER_NAME).info(new String(response.getMDNData()));
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
