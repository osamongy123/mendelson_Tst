//$Header: /as2/de/mendelson/comm/as2/cem/gui/DialogCEMOverview.java 42    14.10.19 15:54 Heller $
package de.mendelson.comm.as2.cem.gui;

import de.mendelson.comm.as2.cem.CEMEntry;
import de.mendelson.comm.as2.cem.clientserver.CEMCancelRequest;
import de.mendelson.comm.as2.cem.clientserver.CEMDeleteRequest;
import de.mendelson.comm.as2.cem.clientserver.CEMListRequest;
import de.mendelson.comm.as2.cem.clientserver.CEMListResponse;
import de.mendelson.comm.as2.clientserver.message.RefreshClientCEMDisplay;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.AS2Payload;
import de.mendelson.comm.as2.message.clientserver.MessagePayloadRequest;
import de.mendelson.comm.as2.message.clientserver.MessagePayloadResponse;
import de.mendelson.comm.as2.message.clientserver.MessageRequestLastMessage;
import de.mendelson.comm.as2.message.clientserver.MessageResponseLastMessage;
import de.mendelson.comm.as2.message.loggui.DialogMessageDetails;
import de.mendelson.comm.as2.partner.gui.TableCellRendererPartner;
import de.mendelson.util.LayoutManagerJToolbar;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.clientserver.ClientsideMessageProcessor;
import de.mendelson.util.clientserver.GUIClient;
import de.mendelson.util.clientserver.messages.ClientServerMessage;
import de.mendelson.util.clientserver.messages.ClientServerResponse;
import de.mendelson.util.log.JTextPaneLoggingHandler;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.security.cert.TableCellRendererCertificates;
import de.mendelson.util.tables.JTableColumnResizer;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Gives an overview on all CEM messages
 *
 * @author S.Heller
 * @version $Revision: 42 $
 */
public class DialogCEMOverview extends JDialog implements ListSelectionListener, ClientsideMessageProcessor {

    private final static MendelsonMultiResolutionImage ICON_EXIT
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/cem/gui/exit.svg", 24, 48);
    private final static MendelsonMultiResolutionImage ICON_DELETE
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/cem/gui/delete.svg", 24, 48);
    private final static MendelsonMultiResolutionImage ICON_MESSAGEDETAILS
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/cem/gui/messagedetails.svg", 24, 48);
    private final static MendelsonMultiResolutionImage ICON_CEM
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/comm/as2/cem/gui/cem.svg", 24, 48);
    
    /**
     * Manages all internal certificates
     */
    private CertificateManager certificateManagerEncSign;
    /**
     * localizes the GUI
     */
    private MecResourceBundle rb;
    private GUIClient guiClient;
    private Logger logger = Logger.getLogger("de.mendelson.as2.client");
    private JTextPaneLoggingHandler handler;

    /**
     * Creates new form DialogCEMOverview
     */
    public DialogCEMOverview(JFrame parent, GUIClient guiClient, CertificateManager certificateManagerEncSign,
            JTextPaneLoggingHandler handler) {
        super(parent, true);
        this.handler = handler;
        this.guiClient = guiClient;
        this.certificateManagerEncSign = certificateManagerEncSign;
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleCEMOverview.class.getName());
        } //load up  resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        initComponents();
        this.setMultiresolutionIcons();
        this.jToolBar.setLayout(new LayoutManagerJToolbar());
        List<CEMEntry> cemEntries = ((CEMListResponse) this.guiClient.getBaseClient().sendSync(new CEMListRequest())).getList();
        ((TableModelCEMOverview) (this.jTable.getModel())).passNewData(cemEntries);
        this.jTable.getColumnModel().getColumn(0).setCellRenderer(new TableCellRendererCEMSystemState());
        this.jTable.getColumnModel().getColumn(1).setCellRenderer(new TableCellRendererCEMState(this.guiClient.getBaseClient()));
        this.jTable.getColumnModel().getColumn(3).setCellRenderer(new TableCellRendererPartner(this.guiClient.getBaseClient()));
        this.jTable.getColumnModel().getColumn(4).setCellRenderer(new TableCellRendererPartner(this.guiClient.getBaseClient()));
        this.jTable.getColumnModel().getColumn(5).setCellRenderer(new TableCellRendererCertificates(this.certificateManagerEncSign,
                TableCellRendererCertificates.TYPE_ISSUER_SERIAL));
        JTableColumnResizer.adjustColumnWidthByContent(this.jTable);
        this.jTable.getSelectionModel().addListSelectionListener(this);
        this.jTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.jTabbedPane.remove(this.jPanelReasonForRejection);
        //this gui may process server messages, register it
        this.guiClient.addMessageProcessor(this);
        this.setButtonState();
    }

    private void setMultiresolutionIcons() {
        this.jButtonExit.setIcon(new ImageIcon(ICON_EXIT));
        this.jButtonSendCEM.setIcon(new ImageIcon(ICON_CEM));
        this.jButtonDisplayRequestDetails.setIcon(new ImageIcon(ICON_MESSAGEDETAILS));
        this.jButtonDisplayResponseDetails.setIcon(new ImageIcon(ICON_MESSAGEDETAILS));
        this.jButtonRemove.setIcon(new ImageIcon(ICON_DELETE));
        this.jButtonCancel.setIcon(new ImageIcon(ICON_DELETE));
    }
    
    private void setButtonState() {
        int selectedRow = this.jTable.getSelectedRow();
        boolean responseExists = false;
        boolean isPending = false;
        if (selectedRow >= 0) {
            CEMEntry entry = ((TableModelCEMOverview) this.jTable.getModel()).getRowAt(selectedRow);
            responseExists = entry.getResponseMessageid() != null;
            CEMSystemActivity activity = new CEMSystemActivity(entry);
            isPending = activity.getState() == CEMEntry.STATUS_PENDING_INT;
        }
        this.jButtonDisplayRequestDetails.setEnabled(selectedRow >= 0);
        this.jButtonDisplayResponseDetails.setEnabled(responseExists);
        this.jButtonCancel.setEnabled(isPending);
        this.jButtonRemove.setEnabled(selectedRow >= 0);
    }

    private void refresh() {
        List<CEMEntry> list = ((CEMListResponse) this.guiClient.getBaseClient().sendSync(new CEMListRequest())).getList();
        //store the current selection
        int selectionIndex = this.jTable.getSelectedRow();
        CEMEntry selectedEntry = null;
        if (selectionIndex >= 0) {
            selectedEntry = ((TableModelCEMOverview) (this.jTable.getModel())).getRowAt(selectionIndex);
        }
        ((TableModelCEMOverview) (this.jTable.getModel())).passNewData(list);
        if (selectedEntry != null) {
            int newIndex = list.lastIndexOf(selectedEntry);
            this.jTable.getSelectionModel().setSelectionInterval(newIndex, newIndex);
        }
    }

    /**
     * Updates the actual selected rows content
     */
    private void updateRowDetails() {
        int selectedRow = this.jTable.getSelectedRow();
        if (selectedRow < 0) {
            this.jTextAreaDetails.setText("");
        } else {
            CEMEntry entry = ((TableModelCEMOverview) (this.jTable.getModel())).getRowAt(selectedRow);
            KeystoreCertificate certificate = this.certificateManagerEncSign.getKeystoreCertificateByIssuerDNAndSerial(
                    entry.getIssuername(), entry.getSerialId());
            if (certificate == null) {
                this.jTextAreaDetails.setText("");
            } else {
                this.jTextAreaDetails.setText(certificate.getInfo());
            }
            if (entry.getCemState() == CEMEntry.STATUS_REJECTED_INT && entry.getReasonForRejection() != null) {
                this.jTabbedPane.addTab(this.rb.getResourceString("tab.reasonforrejection"), this.jPanelReasonForRejection);
                this.jTextAreaReasonForRejection.setText(entry.getReasonForRejection());
            }
        }
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

        jToolBar = new javax.swing.JToolBar();
        jButtonExit = new javax.swing.JButton();
        jButtonSendCEM = new javax.swing.JButton();
        jButtonDisplayRequestDetails = new javax.swing.JButton();
        jButtonDisplayResponseDetails = new javax.swing.JButton();
        jButtonRemove = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jPanelMain = new javax.swing.JPanel();
        jSplitPane = new javax.swing.JSplitPane();
        jScrollPaneTable = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jTabbedPane = new javax.swing.JTabbedPane();
        jPanelCertificateInfo = new javax.swing.JPanel();
        jScrollPaneDetails = new javax.swing.JScrollPane();
        jTextAreaDetails = new javax.swing.JTextArea();
        jPanelReasonForRejection = new javax.swing.JPanel();
        jScrollPaneReasonForRejection = new javax.swing.JScrollPane();
        jTextAreaReasonForRejection = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(this.rb.getResourceString( "title"));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jToolBar.setFloatable(false);
        jToolBar.setRollover(true);

        jButtonExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/cem/gui/missing_image24x24.gif"))); // NOI18N
        jButtonExit.setText(this.rb.getResourceString( "button.exit"));
        jButtonExit.setFocusable(false);
        jButtonExit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonExit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonExit);

        jButtonSendCEM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/cem/gui/missing_image24x24.gif"))); // NOI18N
        jButtonSendCEM.setText(this.rb.getResourceString( "button.sendcem"));
        jButtonSendCEM.setFocusable(false);
        jButtonSendCEM.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSendCEM.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSendCEM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendCEMActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonSendCEM);

        jButtonDisplayRequestDetails.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/cem/gui/missing_image24x24.gif"))); // NOI18N
        jButtonDisplayRequestDetails.setText(this.rb.getResourceString( "button.requestdetails"));
        jButtonDisplayRequestDetails.setFocusable(false);
        jButtonDisplayRequestDetails.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonDisplayRequestDetails.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonDisplayRequestDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDisplayRequestDetailsActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonDisplayRequestDetails);

        jButtonDisplayResponseDetails.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/cem/gui/missing_image24x24.gif"))); // NOI18N
        jButtonDisplayResponseDetails.setText(this.rb.getResourceString( "button.responsedetails"));
        jButtonDisplayResponseDetails.setFocusable(false);
        jButtonDisplayResponseDetails.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonDisplayResponseDetails.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonDisplayResponseDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDisplayResponseDetailsActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonDisplayResponseDetails);

        jButtonRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/cem/gui/missing_image24x24.gif"))); // NOI18N
        jButtonRemove.setText(this.rb.getResourceString( "button.remove"));
        jButtonRemove.setFocusable(false);
        jButtonRemove.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonRemove.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonRemove);

        jButtonCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/cem/gui/missing_image24x24.gif"))); // NOI18N
        jButtonCancel.setText(this.rb.getResourceString( "button.cancel"));
        jButtonCancel.setFocusable(false);
        jButtonCancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonCancel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(jToolBar, gridBagConstraints);

        jPanelMain.setLayout(new java.awt.GridBagLayout());

        jSplitPane.setDividerLocation(200);
        jSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jTable.setModel(new TableModelCEMOverview());
        jTable.setShowHorizontalLines(false);
        jTable.setShowVerticalLines(false);
        jScrollPaneTable.setViewportView(jTable);

        jSplitPane.setLeftComponent(jScrollPaneTable);

        jPanelCertificateInfo.setLayout(new java.awt.GridBagLayout());

        jTextAreaDetails.setColumns(20);
        jTextAreaDetails.setEditable(false);
        jTextAreaDetails.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jTextAreaDetails.setRows(5);
        jScrollPaneDetails.setViewportView(jTextAreaDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelCertificateInfo.add(jScrollPaneDetails, gridBagConstraints);

        jTabbedPane.addTab(this.rb.getResourceString( "tab.certificate"), jPanelCertificateInfo);

        jPanelReasonForRejection.setLayout(new java.awt.GridBagLayout());

        jTextAreaReasonForRejection.setColumns(20);
        jTextAreaReasonForRejection.setEditable(false);
        jTextAreaReasonForRejection.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jTextAreaReasonForRejection.setRows(5);
        jScrollPaneReasonForRejection.setViewportView(jTextAreaReasonForRejection);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelReasonForRejection.add(jScrollPaneReasonForRejection, gridBagConstraints);

        jTabbedPane.addTab(this.rb.getResourceString("tab.reasonforrejection"), jPanelReasonForRejection);

        jSplitPane.setRightComponent(jTabbedPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelMain.add(jSplitPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jPanelMain, gridBagConstraints);

        setSize(new java.awt.Dimension(1055, 614));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSendCEMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendCEMActionPerformed
        JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
        DialogSendCEM dialog = new DialogSendCEM(parent, this.certificateManagerEncSign,
                this.guiClient.getBaseClient());
        dialog.setVisible(true);
        this.refresh();
    }//GEN-LAST:event_jButtonSendCEMActionPerformed

    private void jButtonDisplayRequestDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDisplayRequestDetailsActionPerformed
        int selectedRow = this.jTable.getSelectedRow();
        if (selectedRow >= 0) {
            CEMEntry entry = ((TableModelCEMOverview) this.jTable.getModel()).getRowAt(selectedRow);
            try {
                JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
                String messageId = entry.getRequestMessageid();
                MessageResponseLastMessage response = (MessageResponseLastMessage) this.guiClient.getBaseClient().sendSync(new MessageRequestLastMessage(messageId));
                if (response.getException() != null) {
                    throw response.getException();
                }
                AS2MessageInfo cemInfo = response.getInfo();
                if (cemInfo != null) {
                    List<AS2Payload> payloads = ((MessagePayloadResponse) this.guiClient.getBaseClient().sendSync(new MessagePayloadRequest(entry.getRequestMessageid()))).getList();
                    DialogMessageDetails dialog = new DialogMessageDetails(parent,
                            this.guiClient.getBaseClient(), cemInfo, payloads, this.handler);
                    dialog.setVisible(true);
                } else {
                    throw new Exception("Unable to get information about CEM process for unknown reason.");
                }
            } catch (Throwable e) {
                this.logger.warning("CEMOverview: No message details available for for message id " + entry.getRequestMessageid() + " - ["
                        + e.getClass().getSimpleName() + "]: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jButtonDisplayRequestDetailsActionPerformed

    private void jButtonDisplayResponseDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDisplayResponseDetailsActionPerformed
        int selectedRow = this.jTable.getSelectedRow();
        if (selectedRow >= 0) {
            CEMEntry entry = ((TableModelCEMOverview) this.jTable.getModel()).getRowAt(selectedRow);
            AS2MessageInfo cemInfo = ((MessageResponseLastMessage) this.guiClient.getBaseClient().sendSync(new MessageRequestLastMessage(entry.getResponseMessageid()))).getInfo();
            if (cemInfo != null) {
                List<AS2Payload> payloads = ((MessagePayloadResponse) this.guiClient.getBaseClient().sendSync(new MessagePayloadRequest(entry.getResponseMessageid()))).getList();
                JFrame parent = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this);
                DialogMessageDetails dialog = new DialogMessageDetails(parent,
                        this.guiClient.getBaseClient(), cemInfo, payloads, this.handler);
                dialog.setVisible(true);
            }
        }
    }//GEN-LAST:event_jButtonDisplayResponseDetailsActionPerformed

    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        //this gui must no longer process the server messages
        this.guiClient.removeMessageProcessor(this);
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButtonExitActionPerformed

    private void jButtonRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveActionPerformed
        int selectedRow = this.jTable.getSelectedRow();
        if (selectedRow >= 0) {
            TableModelCEMOverview model = (TableModelCEMOverview) this.jTable.getModel();
            //cancel the operation
            CEMEntry entry = model.getRowAt(selectedRow);
            this.guiClient.getBaseClient().sendSync(new CEMDeleteRequest(entry));
            this.refresh();
            if (selectedRow >= model.getRowCount()) {
                selectedRow = model.getRowCount() - 1;
            }
            //last row?
            if (model.getRowCount() == 0) {
                selectedRow = -1;
            }
            if (selectedRow >= 0) {
                this.jTable.setRowSelectionInterval(selectedRow, selectedRow);
            }
        }
    }//GEN-LAST:event_jButtonRemoveActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        int selectedRow = this.jTable.getSelectedRow();
        if (selectedRow >= 0) {
            CEMEntry entry = ((TableModelCEMOverview) this.jTable.getModel()).getRowAt(selectedRow);
            this.guiClient.getBaseClient().sendSync(new CEMCancelRequest(entry));
            this.refresh();
            this.jTable.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }//GEN-LAST:event_jButtonCancelActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonDisplayRequestDetails;
    private javax.swing.JButton jButtonDisplayResponseDetails;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonRemove;
    private javax.swing.JButton jButtonSendCEM;
    private javax.swing.JPanel jPanelCertificateInfo;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelReasonForRejection;
    private javax.swing.JScrollPane jScrollPaneDetails;
    private javax.swing.JScrollPane jScrollPaneReasonForRejection;
    private javax.swing.JScrollPane jScrollPaneTable;
    private javax.swing.JSplitPane jSplitPane;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTable jTable;
    private javax.swing.JTextArea jTextAreaDetails;
    private javax.swing.JTextArea jTextAreaReasonForRejection;
    private javax.swing.JToolBar jToolBar;
    // End of variables declaration//GEN-END:variables

    /**
     * Makes this a ListSelectionListener
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        this.jTabbedPane.remove(this.jPanelReasonForRejection);
        this.setButtonState();
        //display the selected rows content
        this.updateRowDetails();
    }

    @Override
    public boolean processMessageFromServer(ClientServerMessage message) {
        if (message instanceof RefreshClientCEMDisplay) {
            this.refresh();
            return (true);
        }
        return (false);
    }

    @Override
    public void processSyncResponseFromServer(ClientServerResponse response) {
    }
}
