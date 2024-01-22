//$Header: /as2/de/mendelson/comm/as2/message/AS2MessageCreation.java 66    26/09/22 10:19 Heller $
package de.mendelson.comm.as2.message;

import com.sun.mail.util.LineOutputStream;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.util.AS2Tools;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.security.BCCryptoHelper;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.MGF1ParameterSpec;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedDataStreamGenerator;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.ZlibCompressor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMECompressedGenerator;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.operator.jcajce.JcaAlgorithmParametersConverter;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Packs a message with all necessary headers and attachments
 *
 * @author S.Heller
 * @version $Revision: 66 $
 */
public class AS2MessageCreation {

    private Logger logger = null;
    private MecResourceBundle rb = null;
    private MecResourceBundle rbMessage = null;
    private CertificateManager signatureCertManager = null;
    private CertificateManager encryptionCertManager = null;
    //Database connection
    private IDBDriverManager dbDriverManager = null;

    public AS2MessageCreation(CertificateManager signatureCertManager, CertificateManager encryptionCertManager) {
        //Load resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleAS2MessagePacker.class.getName());
            this.rbMessage = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleAS2Message.class.getName());
        } //load up  resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.signatureCertManager = signatureCertManager;
        this.encryptionCertManager = encryptionCertManager;
    }

    /**
     * Passes a logger to this creation class
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * Passes a database connection to this class to allow logging functionality
     */
    public void setServerResources(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
    }

    /**
     * Escapes the AS2-TO and AS2-FROM headers in sending direction, related to
     * RFC 4130 section 6.2
     *
     * @param identification as2-from or as2-to value to escape
     * @return escaped value
     */
    public static String escapeFromToHeader(String identification) {
        boolean containsBlank = false;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < identification.length(); i++) {
            char singleChar = identification.charAt(i);
            if (singleChar == ' ') {
                containsBlank = true;
            } else if (singleChar == '"') {
                builder.append("\\");
            } else if (singleChar == '\\') {
                builder.append("\\");
            }
            builder.append(singleChar);
        }
        //quote the value if it contains blanks
        if (containsBlank) {
            builder.insert(0, "\"");
            builder.append("\"");
        }
        return (builder.toString());
    }

    /**
     * Displays a bundle of byte arrays as hex string, for debug purpose only
     */
    private String toHexDisplay(byte[] data) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            result.append(Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1));
            result.append(" ");
        }
        return result.toString();
    }

    /**
     * Prepares the message if it contains no MIME structure
     */
    public AS2Message createMessageNoMIME(AS2Message message, Partner receiver) throws Exception {
        AS2MessageInfo info = (AS2MessageInfo) message.getAS2Info();
        BCCryptoHelper cryptoHelper = new BCCryptoHelper();
        //payload content type.
        message.setContentType(receiver.getContentType());
        message.setRawData(message.getPayload(0).getData());
        message.setDecryptedRawData(message.getPayload(0).getData());
        if (this.logger != null) {
            this.logger.log(Level.INFO, this.rb.getResourceString("message.notsigned",
                    new Object[]{
                        info.getMessageId()
                    }), info);
        }
        //compute content mic. Use SHA-1 as hash alg.
        //RFC 4130 7.3.1. Signed Receipt Considerations:        
        //*For unsigned, unencrypted messages, the MIC MUST be calculated
        //over the message contents without the MIME or any other RFC
        //2822 headers, since these are sometimes altered or reordered by
        //Mail Transport Agents (MTAs).
        String digestOIDSHA1 = cryptoHelper.convertAlgorithmNameToOID(BCCryptoHelper.ALGORITHM_SHA1);
        String mic = null;
        cryptoHelper.calculateMIC(new ByteArrayInputStream(message.getPayload(0).getData()), digestOIDSHA1);
        info.setReceivedContentMIC(mic + ", " + BCCryptoHelper.ALGORITHM_SHA1);
        //add compression
        if (receiver.getCompressionType() == AS2Message.COMPRESSION_ZLIB) {
            info.setCompressionType(AS2Message.COMPRESSION_ZLIB);
            int uncompressedSize = message.getDecryptedRawDataSize();
            InputStream dataStream = null;
            int compressedSize = -1;
            MimeBodyPart bodyPart = null;
            try {
                dataStream = message.getDecryptedRawDataInputStream();
                bodyPart = this.compressPayload(receiver, dataStream, receiver.getContentType());
                compressedSize = bodyPart.getSize();
            } finally {
                if (dataStream != null) {
                    dataStream.close();
                }
            }
            //sometimes size() is unable to determine the size of the compressed body part and will return -1. Dont log the
            //compression ratio in this case.
            if (compressedSize == -1) {
                if (this.logger != null) {
                    this.logger.log(Level.INFO, this.rb.getResourceString("message.compressed.unknownratio"), info);
                }
            } else {
                if (this.logger != null) {
                    this.logger.log(Level.INFO, this.rb.getResourceString("message.compressed",
                            new Object[]{
                                AS2Tools.getDataSizeDisplay(uncompressedSize),
                                AS2Tools.getDataSizeDisplay(compressedSize)
                            }), info);
                }
            }
            //write compressed data into the message array
            ByteArrayOutputStream bodyOutStream = new ByteArrayOutputStream();
            bodyPart.writeTo(bodyOutStream);
            bodyOutStream.flush();
            bodyOutStream.close();
            message.setDecryptedRawData(bodyOutStream.toByteArray());
        }
        //no encryption
        if (info.getEncryptionType() == AS2Message.ENCRYPTION_NONE) {
            if (this.logger != null) {
                this.logger.log(Level.INFO, this.rb.getResourceString("message.notencrypted"), info);
            }
            message.setRawData(message.getDecryptedRawData());
        } else {
            //encrypt the message raw data:
            //RFC 4130 7.3.1. Signed Receipt Considerations:
            //*For encrypted, unsigned messages, the MIC to be returned is
            //calculated on the decrypted RFC 1767/RFC3023 MIME header and
            //content.  The content after decryption MUST be canonicalized
            //before the MIC is calculated.
            String cryptAlias = this.encryptionCertManager.getAliasByFingerprint(receiver.getCryptFingerprintSHA1());
            this.encryptDataToMessage(message, cryptAlias, info.getEncryptionType(), receiver);
        }
        return (message);
    }

    /**
     * Enwrapps the data into a signed MIME message structure and returns it
     */
    private void enwrappInMessageAndSign(AS2Message message, Part contentPart, Partner sender, Partner receiver) throws Exception {
        AS2MessageInfo info = (AS2MessageInfo) message.getAS2Info();
        MimeMessage messagePart = new MimeMessage(Session.getInstance(System.getProperties(), null));
        //sign message if this is requested
        if (info.getSignType() != AS2Message.SIGNATURE_NONE) {
            MimeMultipart signedPart = this.signContentPart(info, contentPart, sender, receiver);
            if (this.logger != null) {
                String signAlias = this.signatureCertManager.getAliasByFingerprint(sender.getSignFingerprintSHA1());
                this.logger.log(Level.INFO, this.rb.getResourceString("message.signed",
                        new Object[]{
                            signAlias,
                            this.rbMessage.getResourceString("signature." + receiver.getSignType())
                        }), info);
            }
            messagePart.setContent(signedPart);
            messagePart.saveChanges();
        } else {
            //unsigned message
            if (contentPart instanceof MimeBodyPart) {
                MimeMultipart unsignedPart = new MimeMultipart();
                unsignedPart.addBodyPart((MimeBodyPart) contentPart);
                if (this.logger != null) {
                    this.logger.log(Level.INFO, this.rb.getResourceString("message.notsigned"), info);
                }
                messagePart.setContent(unsignedPart);
            } else if (contentPart instanceof MimeMultipart) {
                messagePart.setContent((MimeMultipart) contentPart);
            } else if (contentPart instanceof MimeMessage) {
                messagePart = (MimeMessage) contentPart;
            } else {
                throw new IllegalArgumentException("enwrappInMessageAndSign: Unable to set the content of a "
                        + contentPart.getClass().getName());
            }
            messagePart.saveChanges();
        }
        //store signed or unsigned data
        ByteArrayOutputStream signedOut = new ByteArrayOutputStream();
        //normally the content type header is folded (which is correct but some products are not able to parse this properly)
        //Now take the content-type, unfold it and write it
        Enumeration headerLines = messagePart.getMatchingHeaderLines(new String[]{"Content-Type"});
        LineOutputStream los = new LineOutputStream(signedOut);
        while (headerLines.hasMoreElements()) {
            //requires java mail API >= 1.4
            String nextHeaderLine = MimeUtility.unfold((String) headerLines.nextElement());
            //write the line only if the as2 message is encrypted. If the as2 message is unencrypted this header is added later
            //in the class MessageHttpUploader
            if (info.getEncryptionType() != AS2Message.ENCRYPTION_NONE) {
                los.writeln(nextHeaderLine);
            }
            //store the content line in the as2 message object, this value is required later in MessageHttpUploader
            message.setContentType(nextHeaderLine.substring(nextHeaderLine.indexOf(':') + 1));
        }
        messagePart.writeTo(signedOut,
                new String[]{"Message-ID", "Mime-Version", "Content-Type"});
        signedOut.flush();
        signedOut.close();
        message.setDecryptedRawData(signedOut.toByteArray());
    }

    /**
     * Converts a file array to a path array
     */
    private Path[] fileToPath(File[] files) {
        Path[] path = new Path[files.length];
        for (int i = 0; i < files.length; i++) {
            path[i] = files[i].toPath();
        }
        return (path);
    }

    /**
     * Builds up a new message from the passed message parts. The original
     * filenames are taken from the payload files
     *
     * @deprecated Use the same method with Path[] parameter instead
     */
    @Deprecated(since = "2020")
    public AS2Message createMessage(Partner sender, Partner receiver, File[] payloadFiles) throws Exception {
        return (this.createMessage(sender, receiver, this.fileToPath(payloadFiles), null));
    }

    /**
     * Builds up a new message from the passed message parts. The original
     * filenames are taken from the payload files
     *
     * @param payloadContentTypes If this is null the receivers content type
     * will be taken
     */
    public AS2Message createMessage(Partner sender, Partner receiver, Path[] payloadFiles, String[] payloadContentTypes) throws Exception {
        String[] originalFilenames = new String[payloadFiles.length];
        for (int i = 0; i < originalFilenames.length; i++) {
            originalFilenames[i] = payloadFiles[i].getFileName().toString().replace(' ', '_');
        }
        return (this.createMessage(sender, receiver, payloadFiles, originalFilenames, AS2Message.MESSAGETYPE_AS2, null,
                receiver.getSubject(), payloadContentTypes));
    }

    /**
     * Builds up a new message from the passed message parts
     *
     * @deprecated Use the same method with Path[] parameter instead
     */
    @Deprecated(since = "2020")
    public AS2Message createMessage(Partner sender, Partner receiver, File[] payloadFiles, String[] originalFilenames) throws Exception {
        return (this.createMessage(sender, receiver, this.fileToPath(payloadFiles), originalFilenames, null));
    }

    /**
     * Builds up a new message from the passed message parts
     */
    public AS2Message createMessage(Partner sender, Partner receiver, Path[] payloadFiles, String[] originalFilenames,
            String[] payloadContentTypes) throws Exception {
        return (this.createMessage(sender, receiver, payloadFiles, originalFilenames, AS2Message.MESSAGETYPE_AS2, null,
                receiver.getSubject(), payloadContentTypes));
    }

    /**
     * Builds up a new message from the passed message parts
     *
     * @deprecated Use the same method with Path[] parameter instead
     */
    @Deprecated(since = "2020")
    public AS2Message createMessage(Partner sender, Partner receiver, File[] payloadFiles, String[] originalFilenames, String userdefinedId,
            String subject) throws Exception {
        return (this.createMessage(sender, receiver, this.fileToPath(payloadFiles), originalFilenames, userdefinedId, subject,
                null));
    }

    /**
     * Builds up a new message from the passed message parts
     */
    public AS2Message createMessage(Partner sender, Partner receiver, Path[] payloadFiles, String[] originalFilenames, String userdefinedId,
            String subject, String[] payloadContentTypes) throws Exception {
        return (this.createMessage(sender, receiver, payloadFiles, originalFilenames, AS2Message.MESSAGETYPE_AS2, userdefinedId, subject,
                payloadContentTypes));
    }

    /**
     * Builds up a new message from the passed payload files - the original
     * filenames are taken from the passed payload files
     *
     * @param messageType one of the message types defined in the class
     * AS2Message
     * @deprecated Use the same method with Path[] parameter instead
     */
    @Deprecated(since = "2020")
    public AS2Message createMessage(Partner sender, Partner receiver, File[] payloadFiles, int messageType, String subject) throws Exception {
        return (this.createMessage(sender, receiver, this.fileToPath(payloadFiles), messageType, subject));
    }

    /**
     * Builds up a new message from the passed payload files - the original
     * filenames are taken from the passed payload files
     *
     * @param messageType one of the message types defined in the class
     * AS2Message
     */
    public AS2Message createMessage(Partner sender, Partner receiver, Path[] payloadFiles, int messageType, String subject) throws Exception {
        String[] originalFilenames = new String[payloadFiles.length];
        for (int i = 0; i < originalFilenames.length; i++) {
            originalFilenames[i] = payloadFiles[i].getFileName().toString().replace(' ', '_');
        }
        return (this.createMessage(sender, receiver, payloadFiles, originalFilenames, messageType, null, subject,
                null));
    }

    /**
     * Builds up a new message from the passed message parts
     *
     * @param messageType one of the message types defined in the class
     * AS2Message
     * @deprecated Use the same method with Path[] parameter instead
     */
    @Deprecated(since = "2020")
    public AS2Message createMessage(Partner sender, Partner receiver, File[] payloadFiles, String[] originalFilenames,
            int messageType, String userdefinedId, String subject) throws Exception {
        return (this.createMessage(sender, receiver, this.fileToPath(payloadFiles), originalFilenames, messageType, userdefinedId,
                subject, null));
    }

    /**
     * Builds up a new message from the passed message parts
     *
     * @param messageType one of the message types defined in the class
     * @param originalFilenames Original filenames for the passed payloads - the
     * array length must match the array length of the payloadFiles
     * @param payloadContentTypes Payload Content Types for the passed payloads
     * - the array length must match the array length of the payloadFiles
     * AS2Message
     */
    public AS2Message createMessage(Partner sender, Partner receiver, Path[] payloadFiles, String[] originalFilenames,
            int messageType, String userdefinedId, String subject, String[] payloadContentTypes) throws Exception {
        if (payloadFiles == null || payloadFiles.length == 0) {
            throw new IllegalArgumentException("AS2MessageCreation.createMessage(): No payload files");
        }
        if (payloadFiles.length > 0 && originalFilenames != null && originalFilenames.length != payloadFiles.length) {
            throw new IllegalArgumentException("AS2MessageCreation.createMessage(): The number of passed payloadfiles and originalfilenames must match");
        }
        if (payloadFiles.length > 0 && payloadContentTypes != null && payloadContentTypes.length != payloadFiles.length) {
            throw new IllegalArgumentException("AS2MessageCreation.createMessage(): The number of passed payloadfiles and payloadContentTypes must match");
        }
        //build the original filenames from the passed payload files as they are not passed to the method
        if (originalFilenames == null) {
            originalFilenames = new String[payloadFiles.length];
            for (int i = 0; i < originalFilenames.length; i++) {
                originalFilenames[i] = payloadFiles[i].getFileName().toString().replace(' ', '_');
            }
        }
        //Build up content types from receiver information if this is not passed
        if (payloadContentTypes == null) {
            payloadContentTypes = new String[payloadFiles.length];
            for (int i = 0; i < payloadFiles.length; i++) {
                payloadContentTypes[i] = receiver.getContentType();
            }
        }
        //fill up content type data from receiver if there are null values in the passed array
        for (int i = 0; i < payloadFiles.length; i++) {
            if (payloadContentTypes[i] == null) {
                payloadContentTypes[i] = receiver.getContentType();
            }
        }
        //create payloads from the payload files
        AS2Payload[] payloads = new AS2Payload[payloadFiles.length];
        for (int i = 0; i < payloadFiles.length; i++) {
            Path payloadFile = payloadFiles[i];
            InputStream inStream = null;
            try {
                inStream = Files.newInputStream(payloadFile);
                ByteArrayOutputStream payloadOut = new ByteArrayOutputStream();
                inStream.transferTo(payloadOut);
                payloadOut.flush();
                payloadOut.close();
                //add payload
                AS2Payload payload = new AS2Payload();
                payload.setData(payloadOut.toByteArray());
                payload.setOriginalFilename(originalFilenames[i]);
                payload.setContentType(payloadContentTypes[i]);
                payloads[i] = payload;
            } finally {
                inStream.close();
            }
        }
        return (this.createMessage(sender, receiver, payloads, messageType, null, userdefinedId, subject));
    }

    /**
     * Builds up a new message from the passed message parts
     *
     * @param messageType one of the message types defined in the class
     * AS2Message
     */
    public AS2Message createMessage(Partner sender, Partner receiver, AS2Payload[] payloads, int messageType) throws Exception {
        return (this.createMessage(sender, receiver, payloads, messageType, null, null, receiver.getSubject()));
    }

    /**
     * Builds up a new message from the passed message parts
     *
     * @param messageType one of the message types defined in the class
     * AS2Message
     */
    public AS2Message createMessage(Partner sender, Partner receiver, AS2Payload[] payloads, int messageType,
            String messageId) throws Exception {
        return (this.createMessage(sender, receiver, payloads, messageType, messageId, null, receiver.getSubject()));
    }

    /**
     * Builds up a new message from the passed message parts
     *
     * @param messageType one of the message types defined in the class
     * AS2Message
     */
    public AS2Message createMessage(Partner sender, Partner receiver, AS2Payload[] payloads, int messageType,
            String messageId, String userdefinedId, String subject) throws Exception {
        if (messageId == null) {
            messageId = UniqueId.createMessageId(sender.getAS2Identification(), receiver.getAS2Identification());
        }
        BCCryptoHelper cryptoHelper = new BCCryptoHelper();
        AS2MessageInfo info = new AS2MessageInfo();
        info.setMessageType(messageType);
        info.setSenderId(sender.getAS2Identification());
        info.setReceiverId(receiver.getAS2Identification());
        info.setSenderEMail(sender.getEmail());
        info.setMessageId(messageId);
        info.setDirection(AS2MessageInfo.DIRECTION_OUT);
        info.setSignType(receiver.getSignType());
        info.setEncryptionType(receiver.getEncryptionType());
        info.setRequestsSyncMDN(receiver.isSyncMDN());
        if (!receiver.isSyncMDN()) {
            info.setAsyncMDNURL(sender.getMdnURL());
        }
        if (subject == null) {
            info.setSubject(receiver.getSubject());
        } else {
            info.setSubject(subject);
        }
        try {
            info.setSenderHost(InetAddress.getLocalHost().getCanonicalHostName());
        } catch (UnknownHostException e) {
            //nop
        }
        info.setUserdefinedId(userdefinedId);
        if (this.dbDriverManager != null) {
            MessageAccessDB messageAccess = new MessageAccessDB(this.dbDriverManager);
            messageAccess.initializeOrUpdateMessage(info);
        }
        if (this.logger != null) {
            this.logger.log(Level.FINE,
                    this.rb.getResourceString("message.creation.start",
                            info.getMessageId()), info);
        }
        //create message object to return
        AS2Message message = new AS2Message(info);
        //stores all the available body parts that have been prepared
        List<MimeBodyPart> contentPartList = new ArrayList<MimeBodyPart>();
        for (AS2Payload as2Payload : payloads) {
            //add payload
            message.addPayload(as2Payload);
            if (this.dbDriverManager != null) {
                MessageAccessDB messageAccess = new MessageAccessDB(this.dbDriverManager);
                messageAccess.initializeOrUpdateMessage(info);
            }
            //no MIME message: single payload, unsigned, no CEM
            if (info.getSignType() == AS2Message.SIGNATURE_NONE && payloads.length == 1
                    && info.getMessageType() != AS2Message.MESSAGETYPE_CEM) {
                return (this.createMessageNoMIME(message, receiver));
            }
            //MIME message
            MimeBodyPart bodyPart = new MimeBodyPart();
            String contentType = null;
            if (as2Payload.getContentType() == null) {
                contentType = receiver.getContentType();
            } else {
                contentType = as2Payload.getContentType();
            }
            bodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(as2Payload.getData(), contentType)));
            bodyPart.addHeader("Content-Type", contentType);
            if (as2Payload.getContentId() != null) {
                bodyPart.addHeader("Content-ID", as2Payload.getContentId());
            }
            if (receiver.getContentTransferEncoding() == AS2Message.CONTENT_TRANSFER_ENCODING_BASE64) {
                bodyPart.addHeader("Content-Transfer-Encoding", "base64");
            } else {
                bodyPart.addHeader("Content-Transfer-Encoding", "binary");
            }
            //prepare filename to not violate the MIME header rules
            if (as2Payload.getOriginalFilename() == null) {
                as2Payload.setOriginalFilename(Paths.get(as2Payload.getPayloadFilename()).getFileName().toString());
            }
            String newFilename = as2Payload.getOriginalFilename().replace(' ', '_');
            newFilename = newFilename.replace('@', '_');
            newFilename = newFilename.replace(':', '_');
            newFilename = newFilename.replace(';', '_');
            newFilename = newFilename.replace('(', '_');
            newFilename = newFilename.replace(')', '_');
            //RFC 822 mail headers must contain only US-ASCII characters. Headers that contain non US-ASCII 
            //characters must be encoded so that they contain only US-ASCII characters. Basically, 
            //this process involves using either BASE64 or QP to encode certain characters. 
            //RFC 2047 describes this in detail. 
            //test if an encoding is required
            boolean filenameEncodingRequired = !MimeUtility.encodeText(newFilename).equals(newFilename);
            if (!filenameEncodingRequired) {
                bodyPart.addHeader("Content-Disposition", "attachment; filename=" + newFilename);
            } else {
                bodyPart.addHeader("Content-Disposition", "attachment; filename=\""
                        + MimeUtility.encodeText(newFilename,
                                StandardCharsets.UTF_8.displayName(), "B")
                        + "\"");
            }            
            contentPartList.add(bodyPart);
        }
        Part contentPart = null;
        //sigle attachment? No CEM? Every CEM is in a multipart/related container
        if (contentPartList.size() == 1 && info.getMessageType() != AS2Message.MESSAGETYPE_CEM) {
            contentPart = contentPartList.get(0);
        } else {
            //build up a new MimeMultipart container for the multiple attachments, content-type
            //is "multipart/related"
            MimeMultipart multipartRelated = null;
            //CEM messages are always in a multipart container (even the response which contains only a single
            //payload) with the subtype "application/ediint-cert-exchange+xml".
            if (info.getMessageType() == AS2Message.MESSAGETYPE_CEM) {
                multipartRelated = new MimeMultipart("related; type=\"application/ediint-cert-exchange+xml\"");
            } else {
                multipartRelated = new MimeMultipart("related");
            }
            for (MimeBodyPart bodyPart : contentPartList) {
                multipartRelated.addBodyPart(bodyPart);
            }
            contentPart = new MimeBodyPart();
            contentPart.setContent(multipartRelated);
            contentPart.setHeader("Content-Type", multipartRelated.getContentType());
        }
        //should the content be compressed and enwrapped or just enwrapped?
        if (receiver.getCompressionType() == AS2Message.COMPRESSION_ZLIB) {
            info.setCompressionType(AS2Message.COMPRESSION_ZLIB);
            int uncompressedSize = contentPart.getSize();
            contentPart = this.compressPayload(receiver, contentPart);
            int compressedSize = contentPart.getSize();
            //sometimes size() is unable to determine the size of the compressed body part and will return -1. Dont log the
            //compression ratio in this case.
            if (uncompressedSize == -1 || compressedSize == -1) {
                if (this.logger != null) {
                    this.logger.log(Level.INFO, this.rb.getResourceString("message.compressed.unknownratio"), info);
                }
            } else {
                if (this.logger != null) {
                    this.logger.log(Level.INFO, this.rb.getResourceString("message.compressed",
                            new Object[]{
                                AS2Tools.getDataSizeDisplay(uncompressedSize),
                                AS2Tools.getDataSizeDisplay(compressedSize)
                            }), info);
                }
            }
        }
        //compute content mic. If the sign digest is md5 use it else use sha-1/sha-2
        String digestOID = null;
        if (info.getSignType() == AS2Message.SIGNATURE_MD5) {
            digestOID = cryptoHelper.convertAlgorithmNameToOID(BCCryptoHelper.ALGORITHM_MD5);
        } else if (info.getSignType() == AS2Message.SIGNATURE_SHA256 || info.getSignType() == AS2Message.SIGNATURE_SHA256_RSASSA_PSS) {
            digestOID = cryptoHelper.convertAlgorithmNameToOID(BCCryptoHelper.ALGORITHM_SHA256);
        } else if (info.getSignType() == AS2Message.SIGNATURE_SHA384 || info.getSignType() == AS2Message.SIGNATURE_SHA384_RSASSA_PSS) {
            digestOID = cryptoHelper.convertAlgorithmNameToOID(BCCryptoHelper.ALGORITHM_SHA384);
        } else if (info.getSignType() == AS2Message.SIGNATURE_SHA512 || info.getSignType() == AS2Message.SIGNATURE_SHA512_RSASSA_PSS) {
            digestOID = cryptoHelper.convertAlgorithmNameToOID(BCCryptoHelper.ALGORITHM_SHA512);
        } else if (info.getSignType() == AS2Message.SIGNATURE_SHA3_224 || info.getSignType() == AS2Message.SIGNATURE_SHA3_224_RSASSA_PSS) {
            digestOID = cryptoHelper.convertAlgorithmNameToOID(BCCryptoHelper.ALGORITHM_SHA3_224);
        } else if (info.getSignType() == AS2Message.SIGNATURE_SHA3_256 || info.getSignType() == AS2Message.SIGNATURE_SHA3_256_RSASSA_PSS) {
            digestOID = cryptoHelper.convertAlgorithmNameToOID(BCCryptoHelper.ALGORITHM_SHA3_256);
        } else if (info.getSignType() == AS2Message.SIGNATURE_SHA3_384 || info.getSignType() == AS2Message.SIGNATURE_SHA3_384_RSASSA_PSS) {
            digestOID = cryptoHelper.convertAlgorithmNameToOID(BCCryptoHelper.ALGORITHM_SHA3_384);
        } else if (info.getSignType() == AS2Message.SIGNATURE_SHA3_512 || info.getSignType() == AS2Message.SIGNATURE_SHA3_512_RSASSA_PSS) {
            digestOID = cryptoHelper.convertAlgorithmNameToOID(BCCryptoHelper.ALGORITHM_SHA3_512);
        } else {
            //For unsigned messages or unknown signing algorithm take sha-1
            digestOID = cryptoHelper.convertAlgorithmNameToOID(BCCryptoHelper.ALGORITHM_SHA1);
        }
        //for multiple payloads in a single transmission that works the same way:
        //RFC 6362 (multiple payloads)
        //2.3.  MIC Calculation
        //MIC calculation in an EDIINT message with multiple attachments is
        //performed in the same manner as for a single EDI payload.  The only
        //difference is calculating the message integrity check (MIC) over the
        //whole multipart/related body rather than a single EDI payload.
        String mic = cryptoHelper.calculateMIC(contentPart, digestOID);
        if (info.getSignType() == AS2Message.SIGNATURE_MD5) {
            info.setReceivedContentMIC(mic + ", " + BCCryptoHelper.ALGORITHM_MD5);
        } else if (info.getSignType() == AS2Message.SIGNATURE_SHA256 || info.getSignType() == AS2Message.SIGNATURE_SHA256_RSASSA_PSS) {
            info.setReceivedContentMIC(mic + ", " + BCCryptoHelper.ALGORITHM_SHA256);
        } else if (info.getSignType() == AS2Message.SIGNATURE_SHA384 || info.getSignType() == AS2Message.SIGNATURE_SHA384_RSASSA_PSS) {
            info.setReceivedContentMIC(mic + ", " + BCCryptoHelper.ALGORITHM_SHA384);
        } else if (info.getSignType() == AS2Message.SIGNATURE_SHA512 || info.getSignType() == AS2Message.SIGNATURE_SHA512_RSASSA_PSS) {
            info.setReceivedContentMIC(mic + ", " + BCCryptoHelper.ALGORITHM_SHA512);
        } else if (info.getSignType() == AS2Message.SIGNATURE_SHA3_224 || info.getSignType() == AS2Message.SIGNATURE_SHA3_224_RSASSA_PSS) {
            info.setReceivedContentMIC(mic + ", " + BCCryptoHelper.ALGORITHM_SHA3_224);
        } else if (info.getSignType() == AS2Message.SIGNATURE_SHA3_256 || info.getSignType() == AS2Message.SIGNATURE_SHA3_256_RSASSA_PSS) {
            info.setReceivedContentMIC(mic + ", " + BCCryptoHelper.ALGORITHM_SHA3_256);
        } else if (info.getSignType() == AS2Message.SIGNATURE_SHA3_384 || info.getSignType() == AS2Message.SIGNATURE_SHA3_384_RSASSA_PSS) {
            info.setReceivedContentMIC(mic + ", " + BCCryptoHelper.ALGORITHM_SHA3_384);
        } else if (info.getSignType() == AS2Message.SIGNATURE_SHA3_512 || info.getSignType() == AS2Message.SIGNATURE_SHA3_512_RSASSA_PSS) {
            info.setReceivedContentMIC(mic + ", " + BCCryptoHelper.ALGORITHM_SHA3_512);
        } else {
            info.setReceivedContentMIC(mic + ", " + BCCryptoHelper.ALGORITHM_SHA1);
        }
        this.enwrappInMessageAndSign(message, contentPart, sender, receiver);
        //encryption requested for the receiver?
        if (info.getEncryptionType() != AS2Message.ENCRYPTION_NONE) {
            String cryptAlias = this.encryptionCertManager.getAliasByFingerprint(receiver.getCryptFingerprintSHA1());
            this.encryptDataToMessage(message, cryptAlias, info.getEncryptionType(), receiver);
        } else {
            message.setRawData(message.getDecryptedRawData());
            if (this.logger != null) {
                this.logger.log(Level.INFO, this.rb.getResourceString("message.notencrypted"), info);
            }
        }
        return (message);
    }

    /**
     * Encrypts a byte array and returns it
     */
    private void encryptDataToMessage(AS2Message message, String receiverCryptAlias, int encryptionType, Partner receiver) throws Exception {
        AS2MessageInfo info = (AS2MessageInfo) message.getAS2Info();
        BCCryptoHelper cryptoHelper = new BCCryptoHelper();
        X509Certificate certificate = this.encryptionCertManager.getX509Certificate(receiverCryptAlias);
        //define key transport scheme
        AlgorithmIdentifier keyTransportScheme = null;
        if (encryptionType == AS2Message.ENCRYPTION_AES_128_RSAES_AOEP) {
            //generate algorithm identifier for RSA-OAEP pkcs#1 v2.1 with SHA-256
            JcaAlgorithmParametersConverter paramsConverter = new JcaAlgorithmParametersConverter();
            OAEPParameterSpec oaepSpec = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
            keyTransportScheme = paramsConverter.getAlgorithmIdentifier(PKCSObjectIdentifiers.id_RSAES_OAEP, oaepSpec);
        } else if (encryptionType == AS2Message.ENCRYPTION_AES_192_RSAES_AOEP) {
            //generate algorithm identifier for RSA-OAEP pkcs#1 v2.1 with SHA-256
            JcaAlgorithmParametersConverter paramsConverter = new JcaAlgorithmParametersConverter();
            OAEPParameterSpec oaepSpec = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
            keyTransportScheme = paramsConverter.getAlgorithmIdentifier(PKCSObjectIdentifiers.id_RSAES_OAEP, oaepSpec);
        } else if (encryptionType == AS2Message.ENCRYPTION_AES_256_RSAES_AOEP) {
            //generate algorithm identifier for RSA-OAEP pkcs#1 v2.1 with SHA-256
            JcaAlgorithmParametersConverter paramsConverter = new JcaAlgorithmParametersConverter();
            OAEPParameterSpec oaepSpec = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
            keyTransportScheme = paramsConverter.getAlgorithmIdentifier(PKCSObjectIdentifiers.id_RSAES_OAEP, oaepSpec);
        }
        CMSEnvelopedDataStreamGenerator dataGenerator = cryptoHelper.generateCMSEnvelopedDataStreamGenerator(certificate, keyTransportScheme);
        DeferredFileOutputStream encryptedOutput = null;
        OutputStream out = null;
        try {
            //if the data is less then 20MB perform the operaion in memory else stream to disk
            encryptedOutput = new DeferredFileOutputStream(20 * 1024 * 1024, "as2encryptdata_", ".mem", null);
            if (encryptionType == AS2Message.ENCRYPTION_3DES) {
                out = dataGenerator.open(encryptedOutput, new JceCMSContentEncryptorBuilder(CMSAlgorithm.DES_EDE3_CBC)
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME).build());
            } else if (encryptionType == AS2Message.ENCRYPTION_DES) {
                out = dataGenerator.open(encryptedOutput, new JceCMSContentEncryptorBuilder(CMSAlgorithm.DES_CBC, 56)
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME).build());
            } else if (encryptionType == AS2Message.ENCRYPTION_RC2_40) {
                out = dataGenerator.open(encryptedOutput, new JceCMSContentEncryptorBuilder(CMSAlgorithm.RC2_CBC, 40)
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME).build());
            } else if (encryptionType == AS2Message.ENCRYPTION_RC2_64) {
                out = dataGenerator.open(encryptedOutput, new JceCMSContentEncryptorBuilder(CMSAlgorithm.RC2_CBC, 64)
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME).build());
            } else if (encryptionType == AS2Message.ENCRYPTION_RC2_128) {
                out = dataGenerator.open(encryptedOutput, new JceCMSContentEncryptorBuilder(CMSAlgorithm.RC2_CBC, 128)
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME).build());
            } else if (encryptionType == AS2Message.ENCRYPTION_RC2_196) {
                out = dataGenerator.open(encryptedOutput, new JceCMSContentEncryptorBuilder(CMSAlgorithm.RC2_CBC, 196)
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME).build());
            } else if (encryptionType == AS2Message.ENCRYPTION_AES_128) {
                out = dataGenerator.open(encryptedOutput, new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES128_CBC)
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME).build());
            } else if (encryptionType == AS2Message.ENCRYPTION_AES_192) {
                out = dataGenerator.open(encryptedOutput, new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES192_CBC)
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME).build());
            } else if (encryptionType == AS2Message.ENCRYPTION_AES_256) {
                out = dataGenerator.open(encryptedOutput, new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES256_CBC)
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME).build());
            } else if (encryptionType == AS2Message.ENCRYPTION_RC4_40) {
                out = dataGenerator.open(encryptedOutput, new JceCMSContentEncryptorBuilder(
                        new ASN1ObjectIdentifier(cryptoHelper.convertAlgorithmNameToOID(BCCryptoHelper.ALGORITHM_RC4)), 40)
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME).build());
            } else if (encryptionType == AS2Message.ENCRYPTION_RC4_56) {
                out = dataGenerator.open(encryptedOutput, new JceCMSContentEncryptorBuilder(
                        new ASN1ObjectIdentifier(cryptoHelper.convertAlgorithmNameToOID(BCCryptoHelper.ALGORITHM_RC4)), 56)
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME).build());
            } else if (encryptionType == AS2Message.ENCRYPTION_RC4_128) {
                out = dataGenerator.open(encryptedOutput, new JceCMSContentEncryptorBuilder(
                        new ASN1ObjectIdentifier(cryptoHelper.convertAlgorithmNameToOID(BCCryptoHelper.ALGORITHM_RC4)), 128)
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME).build());
            } else if (encryptionType == AS2Message.ENCRYPTION_AES_128_RSAES_AOEP) {
                out = dataGenerator.open(encryptedOutput, new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES128_CBC)
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME).build());
            } else if (encryptionType == AS2Message.ENCRYPTION_AES_192_RSAES_AOEP) {
                out = dataGenerator.open(encryptedOutput, new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES192_CBC)
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME).build());
            } else if (encryptionType == AS2Message.ENCRYPTION_AES_256_RSAES_AOEP) {
                out = dataGenerator.open(encryptedOutput, new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES256_CBC)
                        .setProvider(BouncyCastleProvider.PROVIDER_NAME).build());
            }
            if (out == null) {
                throw new Exception("Internal failure: Unsupported encryption type " + encryptionType + " during the encryption process (encryptDataToMessage)");
            }
            InputStream in = null;
            try {
                in = message.getDecryptedRawDataInputStream();
                in.transferTo(out);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } finally {
            if (out != null) {
                out.close();
            }
            if (encryptedOutput != null) {
                encryptedOutput.close();
            }
        }
        //size of the data was < than the threshold
        if (encryptedOutput.isInMemory()) {
            message.setRawData(encryptedOutput.getData());
        } else {
            //data has been written to a temp file: reread and return
            ByteArrayOutputStream memOut = new ByteArrayOutputStream();
            encryptedOutput.writeTo(memOut);
            memOut.flush();
            memOut.close();
            //finally delete the temp file
            try {
                Files.delete(encryptedOutput.getFile().toPath());
            } catch (Exception e) {
                SystemEvent event = new SystemEvent(
                        SystemEvent.SEVERITY_WARNING,
                        SystemEvent.ORIGIN_SYSTEM,
                        SystemEvent.TYPE_FILE_DELETE);
                event.setSubject(event.typeToTextLocalized());
                event.setBody("[" + e.getClass().getSimpleName() + "]: " + e.getMessage());
                SystemEventManagerImplAS2.newEvent(event);
            }
            message.setRawData(memOut.toByteArray());
        }
        if (this.logger != null) {
            String cryptAlias = this.encryptionCertManager.getAliasByFingerprint(receiver.getCryptFingerprintSHA1());
            this.logger.log(Level.INFO, this.rb.getResourceString("message.encrypted",
                    new Object[]{
                        cryptAlias,
                        this.rbMessage.getResourceString("encryption." + receiver.getEncryptionType())
                    }), info);
        }
    }

    /**
     * Compresses the payload using the ZLIB algorithm
     */
    private MimeBodyPart compressPayload(Partner receiver, InputStream dataStream, String contentType) throws Exception {
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(dataStream, contentType)));
        bodyPart.addHeader("Content-Type", contentType);
        if (receiver.getContentTransferEncoding() == AS2Message.CONTENT_TRANSFER_ENCODING_BASE64) {
            bodyPart.addHeader("Content-Transfer-Encoding", "base64");
        } else {
            bodyPart.addHeader("Content-Transfer-Encoding", "binary");
        }
        SMIMECompressedGenerator generator = new SMIMECompressedGenerator();
        if (receiver.getContentTransferEncoding() == AS2Message.CONTENT_TRANSFER_ENCODING_BASE64) {
            generator.setContentTransferEncoding("base64");
        } else {
            generator.setContentTransferEncoding("binary");
        }
        return (generator.generate(bodyPart, new ZlibCompressor()));
    }

    /**
     * Compresses the payload using the ZLIB algorithm
     */
    private MimeBodyPart compressPayload(Partner receiver, Part contentPart) throws SMIMEException {
        SMIMECompressedGenerator generator = new SMIMECompressedGenerator();
        if (receiver.getContentTransferEncoding() == AS2Message.CONTENT_TRANSFER_ENCODING_BASE64) {
            generator.setContentTransferEncoding("base64");
        } else {
            generator.setContentTransferEncoding("binary");
        }
        if (contentPart instanceof MimeBodyPart) {
            return (generator.generate((MimeBodyPart) contentPart, new ZlibCompressor()));
        } else if (contentPart instanceof MimeMessage) {
            return (generator.generate((MimeMessage) contentPart, new ZlibCompressor()));
        } else {
            throw new IllegalArgumentException("compressPayload: Unable to compress a Part of class " + contentPart.getClass().getName());
        }
    }

    /**
     * Signs the passed data and returns it
     */
    private MimeMultipart signContentPart(AS2MessageInfo info, Part part, Partner sender, Partner receiver) throws Exception {
        MimeMultipart signedPart = null;
        if (part instanceof MimeBodyPart) {
            signedPart = this.signContent(info, (MimeBodyPart) part, sender, receiver);
        } else if (part instanceof MimeMessage) {
            signedPart = this.signContent(info, (MimeMessage) part, sender, receiver);
        } else {
            throw new IllegalArgumentException("signContentPart: unable to sign a " + part.getClass().getName() + ".");
        }
        return (signedPart);
    }

    /**
     * Converts the internal sign type number of the message to the digest name
     * of the crypto helper
     *
     * @param signType
     * @return
     * @throws Exception
     */
    private String getDigestForInternalSignType(int signType) throws Exception {
        String digest = null;
        if (signType == AS2Message.SIGNATURE_SHA1) {
            digest = BCCryptoHelper.ALGORITHM_SHA1;
        } else if (signType == AS2Message.SIGNATURE_MD5) {
            digest = BCCryptoHelper.ALGORITHM_MD5;
        } else if (signType == AS2Message.SIGNATURE_SHA224) {
            digest = BCCryptoHelper.ALGORITHM_SHA224;
        } else if (signType == AS2Message.SIGNATURE_SHA256) {
            digest = BCCryptoHelper.ALGORITHM_SHA256;
        } else if (signType == AS2Message.SIGNATURE_SHA384) {
            digest = BCCryptoHelper.ALGORITHM_SHA384;
        } else if (signType == AS2Message.SIGNATURE_SHA512) {
            digest = BCCryptoHelper.ALGORITHM_SHA512;
        } else if (signType == AS2Message.SIGNATURE_SHA1_RSASSA_PSS) {
            digest = BCCryptoHelper.ALGORITHM_SHA_1_RSASSA_PSS;
        } else if (signType == AS2Message.SIGNATURE_SHA224_RSASSA_PSS) {
            digest = BCCryptoHelper.ALGORITHM_SHA_224_RSASSA_PSS;
        } else if (signType == AS2Message.SIGNATURE_SHA256_RSASSA_PSS) {
            digest = BCCryptoHelper.ALGORITHM_SHA_256_RSASSA_PSS;
        } else if (signType == AS2Message.SIGNATURE_SHA384_RSASSA_PSS) {
            digest = BCCryptoHelper.ALGORITHM_SHA_384_RSASSA_PSS;
        } else if (signType == AS2Message.SIGNATURE_SHA512_RSASSA_PSS) {
            digest = BCCryptoHelper.ALGORITHM_SHA_512_RSASSA_PSS;
        } else if (signType == AS2Message.SIGNATURE_SHA3_224) {
            digest = BCCryptoHelper.ALGORITHM_SHA3_224;
        } else if (signType == AS2Message.SIGNATURE_SHA3_256) {
            digest = BCCryptoHelper.ALGORITHM_SHA3_256;
        } else if (signType == AS2Message.SIGNATURE_SHA3_384) {
            digest = BCCryptoHelper.ALGORITHM_SHA3_384;
        } else if (signType == AS2Message.SIGNATURE_SHA3_512) {
            digest = BCCryptoHelper.ALGORITHM_SHA3_512;
        } else if (signType == AS2Message.SIGNATURE_SHA3_224_RSASSA_PSS) {
            digest = BCCryptoHelper.ALGORITHM_SHA3_224_RSASSA_PSS;
        } else if (signType == AS2Message.SIGNATURE_SHA3_256_RSASSA_PSS) {
            digest = BCCryptoHelper.ALGORITHM_SHA3_256_RSASSA_PSS;
        } else if (signType == AS2Message.SIGNATURE_SHA3_384_RSASSA_PSS) {
            digest = BCCryptoHelper.ALGORITHM_SHA3_384_RSASSA_PSS;
        } else if (signType == AS2Message.SIGNATURE_SHA3_512_RSASSA_PSS) {
            digest = BCCryptoHelper.ALGORITHM_SHA3_512_RSASSA_PSS;
        } else {
            throw new Exception("Internal failure: Unsupported sign type " + signType);
        }
        return (digest);
    }

    /**
     * Signs the passed data and returns it
     */
    private MimeMultipart signContent(AS2MessageInfo info, MimeMessage message, Partner sender, Partner receiver) throws Exception {
        PrivateKey senderKey = this.signatureCertManager.getPrivateKeyByFingerprintSHA1(sender.getSignFingerprintSHA1());
        if (senderKey == null) {
            throw new Exception("AS2MessageCreation.signContent: Key with serial " + sender.getSignFingerprintSHA1()
                    + " does not exist in the keystore.");
        }
        String senderSignAlias = this.signatureCertManager.getAliasByFingerprint(sender.getSignFingerprintSHA1());
        Certificate[] chain = this.signatureCertManager.getCertificateChain(senderSignAlias);
        String digest = this.getDigestForInternalSignType(receiver.getSignType());
        BCCryptoHelper helper = new BCCryptoHelper();
        boolean useAlgorithmIdentifierProtectionAttribute = true;
        if (receiver != null) {
            useAlgorithmIdentifierProtectionAttribute = receiver.getUseAlgorithmIdentifierProtectionAttribute();
        }
        if (this.logger != null && !useAlgorithmIdentifierProtectionAttribute) {
            this.logger.log(Level.WARNING, this.rb.getResourceString("signature.no.aipa"), info);
        }
        MimeMultipart signedMultipart = helper.sign(message, chain, senderKey, digest,
                useAlgorithmIdentifierProtectionAttribute);
        return (signedMultipart);
    }

    /**
     * Signs the passed message and returns it
     */
    private MimeMultipart signContent(AS2MessageInfo info, MimeBodyPart body, Partner sender, Partner receiver) throws Exception {
        PrivateKey senderKey = this.signatureCertManager.getPrivateKeyByFingerprintSHA1(sender.getSignFingerprintSHA1());
        String senderSignAlias = this.signatureCertManager.getAliasByFingerprint(sender.getSignFingerprintSHA1());
        Certificate[] chain = this.signatureCertManager.getCertificateChain(senderSignAlias);
        String digest = this.getDigestForInternalSignType(receiver.getSignType());
        BCCryptoHelper helper = new BCCryptoHelper();
        boolean useAlgorithmIdentifierProtectionAttribute = receiver.getUseAlgorithmIdentifierProtectionAttribute();
        if (this.logger != null && !useAlgorithmIdentifierProtectionAttribute) {
            this.logger.log(Level.WARNING, this.rb.getResourceString("signature.no.aipa"), info);
        }
        MimeMultipart signedMultipart = helper.sign(body, chain, senderKey, digest,
                useAlgorithmIdentifierProtectionAttribute);
        return (signedMultipart);
    }

}
