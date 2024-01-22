//$Header: /as2/de/mendelson/comm/as2/send/ResourceBundleDirPollManager_fr.java 13    19/01/22 16:17 Heller $
package de.mendelson.comm.as2.send;

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
 *
 * @author S.Heller
 * @author E.Pailleau
 * @version $Revision: 13 $
 */
public class ResourceBundleDirPollManager_fr extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"none", "Aucun"},
        {"manager.status.modified", "La surveillance des répertoires a changé, {0} les répertoires sont surveillés."},
        {"poll.stopped", "[Gestionnaire de scrutation des répertoires] Scrutation pour les relations \"{0}/{1}\" stoppé."},
        {"poll.started", "[Gestionnaire de scrutation des répertoires] Scrutation pour les relations \"{0}/{1}\" démarré. Fichiers ignorés: \"{2}\". Intervalle de scrutation: {3}s"},
        {"poll.stopped.notscheduled", "[Gestionnaire de scrutation des répertoires] Le système a essayé d''arrêter la surveillance du répertoire pour \"{0}/{1}\" - mais il n''y a pas eu de surveillance." },
        {"poll.modified", "[Gestionnaire de scrutation des répertoires] Paramètres de partenaire pour la relation \"{0}/{1}\" ont été modifiés."},
        {"warning.noread", "[Gestionnaire de scrutation des répertoires] Pas d''accès en lecture pour le fichier outbox {0}, ignorer."},
        {"warning.ro", "[Gestionnaire de scrutation des répertoires] Le fichier {0} dans la boîte de départ est en lecture seule, ignoré."},
        {"warning.notcomplete", "[Gestionnaire de scrutation des répertoires] {0}: Le dossier d'outbox n'est pas complet jusqu'ici et sera ignoré."},
        {"messagefile.deleted", "Le fichier \"{0}\" a été déplacé dans la queue de messages à traiter par le serveur."},
        {"processing.file", "Traitement du fichier \"{0}\" pour les relations \"{1}/{2}\"."},
        {"processing.file.error", "Erreur de traitement du fichier \"{0}\" pour les relations \"{1}/{2}\": \"{3}\"."},
        {"poll.log.wait", "[Répertoire sondage] {0}->{1}: Suivant processus de sondage sortant dans {2}s ({3})"},
        {"poll.log.polling", "[Répertoire sondage] {0}->{1}: Répertoire de vote \"{2}\""},
        {"title.list.polls.running", "Résumé des annuaires surveillés:"},
        {"title.list.polls.stopped", "Les opérations de surveillance suivantes ont pris fin"},
        {"title.list.polls.started", "Le suivi suivant a été entamé"},};

}
