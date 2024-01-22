///$Header: /mec_as2/de/mendelson/comm/as2/servlet/ServerState.java 14    2/01/23 15:57 Heller $
package de.mendelson.comm.as2.servlet;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Servlet to display the server state
 *
 * @author S.Heller
 * @version $Revision: 14 $
 */
import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.util.clientserver.about.ServerInfoRequest;
import de.mendelson.util.clientserver.about.ServerInfoResponse;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.clientserver.AnonymousTextClient;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServerState extends HttpServlet {

    /**
     * Format the date display
     */
    private DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    public ServerState() {
    }

    /**
     * A GET request should be rejected
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Map map = req.getParameterMap();
        String output = "html";
        if (map.containsKey("output")) {
            output = ((String[]) map.get("output"))[0];
            if (output == null) {
                output = "html";
            }
        }
        PrintWriter out = res.getWriter();
        if (output.equalsIgnoreCase("html")) {
            res.setContentType("text/html");
            out.println(this.getOutputHTML());
        } else {
            res.setContentType("text/html");
            out.println("<html>Unknown output type, please use one of 'html'</html>");
        }
    }

    private String getOutputHTML() {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        builder.append("<html>");
        builder.append("    <head>");
        builder.append("        <META NAME=\"description\" CONTENT=\"mendelson-e-commerce GmbH: Your EAI partner\">");
        builder.append("        <META NAME=\"copyright\" CONTENT=\"mendelson-e-commerce GmbH\">");
        builder.append("        <META NAME=\"robots\" CONTENT=\"NOINDEX,NOFOLLOW,NOARCHIVE,NOSNIPPET\">");
        builder.append("        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        builder.append("        <title>").append(AS2ServerVersion.getProductName()).append("</title>");
        builder.append("        <link rel=\"shortcut icon\" href=\"images/mendelson_favicon.png\" type=\"image/x-icon\" />");
        builder.append("    </head>");
        builder.append("    <body>");
        boolean processingUnitUp = false;
        AnonymousTextClient client = null;
        try {
            client = new AnonymousTextClient();
            client.setDisplayServerLogMessages(false);
            client.connect("localhost", AS2Server.CLIENTSERVER_COMM_PORT, 30000);
            ServerInfoResponse response = (ServerInfoResponse) client.sendSync(new ServerInfoRequest(), 30000);
            long startTime = Long.valueOf(response.getProperties().getProperty(ServerInfoResponse.SERVER_START_TIME)).longValue();
            builder.append("The AS2 processing unit "
                    + response.getProperties().getProperty(ServerInfoResponse.SERVER_PRODUCT_NAME) + " "
                    + response.getProperties().getProperty(ServerInfoResponse.SERVER_VERSION) + " "
                    + response.getProperties().getProperty(ServerInfoResponse.SERVER_BUILD) + " is up and running since "
                    + format.format(startTime) + ".");
            processingUnitUp = true;
        } catch (Exception e) {
            builder.append("Error connecting to AS2 processing unit: ");
            builder.append(e.getMessage());
        } finally {
            if (client != null && client.isConnected()) {
                client.disconnect();
            }
        }
        builder.append("<br><br>");
        if (processingUnitUp) {
            builder.append("System status is fine.");
        } else {
            builder.append("Errors encountered.");
        }
        builder.append("</body>");
        builder.append("</html>");
        return (builder.toString());
    }

    /**
     * POST by the HTTP client: receive the message and work on it
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //simulate HTTP state for test purpose
        //HTTP 400
        //response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        //HTTP 500
        //response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        //HTTP 503
        //response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);   
        //HTTP 502
        //response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);   
        this.doGet(request, response);
    }//end of doPost

    /**
     * Returns a short description of the servlet.
     */
    @Override
    public String getServletInfo() {
        return "Display mendelson AS2 server state";
    }
}
