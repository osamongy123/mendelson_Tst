//$Header: /as2/de/mendelson/util/clientserver/clients/filesystemview/ResourceBundleFileBrowser.java 3     4/06/18 1:35p Heller $
package de.mendelson.util.clientserver.clients.filesystemview;

import de.mendelson.util.MecResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize the mendelson products - if you want to localize
 * eagle to your language, please contact us: localize@mendelson.de
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class ResourceBundleFileBrowser extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        //dialog
        {"button.ok", "Select"},
        {"button.cancel", "Cancel"},
        {"wait", "Please wait.."},};

}
