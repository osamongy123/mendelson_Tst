//$Header: /mendelson_business_integration/de/mendelson/util/security/cert/gui/keygeneration/JDialogEditSubjectAlternativeNames.java 4     27 $
package de.mendelson.util.security.cert.gui.keygeneration;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import org.bouncycastle.asn1.x509.GeneralName;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Dialog that is shown if multiple files should be closed
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class JDialogEditSubjectAlternativeNames extends JDialog implements ListSelectionListener {

    private boolean canceled = true;
    /**
     * ResourceBundle to localize the output
     */
    private MecResourceBundle rb = null;
    private JFrame parent;
    private List<GeneralName> namesList;

    private final static MendelsonMultiResolutionImage ICON_ADD
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/keygeneration/add.svg", 24, 48);
    private final static MendelsonMultiResolutionImage ICON_DELETE
            = MendelsonMultiResolutionImage.fromSVG("/de/mendelson/util/security/cert/gui/keygeneration/delete.svg", 24, 48);

    public JDialogEditSubjectAlternativeNames(JFrame parent, List<GeneralName> namesList) {
        super(parent, true);
        this.namesList = namesList;
        this.parent = parent;
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleDialogSubjectAlternativeNames.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
        setTitle(this.rb.getResourceString("title"));
        initComponents();
        this.setMultiresolutionIcons();
        this.jLabelInfo.setText("<HTML>" + this.rb.getResourceString("info") + "</HTML>");
        ((TableModelSubjectAlternativeNames) this.jTable.getModel()).passNewData(this.parent, namesList);
        this.jTable.getTableHeader().setReorderingAllowed(false);
        this.jTable.getSelectionModel().addListSelectionListener(this);
        TableColumn columnType = this.jTable.getColumn(this.jTable.getColumnName(0));
        List<String> allValues = new ArrayList<String>();
        allValues.add(TagNo.DIR_NAME);
        allValues.add(TagNo.DNS_NAME);
        allValues.add(TagNo.IP_ADDRESS);
        allValues.add(TagNo.REGISTERED_ID);
        allValues.add(TagNo.RFC822_NAME);
        allValues.add(TagNo.URI);
        //The following values are in the API but seem not to work during the generation process:
        //allValues.add( TableModelSubjectAlternativeNames.TagNo.X400);    
        //allValues.add( TableModelSubjectAlternativeNames.TagNo.EDI_PARTY_NAME);
        columnType.setCellEditor(new TableCellEditorSubjectAlternativeNames(allValues));
        this.getRootPane().setDefaultButton(this.jButtonOk);
        this.setButtonState();
    }
    
    /**
     * Overwrite the designers icons by multi resolution icons
     */
    private void setMultiresolutionIcons() {
        this.jButtonAdd.setIcon(new ImageIcon(ICON_ADD));
        this.jButtonDel.setIcon(new ImageIcon(ICON_DELETE));
    }

    public List<GeneralName> getNewValue() {
        return (this.namesList);
    }

    private void setButtonState() {
        this.jButtonDel.setEnabled(this.jTable.getSelectedRow() != -1);
    }

    public boolean getCanceled() {
        return (this.canceled);
    }

    private void addRow() {
        ((TableModelSubjectAlternativeNames) this.jTable.getModel()).addRow();
        this.jTable.getSelectionModel().setSelectionInterval(this.jTable.getRowCount() - 1, this.jTable.getRowCount() - 1);
    }

    private void deleteCurrentRow() {
        int selectedRow = this.jTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        ((TableModelSubjectAlternativeNames) this.jTable.getModel()).delRow(selectedRow);
        if (selectedRow > this.jTable.getRowCount() - 1) {
            selectedRow--;
        }
        if (selectedRow < 0) {
            return;
        }
        this.jTable.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
    }

    private void okPressed() {
        this.canceled = false;
        this.setVisible(false);
        this.namesList.clear();
        this.namesList.addAll(((TableModelSubjectAlternativeNames) this.jTable.getModel()).getNamesList());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanelMain = new javax.swing.JPanel();
        jLabelInfo = new javax.swing.JLabel();
        jScrollPane = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jButtonAdd = new javax.swing.JButton();
        jButtonDel = new javax.swing.JButton();
        jPanelButtons = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelMain.setLayout(new java.awt.GridBagLayout());

        jLabelInfo.setText("Info Text");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(25, 10, 15, 10);
        jPanelMain.add(jLabelInfo, gridBagConstraints);

        jTable.setModel(new de.mendelson.util.security.cert.gui.keygeneration.TableModelSubjectAlternativeNames());
        jTable.setShowHorizontalLines(false);
        jTable.setShowVerticalLines(false);
        jScrollPane.setViewportView(jTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelMain.add(jScrollPane, gridBagConstraints);

        jButtonAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/keygeneration/missing_image24x24.gif"))); // NOI18N
        jButtonAdd.setText(this.rb.getResourceString( "label.add")
        );
        jButtonAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 10);
        jPanelMain.add(jButtonAdd, gridBagConstraints);

        jButtonDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/util/security/cert/gui/keygeneration/missing_image24x24.gif"))); // NOI18N
        jButtonDel.setText(this.rb.getResourceString( "label.del"));
        jButtonDel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonDel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanelMain.add(jButtonDel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jPanelMain, gridBagConstraints);

        jPanelButtons.setLayout(new java.awt.GridBagLayout());

        jButtonOk.setText(this.rb.getResourceString( "button.ok" ));
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelButtons.add(jButtonOk, gridBagConstraints);

        jButtonCancel.setText(this.rb.getResourceString( "button.cancel" ));
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelButtons.add(jButtonCancel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanelButtons, gridBagConstraints);

        setSize(new java.awt.Dimension(581, 443));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        this.okPressed();
    }//GEN-LAST:event_jButtonOkActionPerformed

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        this.addRow();
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void jButtonDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDelActionPerformed
        this.deleteCurrentRow();
    }//GEN-LAST:event_jButtonDelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonDel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabelInfo;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JTable jTable;
    // End of variables declaration//GEN-END:variables

    @Override
    /**
     * Makes this a ListSelectionListener
     */
    public void valueChanged(ListSelectionEvent e) {
        this.setButtonState();
    }
}
