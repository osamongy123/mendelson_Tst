//$Header: /as2/de/mendelson/comm/as2/send/ResourceBundleHttpUploader_fr.java 20    4/08/22 10:44 Heller $
package de.mendelson.comm.as2.send;
import de.mendelson.util.MecResourceBundle;

/**
 * ResourceBundle to localize a mendelson product
 * @author S.Heller
 * @author E.Pailleau
 * @version $Revision: 20 $
 */
public class ResourceBundleHttpUploader_fr extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"returncode.ok", "Message envoyé avec succès (HTTP {0}); {1} transféré en {2} [{3} KB/s]." },
        {"returncode.accepted", "Message envoyé avec succès (HTTP {0}); {1} transféré en {2} [{3} KB/s]." },
        {"sending.msg.sync", "Envoi du AS2 message vers {0}, sync MDN demandé." },
        {"sending.cem.sync", "Envoi du CEM message vers {0}, sync MDN demandé." },
        {"sending.msg.async", "Envoi du AS2 message vers {0}, async MDN demandé vers {1}." },
        {"sending.cem.async", "Envoi du CEM message vers {0}, async MDN demandé vers {1}." },
        {"sending.mdn.async", "Envoi d''un MDN asynchrone vers {0}." },
        {"error.httpupload", "La transmission a echouée, le serveur AS2 distant signale \"{0}\"." },
        {"error.noconnection", "Problème de connexion, données non transmises." },
        {"error.http502", "Problème de connexion, données non transmises. (HTTP 502 - BAD GATEWAY)" },
        {"error.http503", "Problème de connexion, données non transmises. (HTTP 503 - SERVICE UNAVAILABLE)" },
        {"error.http504", "Problème de connexion, données non transmises. (HTTP 504 - GATEWAY TIMEOUT)" },
        {"using.proxy", "Utiliser le proxy {0}:{1}." },
        {"using.proxy.auth", "Utiliser le proxy {0}:{1} (authentification en tant que {2})." },
        {"answer.no.sync.mdn", "La synchronisation MDN reçue ne semble pas être au bon format. Comme les problèmes de structure MDN sont rares, il se peut que ce n''est pas une réponse du système AS2 que vous vouliez aborder, mais peut-être un proxy ou un site web standard? Les valeurs d'en-tête HTTP suivantes sont manquantes: [{0}].\nLes données reçues commencent par les structures suivantes: \n{1}" },
        {"answer.no.sync.empty", "L''accusé de réception synchrone (MDN) reçu est vide. Il y a probablement eu un problème lors du traitement des messages AS2 de la part de votre partenaire - veuillez contacter votre partenaire en conséquence." },
        {"hint.SSLPeerUnverifiedException", "Indice:\nCette est un problème qui est survenue au cours de la négociation SSL. Le système a été incapable d''établir une connexion sécurisée avec votre partenaire, ce problème n''est pas lié protocole AS2.\nVeuillez vérifier les points suivants à corriger cette question:\n* Avez-vous importé toutes vos partenaires certificats SSL dans votre magasin de clés SSL (certificats root/intermédiaires)\n*Votre partenaire importé tous vos certificats SSL dans son magasin de clés (Les certificats root/intermédiaire)? "},
        {"hint.ConnectTimeoutException", "Indice:\nCeci est un problème essentiellement d''une infrastructure qui est pas le protocole AS2 connexes. Le système n'a pas pu établir une connexion sortante à votre système partenaires AS2.\nPlease check the following to fix this issue:\n*S'il vous plaît vérifier les points suivants pour résoudre ce problème?\n*S''il vous plaît Vérifier l''URL de la réception de votre partenaire, est-il une faute de frappe?\n*S''il vous plaît communiquer avec votre partenaire, peut-être son système de AS2 est en baisse?" },
        {"hint.SSLException", "Indice:\nC''est principalement un problème de négociation au niveau du protocole. Votre partenaire a rejeté votre connexion.\nSoit votre partenaire s''attendait à une connexion sécurisée (HTTPS) et vous avez essayé une connexion non sécurisée (HTTP) ou vice versa.\nIl est également possible que votre partenaire s'attende à une autre version du protocole SSL/TLS ou à un autre chiffrement que celui que vous proposez." },
        {"hint.httpcode.signals.problem", "Indice:\nUne connexion a été établie avec l''hôte de vos partenaires - un serveur web s''y trouve. Le serveur distant signale que quelque chose ne va pas avec le chemin de requête ou le port et renvoie le code HTTP {0}.\nVeuillez vous référer à un moteur de recherche Internet si vous souhaitez plus d''informations sur ce code HTTP." },
        {"trust.all.server.certificates", "La connexion TLS sortante fera confiance à tous les certificats du serveur d''extrémité distant si les certificats racine et intermédiaire sont disponibles." },
        {"strict.hostname.check", "Lors de la connexion TLS sortante, un contrôle strict du nom d''hôte est effectué en ce qui concerne le certificat du serveur." },
        {"strict.hostname.check.skipped.selfsigned", "TLS: La vérification stricte du nom d''hôte a été ignorée - le serveur distant utilise un certificat self signed." },
    };
    
}
