//$Header: /as2/de/mendelson/util/security/cert/gui/keygeneration/ResourceBundleDialogSubjectAlternativeNames.java 3     4/06/18 1:35p Heller $
package de.mendelson.util.security.cert.gui.keygeneration;

import de.mendelson.util.MecResourceBundle;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * ResourceBundle to localize the converter IDE internal editor frame - if you want to
 * localize eagle to your language, please contact us: localize@mendelson.de
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class ResourceBundleDialogSubjectAlternativeNames extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {    
        {"title", "Manage Subject Alternative Name for the key" },
        {"info", "In this dialog it is possible to manage the subject alternative names for the key generation process. These values are an extension to the x.509 certificate. It allows for example to use multiple domains for a single certificate if your partner accepts this. In OFPT2 it might also be required by your partner to set up your Odette id as URL in the format \"oftp://OdetteId\" and again your domain in the field DNS-Name." },
        {"button.ok", "Ok" },
        {"button.cancel", "Cancel" },
        {"label.add", "Add" },
        {"label.del", "Del" },
        {"header.name", "Type" },
        {"header.value", "Value" },
    };
    
}