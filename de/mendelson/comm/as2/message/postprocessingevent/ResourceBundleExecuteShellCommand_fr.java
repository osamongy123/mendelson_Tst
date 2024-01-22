//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ResourceBundleExecuteShellCommand_fr.java 3     4.09.20 15:34 Heller $
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
 * @author E.Pailleau
 * @version $Revision: 3 $
 */
public class ResourceBundleExecuteShellCommand_fr extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"executing.receipt", "[Post-traitement] ({0} --> {1}) Exécution d''un événement de post-traitement après réception de la charge utile." },
        {"executing.send", "[Post-traitement] ({0} --> {1}) Exécution d''un événement de post-traitement après l''envoi de la charge utile." },
        {"executing.command", "[Post-traitement] Commande système: \"{0}\"." },
        {"executed.command", "[Post-traitement] Commande système a exporté, returncode={0}." },
        {"messageid.nolonger.exist", "[Post-traitement] Impossible d''exécuter un événement de post-traitement pour le message \"{0}\" - ce message n''existe plus. Sauter.." },
    };
    
}
