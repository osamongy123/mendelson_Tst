//$Header: /as2/de/mendelson/comm/as2/partner/ResourceBundleCertificateUsedByPartnerChecker.java 4     7/04/22 10:33 Heller $ 
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
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class ResourceBundleCertificateUsedByPartnerChecker extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"used.crypt", "data encryption"},
        {"used.sign", "data signature"},};

}
