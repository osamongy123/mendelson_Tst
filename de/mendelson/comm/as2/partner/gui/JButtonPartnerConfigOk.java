//$Header: /mec_as2/de/mendelson/comm/as2/partner/gui/JButtonPartnerConfigOk.java 4     18.12.20 10:40 Heller $
package de.mendelson.comm.as2.partner.gui;

import de.mendelson.comm.as2.partner.Partner;
import java.awt.Color;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Ok Button for the partner config
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class JButtonPartnerConfigOk extends JButton {

    //HEX #FFCCCC. Do not just modify this as the partner icons have the same
    //background color in an error case. This has to reworked, too if this value is changed.
    private final Color errorColor = new Color(255, 204, 204);
    private JTreePartner tree;
    private JTextField jTextFieldName;
    private JTextField jTextFieldURL;
    private JTextField jTextFieldMDNURL;
    private JTextField jTextFieldAS2Id;
    private boolean changesAllowed;

    private Partner remotePartner;

    public void initialize(JTreePartner tree, JTextField jTextFieldName, JTextField jTextFieldAS2Id,
            JTextField jTextFieldURL, JTextField jTextFieldMDNURL, boolean changesAllowed) {
        this.tree = tree;
        this.changesAllowed = changesAllowed;
        this.jTextFieldName = jTextFieldName;
        this.jTextFieldAS2Id = jTextFieldAS2Id;
        this.jTextFieldURL = jTextFieldURL;
        this.jTextFieldMDNURL = jTextFieldMDNURL;
    }

    public void setPartner(Partner remotePartner) {
        this.remotePartner = remotePartner;
    }

    /**
     * Checks if the passed URLs contain a leading protocol entry
     *
     */
    private boolean checkURLProtocol(Partner checkPartner) {
        String receiverURL = checkPartner.getURL();
        String mdnURL = checkPartner.getMdnURL();
        boolean error = false;
        if (!checkPartner.isLocalStation()) {
            //no local station
            if (receiverURL == null || (!receiverURL.startsWith("http://") && !receiverURL.startsWith("https://"))) {
                //graphical modifications for current displayed partner only!
                if (this.remotePartner.equals(checkPartner)) {
                    this.jTextFieldURL.setBackground(this.errorColor);
                }
                error = true;
            } else {
                //graphical modifications for current displayed partner only!
                if (this.remotePartner.equals(checkPartner)) {
                    this.jTextFieldURL.setBackground(UIManager.getDefaults().getColor("TextField.background"));
                }
            }
        } else {
            //local station
            if (mdnURL == null || (!mdnURL.startsWith("http://") && !mdnURL.startsWith("https://"))) {
                //graphical modifications for current displayed partner only!
                if (this.remotePartner.equals(checkPartner)) {
                    this.jTextFieldMDNURL.setBackground(this.errorColor);
                }
                error = true;
            } else {
                //graphical modifications for current displayed partner only!
                if (this.remotePartner.equals(checkPartner)) {
                    this.jTextFieldURL.setBackground(UIManager.getDefaults().getColor("TextField.background"));
                }
            }
        }
        return (error);
    }

    /**Returns the number of partner names found in the passed partner list*/
    private int getNameCountInList( String partnerName, List<Partner> partnerList){
        int count = 0;
        for( Partner partner:partnerList){
            if( partner.getName().equals(partnerName)){
                count++;
            }
        }
        return( count );
    }
    
    /**Returns the number of as2 ids names found in the passed partner list*/
    private int getAS2IdCountInList( String as2Id, List<Partner> partnerList){
        int count = 0;
        for( Partner partner:partnerList){
            if( partner.getAS2Identification().equals(as2Id)){
                count++;
            }
        }
        return( count );
    }
    
    /**
     * Checks if new name is unique and changes color in textfield if not
     */
    private boolean checkForNonUniqueValues(Partner checkPartner, List<Partner> partnerList) {
        boolean error = false;
        String newName = checkPartner.getName();
        int nameCount = this.getNameCountInList(newName, partnerList);
        if (newName != null && newName.trim().length() > 0 && nameCount == 1) {
            //graphical modifications for current displayed partner only!
            if (this.remotePartner.equals(checkPartner)) {
                this.jTextFieldName.setBackground(UIManager.getDefaults().getColor("TextField.background"));
            }
        } else {
            //graphical modifications for current displayed partner only!
            if (this.remotePartner.equals(checkPartner)) {
                this.jTextFieldName.setBackground(this.errorColor);
            }
            error = true;
        }
        String newAS2Id = checkPartner.getAS2Identification();
        int idCount = this.getAS2IdCountInList(newAS2Id, partnerList);
        if (newAS2Id != null && newAS2Id.trim().length() > 0 && idCount == 1) {
            //graphical modifications for current displayed partner only!
            if (this.remotePartner.equals(checkPartner)) {
                this.jTextFieldAS2Id.setBackground(UIManager.getDefaults().getColor("TextField.background"));
            }
        } else {
            //graphical modifications for current displayed partner only!
            if (this.remotePartner.equals(checkPartner)) {
                this.jTextFieldAS2Id.setBackground(this.errorColor);
            }
            error = true;
        }
        return (error);
    }

    /**
     * Checks if new name is unique and changes color in textfield if not
     */
    private boolean checkForNonUniqueOrInvalidValues(Partner checkPartner, List<Partner> partnerList) {
        boolean error = false;
        error = error | this.checkForNonUniqueValues(checkPartner, partnerList);
        error = error | this.checkURLProtocol(checkPartner);
        return (error);
    }

    public void computeErrorState() {
        if (!this.changesAllowed) {
            this.setEnabled(false);
            return;
        } else {
            final List<Partner> partnerList = this.tree.getAllPartner();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    boolean errorInConfig = false;
                    for (Partner checkPartner : partnerList) {
                        boolean error = JButtonPartnerConfigOk.this.checkForNonUniqueOrInvalidValues(checkPartner, partnerList);
                        boolean hasErrorBefore = checkPartner.hasConfigError();
                        if (error != hasErrorBefore) {
                            checkPartner.setConfigError(error);
                            JButtonPartnerConfigOk.this.tree.partnerChanged(checkPartner);
                        }
                        if (error) {
                            errorInConfig = true;
                        }
                    }
                    JButtonPartnerConfigOk.this.setEnabled(!errorInConfig);
                }
            };
            SwingUtilities.invokeLater(runnable);
        }
    }

}
