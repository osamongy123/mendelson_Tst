//$Header: /as2/de/mendelson/comm/as2/partner/gui/global/ResourceBundleGlobalChange_fr.java 2     26/08/22 14:17 Heller $
package de.mendelson.comm.as2.partner.gui.global;
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
 * @version $Revision: 2 $
 */
public class ResourceBundleGlobalChange_fr extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"title", "Changements globaux pour tous les partenaires" },
        {"button.ok", "Fermer" },    
        {"button.set", "Définir" },
        {"partnersetting.changed", "Changement de réglage pour {0} partenaires." },
        {"partnersetting.notchanged", "Les paramètres n''ont pas été modifiés - valeur erronée" },  
        {"info.text", "<HTML>Cette boîte de dialogue vous permet de définir des valeurs pour les paramètres "
            + "de tous les partenaires en même temps. Si vous avez appuyé sur \"Définir\", la valeur correspondante "
            + "est remplacée pour <strong>TOUS<strong> les partenaires.</HTML>" },
        {"label.dirpoll", "Effectuer un sondage d''annuaire pour tous les partenaires" },
        {"label.maxpollfiles", "Nombre maximum de fichiers de tous les partenaires par processus de polling" },
        {"label.pollinterval", "Répertoire Intervalle de poll de tous les partenaires" },
    };
    
}