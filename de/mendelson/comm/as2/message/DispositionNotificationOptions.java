//$Header: /as2/de/mendelson/comm/as2/message/DispositionNotificationOptions.java 14    6/05/18 3:14p Heller $
package de.mendelson.comm.as2.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Stores the options about the MDN, have been set by an inbound AS2 message
 *
 * @author S.Heller
 * @version $Revision: 14 $
 */
public class DispositionNotificationOptions implements Serializable {

    public static final long serialVersionUID = 1L;
    
    //"signed-receipt-protocol=optional, pkcs7-signature; signed-receipt-micalg=optional"
    private String headerValue = "";
    private final String DEFAULT_HEADER_VALUE = "signed-receipt-protocol=optional, pkcs7-signature; signed-receipt-micalg=optional";
    /**
     * Stores the parsed options
     */
    private final Map<String, String> propertyMap = new HashMap<String, String>();

    /**
     * Creates a new instance of DispositionNotificationOptions
     */
    public DispositionNotificationOptions(String headerValue) {
        this.setHeaderValue(headerValue);
    }
    
    /**
     * Creates a new instance of DispositionNotificationOptions
     */
    public DispositionNotificationOptions(String[] digestList) {
        this.headerValue = this.DEFAULT_HEADER_VALUE;
        for (String digest : digestList) {
            this.headerValue += ", " + digest;
        }
        this.parseHeaderValue();
    }

    public void setSignaturHashFunction(final String DIGEST) {
        this.headerValue = this.DEFAULT_HEADER_VALUE + ", " + DIGEST;
        this.parseHeaderValue();
    }

    private void parseHeaderValue() {
        this.propertyMap.clear();
        if (this.headerValue == null) {
            return;
        }
        StringTokenizer tokenizer = new StringTokenizer(headerValue.toLowerCase(), ";");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            int index = token.indexOf("=");
            if (index > 0 && index < token.length()) {
                String key = token.substring(0, index).trim();
                String value = token.substring(index + 1);
                this.propertyMap.put(key, value);
            }
        }
    }

    /**
     * Returns the disposition-notification-options header
     */
    public String getHeaderValue() {
        return (headerValue);
    }

    /**
     * Sets the header for the disposition-notification-options
     */
    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
        this.parseHeaderValue();

    }

    public boolean signMDN() {
        String value = this.propertyMap.get("signed-receipt-protocol");
        if (value == null) {
            return (false);
        }
        return (value.indexOf("pkcs7-signature") >= 0);
    }

    /**
     * Its possible to have more then one signature algorithm - this returns the
     * preferred (sha1 > md5, sha-512 > sha-384 etc)
     * returns SIGNATURE_NONE if no signature digest is defined
     *
     */
    public int getPreferredSignatureAlgorithm() {
        int[] algorithmList = this.getPossibleSignatureAlgorithm();
        if (algorithmList.length == 0) {
            return (AS2Message.SIGNATURE_NONE);
        } else if (algorithmList.length == 1) {
            return (algorithmList[0]);
        } else {
            //sort the list
            Arrays.sort(algorithmList);
            int maxValue = algorithmList[algorithmList.length - 1];
            //wrong order in constants, MD5 > sha1 :(
            if (maxValue == AS2Message.SIGNATURE_MD5) {
                //in this case there are 2 values and the second one MUST be sha-1: this is preferred
                return (AS2Message.SIGNATURE_SHA1);
            }else{
                return( maxValue);
            }
        }
    }

    /**
     * Returns the allowed signature algorithm requested by the disposition
     * notification
     */
    protected int[] getPossibleSignatureAlgorithm() {
        String value = this.propertyMap.get("signed-receipt-micalg");
        if (value == null) {
            return (new int[0]);
        }
        //may be sha1 or md5 but older S/MIME implementations also allow rsa-md5 and rsa-sha1
        List<Integer> list = new ArrayList<Integer>();
        if (value.indexOf("sha1") >= 0 || value.indexOf("sha-1") >= 0) {
            list.add(Integer.valueOf(AS2Message.SIGNATURE_SHA1));
        }
        if (value.indexOf("sha224") >= 0 || value.indexOf("sha-224") >= 0) {
            list.add(Integer.valueOf(AS2Message.SIGNATURE_SHA224));
        }
        if (value.indexOf("sha256") >= 0 || value.indexOf("sha-256") >= 0) {
            list.add(Integer.valueOf(AS2Message.SIGNATURE_SHA256));
        }
        if (value.indexOf("sha384") >= 0 || value.indexOf("sha-384") >= 0) {
            list.add(Integer.valueOf(AS2Message.SIGNATURE_SHA384));
        }
        if (value.indexOf("sha512") >= 0 || value.indexOf("sha-512") >= 0) {
            list.add(Integer.valueOf(AS2Message.SIGNATURE_SHA512));
        }
        if (value.indexOf("sha3-224") >= 0 ) {
            list.add(Integer.valueOf(AS2Message.SIGNATURE_SHA3_224));
        }
        if (value.indexOf("sha3-256") >= 0 ) {
            list.add(Integer.valueOf(AS2Message.SIGNATURE_SHA3_256));
        }
        if (value.indexOf("sha3-384") >= 0 ) {
            list.add(Integer.valueOf(AS2Message.SIGNATURE_SHA3_384));
        }
        if (value.indexOf("sha3-512") >= 0 ) {
            list.add(Integer.valueOf(AS2Message.SIGNATURE_SHA3_512));
        }
        if (value.indexOf("md5") >= 0) {
            list.add(Integer.valueOf(AS2Message.SIGNATURE_MD5));
        }
        int[] returnValues = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            returnValues[i] = list.get(i).intValue();
        }
        return (returnValues);
    }
//    public static final void main( String[]args ){
//        int[] test = {1, 3, 5, 0, 2};
//        Arrays.sort(test);
//        for( int i = 0; i < test.length; i++ ){
//            System.out.println(test[i]);
//        }
//    }
}
