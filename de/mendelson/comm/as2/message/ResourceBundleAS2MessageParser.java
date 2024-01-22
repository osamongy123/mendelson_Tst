//$Header: /as2/de/mendelson/comm/as2/message/ResourceBundleAS2MessageParser.java 47    26.08.21 11:30 Heller $
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
 * @version $Revision: 47 $
 */
public class ResourceBundleAS2MessageParser extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {        
        {"mdn.answerto", "The inbound MDN with the message id \"{0}\" is the answer to the outbound AS2 message \"{1}\"." },  
        {"mdn.state", "Inbound MDN state is [{0}]." },          
        {"mdn.details", "Inbound MDN details received from {0}: \"{1}\"" },
        {"mdn.incoming.ha", "Inbound transmission is a MDN, processed by [{0}]." }, 
        {"mdn.incoming", "Inbound transmission is a MDN." },
        {"mdn.incoming.relationship", "Inbound transmission is a MDN [{0}]" },
        {"mdn.incoming.relationship.ha", "Inbound transmission is a MDN [{0}], processed by [{1}]" },
        {"msg.incoming", "Inbound transmission is a AS2 message [{0}], raw message size: {1}." },   
        {"msg.incoming.ha", "Inbound transmission is a AS2 message [{0}], raw message size: {1}, processed by [{2}]." },
        {"msg.incoming.identproblem", "Inbound transmission is a AS2 message. It has not been processed because of a trading partner identification problem." },   
        {"msg.already.processed", "A message with the message id [{0}] has been already processed" },
        {"mdn.signed", "Inbound MDN is signed ({0})." },
        {"mdn.unsigned.error", "Inbound MDN is not signed. The partner configuration defines MDN from the partner \"{0}\" to be signed." },
        {"mdn.signed.error", "Inbound MDN is signed. The partner configuration defines MDN from the partner \"{0}\" to be not signed." },
        {"msg.signed", "Inbound AS2 message is signed." },        
        {"msg.encrypted", "Inbound AS2 message is encrypted." },        
        {"msg.notencrypted", "Inbound AS2 message is not encrypted." },                
        {"msg.notsigned", "Inbound AS2 message is not signed." },                
        {"mdn.notsigned", "Inbound MDN is not signed." },
        {"mdn.signature.ok", "Digital signature of inbound MDN has been verified successful." },
        {"message.signature.ok", "Digital signature of inbound AS2 message has been verified successful." },
        {"message.signature.failure", "Verification of digital signature of inbound AS2 message failed {0}" },
        {"mdn.signature.failure", "Verification of digital signature of inbound MDN failed {0}" },
        {"mdn.signature.using.alias", "Using certificate \"{0}\" to verify inbound MDN signature." }, 
        {"message.signature.using.alias", "Using certificate \"{0}\" to verify inbound AS2 message signature." }, 
        {"decryption.done.alias", "The inbound AS2 message data has been decrypted using the key \"{0}\", the encryption algorithm was \"{1}\", the key encryption algorithm was \"{2}\"." },
        {"mdn.unexpected.messageid", "The inbound MDN references a AS2 message with the message id \"{0}\" that does not exist." },
        {"mdn.unexpected.state", "The inbound MDN references the AS2 message with the message id \"{0}\" that is not waiting for an MDN." },
        {"data.compressed.expanded", "The compressed payload of the inbound AS2 message has been expanded from {0} to {1}." },
        {"found.attachments", "Found {0} payload attachments in the inbound AS2 message." },
        {"decryption.inforequired", "To decrypt the data of the inbound AS2 message a key with the following parameter is required:\n{0}" },
        {"decryption.infoassigned", "A key with the following parameter has been used to decrypt the data of the inbound AS2 message (alias \"{0}\"):\n{1}" },
        {"signature.analyzed.digest", "The sender used the algorithm \"{0}\" to sign the inbound AS2 message." },
        {"signature.analyzed.digest.failed", "The system is unable to find out the sign algorithm of the inbound AS2 message." },
        {"filename.extraction.error", "Unable to extract original filename from the inbound AS2 message: \"{0}\", ignoring filename." },
        {"contentmic.match", "The Message Integrity Code (MIC) matches the sent AS2 message." },
        {"contentmic.failure", "The Message Integrity Code (MIC) does not match the sent AS2 message (required: {0}, returned: {1})." },
        {"found.cem", "The received message is a Certificate Exchange Message (CEM)." },
        {"data.unable.to.process.content.transfer.encoding", "Data has arrived that could not be processed because it contains errors. The defined content transfer encoding \"{0}\" is unknown."},
        {"original.filename.found", "The original payload filename has been transmitted by the message sender as \"{0}\"." },
        {"original.filename.undefined", "The original payload filename has not transmitted by the message sender." },
        {"data.not.compressed", "The inbound AS2 message data is not compressed." },
    };
    
}