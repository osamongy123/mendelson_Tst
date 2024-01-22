//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleAS2Server_fr.java 18    29/04/22 17:03 Heller $
package de.mendelson.comm.as2.server;

import de.mendelson.comm.as2.AS2ServerVersion;
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
 * @version $Revision: 18 $
 */
public class ResourceBundleAS2Server_fr extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"fatal.limited.strength", "La force principale limitée a été détectée dans le JVM. Veuillez installer le \"Unlimited jurisdiction key strength policy\" dossiers avant de courir le serveur " + AS2ServerVersion.getProductName() + "." },
        {"server.willstart", "{0} commence maintenant"},
        {"server.start.details", "{0} paramètre:\n\nDémarrer le serveur HTTP intégré: {1}\nAutoriser les connexions client-serveur à partir d''autres hôtes: {2}\nMémoire: {3}\nVersion Java : {4}\nUtilisateur du système: {5}"},
        {"server.started", "Démarrage du " + AS2ServerVersion.getFullProductName() + " dans {0} ms."},
        {"server.already.running", "Une instance de " + AS2ServerVersion.getProductName() + " semble déjà en cours.\nIl est aussi possible qu''une instance précédente du programme ne s''est pas terminée correctement. Si vous êtes sûr qu''aucune autre instance n''est en cours\nmerci de supprimer le fichier de lock \"{0}\" (Date de démarrage {1}) et redémarrer le serveur."},
        {"server.nohttp", "Le HTTP serveur intégré n''a pas été commencé." },  
        {"server.startup.failed", "Il y a eu un problème lors du démarrage du serveur - le démarrage a été interrompu." },
        {"server.shutdown", "{0} est en train de s''éteindre." },
        {"bind.exception", "{0}\nVous avez défini un port qui est actuellement utilisé dans votre système par un autre processus. Il peut s''agir du port client-serveur ou du port HTTP/S que vous avez défini dans la configuration HTTP.\nVeuillez modifier votre configuration ou arrêter l''autre processus avant d'utiliser le {1}."},
         {"server.started.issues", "Avertissement: Des problèmes de configuration ont été trouvés {0} lors du démarrage du serveur." },
        {"server.started.issue", "Avertissement: Un problème de configuration a été détecté lors du démarrage du serveur." },
        {"ha.notavailable.subject", "Le mode haute disponibilité n''est pas autorisé" },
        {"ha.notavailable.body", "Veuillez obtenir une licence pour le plugin HA (haute disponibilité) pour utiliser cette fonctionnalité." },
        {"server.hello", "C''est {0}" },
        {"server.hello.licenseexpire", "La licence expire dans {0} jours ({1}). Vous devez renouveler la licence via le support mendelson (service@mendelson.de) si vous souhaitez continuer à l''utiliser par la suite." },
         {"server.hello.licenseexpire.single", "La licence expire dans {0} jour ({1}). Vous devez renouveler la licence via le support mendelson (service@mendelson.de) si vous souhaitez continuer à l''utiliser par la suite." },
    };
}
