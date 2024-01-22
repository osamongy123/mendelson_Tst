///$Header: /as2/de/mendelson/comm/as2/servlet/HttpReceiver.java 56    1/09/22 14:11 Heller $
package de.mendelson.comm.as2.servlet;

import de.mendelson.Copyright;
import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.clientserver.message.IncomingMessageRequest;
import de.mendelson.comm.as2.clientserver.message.IncomingMessageResponse;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.AS2Tools;
import de.mendelson.util.clientserver.AnonymousTextClient;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Servlet to receive AS2 messages via HTTP
 *
 * @author S.Heller
 * @version $Revision: 56 $
 */
public class HttpReceiver extends HttpServlet {

    public HttpReceiver() {
    }

    /**
     * A GET request should be rejected
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        res.setContentType("text/html");
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        out.println("<HTML>");
        out.println("    <HEAD>");
        out.println("        <META NAME=\"description\" CONTENT=\"mendelson-e-commerce GmbH: Your EAI partner\">");
        out.println("        <META NAME=\"copyright\" CONTENT=\"mendelson-e-commerce GmbH\">");
        out.println("        <META NAME=\"robots\" CONTENT=\"NOINDEX,NOFOLLOW,NOARCHIVE,NOSNIPPET\">");
        out.println("        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        out.println("        <title>" + AS2ServerVersion.getProductName() + "</title>");
        out.println("        <link rel=\"shortcut icon\" href=\"images/mendelson_favicon.png\" type=\"image/x-icon\" />");
        out.println("    </HEAD>");
        out.println("    <BODY>");
        out.println("<H2>" + AS2ServerVersion.getProductName() + " " + AS2ServerVersion.getVersion() + " " + AS2ServerVersion.getBuild() + "</H2>");
        out.println("<BR> " + Copyright.getCopyrightMessage());
        out.println("<BR><br>You have performed an HTTP GET on this URL. <BR>");
        out.println("To submit an AS2 message, you must POST the message to this URL <BR>");
        out.println("    </BODY>");
        out.println("</HTML>");
    }

    /**
     * POST by the HTTP client: receive the message and work on it
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {        
        //stores if the commit already occured. Do not send an additional error in this case        
        boolean committed = false;
        Path dataFile = null;
        try {
            InputStream inStream = request.getInputStream();
            //store the data in a file to process it later. This may be useful
            //for a huge data request that may lead to a out of memory fairly easy.
            dataFile = AS2Tools.createTempFile("as2", "request");
            Files.copy(inStream, dataFile, StandardCopyOption.REPLACE_EXISTING);
            //extract header
            LinkedHashMap<String, String> headerMap = new LinkedHashMap<String, String>();
            Enumeration enumeration = request.getHeaderNames();
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                headerMap.put(key.toLowerCase(), request.getHeader(key));
            }
            //check if this is a AS2 message that requests async MDN. In this case return the ok code
            //before processing the message, there is no need to keep the connection alive.
            boolean isAS2MessageRequestingAsyncMDN = headerMap.containsKey("receipt-delivery-option") && headerMap.get("receipt-delivery-option") != null && headerMap.get("receipt-delivery-option").trim().length() > 0;
            if (isAS2MessageRequestingAsyncMDN) {
                this.informAS2ServerIncomingMessage(dataFile, headerMap, request, null);
                committed = true;
                response.setStatus(HttpServletResponse.SC_OK);
                //close the connection
                response.getWriter().flush();
                response.getWriter().close();
            } else {
                this.informAS2ServerIncomingMessage(dataFile, headerMap, request, response);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            if (!committed) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } finally {
            if (dataFile != null) {
                try {
                    Files.delete(dataFile);
                } catch (IOException e) {
                    SystemEvent event = new SystemEvent(
                            SystemEvent.SEVERITY_WARNING,
                            SystemEvent.ORIGIN_SYSTEM,
                            SystemEvent.TYPE_FILE_DELETE);
                    event.setSubject(event.typeToTextLocalized());
                    event.setBody("[" + e.getClass().getSimpleName() + "]: " + e.getMessage());
                    SystemEventManagerImplAS2.newEvent(event);
                }
            }
        }
    }//end of doPost

    /**
     * Informs the AS2 server that a new message arrived and returns the HTTP
     * returncode that has been set by the processing server
     */
    private void informAS2ServerIncomingMessage(Path dataFile,
            LinkedHashMap<String, String> headerMap, HttpServletRequest request,
            HttpServletResponse response) throws Throwable {
        AnonymousTextClient client = null;
        try {
            client = new AnonymousTextClient();
            client.setDisplayServerLogMessages(false);
            client.connect("localhost", AS2Server.CLIENTSERVER_COMM_PORT, 30000);
            IncomingMessageRequest messageRequest = new IncomingMessageRequest();
            messageRequest.setMessageDataFilename(dataFile.toAbsolutePath().toString());
            messageRequest.setContentType(request.getContentType());
            messageRequest.setUsesTLS(request.isSecure());
            String remoteHost = request.getRemoteHost();
            if (remoteHost == null) {
                remoteHost = request.getRemoteAddr();
            }
            messageRequest.setRemoteHost(remoteHost);
            Iterator<String> headerIterator = headerMap.keySet().iterator();
            while (headerIterator.hasNext()) {
                String key = headerIterator.next();
                if (key != null) {
                    String value = headerMap.get(key);
                    if (value != null) {
                        messageRequest.addHeader(key, value);
                    }
                }
            }
            IncomingMessageResponse messageResponse = (IncomingMessageResponse) client.sendSyncWaitInfinite(messageRequest);
            if (messageResponse.getException() != null) {
                throw (messageResponse.getException());
            }
            //build up response, this is the sync MDN
            if (response != null) {
                if (messageResponse.getHttpReturnCode() != HttpServletResponse.SC_OK) {
                    response.setStatus(messageResponse.getHttpReturnCode());
                }
                //add MDN data
                if (messageResponse.getMDNData() != null) {
                    Properties header = messageResponse.getHeader();
                    Iterator iterator = header.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        response.setHeader(key, header.getProperty(key));
                    }
                    ByteArrayInputStream inStream = new ByteArrayInputStream(messageResponse.getMDNData());
                    ServletOutputStream outStream = response.getOutputStream();
                    inStream.transferTo(outStream);
                    inStream.close();
                    outStream.flush();
                }
            }
        } finally {
            if (client != null && client.isConnected()) {
                client.disconnect();
            }
        }
    }

    /**
     * Returns a short description of the servlet.
     */
    @Override
    public String getServletInfo() {
        return "Receive AS2 messages via HTTP/S";
    }
}
