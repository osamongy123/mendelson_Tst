//$Header: /as2/de/mendelson/util/security/cert/gui/ResourceBundleCertificateReference.java 2     7/04/22 10:33 Heller $
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
 * @version $Revision: 2 $
 */
public class ResourceBundleCertificateReference extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        
        {"title", "Use of certificate" },  
        {"button.ok", "Ok" },
        {"label.info.certificate", "<HTML>The certificate with the alias <strong>{0}</strong> is in use by following partners</HTML>" },
        {"label.info.key", "<HTML>The private key with the alias <strong>{0}</strong> is in use by following partners</HTML>" },
        {"label.notinuse.key", "<HTML>The private key with the alias <strong>{0}</strong> is not assigned to a partner in the configuration</HTML>" },
        {"label.notinuse.certificate", "<HTML>The certificate key with the alias <strong>{0}</strong> is not assigned to a partner in the configuration</HTML>" },
    };
    
}