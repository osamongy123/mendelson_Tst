//$Header: /as2/de/mendelson/comm/as2/message/AS2Message.java 65    22/11/22 16:38 Heller $
package de.mendelson.comm.as2.message;

import de.mendelson.util.security.encryption.EncryptionConstantsAS2;
import de.mendelson.util.security.signature.SignatureConstantsAS2;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * Stores a AS2 message
 *
 * @author S.Heller
 * @version $Revision: 65 $
 */
public class AS2Message implements Serializable {

    public static final long serialVersionUID = 1L;

    public static final int ENCRYPTION_UNKNOWN = EncryptionConstantsAS2.ENCRYPTION_UNKNOWN;
    public static final int ENCRYPTION_NONE = EncryptionConstantsAS2.ENCRYPTION_NONE;
    public static final int ENCRYPTION_3DES = EncryptionConstantsAS2.ENCRYPTION_3DES;
    public static final int ENCRYPTION_RC2_40 = EncryptionConstantsAS2.ENCRYPTION_RC2_40;
    public static final int ENCRYPTION_RC2_64 = EncryptionConstantsAS2.ENCRYPTION_RC2_64;
    public static final int ENCRYPTION_RC2_128 = EncryptionConstantsAS2.ENCRYPTION_RC2_128;
    public static final int ENCRYPTION_RC2_196 = EncryptionConstantsAS2.ENCRYPTION_RC2_196;
    public static final int ENCRYPTION_RC2_UNKNOWN = EncryptionConstantsAS2.ENCRYPTION_RC2_UNKNOWN;
    public static final int ENCRYPTION_AES_128 = EncryptionConstantsAS2.ENCRYPTION_AES_128;
    public static final int ENCRYPTION_AES_192 = EncryptionConstantsAS2.ENCRYPTION_AES_192;
    public static final int ENCRYPTION_AES_256 = EncryptionConstantsAS2.ENCRYPTION_AES_256;
    public static final int ENCRYPTION_RC4_40 = EncryptionConstantsAS2.ENCRYPTION_RC4_40;
    public static final int ENCRYPTION_RC4_56 = EncryptionConstantsAS2.ENCRYPTION_RC4_56;
    public static final int ENCRYPTION_RC4_128 = EncryptionConstantsAS2.ENCRYPTION_RC4_128;
    public static final int ENCRYPTION_RC4_UNKNOWN = EncryptionConstantsAS2.ENCRYPTION_RC4_UNKNOWN;
    public static final int ENCRYPTION_DES = EncryptionConstantsAS2.ENCRYPTION_DES;
    public static final int ENCRYPTION_AES_128_RSAES_AOEP = EncryptionConstantsAS2.ENCRYPTION_AES_128_RSAES_AOEP;
    public static final int ENCRYPTION_AES_192_RSAES_AOEP = EncryptionConstantsAS2.ENCRYPTION_AES_192_RSAES_AOEP;
    public static final int ENCRYPTION_AES_256_RSAES_AOEP = EncryptionConstantsAS2.ENCRYPTION_AES_256_RSAES_AOEP;
    public static final int ENCRYPTION_UNKNOWN_ALGORITHM = EncryptionConstantsAS2.ENCRYPTION_UNKNOWN_ALGORITHM;
    public static final int SIGNATURE_UNKNOWN = SignatureConstantsAS2.SIGNATURE_UNKNOWN;
    public static final int SIGNATURE_NONE = SignatureConstantsAS2.SIGNATURE_NONE;
    public static final int SIGNATURE_SHA1 = SignatureConstantsAS2.SIGNATURE_SHA1;
    public static final int SIGNATURE_MD5 = SignatureConstantsAS2.SIGNATURE_MD5;
    public static final int SIGNATURE_SHA224 = SignatureConstantsAS2.SIGNATURE_SHA224;
    public static final int SIGNATURE_SHA256 = SignatureConstantsAS2.SIGNATURE_SHA256;
    public static final int SIGNATURE_SHA384 = SignatureConstantsAS2.SIGNATURE_SHA384;
    public static final int SIGNATURE_SHA512 = SignatureConstantsAS2.SIGNATURE_SHA512;
    public static final int SIGNATURE_SHA1_RSASSA_PSS = SignatureConstantsAS2.SIGNATURE_SHA1_RSASSA_PSS;
    public static final int SIGNATURE_SHA224_RSASSA_PSS = SignatureConstantsAS2.SIGNATURE_SHA224_RSASSA_PSS;
    public static final int SIGNATURE_SHA256_RSASSA_PSS = SignatureConstantsAS2.SIGNATURE_SHA256_RSASSA_PSS;
    public static final int SIGNATURE_SHA384_RSASSA_PSS = SignatureConstantsAS2.SIGNATURE_SHA384_RSASSA_PSS;
    public static final int SIGNATURE_SHA512_RSASSA_PSS = SignatureConstantsAS2.SIGNATURE_SHA512_RSASSA_PSS;
    public static final int SIGNATURE_SHA3_224 = SignatureConstantsAS2.SIGNATURE_SHA3_224;
    public static final int SIGNATURE_SHA3_256 = SignatureConstantsAS2.SIGNATURE_SHA3_256;
    public static final int SIGNATURE_SHA3_384 = SignatureConstantsAS2.SIGNATURE_SHA3_384;
    public static final int SIGNATURE_SHA3_512 = SignatureConstantsAS2.SIGNATURE_SHA3_512;
    public static final int SIGNATURE_SHA3_224_RSASSA_PSS = SignatureConstantsAS2.SIGNATURE_SHA3_224_RSASSA_PSS;
    public static final int SIGNATURE_SHA3_256_RSASSA_PSS = SignatureConstantsAS2.SIGNATURE_SHA3_256_RSASSA_PSS;
    public static final int SIGNATURE_SHA3_384_RSASSA_PSS = SignatureConstantsAS2.SIGNATURE_SHA3_384_RSASSA_PSS;
    public static final int SIGNATURE_SHA3_512_RSASSA_PSS = SignatureConstantsAS2.SIGNATURE_SHA3_512_RSASSA_PSS;
    public static final int COMPRESSION_UNKNOWN = 0;
    public static final int COMPRESSION_NONE = 1;
    public static final int COMPRESSION_ZLIB = 2;
    public static final int STATE_FINISHED = 1;
    public static final int STATE_PENDING = 2;
    public static final int STATE_STOPPED = 3;
    public static final int CONTENT_TRANSFER_ENCODING_BINARY = 1;
    public static final int CONTENT_TRANSFER_ENCODING_BASE64 = 2;
    public static final int MESSAGETYPE_AS2 = 1;
    public static final int MESSAGETYPE_CEM = 2;
    /**
     * Stores all details about the message
     */
    private AS2Info as2Info = null;
    /**
     * Stores the raw message data
     */
    private final ByteStorage rawData = new ByteStorage();
    /**
     * Stores the raw message data, decrypted. Contains the same data as the raw
     * data if the message has been sent unencrypted
     */
    private final ByteStorage decryptedRawData = new ByteStorage();
    /**
     * Payload of the as2 message, will be only one if the AS2 version is < AS2
     * 1.2
     */
    private final List<AS2Payload> payload = Collections.synchronizedList(new ArrayList<AS2Payload>());
    private Properties header = new Properties();
    private String contentType;

    /**
     * Constructor to create a new message, empty message object
     */
    public AS2Message(AS2Info as2Info) {
        this.as2Info = as2Info;
    }

    public boolean isMDN() {
        return (this.as2Info.isMDN());
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
     * Returns the number of attachments of the AS2 message. This will mainly be
     * 1 if the AS2 version is < AS2 1.2
     */
    public int getPayloadCount() {
        synchronized (this.payload) {
            return (this.payload.size());
        }
    }

    /**
     * Returns the actual size of the stored raw data
     */
    public int getRawDataSize() {
        return (this.rawData.getSize());
    }

    public InputStream getRawDataInputStream() throws Exception {
        return (this.rawData.getInputStream());
    }

    public byte[] getRawData() throws Exception {
        return (this.rawData.get());
    }

    public void setRawData(byte[] rawData) throws Exception {
        this.rawData.put(rawData);
    }

    /**
     * Returns the actual size of the stored decrypted raw data
     */
    public int getDecryptedRawDataSize() {
        return (this.rawData.getSize());
    }

    public InputStream getDecryptedRawDataInputStream() throws Exception {
        return (this.decryptedRawData.getInputStream());
    }

    public byte[] getDecryptedRawData() throws Exception {
        return (this.decryptedRawData.get());
    }

    public void setDecryptedRawData(byte[] decryptedRawData) throws Exception {
        this.decryptedRawData.put(decryptedRawData);
    }

    public Properties getHeader() {
        return header;
    }

    public void setHeader(Properties header) {
        this.header = header;
    }

    /**
     * Will return the payload of the passed index. The index should be 0 if the
     * AS2 version is < AS2 1.2
     */
    public AS2Payload getPayload(int index) {
        synchronized (this.payload) {
            if (this.payload.isEmpty()) {
                throw new IllegalArgumentException("AS2 message does not contain " + index + " payloads.");
            }
            return (this.payload.get(index));
        }
    }

    public void addPayload(AS2Payload data) {
        synchronized (this.payload) {
            this.payload.add(data);
        }
    }
    
    public void clearPayloads() {
        synchronized (this.payload) {
            this.payload.clear();
        }
    }

    /**
     * Will return the payloads of the message
     */
    public List<AS2Payload> getPayloads() {
        List<AS2Payload> list = new ArrayList<AS2Payload>();
        synchronized (this.payload) {
            list.addAll(this.payload);
        }
        return (list);
    }

    /**
     * Deletes the actual payloads and adds the passed ones
     */
    public void setPayloads(List<AS2Payload> payloads) {
        synchronized (this.payload) {
            this.payload.clear();
            this.payload.addAll(payloads);
        }
    }

    /**
     * Writes the payload to the message to the passed file
     */
    public void writeRawDecryptedTo(Path file) throws Exception {
        OutputStream outStream = null;
        InputStream inStream = null;
        try {
            outStream = Files.newOutputStream(file,
                    StandardOpenOption.SYNC,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
            inStream = this.decryptedRawData.getInputStream();
            inStream.transferTo(outStream);
        } finally {
            if (outStream != null) {
                outStream.flush();
                outStream.close();
            }
            if (inStream != null) {
                inStream.close();
            }
        }
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return the as2Info
     */
    public AS2Info getAS2Info() {
        return as2Info;
    }

    /**
     * @param as2Info the as2Info to set
     */
    public void setAS2Info(AS2Info as2Info) {
        this.as2Info = as2Info;
    }
    
    /**
     * Overwrite the equal method of object
     *
     * @param anObject object to compare
     */
    @Override
    public boolean equals(Object anObject) {
        if (anObject == this) {
            return (true);
        }
        if (anObject != null && anObject instanceof AS2Message) {
            AS2Message aMessage = (AS2Message)anObject;
            if( aMessage.getAS2Info() != null && this.getAS2Info() != null ){
                return( aMessage.getAS2Info().equals( this.getAS2Info()));
            }
            return( false );
        }
        return (false);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.as2Info);
        hash = 97 * hash + Objects.hashCode(this.rawData);
        hash = 97 * hash + Objects.hashCode(this.decryptedRawData);
        hash = 97 * hash + Objects.hashCode(this.payload);
        hash = 97 * hash + Objects.hashCode(this.header);
        hash = 97 * hash + Objects.hashCode(this.contentType);
        return hash;
    }
    
}
