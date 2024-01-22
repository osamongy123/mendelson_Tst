//$Header: /mec_as2/de/mendelson/comm/as2/client/about/AboutDialog.java 10    3/02/22 14:35 Heller $
package de.mendelson.comm.as2.client.about;
import javax.swing.*;
import de.mendelson.Copyright;
import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.client.AS2Gui;
import de.mendelson.util.ColorUtil;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.font.FontUtil;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Dialog to show the about info
 * @author S.Heller
 * @version $Revision: 10 $
 */
public class AboutDialog extends JDialog {
    
    /**Localize the GUI*/
    private MecResourceBundle rb = null;
    
    /** Creates new form AboutDialog */
    public AboutDialog( JFrame parent ) {
        super(parent, true);
        
        //load resource bundle
        try{
            this.rb = (MecResourceBundle)ResourceBundle.getBundle(
                    ResourceBundleAboutDialog.class.getName());
        } catch ( MissingResourceException e ) {
            throw new RuntimeException( "Oops..resource bundle "
                    + e.getClassName() + " not found." );
        }
        this.setTitle( this.rb.getResourceString( "title" ));
        this.initComponents();
        this.setMultiresolutionIcons();        
        this.getRootPane().setDefaultButton( this.jButtonOk );
        Font font = FontUtil.getProductFont(FontUtil.STYLE_PRODUCT_PLAIN, 12);
        Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
        attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        this.jLabelProductName.setFont(font.deriveFont(attributes));
        this.jLabelProductName.setText( 
                AS2ServerVersion.getProductName()
                + " " + AS2ServerVersion.getVersion()
                + " " + AS2ServerVersion.getBuild());
        this.jLabelBuildDate.setText(
                this.rb.getResourceString( "builddate",
                new Object[]{ AS2ServerVersion.getLastModificationDate() }));
        
        this.jLabelCopyright.setText( Copyright.getCopyrightMessage() );
        this.jLabelStreet.setText( AS2ServerVersion.getStreet() );
        this.jLabelZip.setText( AS2ServerVersion.getZip());
        this.jLabelEMail.setText( AS2ServerVersion.getInfoEmail() );
        //create hyperlinks - or not if this is not supported by the OS       
        if (Desktop.isDesktopSupported()) {
            this.jLabelWebsiteGerman.setText("<html><a href=\"\">http://www.mendelson.de/</a></html>");
            this.jLabelWebsiteGerman.setCursor(new Cursor(Cursor.HAND_CURSOR));
            this.jLabelWebsiteGerman.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        Desktop.getDesktop().browse(new URI("http://www.mendelson.de"));
                    } catch (Exception ex) {
                        //ignore
                    }
                }
            });
            this.jLabelWebSiteInternational.setText("<html><a href=\"\">http://www.mendelson-e-c.com/</a></html>");
            this.jLabelWebSiteInternational.setCursor(new Cursor(Cursor.HAND_CURSOR));
            this.jLabelWebSiteInternational.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        Desktop.getDesktop().browse(new URI("http://www.mendelson-e-c.com"));
                    } catch (Exception ex) {
                        //ignore
                    }
                }
            });
        }
        this.displayLicense();
        Color foregroundColor = ColorUtil.getBestContrastColorAroundForeground(UIManager.getColor("Label.background"),
                FontUtil.getFontColor(FontUtil.PRODUCT_OFTP2_COMMUNITY));
        this.jLabelCopyright.setForeground(foregroundColor);
        this.jLabelStreet.setForeground(foregroundColor);
        this.jLabelZip.setForeground(foregroundColor);
        this.jLabelEMail.setForeground(foregroundColor);
        this.jLabelProductName.setForeground(foregroundColor);
        this.jLabelBuildDate.setForeground(foregroundColor);
    }
    
    private void setMultiresolutionIcons() {
        this.jLabelImage.setIcon(new ImageIcon(AS2Gui.IMAGE_PRODUCT_LOGO_WITH_TEXT.toMinResolution(120)));
    }
    
    /**Displays the license text in the GUI*/
    private void displayLicense(){
        try{
            Path file = Paths.get( "license/LICENSE.gpl.txt" );
            String text = Files.readString(file);
            this.jTextAreaLicense.setText( text );
            this.jTextAreaLicense.setCaretPosition( 0 );
        } catch( Exception e ){
            this.jTextAreaLicense.setText( "License agreement not found." );
        }
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane = new javax.swing.JTabbedPane();
        jPanelMain = new javax.swing.JPanel();
        jPanelInfo = new javax.swing.JPanel();
        jPanelSpace = new javax.swing.JPanel();
        jLabelProductName = new javax.swing.JLabel();
        jLabelCopyright = new javax.swing.JLabel();
        jLabelImage = new javax.swing.JLabel();
        jLabelStreet = new javax.swing.JLabel();
        jLabelZip = new javax.swing.JLabel();
        jLabelEMail = new javax.swing.JLabel();
        jLabelBuildDate = new javax.swing.JLabel();
        jLabelWebsiteGerman = new javax.swing.JLabel();
        jLabelWebSiteInternational = new javax.swing.JLabel();
        jPanelSep = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanelLicense = new javax.swing.JPanel();
        jScrollPaneLicense = new javax.swing.JScrollPane();
        jTextAreaLicense = new javax.swing.JTextArea();
        jPanelButton = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelMain.setLayout(new java.awt.GridBagLayout());

        jPanelInfo.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelInfo.add(jPanelSpace, gridBagConstraints);

        jLabelProductName.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabelProductName.setText("<productName>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 5, 5);
        jPanelInfo.add(jLabelProductName, gridBagConstraints);

        jLabelCopyright.setText("<copyright>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanelInfo.add(jLabelCopyright, gridBagConstraints);

        jLabelImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/mendelson/comm/as2/client/about/missing_image32x32.gif"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 10, 15);
        jPanelInfo.add(jLabelImage, gridBagConstraints);

        jLabelStreet.setText("<street>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanelInfo.add(jLabelStreet, gridBagConstraints);

        jLabelZip.setText("<zip>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanelInfo.add(jLabelZip, gridBagConstraints);

        jLabelEMail.setText("<email>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelInfo.add(jLabelEMail, gridBagConstraints);

        jLabelBuildDate.setText("<build date>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelInfo.add(jLabelBuildDate, gridBagConstraints);

        jLabelWebsiteGerman.setText("http://www.mendelson-e-c.com");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 15, 5);
        jPanelInfo.add(jLabelWebsiteGerman, gridBagConstraints);

        jLabelWebSiteInternational.setText("http://www.mendelson.de");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 0, 5);
        jPanelInfo.add(jLabelWebSiteInternational, gridBagConstraints);

        jPanelSep.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanelSep.add(jSeparator1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 5);
        jPanelInfo.add(jPanelSep, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelMain.add(jPanelInfo, gridBagConstraints);

        jTabbedPane.addTab(this.rb.getResourceString( "tab.about" ), jPanelMain);

        jPanelLicense.setLayout(new java.awt.GridBagLayout());

        jTextAreaLicense.setColumns(20);
        jTextAreaLicense.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTextAreaLicense.setRows(5);
        jScrollPaneLicense.setViewportView(jTextAreaLicense);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelLicense.add(jScrollPaneLicense, gridBagConstraints);

        jTabbedPane.addTab(this.rb.getResourceString( "tab.license" ), jPanelLicense);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jTabbedPane, gridBagConstraints);

        jPanelButton.setLayout(new java.awt.GridBagLayout());

        jButtonOk.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jButtonOk.setText(this.rb.getResourceString( "button.ok" ));
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanelButton.add(jButtonOk, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanelButton, gridBagConstraints);

        setSize(new java.awt.Dimension(598, 558));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    
    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        this.setVisible( false );
        this.dispose();
    }//GEN-LAST:event_jButtonOkActionPerformed
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_closeDialog
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabelBuildDate;
    private javax.swing.JLabel jLabelCopyright;
    private javax.swing.JLabel jLabelEMail;
    private javax.swing.JLabel jLabelImage;
    private javax.swing.JLabel jLabelProductName;
    private javax.swing.JLabel jLabelStreet;
    private javax.swing.JLabel jLabelWebSiteInternational;
    private javax.swing.JLabel jLabelWebsiteGerman;
    private javax.swing.JLabel jLabelZip;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelInfo;
    private javax.swing.JPanel jPanelLicense;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JPanel jPanelSep;
    private javax.swing.JPanel jPanelSpace;
    private javax.swing.JScrollPane jScrollPaneLicense;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JTextArea jTextAreaLicense;
    // End of variables declaration//GEN-END:variables
    
}
