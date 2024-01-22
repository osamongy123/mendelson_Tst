//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ResourceBundleExecuteMoveToDir_fr.java 1     4.09.20 15:33 Heller $
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
public class ResourceBundleExecuteMoveToDir_fr extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"executing.receipt", "[Post-traitement] ({0} --> {1}) Exécution d''un événement de post-traitement après réception." },
        {"executing.send", "[Post-traitement] ({0} --> {1}) Exécution d''un événement de post-traitement après l''envoi." },
        {"executing.targetdir", "[Post-traitement] Répertoire cible: \"{0}\"." },
        {"executing.movetodir", "[Post-traitement] Déplacement de \"{0}\" à \"{1}\"." },
        {"executing.movetodir.success", "[Post-traitement] Fichier déplacé avec succès." },
        {"messageid.nolonger.exist", "[Post-traitement] Impossible d'exécuter un événement de post-traitement pour le message \"{0}\" - ce message n'existe plus. Sauter.." },
    };
    
}