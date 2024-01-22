//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleAS2ServerProcessing_fr.java 13    16/06/22 13:39 Heller $
package de.mendelson.comm.as2.server;

import de.mendelson.util.MecResourceBundle;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize a mendelson product
 *
 * @author S.Heller
 * @version $Revision: 13 $
 */
public class ResourceBundleAS2ServerProcessing_fr extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"send.failed", "Send a échoué"},
        {"unable.to.process", "Impossible de traiter sur serveur : {0}"},
        {"server.shutdown", "L''utilisateur {0} demande l''arrêt du serveur."},
        {"sync.mdn.sent", "MDN synchrone envoyé comme réponse au message {0}." },
        {"info.mdn.inboundfiles", "Pour le MDN a reçu il n'a pas été possible de déterminer le message AS2 référencé.\n[Commentaires reçus MDN: {0}]\n[Commentaires reçus MDN (Header): {1}]"},
        {"message.resend.oldtransaction", "Cette transaction a été envoyée à nouveau manuellement avec le nouveau numéro de transaction [{0}]." },
        {"message.resend.newtransaction", "Cette transaction est un renvoi de la transaction [{0}]." },    
        {"message.resend.title", "Envoi manuel des données dans la nouvelle transaction" },    
        {"local.station", "Station locale" },
    };
}
