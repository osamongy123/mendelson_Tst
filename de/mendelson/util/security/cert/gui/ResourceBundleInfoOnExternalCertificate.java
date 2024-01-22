//$Header: /as2/de/mendelson/util/security/cert/gui/ResourceBundleInfoOnExternalCertificate.java 5     4/06/18 1:35p Heller $ 
package de.mendelson.util.security.cert.gui;

import de.mendelson.util.MecResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize gui entries
 *
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class ResourceBundleInfoOnExternalCertificate extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"button.ok", "Import >>"},
        {"button.cancel", "Close"},
        {"title.single", "Info on external certificate"},
        {"title.multiple", "Info on external certificates"},
        {"certinfo.certfile", "Certificate file: {0}"},
        {"certinfo.index", "Certificate {0} of {1}"},
        {"certificate.exists", "This certificate does already exist in the keystore, alias is \"{0}\""},
        {"certificate.doesnot.exist", "This certificate does not exist in the keystore so far"},
        {"no.certificate", "Unable to identify certificate" },
    };    
}
