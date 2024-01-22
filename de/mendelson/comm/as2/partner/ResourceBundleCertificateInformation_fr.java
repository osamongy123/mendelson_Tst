//$Header: /as2/de/mendelson/comm/as2/partner/ResourceBundleCertificateInformation_fr.java 4     6/21/18 5:22p Heller $
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
 * @version $Revision: 4 $
 */
public class ResourceBundleCertificateInformation_fr extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"localstation.decrypt", "Les messages entrants pour la station locale \"{0}\" vont être déchiffrées en utilisant le certificat \"{1}\"."},
        {"localstation.sign", "Messages sortants de la station locale \"{0}\" seront signés à l'aide du certificat \"{1}\"."},
        {"partner.encrypt", "Les messages sortants au partenaire \"{0}\" seront chiffrées à l'aide du certificat \"{1}\"."},
        {"partner.sign", "Signatures de message entrant provenant du partenaire \"{0}\" seront vérifiées à l'aide du certificat \"{1}\"."},        
    };
}
