//$Header: /as2/de/mendelson/comm/as2/message/loggui/ResourceBundleMessageDetails.java 21    1/09/22 14:19 Heller $
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
 * @version $Revision: 21 $
 */
public class ResourceBundleMessageDetails extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        
        {"title", "Message details" },
        {"title.cem", "Certificate Exchange Message details (CEM)" },
        {"transactionstate.ok.send", "<HTML>The {0} message has been succesfully sent to your partner \"{1}\", he acknowledged that everything was fine.</HTML>"},
        {"transactionstate.ok.receive", "<HTML>The {0} message has been received from your partner \"{1}\", your system acknowledged that everything was fine.</HTML>"},
        {"transactionstate.ok.details", "<HTML>The data has been transfered and the transaction has been finished successfully</HTML>" },
        {"transactionstate.error.unknown", "An unknown error occured." },
        {"transactionstate.error.out", "<HTML>You sent the {0} message successfully to your partner \"{1}\" but he was unable to process it and he answered with the error state [{2}]</HTML>" },
        {"transactionstate.error.in", "<HTML>You received a {0} message from your partner \"{1}\" but was unable to process it and answered him with the error state [{2}]</HTML>" },
        {"transactionstate.error.unknown-trading-partner", "<HTML>You and your partner have different AS2 ids for the partner of the relationship in the system configuration. The following ids have been used: \"{0}\" (message sender), \"{1}\" (message receiver)</HTML>" },
        {"transactionstate.error.authentication-failed", "<HTML>The AS2 message receiver was unable to verify the senders signature in the transmitted AS2 data. This is mainly a configuration problem as sender and receiver have to use the same certificate for this process. Please also have a look at the MDN details found in the log - this might contain further information.</HTML>" },
        {"transactionstate.error.decompression-failed", "<HTML>The message receiver was unable to decompress the transmitted message</HTML>" },
        {"transactionstate.error.insufficient-message-security", "<HTML>The message receiver expected a higher security level for the transmitted AS2 message (e.g. encrypted data instead of raw data transmission)</HTML>" },
        {"transactionstate.error.unexpected-processing-error", "<HTML>This is a very generic error. The message receiver was unable to process the received AS2 message for unknown reason.</HTML>" },
        {"transactionstate.error.decryption-failed", "<HTML>The message receiver was unable to decrypt the AS2 message. Looks like the message sender used the wrong certificate to encrypt?</HTML>" },
        {"transactionstate.error.connectionrefused", "<HTML>Your system tried to connect the partner system but was unable to reach it or your partner system did not answer with an acknowledgement in the defined time</HTML>" },
        {"transactionstate.error.connectionrefused.details", "<HTML>This might be an infrastructure problem, your partners system is not running or you entered the wrong receipt URL for this partner? It is also possible that your system transmitted the message successfully and your partner did not answer with an acknowledgement in the defined time. Perhaps you defined the time frame for the answer too short?</HTML>" },
        {"transactionstate.pending", "This transaction is in pending state." },
        {"transactionstate.error.asyncmdnsend", "<HTML>A message with an async MDN request has been received and has been processed successfully but your system was not able to send back the async MDN or it was not accepted by the partner system</HTML>" },
        {"transactionstate.error.asyncmdnsend.details", "<HTML>The AS2 message sender transmits the URL where to send back the MDN - either this system is not reachable (infrastructure problem or the partner system is down?) or the partner system did not accept the async MDN and answered with a HTTP 400.</HTML>" },
        {"transactiondetails.outbound.secure", "This is an outbound secure connection, you send data to the partner \"{0}\"." },
        {"transactiondetails.outbound.insecure", "This is an outbound unsecured connection, you send data to the partner \"{0}\"." },
        {"transactiondetails.inbound.secure", "This is an incoming secure connection, you receive data from the partner \"{0}\"." },
        {"transactiondetails.inbound.insecure", "This is an incoming unsecured connection, you receive data from the partner \"{0}\"." },
        {"transactiondetails.outbound.sync", " You receive the confirmation directly as a response on the backchannel of your outgoing connection (synchronous MDN)." },
        {"transactiondetails.outbound.async", " Your partner establishes a new connection to you for confirmation (asynchronous MDN)." },
        {"transactiondetails.inbound.sync", " You send the confirmation directly as a response on the backchannel of the incoming connection (synchronous MDN)." },
        {"transactiondetails.inbound.async", " You send the confirmation by establishing a new connection to the partner (asynchronous MDN)." },        
        {"button.ok", "Ok" },
        {"header.timestamp", "Date" },
        {"header.messageid", "Ref No" },
        {"message.raw.decrypted", "Raw data (unencrypted)" },
        {"message.header", "Message header" },
        {"message.payload", "Transfered payload" },
        {"message.payload.multiple", "Payload ({0})" },
        {"tab.log", "Log of this message instance" },
        {"header.encryption", "Encryption" },
        {"header.signature", "Signature" },
        {"header.senderhost", "Sender" },
        {"header.useragent", "AS2 server" },
    };
    
}