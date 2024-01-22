//$Header: /as2/de/mendelson/util/security/cert/gui/ResourceBundleInfoOnExternalCertificate_de.java 4     4/06/18 1:35p Heller $ 
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
 * @version $Revision: 4 $
 */
public class ResourceBundleInfoOnExternalCertificate_de extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"button.ok", "Importieren >>"},
        {"button.cancel", "Schliessen"},
        {"title.single", "Information über ein externes Zertifikat"},
        {"title.multiple", "Information über externe Zertifikate"},
        {"certinfo.certfile", "Zertifikatdatei: {0}"},
        {"certinfo.index", "Zertifikat {0} von {1}"},
        {"certificate.exists", "Dieses Zertifikat existiert bereits im Keystore, der Alias ist \"{0}\""},
        {"certificate.doesnot.exist", "Dieses Zertifikat existiert noch nicht im Keystore"},
        {"no.certificate", "Das Zertifikat wurde nicht erkannt" },
    };

}
