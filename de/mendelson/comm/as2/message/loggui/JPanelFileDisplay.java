//$Header: /as2/de/mendelson/comm/as2/message/loggui/JPanelFileDisplay.java 27    15.12.20 11:39 Heller $
package de.mendelson.comm.as2.message.loggui;

import de.mendelson.util.AS2Tools;
import de.mendelson.util.FileEncodingDetection;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.BaseClient;
import de.mendelson.util.clientserver.clients.datatransfer.DownloadRequestFileLimited;
import de.mendelson.util.clientserver.clients.datatransfer.DownloadResponseFileLimited;
import de.mendelson.util.clientserver.clients.datatransfer.TransferClient;
import de.mendelson.util.xmleditorkit.XMLEditorKit;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Panel to display the content of a file
 *
 * @author S.Heller
 * @version $Revision: 27 $
 */
public class JPanelFileDisplay extends JPanel {

    public static final int EDITOR_TYPE_XML = 1;
    public static final int EDITOR_TYPE_RAW = 0;

    /**
     * Max filesize for the display of data in the panel, actual 1000kB
     */
    public final static long MAX_FILESIZE = (long) (1024 * Math.pow(2, 10));
    /**
     * Resourcebundle to localize the GUI
     */
    private MecResourceBundle rb = null;
    private BaseClient baseClient;

    /**
     * Creates new form JPanelFunctionGraph
     */
    public JPanelFileDisplay(BaseClient baseClient) {
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleFileDisplay.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
        this.baseClient = baseClient;
        this.initComponents();
        //this is just displayed if it is an image
        this.jPanelImage.setVisible(false);
        this.jScrollPaneImage.getVerticalScrollBar().setUnitIncrement(16);
        this.jEditorPaneXML.setEditorKit(new XMLEditorKit());
        this.jSplitPaneTextAndXML.setVisible(false);
    }

    /**
     * Loads a file to the editor and displays it
     */
    public void displayFile(String filename, boolean detectEncoding) {
        this.jLabelImage.setIcon(null);
        this.jPanelImage.setVisible(false);
        this.jLabelEncoding.setVisible(false);
        this.jScrollPaneTextEditor.setVisible(true);
        if (filename == null) {
            this.jTextFieldFilename.setText("");
            this.jEditorPaneRawText.setText(this.rb.getResourceString("no.file"));
            return;
        }
        TransferClient transferClient = new TransferClient(this.baseClient);
        try {
            DownloadRequestFileLimited request = new DownloadRequestFileLimited();
            request.setMaxSize(MAX_FILESIZE);
            request.setFilename(filename);
            DownloadResponseFileLimited response = (DownloadResponseFileLimited) transferClient.download(request);
            this.jTextFieldFilename.setText(response.getFullFilename());
            if (response.isSizeExceeded()) {
                this.jEditorPaneRawText.setText(this.rb.getResourceString("file.tolarge",
                        new Object[]{filename}));
            } else {
                byte[] data = response.getDataStream().readAllBytes();
                if (this.isImage(new ByteArrayInputStream(data))) {
                    ImageIcon icon = new ImageIcon(ImageIO.read(new ByteArrayInputStream(data)));
                    this.jLabelImage.setIcon(icon);
                    this.getToolkit().sync();
                    this.jScrollPaneTextEditor.setVisible(false);
                    this.jPanelImage.setVisible(true);
                } else {
                    if( detectEncoding ){
                        this.displayRawTextDetectEncoding(data);
                    }else{
                        this.displayRawTextIgnoreEncoding(data);
                    }
                    try {
                        this.jEditorPaneXML.read(new ByteArrayInputStream(data), data);
                        //the XML data is parsable and could be displayed: move the raw text editor to the split pane
                        this.jScrollPaneTextEditor.getParent().remove(this.jScrollPaneTextEditor);
                        this.jSplitPaneTextAndXML.setTopComponent(this.jScrollPaneTextEditor);
                        this.jSplitPaneTextAndXML.setVisible(true);
                    } catch (Throwable e) {
                        //its no parsable XML data: no action required
                    }
                }
            }
        } catch (Throwable e) {
            if (e instanceof FileNotFoundException) {
                this.jEditorPaneRawText.setText(this.rb.getResourceString("file.notfound",
                        filename));
            } else {
                this.jEditorPaneRawText.setText(e.getMessage());
            }
            return;
        } 
    }

    /**
     * Displays a byte array as raw text and tries to detect the encoding
     */
    private void displayRawTextDetectEncoding(byte[] data) throws Exception {
        Charset encoding = null;
        CharsetDecoder decoder = null;
        InputStream inStream = null;
        Reader reader = null;
        Path testFile = null;
        try {
            testFile = AS2Tools.createTempFile("encoding_testdata", ".txt");
            Files.write(testFile, data);
            FileEncodingDetection detection = new FileEncodingDetection();
            encoding = detection.guessBestCharset(testFile);
            this.jLabelEncoding.setVisible(true);
            this.jLabelEncoding.setText("[" + encoding.displayName() + "]");
            decoder = encoding.newDecoder().reset();
            inStream = new ByteArrayInputStream(data);
            reader = new InputStreamReader(inStream, decoder);
            this.jEditorPaneRawText.read(reader, null);
        } catch (CharacterCodingException e) {
            //ignore
        } finally {
            if (inStream != null) {
                inStream.close();
            }
            if (testFile != null) {
                try {
                    Files.delete(testFile);
                } catch (Exception e) {
                    //NOP
                }
            }
        }
    }

    /**
     * Displays a byte array as raw text and tries to detect the encoding
     */
    private void displayRawTextIgnoreEncoding(byte[] data) throws Exception {
        Charset encoding = Charset.defaultCharset();
        InputStream inStream = null;
        Path testFile = null;
        try {
            this.jLabelEncoding.setVisible(true);
            this.jLabelEncoding.setText("[" + encoding.displayName() + "]");
            inStream = new ByteArrayInputStream(data);
            this.jEditorPaneRawText.read(inStream, null);
        } finally {
            if (inStream != null) {
                inStream.close();
            }
            if (testFile != null) {
                try {
                    Files.delete(testFile);
                } catch (Exception e) {
                    //NOP
                }
            }
        }
    }
    
    /**
     * Checks if the passed stream is an image
     *
     * @param inStream
     * @return
     * @throws Exception
     */
    private boolean isImage(InputStream inStream) throws Exception {
        if (ImageIO.read(inStream) == null) {
            return (false);
        } else {
            return (true);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanelImage = new javax.swing.JPanel();
        jScrollPaneImage = new javax.swing.JScrollPane();
        jLabelImage = new javax.swing.JLabel();
        jScrollPaneTextEditor = new javax.swing.JScrollPane();
        jEditorPaneRawText = new javax.swing.JEditorPane();
        jSplitPaneTextAndXML = new javax.swing.JSplitPane();
        jPanelXMLStructure = new javax.swing.JPanel();
        jScrollPaneXML = new javax.swing.JScrollPane();
        jEditorPaneXML = new javax.swing.JEditorPane();
        jTextFieldFilename = new javax.swing.JTextField();
        jLabelEncoding = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        jPanelImage.setLayout(new java.awt.GridBagLayout());

        jScrollPaneImage.setViewportView(jLabelImage);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelImage.add(jScrollPaneImage, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanelImage, gridBagConstraints);

        jEditorPaneRawText.setEditable(false);
        jScrollPaneTextEditor.setViewportView(jEditorPaneRawText);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPaneTextEditor, gridBagConstraints);

        jSplitPaneTextAndXML.setDividerLocation(200);
        jSplitPaneTextAndXML.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanelXMLStructure.setLayout(new java.awt.GridBagLayout());

        jScrollPaneXML.setViewportView(jEditorPaneXML);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanelXMLStructure.add(jScrollPaneXML, gridBagConstraints);

        jSplitPaneTextAndXML.setBottomComponent(jPanelXMLStructure);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jSplitPaneTextAndXML, gridBagConstraints);

        jTextFieldFilename.setEditable(false);
        jTextFieldFilename.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(jTextFieldFilename, gridBagConstraints);

        jLabelEncoding.setText("[enc]");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jLabelEncoding, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane jEditorPaneRawText;
    private javax.swing.JEditorPane jEditorPaneXML;
    private javax.swing.JLabel jLabelEncoding;
    private javax.swing.JLabel jLabelImage;
    private javax.swing.JPanel jPanelImage;
    private javax.swing.JPanel jPanelXMLStructure;
    private javax.swing.JScrollPane jScrollPaneImage;
    private javax.swing.JScrollPane jScrollPaneTextEditor;
    private javax.swing.JScrollPane jScrollPaneXML;
    private javax.swing.JSplitPane jSplitPaneTextAndXML;
    private javax.swing.JTextField jTextFieldFilename;
    // End of variables declaration//GEN-END:variables
}
