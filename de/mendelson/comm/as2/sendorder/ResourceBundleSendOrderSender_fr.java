//$Header: /as2/de/mendelson/comm/as2/sendorder/ResourceBundleSendOrderSender_fr.java 6     6.12.18 16:26 Heller $
package de.mendelson.comm.as2.sendorder;
import de.mendelson.util.MecResourceBundle;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize a mendelson product
 * @author S.Heller
 * @author E.Pailleau
 * @version $Revision: 6 $
 */
public class ResourceBundleSendOrderSender_fr extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"message.packed", "Message AS2 sortant créé avec \"{0}\" du destinataire \"{1}\" dans {3}, taille brute du message: {2}, id défini par l''utilisateur: \"{4}\"" },
        {"sendoder.sendfailed", "Un problème s'est produit lors du traitement d'une commande d'envoi: [{0}] \"{1}\" - les données n''ont pas été transmises au partenaire." },        
    };
    
}
