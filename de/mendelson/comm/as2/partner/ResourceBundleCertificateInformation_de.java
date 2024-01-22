//$Header: /as2/de/mendelson/comm/as2/partner/ResourceBundleCertificateInformation_de.java 3     6/21/18 5:22p Heller $
package de.mendelson.comm.as2.partner;

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
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class ResourceBundleCertificateInformation_de extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"localstation.decrypt", "Eingehende Nachrichten für die lokale Station \"{0}\" werden mit Hilfe des Zertifikats \"{1}\" entschlüsselt."},
        {"localstation.sign", "Ausgehende Nachrichten der lokalen Station \"{0}\" werden über das Zertifikat \"{1}\" digital signiert."},
        {"partner.encrypt", "Ausgehende Nachrichten an den Partner \"{0}\" werden mit Hilfe des Zertifikats \"{1}\" verschlüsselt."},
        {"partner.sign.prio", "Die digitale Signatur eingehender Nachrichten des Partners \"{0}\" werden mit Hilfe des Zertifikats \"{1}\" überprüft."},
    };
}
