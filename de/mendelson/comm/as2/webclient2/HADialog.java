//$Header: /as2/de/mendelson/comm/as2/webclient2/HADialog.java 6     10.03.21 8:39 Heller $
package de.mendelson.comm.as2.webclient2;

import com.vaadin.server.ThemeResource;
import java.sql.Connection;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import de.mendelson.comm.as2.ha.HAAccessDB;
import de.mendelson.comm.as2.ha.ServerInstanceHA;
import de.mendelson.util.database.IDBDriverManager;
import java.text.DateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;


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
 * @version $Revision: 6 $
 */
public class HADialog extends OkDialog {

    private HAAccessDB haAccess;
    private IDBDriverManager dbDriverManager;

    public HADialog(IDBDriverManager dbDriverManager) {
        super(850, 350, "");
        super.setCaption("&nbsp;<strong>H</strong>igh <strong>A</strong>vailability cluster overview");
        super.setCaptionAsHtml(true);
        this.setIcon(new ThemeResource("../mendelson/images/ha_24x24.png"));
        this.setResizable(true);
        this.setClosable(true);
        this.dbDriverManager = dbDriverManager;
        this.haAccess = new HAAccessDB();
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
        sourceBuffer.append("<br>");
        try {
            List<ServerInstanceHA> nodeList = this.haAccess.getServerInstanceHA(this.dbDriverManager,
                    TimeUnit.MINUTES.toSeconds(1));
            sourceBuffer.append("The following instances are currently working together in the HA cluster<br>");
            sourceBuffer.append("Number of instances: " + nodeList.size() + "<br><br>");
            sourceBuffer.append("<table border=\"0\">");
            for (ServerInstanceHA haInstance : nodeList) {
                sourceBuffer.append("<tr>");
                sourceBuffer.append("<td><strong>").append(haInstance.getUniqueId()).append("</strong></td>");
                sourceBuffer.append("<td>[Running since ").append(format.format(haInstance.getStartTime())).append("]</td>");
                sourceBuffer.append("</tr>");
            }
            sourceBuffer.append("</table>");
        } catch (Exception e) {
                sourceBuffer.append(e.getMessage());
        }
        Label label = new Label(sourceBuffer.toString(), ContentMode.HTML);
        statePanelLayout.addComponent(label);
        statePanel.setContent(statePanelLayout);
        return (statePanel);
    }
}
