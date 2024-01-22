//$Header: /as2/de/mendelson/comm/as2/sendorder/ResourceBundleSendOrderReceiver_fr.java 8     9/02/22 13:26 Heller $
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
 * @version $Revision: 8 $
 */
public class ResourceBundleSendOrderReceiver_fr extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"async.mdn.wait", "Attente du MDN asynchrone jusqu''à {0}." },
        {"max.retry.reached", "Le maximum a été atteint réessayer ({0}), la transmission annulée." },
        {"retry", "Va réessayer d''envoyer transmission après {0}s, réessayez {1}/{2}." },
        {"as2.send.disabled", "** Le système ne sera pas envoyer de message AS2/MDN parce que le nombre de connexions sortantes parallèles est mis à 0. S''il vous plaît modifier ces paramètres dans la boîte de dialogue de configuration du serveur pour permettre l''envoi de nouveau **" },        
        {"outbound.connection.prepare.mdn", "Préparer la connexion MDN sortante vers \"{0}\", connexions actives: {1}/{2}." },
        {"outbound.connection.prepare.message", "Préparer la connexion AS2 message sortante vers \"{0}\", Connexions actives: {1}/{2}." },
        {"send.connectionsstillopen", "Vous avez réduit le nombre de connexions sortantes à {0}, mais actuellement, il y a encore {1} connexions sortantes." },
    };
    
}
