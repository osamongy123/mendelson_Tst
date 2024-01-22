//$Header: /as2/de/mendelson/comm/as2/webclient2/StateDialog.java 16    21.12.21 16:11 Heller $
package de.mendelson.comm.as2.webclient2;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import de.mendelson.util.clientserver.about.ServerInfoRequest;
import de.mendelson.util.clientserver.about.ServerInfoResponse;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.clientserver.AnonymousTextClient;
import java.text.DateFormat;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Displays the state of the receipt unit
 *
 * @author S.Heller
 * @version $Revision: 16 $
 */
public class StateDialog extends OkDialog {

    public StateDialog() {
        super(850, 550, "Server state");
        this.setResizable(true);
        this.setClosable(true);
    }

    /**
     * Could be overwritten, contains the content to display
     */
    @Override
    public AbstractComponent getContentPanel() {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        Panel statePanel = new Panel();
        VerticalLayout statePanelLayout = new VerticalLayout();
        statePanelLayout.setSizeFull();
        StringBuilder sourceBuffer = new StringBuilder();
        boolean processingUnitUp = false;
        AnonymousTextClient client = null;
        try {
            client = new AnonymousTextClient();
            client.setDisplayServerLogMessages(false);
            client.connect("localhost", AS2Server.CLIENTSERVER_COMM_PORT, 30000);
            ServerInfoResponse response = (ServerInfoResponse) client.sendSync(new ServerInfoRequest(), 30000);
            long startTime = Long.valueOf(response.getProperties().getProperty(ServerInfoResponse.SERVER_START_TIME)).longValue();
            sourceBuffer.append("<p>The AS2 processing unit <strong>"
                    + response.getProperties().getProperty(ServerInfoResponse.SERVER_PRODUCT_NAME) + " "
                    + response.getProperties().getProperty(ServerInfoResponse.SERVER_VERSION) + " "
                    + response.getProperties().getProperty(ServerInfoResponse.SERVER_BUILD) + "</strong> is up<br>and running since <strong>"
                    + format.format(startTime) + "</strong>.</p>");
            processingUnitUp = true;
        } catch (Exception e) {
            sourceBuffer.append("Error connecting to AS2 processing unit: ");
            sourceBuffer.append(e.getMessage());
        } finally {
            if (client != null && client.isConnected()) {
                client.disconnect();
            }
        }
        sourceBuffer.append("<br><br>");
        if (processingUnitUp) {
            sourceBuffer.append("<strong><font color='green'>System status is fine.</font></strong><br><br><br>Please send your AS2 messages now to <a href=\"/as2/HttpReceiver\" target=\"_new\"><strong>HttpReceiver</strong></a>.");
        } else {
            sourceBuffer.append("<strong><font color='red'>Errors encounted.</font></strong><br>Please fix them before sending messages to <a href=\"/as2/HttpReceiver\" target=\"_new\"><strong>HttpReceiver</strong></a>.");
        }
        sourceBuffer.append("<br><br><br><hr><p>If you are running into any problem please visit the forum at <a href=\"http://mendelson-e-c.com/community\"><strong>community.mendelson-e-c.com</strong></a><br>or contact the mendelson team by sending a mail to <a href=\"mailto: service@mendelson.de\"><strong>service@mendelson.de</strong></a>.</p>");
        Label label = new Label(sourceBuffer.toString(), ContentMode.HTML);
        statePanelLayout.addComponent(label);
        statePanel.setContent(statePanelLayout);
        return (statePanel);
    }
}
