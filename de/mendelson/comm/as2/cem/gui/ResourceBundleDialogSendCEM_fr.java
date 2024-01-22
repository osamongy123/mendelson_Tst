//$Header: /as2/de/mendelson/comm/as2/cem/gui/ResourceBundleDialogSendCEM_fr.java 5     6/22/18 1:48p Heller $
package de.mendelson.comm.as2.cem.gui;
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
 * @version $Revision: 5 $
 */
public class ResourceBundleDialogSendCEM_fr extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"title", "Certificat d'échange avec les partenaires via CEM" },
        {"button.ok", "Ok" },
        {"button.cancel", "Annuler" },
        {"label.initiator", "Station locale:" },
        {"label.receiver", "Récepteur:" },
        {"label.certificate", "Certificat:"},
        {"label.activationdate", "Date d''activation:"},
        {"cem.request.failed", "L''échec de la demande CEM:\n{0}" },
        {"cem.request.success", "La demande CEM a été envoyée avec succès." },
        {"cem.request.title", "Échange de certificat via CEM" },
        {"cem.informed", "Le système a tenté d''informer les partenaires suivants via CEM, s''il vous plaît jeter un oeil à la gestion CEM pour voir si cela a été un succès: {0}" },
        {"cem.not.informed", "Les partenaires suivants n'ont pas été informés via CEM, veuillez envoyer le certificat en utilisant un autre canal, par exemple email: {0}" },
        {"partner.all", "--Tous les partenaires--" },
        {"partner.cem.hint", "Les systèmes partenaires doivent supporter CEM pour être listés ici" },
        {"purpose.ssl", "SSL/TLS" },
        {"purpose.encryption", "Codification" },
        {"purpose.signature", "Signature numérique" },
    };
    
}