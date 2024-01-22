//$Header: /as2/de/mendelson/comm/as2/webclient2/AboutDialog.java 10    7.12.20 11:29 Heller $
package de.mendelson.comm.as2.webclient2;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.Version;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import de.mendelson.Copyright;
import de.mendelson.comm.as2.AS2ServerVersion;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * The about dialog for the as2 server web ui
 *
 * @author S.Heller
 * @version $Revision: 10 $
 */
public class AboutDialog extends OkDialog {

    public AboutDialog() {
        super(580,580, "About");
        this.setResizable(false);
        this.setClosable(true);
        this.setDraggable(true);
    }

    /**
     * Could be overwritten, contains the content to display
     */
    @Override
    public AbstractComponent getContentPanel() {
        int maxCols = 7;
        int maxRows = 16;
        Panel aboutPanel = new Panel();
        VerticalLayout aboutPanelLayout = new VerticalLayout();
        aboutPanelLayout.setSizeFull();
        ThemeResource logo = new ThemeResource("../mendelson/images/logocommprotocols.gif");        
        GridLayout gridLayout = new GridLayout(maxCols, maxRows);
        gridLayout.setWidth(100, Unit.PERCENTAGE);
        gridLayout.addComponent(new Image("", logo), 0, 0, 1, 3);
        gridLayout.addComponent(new Label("<strong>" + AS2ServerVersion.getFullProductName() + "</strong>", 
                ContentMode.HTML), 2, 1, maxCols - 1, 1);
        gridLayout.addComponent(new Label(AS2ServerVersion.getLastModificationDate()), 2, 2, maxCols - 1, 2);
        gridLayout.addComponent(new Label("<br>", ContentMode.HTML), 0, 4, maxCols - 1, 4);
        gridLayout.addComponent(new Label(Copyright.getCopyrightMessage(), ContentMode.HTML), 0, 5, maxCols - 1, 5);
        gridLayout.addComponent(new Label(AS2ServerVersion.getStreet(), ContentMode.HTML), 0, 6, maxCols - 1, 6);
        gridLayout.addComponent(new Label(AS2ServerVersion.getZip(), ContentMode.HTML), 0, 7, maxCols - 1, 7);
        gridLayout.addComponent(new Label(AS2ServerVersion.getInfoEmail(), ContentMode.HTML), 0, 9, maxCols - 1, 9);
        gridLayout.addComponent(new Label("<br>", ContentMode.HTML), 0, 10, maxCols - 1, 10);
        gridLayout.addComponent(new Link("http://www.mendelson.de",
                new ExternalResource("http://www.mendelson.de")), 0, 11, maxCols - 1, 11);
        gridLayout.addComponent(new Link("http://www.mendelson-e-c.com", 
                new ExternalResource("http://www.mendelson-e-c.com")), 0, 12, maxCols - 1, 12);
        gridLayout.addComponent(new Label("<br>", ContentMode.HTML), 0, 13, maxCols - 1, 13);
        gridLayout.addComponent(new Label("<br>", ContentMode.HTML), 0, 14, maxCols - 1, 14);
        gridLayout.addComponent(new Label("[Based on VAADIN "
                + Version.getFullVersion() + "]"), 0, 15, maxCols - 1, 15);
        aboutPanelLayout.addComponent(gridLayout);
        aboutPanel.setContent(aboutPanelLayout);
        return (aboutPanel);
    }
}
