//$Header: /as2/de/mendelson/util/security/cert/gui/ResourceBundleCertificates_fr.java 22    12/12/22 14:18 Heller $
package de.mendelson.util.security.cert.gui;

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
 *
 * @author S.Heller
 * @author E.Pailleau
 * @version $Revision: 22 $
 */
public class ResourceBundleCertificates_fr extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"display.ca.certs", "Afficher les certificats CA"},
        {"button.delete", "Suppression clef/certificat"},
        {"button.delete.all.expired", "Supprimer toutes les clés/certificats expirés" },
        {"button.edit", "Renommer l''alias"},
        {"button.newkey", "Importer clef"},
        {"button.newcertificate", "Importer certificat"},
        {"button.export", "Exporter certificat"},
        {"button.reference", "Afficher l''utilisation" },  
        {"button.keycopy", "Copier vers l''administration {0}" },
        {"button.keycopy.tls", "TLS" },
        {"button.keycopy.signencrypt", "Cryptage/Signature" },
        {"menu.file", "Fichier"},
        {"menu.file.close", "Fermer"},
        {"menu.import", "Importer"},
        {"menu.export", "Export"},
        {"menu.tools", "Tools"},
        {"menu.tools.generatekey", "Générer une nouvelle clé (Self signed)"},
        {"menu.tools.generatecsr", "Confiance au certificat: Générer la CSR (en CA)"},
        {"menu.tools.generatecsr.renew", "Renouveler le certificat: Générer la CSR (en CA)"},
        {"menu.tools.importcsr", "Confiance au certificat: Réponse de CAs en matière de CSR d''importation"},
        {"menu.tools.importcsr.renew", "Renouveler le certificat: Réponse de CAs en matière de CSR d''importation"},
        {"label.selectcsrfile", "Veuillez sélectionner le fichier pour enregistrer la CSR"},
        {"label.cert.import", "Importer certificat (de votre partenaire commercial)"},
        {"label.cert.export", "Exporter certificat (pour votre partenaire commercial)"},
        {"label.key.import.pem", "Importer votre propre clef privée (depuis un PEM)"},
        {"label.key.import.pkcs12", "Importer votre propre clef privée (depuis un PKCS#12)"},
        {"label.key.import.jks", "Importer votre propre clef privée (depuis un JKS, format porte-clef JAVA)"},
        {"label.key.export.pkcs12", "Exporter votre propre clef privée (PKCS#12) (pour sauvegarde seulement !)"},
        {"label.keystore", "Dossier de keystore:"},
        {"title.signencrypt", "Certificats et clefs disponibles (encryption, signature)"},
        {"title.ssl", "Certificats et clefs disponibles (SSL/TLS)"},
        {"button.ok", "Valider"},
        {"button.cancel", "Annuler"},
        {"filechooser.certificate.import", "Merci de sélectionner le fichier certificat pour l''import"},
        {"certificate.import.success.message", "Le certificat a été importé avec succès ({0})."},
        {"certificate.ca.import.success.message", "Le certificat a été importé avec succès (CA, {0})."},
        {"certificate.import.success.title", "Succès"},
        {"certificate.import.error.message", "Une erreur a eu lieu lors du processus d''import.\n{0}"},
        {"certificate.import.error.title", "Erreur"},
        {"certificate.import.alias", "Alias de certificat à utiliser:"},
        {"keystore.readonly.message", "Le porte-clef est en lecture seule.\nToute opération d'écriture de certificat (modifier/éditer) n'est pas possible dans cet état."},
        {"keystore.readonly.title", "Porte-clef r/o"},
        {"modifications.notalllowed.message", "Modifications ne sont pas possibles"},
        {"generatekey.error.message", "{0}"},
        {"generatekey.error.title", "Erreur lors de la génération de clés"},
        {"tab.info.basic", "Base"},
        {"tab.info.extension", "Extension"},
        {"tab.info.trustchain", "Chaîne de confiance"},        
        {"dialog.cert.delete.message", "Vous voulez vraiment supprimer le certificat avec le \"{0}\" alias?"},
        {"dialog.cert.delete.title", "Supprimer le certificat"},
        {"title.cert.in.use", "Le certificat est en cours d'utilisation"},
        {"cert.delete.impossible", "L''entrée ne peut pas être supprimée, elle est utilisée.\nVeuillez utiliser \"Afficher l''utilisation\" pour plus d''informations"},
        {"module.locked", "Cette gestion des certificats est verrouillé par un autre client, vous n'êtes pas autorisé à valider vos modifications!"},
        {"label.trustanchor", "Trust anchor:" },
        {"warning.testkey", "Touche de test mendelson accessible au public - ne l''utilisez pas en mode productif!" },        
        {"label.key.valid", "Cette clé est invalide" },
        {"label.key.invalid", "Cette clé est valide" },
        {"label.cert.valid", "Ce certificat est valid" },
        {"label.cert.invalid", "Ce certificat est invalid" },
        {"warning.deleteallexpired.text", "Voulez-vous vraiment supprimer les entrées expirées et inutilisées {0}?" },
        {"warning.deleteallexpired.title", "Supprimer toutes les clés/certificats expirés et non utilisés" },
        {"warning.deleteallexpired.noneavailable.title", "Aucun disponible" },
        {"warning.deleteallexpired.noneavailable.text", "Il n''y a pas d'entrées expirées et inutilisées à supprimer" },
        {"success.deleteallexpired.title", "Il n''y a pas d'entrées expirées et inutilisées à supprimer" },
        {"success.deleteallexpired.text", "{0} clés/certificats expirés et non utilisés ont été retirés" },
        {"warning.deleteallexpired.expired.but.used.title", "Clés/certificats utilisés non effacés" },
        {"warning.deleteallexpired.expired.but.used.text", "Les clés/certificats sont expirés mais toujours utilisés - le système les conservera" },
        {"module.locked.title", "Module est en cours d''utilisation" },
        {"module.locked.text", "Le module {0} est utilisé exclusivement par un autre client ({1})." },  
        {"keycopy.target.exists.title", "L''entrée existe déjà dans la cible"},
        {"keycopy.target.exists.text", "Cette entrée existe déjà dans la gestion des certificats cibles (alias {0})."},
        {"keycopy.target.ro.title", "Cible en lecture seule" },
        {"keycopy.target.ro.text", "Échec de l''opération - Le fichier clé de la cible est en lecture seule." },
        {"keycopy.success.text", "L''entrée [{0}] a été copiée avec succès" },
    };
}
