//$Header: /as2/de/mendelson/comm/as2/send/ResourceBundleHttpUploader.java 28    4/08/22 10:44 Heller $
package de.mendelson.comm.as2.send;
import de.mendelson.util.MecResourceBundle;

/**
 * ResourceBundle to localize a mendelson product
 * @author S.Heller
 * @version $Revision: 28 $
 */
public class ResourceBundleHttpUploader extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"returncode.ok", "Message sent successfully (HTTP {0}); {1} transfered in {2} [{3} KB/s]." },
        {"returncode.accepted", "Message sent successfully (HTTP {0}); {1} transfered in {2} [{3} KB/s]." },
        {"sending.msg.sync", "Sending AS2 message to {0}, sync MDN requested." },
        {"sending.cem.sync", "Sending CEM message to {0}, sync MDN requested." },
        {"sending.msg.async", "Sending AS2 message to {0}, async MDN requested at {1}." },
        {"sending.cem.async", "Sending CEM message to {0}, async MDN requested at {1}." },
        {"sending.mdn.async", "Sending async MDN to {0}." },  
        {"error.httpupload", "Transmission failed, remote AS2 server reports \"{0}\"." },
        {"error.noconnection", "Connection problem, failed to transmit data." },
        {"error.http502", "Connection problem, failed to transmit data (HTTP 502 - BAD GATEWAY)" },
        {"error.http503", "Connection problem, failed to transmit data (HTTP 503 - SERVICE UNAVAILABLE)" },
        {"error.http504", "Connection problem, failed to transmit data (HTTP 504 - GATEWAY TIMEOUT)" },
        {"using.proxy", "Using proxy {0}:{1}." },
        {"using.proxy.auth", "Using proxy {0}:{1} (authenticating as {2})." },
        {"answer.no.sync.mdn", "The received sync MDN seems not to be in right format. As MDN structure problems are uncommon it might be that this is no answer of the AS2 system you wanted to address but perhaps a proxy or a standard web site? The following HTTP header values are missing: [{0}].\nThe returned data starts with the following :\n{1}" },
        {"answer.no.sync.empty", "The received synchronous acknowledgement (MDN) is empty. There must have been a problem processing the AS2 messages on your partners side - please contact your partner accordingly." },
        {"hint.SSLPeerUnverifiedException", "Hint:\nThis is a problem that occured during the SSL handshake. The system was unable to establish a secure connection to your partner, this problem is not AS2 protocol related.\nPlease check the following to fix this issue:\n*Have you imported all your partners SSL certificates into your SSL keystore (incl. root/intermediate certificates)?\n*Has your partner imported all your certificates into his SSL keystore (incl. root/intermediate certificates)?" },
        {"hint.ConnectTimeoutException", "Hint:\nThis is a mainly an infrastructure problem which is not AS2 protocol related. The system was unable to establish an outbound connection to your partners AS2 system.\nPlease check the following to fix this issue:\n*Do you have an active internet connection?\n*Please recheck the receipt URL of your partner, is there a typo?\n*Please contact your partner, perhaps his AS2 system is down?" },
        {"hint.SSLException", "Hint:\nThis is a mainly a negotiation problem on the protocol level. Your partner rejected your connection.\nEither your partner expected a secure connection (HTTPS) and you tried a raw connection (HTTP) or vice versa.\nIt is also possible that your partner expects an other SSL/TLS protocol version or cipher than you offer." },
        {"hint.httpcode.signals.problem", "Hint:\nA connection has been established to your partners host - a webserver is running there.\nAnyway the remote server signals that something was wrong with the request path or port and returns the HTTP code {0}.\nPlease refer to an internet search engine if your need more information about this HTTP code." },
        {"trust.all.server.certificates", "The outbound TLS connection will trust all remote end server certificates if the root and intermediate certificates are available." },
        {"strict.hostname.check", "The outbound TLS connection will perform a strict hostname check on the server end certificate." },
        {"strict.hostname.check.skipped.selfsigned", "TLS: The strict hostname check has been skipped, self signed certificate found on remote server." },
    };
    
}