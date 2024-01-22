//$Header: /as2/de/mendelson/util/clientserver/log/search/gui/ResourceBundleDialogSearchLogfile.java 3     19/01/23 9:26 Heller $
package de.mendelson.util.clientserver.log.search.gui;

import de.mendelson.util.MecResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize the mendelson products
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class ResourceBundleDialogSearchLogfile extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"title", "Search in server side log files"},
        {"no.data.messageid", "**There is no log data in that time range that matches the AS2 message id \"{0}\". Please enter the full message id into the search field." },        
        {"no.data.mdnid", "**There is no log data in that time range that matches the MDN id \"{0}\". Please enter the full MDN id into the search field." },       
        {"no.data.uid", "**There is no log data in that time range that matches the user defined id \"{0}\". Please use the full user defined id you have defined for a transmission." },       
        {"label.startdate", "Start" },
        {"label.enddate", "End" },
        {"button.close", "Close" },
        {"label.search", "Log search" },
        {"label.info", "<html>Please enter a full AS2 message id or a full MDN id into the search field to find all log entries in the server side log files for the defined date period - then press the \"Log search\" button. The user defined id could be set for each transaction using the command line send process.</html>" },
        {"textfield.preset", "AS2 message id, MDN id or userdefined id to search for" },
        {"label.messageid", "AS2 message id" },
        {"label.mdnid", "MDN id" },
        {"label.uid", "Userdefined id" },
        {"problem.serverside", "A problem occured on the server during the search process: [{0}] {1}" },
    };
}
