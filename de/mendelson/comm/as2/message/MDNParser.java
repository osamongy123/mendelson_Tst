//$Header: /as2/de/mendelson/comm/as2/message/MDNParser.java 29    3.08.21 17:48 Heller $
package de.mendelson.comm.as2.message;

import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.MecResourceBundle;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Parses MDNs, this is NOT thread safe!
 *
 * @author S.Heller
 * @version $Revision: 29 $
 */
public class MDNParser {

    private Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    /**
     * Contains the details message of the MDN
     */
    private String mdnDetails;
    private Properties dispositionProperties = new Properties();
    private String dispositionState;
    private MecResourceBundle rb;
    /**
     * contains the parsed MIC is it has been transfered
     */
    private String mic = null;
    /**
     * contains the related message for the MDN, from sync MDN this is a SHOULD
     * value
     */
    private String relatedMessageId = null;

    public MDNParser() {
        //Load resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleMDNParser.class.getName());
        } //load up  resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    /**
     * Checks if the pass raw data is an MDN or a message. It will write a found
     * AS2MDNInfo to the passed message if it is a MDN. Nothing will happen if
     * the found data is a AS2 message - the message object is already
     * instanciated as AS2 message at creation time
     */
    public void parseMDNData(AS2Message message, byte[] data, String contentType) throws Exception {
        //no content type defined? Throw an exception
        if (contentType == null || contentType.trim().length() == 0) {
            throw new Exception(this.rb.getResourceString("invalid.mdn.nocontenttype"));
        }
        //encrypted AS2 message found, MDNs are not encrypted
        if (contentType.startsWith("application/pkcs7-mime")) {
            return;
        }
        ByteArrayInputStream inStream = new ByteArrayInputStream(data);
        MimeMultipart multipart = new MimeMultipart(new ByteArrayDataSource(data, contentType));
        inStream.close();
        MimeMessage messagePart = new MimeMessage(Session.getInstance(System.getProperties(), null));
        messagePart.setContent(multipart, multipart.getContentType());
        messagePart.saveChanges();
        Part reportPart = this.parsePartsForReport(messagePart);
        //it is NO MDN as there is no report part
        if (reportPart == null) {
            return;
        }
        //If the parse process comes to this point it must be a new MDN
        AS2MDNInfo info = new AS2MDNInfo();
        message.setAS2Info(info);
        info.setDirection(AS2MessageInfo.DIRECTION_IN);
        try {
            this.extractMessageDispositionDetailsFromMDN(reportPart);
        } catch (Exception e) {
            //there is a structure problem in the MDN. But perhaps the original message id has been already identified
            //in this case write the original message id to the message info for a better error tracking in the log and to
            //inform the user which related message is affected
            if (this.dispositionProperties.getProperty("original-message-id") != null) {
                info.setRelatedMessageId(this.dispositionProperties.getProperty("original-message-id"));
            }
            throw (e);
        }
        //the MDN parsing process was successful
        this.relatedMessageId = this.dispositionProperties.getProperty("original-message-id");
        info.setRelatedMessageId(this.relatedMessageId);
        //RFC 4130, section 7.4.3
        //The "Received-content-MIC" extension field is set when the integrity of the received
        //message is verified. The MIC is the base64-encoded message-digest computed over the received
        //message with a hash function. This field is required for signed receipts but optional for unsigned receipts.
        //
        //This field will be taken anyway, if it does not exist a null will be taken
        this.mic = this.dispositionProperties.getProperty("received-content-mic");
        info.setReceivedContentMIC(this.mic);
        message.setAS2Info(info);
    }

    /**
     * Reads the content of a body part and returns it als byte array. If a
     * content transfer encoding is set this is computed
     */
    private byte[] bodypartContentToByteArrayEncoded(BodyPart body) throws Exception {
        //check if a content transfer encoding is set. Process it if so
        String contentTransferEncoding = null;
        String[] encodingHeader = body.getHeader("content-transfer-encoding");
        if (encodingHeader != null && encodingHeader.length > 0) {
            contentTransferEncoding = encodingHeader[0];
        }
        Object content = body.getContent();
        if (content instanceof InputStream) {
            InputStream inStream = (InputStream) body.getContent();
            ByteArrayOutputStream rawDataStreamMem = new ByteArrayOutputStream();
            inStream.transferTo(rawDataStreamMem);
            rawDataStreamMem.flush();
            rawDataStreamMem.close();            
            byte[] rawData = rawDataStreamMem.toByteArray();
            inStream.close();
            return (this.decodeBodypartContentTransferEncoding(rawData, contentTransferEncoding));
        } else if (content instanceof String) {
            //in the case of casting the content transfer encoding processing is performed by the API
            //automatically. There is no need to call decodeContentTransferEncoding here
            String data = (String) content;
            byte[] rawData = data.getBytes();
            return (rawData);
        } else {
            //should never happen
            throw new Exception("Unable to process MDN body part content - unexpected content Object " + content.getClass().getName());
        }
    }

    /**
     * Decodes data by its content transfer encoding and returns it
     */
    private byte[] decodeBodypartContentTransferEncoding(byte[] encodedData, String contentTransferEncoding) throws Exception {
        if (contentTransferEncoding == null) {
            return (encodedData);
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(encodedData);
        InputStream b64is = MimeUtility.decode(bais, contentTransferEncoding);
        byte[] tmp = new byte[encodedData.length];
        int n = b64is.read(tmp);
        byte[] res = new byte[n];
        System.arraycopy(tmp, 0, res, 0, n);
        return res;
    }

    /**
     * Returns the details of the report part as properties
     */
    private void extractMessageDispositionDetailsFromMDN(Part reportPart) throws Exception {
        if (reportPart.isMimeType("multipart/*")) {
            Multipart multiPart = (Multipart) reportPart.getContent();
            try {
                int count = multiPart.getCount();
                for (int i = 0; i < count; i++) {
                    BodyPart body = multiPart.getBodyPart(i);
                    byte[] bodypartData = this.bodypartContentToByteArrayEncoded(body);
                    //text/plain content type marks the MDN text
                    if (body.getContentType().toLowerCase().startsWith("text/plain")) {
                        this.mdnDetails = new String(bodypartData).trim();
                    } else if (body.getContentType().toLowerCase().startsWith("message/disposition-notification")) {
                        InputStream inStream = new ByteArrayInputStream(bodypartData);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
                        String line = "";
                        while (line != null) {
                            line = reader.readLine();
                            if (line != null) {
                                int index = line.indexOf(':');
                                if (index > 0) {
                                    String key = line.substring(0, index).toLowerCase();
                                    String value = line.substring(index + 1).trim();
                                    this.dispositionProperties.setProperty(key, value);
                                    if (key.equals("disposition")) {
                                        this.computeDispositionState(value);
                                    }
                                }
                            }
                        }
                        inStream.close();
                    }
                }
            } catch (MessagingException structureException) {
                StringBuilder errorMessage = new StringBuilder(structureException.getMessage());
                errorMessage.insert(0, "[" + structureException.getClass().getSimpleName() + "@extractMessageDispositionDetailsFromMDN] ");
                throw new Exception(this.rb.getResourceString("structure.failure.mdn", errorMessage));
            }
        }
    }

    /**
     * Parses the passed message an returns the report body type if this is an
     * MDN
     */
    private Part parsePartsForReport(Part part) throws Exception {
        if (part.getContentType().toLowerCase().startsWith("multipart/report")) {
            return (part);
        }
        if (part.isMimeType("multipart/*")) {
            Multipart multiPart = (Multipart) part.getContent();
            int count = multiPart.getCount();
            for (int i = 0; i < count; i++) {
                Part foundPart = parsePartsForReport(multiPart.getBodyPart(i));
                if (foundPart != null) {
                    return (foundPart);
                }
            }
        }
        //nothing found, no MDN
        return (null);
    }

    public String getMdnDetails() {
        return mdnDetails;
    }

    public void setMdnDetails(String mdnDetails) {
        this.mdnDetails = mdnDetails;
    }

    public Properties getDispositionProperties() {
        return dispositionProperties;
    }

    public String getDispositionState() {
        return dispositionState;
    }

    private void computeDispositionState(String dispositionValue) {
        int index = dispositionValue.indexOf(';');
        if (index > 0) {
            this.dispositionState = dispositionValue.substring(index + 1).trim();
        }
    }

    /**
     * Returns the MIC if it has been transferred (available after the parsing
     * process)
     *
     * @return the mic
     */
    public String getMIC() {
        return mic;
    }

    /**
     * @return the relatedMessageId
     */
    public String getRelatedMessageId() {
        return relatedMessageId;
    }
}
