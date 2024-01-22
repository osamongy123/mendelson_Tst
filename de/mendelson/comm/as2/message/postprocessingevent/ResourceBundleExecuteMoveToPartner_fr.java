//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ResourceBundleExecuteMoveToPartner_fr.java 1     7.09.20 13:58 Heller $
package de.mendelson.comm.as2.message.postprocessingevent;
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
 * @version $Revision: 1 $
 */
public class ResourceBundleExecuteMoveToPartner_fr extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"executing.receipt", "[Post-traitement] ({0} --> {1}) Exécuter l''événement après la réception." },
        {"executing.send", "[Post-traitement] ({0} --> {1}) Exécution de l'événement après l'envoi." },
        {"targetpartner.does.not.exist", "[Post-traitement] Le partenaire cible avec l'identification AS2 \"{0}\" n''existe pas dans le système..sauter l''exécution d''un événement" },
        {"executing.targetpartner", "[Post-traitement] Partenaires cibles: \"{0}\"." },
        {"executing.movetopartner", "[Post-traitement] Transférer le message du fichier \"{0}\" au partenaire de destination \"{1}\"." },
        {"executing.movetopartner.success", "[Post-traitement] L''ordre d''expédition a été créé avec succès (\"{0}\")." },
        {"messageid.nolonger.exist", "[Post-traitement] L'événement de post-traitement n'a pas pu être exécuté - le message \"{0}\" n'existe plus dans le système..sauter l''exécution d''un événement" },
    };
    
}