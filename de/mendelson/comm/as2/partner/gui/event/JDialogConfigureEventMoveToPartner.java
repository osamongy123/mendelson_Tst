//$Header: /mec_as2/de/mendelson/comm/as2/partner/gui/event/JDialogConfigureEventMoveToPartner.java 4     18.12.20 14:25 Heller $
package de.mendelson.comm.as2.partner.gui.event;

import de.mendelson.comm.as2.client.AS2Gui;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerEventInformation;
import de.mendelson.comm.as2.partner.clientserver.PartnerListRequest;
import de.mendelson.comm.as2.partner.clientserver.PartnerListResponse;
import de.mendelson.comm.as2.partner.gui.ListCellRendererPartner;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.BaseClient;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Configure a shell execution command
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class JDialogConfigureEventMoveToPartner extends JDialog {

    private final MecResourceBundle rb;
    private JFrame parent;
    private Partner eventPartner;
    private int eventType;
    private List<Partner> partnerList;

    public JDialogConfigureEventMoveToPartner(JFrame parent, List<Partner> partnerList,
            Partner eventPartner, final int EVENT_TYPE) {
        super(parent, true);
        this.parent = parent;
        this.eventPartner = eventPartner;
        this.partnerList = partnerList;
        this.eventType = EVENT_TYPE;
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundlePartnerEvent.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.setTitle(this.rb.getResourceString("title.configuration.movetopartner",
                new Object[]{
                    eventPartner.getName(),
                    this.rb.getResourceString("type." + EVENT_TYPE)
                }
        ));
        initComponents();
        this.setMultiresolutionIcons();
        this.jComboBoxPartner.setRenderer(new ListCellRendererPartner());
        this.jLabelInfo.setText(this.rb.getResourceString("label.movetopartner.info"));
        this.jLabelTargetPartner.setText(this.rb.getResourceString("label.movetopartner",
                this.rb.getResourceString("type." + EVENT_TYPE)));
        this.displayParameter();
        this.getRootPane().setDefaultButton(this.jButtonOk);
    }

    private void setMultiresolutionIcons() {
        this.jLabelImage.setIcon(new ImageIcon(PartnerEventResource.IMAGE_PROCESS_MOVE_TO_PARTNER.toMinResolution(AS2Gui.IMAGE_SIZE_DIALOG)));
    }

    /**
     * Just fill in the parameter if the partner processtype is the one of this
     * dialog - else this is a create call
     *
     */
    private void displayParameter() {
        for (Partner partner : this.partnerList) {
            //makes no sense to forward messages to self or to a local station
            if (!partner.equals(this.eventPartner) && !partner.isLocalStation()) {
                this.jComboBoxPartner.addItem(partner);
            }
        }
        //no routing possibilities - no additional partners defined
        if (this.jComboBoxPartner.getItemCount() == 0) {
            this.jLabelTargetPartner.setVisible(false);
            this.jComboBoxPartner.setVisible(false);
            this.jLabelNoRoutingPartnersAvailable.setText( this.rb.getResourceString("label.movetopartner.noroutingpartner"));
            this.jButtonOk.setEnabled(false);
        } else {
            this.jLabelNoRoutingPartnersAvailable.setVisible(false);
            if (this.eventPartner.getPartnerEvents().getProcess(this.eventType)
                    == PartnerEventInformation.PROCESS_MOVE_TO_PARTNER) {
                List<String> parameter = this.eventPartner.getPartnerEvents().getParameter(this.eventType);
                if (parameter.size() > 0) {
                    for (Partner partner : this.partnerList) {
                        if (partner.getAS2Identification().equals(parameter.get(0))) {
                            this.jComboBoxPartner.setSelectedItem(partner);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void captureGUIValues() {
        List<String> newParameter = new ArrayList<String>();
        Partner partner = (Partner) this.jComboBoxPartner.getSelectedItem();
        newParameter.add(partner.getAS2Identification());
        this.eventPartner.getPartnerEvents().setParameter(this.eventType, newParameter);
        this.eventPartner.getPartnerEvents().setProcess(this.eventType, PartnerEventInformation.PROCESS_MOVE_TO_PARTNER);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanelButtons = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jLabelImage = new javax.swing.JLabel();
        jLabelInfo = new javax.swing.JLabel();
        jPanelMain = new javax.swing.JPanel();
        jLabelTargetPartner = new javax.swing.JLabel();
        jPanelSpace = new javax.swing.JPanel();
        jComboBoxPartner = new javax.swing.JComboBox<>();
        jLabelNoRoutingPartnersAvailable = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelButtons.setLayout(new java.awt.GridBagLayout());

        jButtonOk.setText(this.rb.getResourceString( "button.ok"));
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 5);
        jPanelButtons.add(jButtonOk, gridBagConstraints);

        jButtonCancel.setText(this.rb.getResourceString( "button.cancel")
        );
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 10);
        jPanelButtons.add(jButtonCancel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanelButtons, gridBagConstraints);

        jLabelImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/partner/gui/missing_image32x32.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(jLabelImage, gridBagConstraints);

        jLabelInfo.setText("Info");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        getContentPane().add(jLabelInfo, gridBagConstraints);

        jPanelMain.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanelMain.setLayout(new java.awt.GridBagLayout());

        jLabelTargetPartner.setText("Target Dir:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 20, 5);
        jPanelMain.add(jLabelTargetPartner, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanelMain.add(jPanelSpace, gridBagConstraints);

        jComboBoxPartner.setMinimumSize(new java.awt.Dimension(160, 22));
        jComboBoxPartner.setPreferredSize(new java.awt.Dimension(160, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanelMain.add(jComboBoxPartner, gridBagConstraints);

        jLabelNoRoutingPartnersAvailable.setText("lNoRoutingPartnersAvailable");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 20, 10);
        jPanelMain.add(jLabelNoRoutingPartnersAvailable, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(jPanelMain, gridBagConstraints);

        setSize(new java.awt.Dimension(668, 284));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        this.captureGUIValues();
        this.setVisible(false);
    }//GEN-LAST:event_jButtonOkActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JComboBox<Partner> jComboBoxPartner;
    private javax.swing.JLabel jLabelImage;
    private javax.swing.JLabel jLabelInfo;
    private javax.swing.JLabel jLabelNoRoutingPartnersAvailable;
    private javax.swing.JLabel jLabelTargetPartner;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelSpace;
    // End of variables declaration//GEN-END:variables
}
