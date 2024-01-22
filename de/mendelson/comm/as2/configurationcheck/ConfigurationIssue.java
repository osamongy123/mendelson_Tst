//$Header: /as2/de/mendelson/comm/as2/configurationcheck/ConfigurationIssue.java 16    24/11/22 10:22 Heller $
package de.mendelson.comm.as2.configurationcheck;

import de.mendelson.util.MecResourceBundle;
import java.io.Serializable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Contains a single configuration issue
 *
 * @author S.Heller
 * @version $Revision: 16 $
 */
public class ConfigurationIssue implements Serializable {

    public static final long serialVersionUID = 1L;
    public static final int NO_KEY_IN_TLS_KEYSTORE = 1;
    public static final int MULTIPLE_KEYS_IN_TLS_KEYSTORE = 2;
    public static final int CERTIFICATE_EXPIRED_TLS = 3;
    public static final int CERTIFICATE_EXPIRED_ENC_SIGN = 4;
    public static final int HUGE_AMOUNT_OF_TRANSACTIONS_NO_AUTO_DELETE = 5;
    public static final int FEW_CPU_CORES = 6;
    public static final int LOW_MAX_HEAP_MEMORY = 7;
    public static final int NO_OUTBOUND_CONNECTIONS_ALLOWED = 8;
    public static final int CERTIFICATE_MISSING_ENC_REMOTE_PARTNER = 9;
    public static final int CERTIFICATE_MISSING_SIGN_REMOTE_PARTNER = 10;
    public static final int KEY_MISSING_ENC_LOCAL_STATION = 11;
    public static final int KEY_MISSING_SIGN_LOCAL_STATION = 12;
    public static final int USE_OF_TEST_KEYS_IN_TLS = 13;
    public static final int JVM_32_BIT = 14;
    public static final int DIFFERENT_KEYSTORES_TLS = 15;
    public static final int WINDOWS_SERVICE_LOCAL_SYSTEM_ACCOUNT = 16;
    public static final int TOO_MANY_DIR_POLLS = 17;
    public static final int KEYSTORE_TLS_RO = 18;
    public static final int KEYSTORE_SIGN_ENCRYPT_RO = 19;

    private int issueId;
    private String details = null;
    private String subject = null;
    private String hint = null;

    private static final MecResourceBundle rb;

    static {
        try {
            rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleConfigurationIssue.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    public ConfigurationIssue(int issueId) {
        this.issueId = issueId;
        this.subject = rb.getResourceString(String.valueOf(this.issueId));
        this.hint = rb.getResourceString("hint." + String.valueOf(this.issueId));
    }

    /**Returns a list of issues that allow the user to jump into a configuration*/
    public boolean hasJumpTargetInUI(){
        return( this.issueId == NO_KEY_IN_TLS_KEYSTORE
                || this.issueId == MULTIPLE_KEYS_IN_TLS_KEYSTORE
                || this.issueId == CERTIFICATE_EXPIRED_TLS
                || this.issueId == CERTIFICATE_EXPIRED_ENC_SIGN
                || this.issueId == HUGE_AMOUNT_OF_TRANSACTIONS_NO_AUTO_DELETE
                || this.issueId == NO_OUTBOUND_CONNECTIONS_ALLOWED
                || this.issueId == CERTIFICATE_MISSING_ENC_REMOTE_PARTNER
                || this.issueId == CERTIFICATE_MISSING_SIGN_REMOTE_PARTNER
                || this.issueId == KEY_MISSING_ENC_LOCAL_STATION
                || this.issueId == KEY_MISSING_SIGN_LOCAL_STATION
                || this.issueId == USE_OF_TEST_KEYS_IN_TLS
                || this.issueId == DIFFERENT_KEYSTORES_TLS);
    }
    
    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @return the issueId
     */
    public int getIssueId() {
        return issueId;
    }

    /**
     * @return the details
     */
    public String getDetails() {
        return details;
    }

    /**
     * @param details the details to set
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * @return Some sentences about the problem and how to fix it in the program configuration etc
     */
    public String getHintAsHTML() {
        return hint;
    }

    /**
     * @param Sets additional parameter for the hint to display. Has to match the resourcebundle parameter for the hint
     */
    public void setHintParameter(Object[] parameter) {
        this.hint = rb.getResourceString("hint." + String.valueOf(this.issueId), parameter);
    }
    
}
