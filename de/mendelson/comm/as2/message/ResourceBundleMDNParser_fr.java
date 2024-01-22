//$Header: /as2/de/mendelson/comm/as2/message/ResourceBundleMDNParser_fr.java 5     4/06/18 1:35p Heller $
package de.mendelson.comm.as2.message;
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
 * @version $Revision: 5 $
 */
public class ResourceBundleMDNParser_fr extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"invalid.mdn.nocontenttype", "Un MDN entrant est invalide: Aucun type de contenu trouvé" },
        {"structure.failure.mdn", "Un MDN entrant a été analysé et il y a un échec de structure dans le MDN (\"{0}\"). Le MDN est inadmissible et ne pourrait pas être traité, le statut du message AS2/de transaction référencés ne sera pas changé." },
    };
    
}
