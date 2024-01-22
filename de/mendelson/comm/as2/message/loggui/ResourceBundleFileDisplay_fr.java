//$Header: /as2/de/mendelson/comm/as2/message/loggui/ResourceBundleFileDisplay_fr.java 4     4/06/18 1:35p Heller $
package de.mendelson.comm.as2.message.loggui;
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
 * @author E.Pailleau
 * @version $Revision: 4 $
 */
public class ResourceBundleFileDisplay_fr extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        
        {"no.file", "** AUCUNE DONNEE DISPONIBLE **" },
        {"file.notfound", "** LE FICHIER {0} N''EST PLUS DISPONIBLE **" },
        {"file.tolarge", "** {0}: LA TAILLE DES DONNEES EST TROP GRANDE POUR ETRE AFFICHEE **" },
    };
    
}
