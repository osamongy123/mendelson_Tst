//$Header: /as4/de/mendelson/util/clientserver/connectiontest/ResourceBundleConnectionTest_fr.java 8     20/06/22 11:22 Heller $
package de.mendelson.util.clientserver.connectiontest;

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
 * @version $Revision: 8 $
 */
public class ResourceBundleConnectionTest_fr extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"tag", "Test de connexion sur {0}"},
        {"timeout.set", "Réglage du délai d''attente sur {0}ms"},
        {"test.start.ssl", "Démarrer la vérification de la connexion sur {0} à l''aide de la connexion SSL/TLS. N''oubliez pas que pendant ce test, votre client fera confiance à tous les certificats de serveur et ignorera votre keystore TLS - ce qui signifie qu''il n''est pas assuré que votre keystore TLS est configuré correctement même si ce test est réussi."},
        {"test.start.plain", "Démarrage du contrôle de connexion sur {0} à l''aide de la connexion PLAIN....."},
        {"connection.problem", "Impossible d''atteindre {0} - problème d'infrastructure ou mauvaise adresse saisie"},
        {"connection.success", "Connexion à {0} établie avec succès"},
        {"exception.occured", "Une exception s''est produite pendant le test de connexion : [{0}] {1}"},
        {"exception.occured.oftpservice", "Le système est incapable d''identifier un système OFTP2 en cours d'exécution à l''adresse et au port souhaités. Cela peut être un problème temporaire - cela signifie qu''il est possible que les paramètres d'adresse soient corrects et que le serveur OFTP2 distant ne fonctionne pas actuellement ou qu''il existe un problème d''infrastructure temporaire au niveau de vos partenaires. L''exception suivante s'est produite: [{0}] {1}"},
        {"remote.service.identification", "Identification du service à distance: \"{0}\""},
        {"service.found.success", "Le succès: Exécution du service OFTP trouvé à {0}."},
        {"service.found.failure", "Echec: Aucun service OFTP en cours d''exécution trouvé à {0}."},
        {"wrong.protocol", "Le protocole trouvé est\"{0}\", ce n'est pas une connexion sécurisée. "
            + "Vous avez essayé de vous connecter via [{1}] à cette adresse mais ce n''est pas fourni par le serveur distant à cette adresse et à ce port."},
        {"wrong.protocol.hint", "Soit votre partenaire s''attend à une connexion simple, soit il utilise le mauvais protocole, soit l''authentification du client est requise."},
        {"protocol.information", "Le protocole utilisé a été identifié comme suit \"{0}\""},
        {"requesting.certificates", "Demande de certificat(s) à un hôte distant"},
        {"certificates.found", "{0} certificats ont été trouvés et téléchargés"},
        {"certificates.found.details", "Certificat [{0}/{1}]: {2}"},
        {"check.for.service.oftp2", "Vérifier le fonctionnement du service OFTP2...."},
        {"certificate.ca", "CA Certificat"},
        {"certificate.enduser", "Certificat d''utilisateur final"},
        {"certificate.selfsigned", "Auto-signé"},
        {"certificate.does.not.exist.local", "Ce certificat n''existe pas dans votre keystore TLS/SSL local - veuillez l''importer."},
        {"certificate.does.exist.local", "Ce certificat existe dans votre keystore TLS/SSL local, l''alias est \"{0}\""},
        {"test.connection.direct", "Une connexion IP directe est utilisée"},
        {"test.connection.proxy.auth", "La connexion utilise le proxy {0} avec authentification (Utilisateur \"{1}\")"},
        {"test.connection.proxy.noauth", "La connexion utilise le proxy {0} sans authentification"},
        {"result.exception", "L''erreur suivante s''est produite pendant le test :{0}."},
        {"info.protocols", "Le client permet la négociation via les protocoles TLS suivants: {0}" },
        {"info.securityprovider", "Fournisseur de sécurité TLS utilisé: {0}" },
        {"sni.extension.set", "L''extension TLS SNI (nom de l''hôte) a été fixée à \"{0}\"" },
        {"local.station", "Station locale" },
    };

}
