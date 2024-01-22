//$Header: /as2/de/mendelson/util/security/cert/gui/ResourceBundleCertificateReference_de.java 1     7/04/22 10:33 Heller $
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
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class ResourceBundleCertificateReference_de extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        
        {"title", "Zertifikatverwendung" },   
        {"button.ok", "Ok" },
        {"label.info.certificate", "<HTML>Das Zertifikat mit dem Alias <strong>{0}</strong> wird von folgenden Partnern verwendet</HTML>" },
        {"label.info.key", "<HTML>Der private Schlüssel mit dem Alias <strong>{0}</strong> wird von folgenden Partnern verwendet</HTML>" },
        {"label.notinuse.key", "<HTML>Der private Schlüssel mit dem Alias <strong>{0}</strong> wird in der Konfiguration von keinem Partner verwendet</HTML>" },
        {"label.notinuse.certificate", "<HTML>Das Zertifikat mit dem Alias <strong>{0}</strong> wird in der Konfiguration von keinem Partner verwendet</HTML>" },
    };
    
}