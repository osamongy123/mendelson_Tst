//$Header: /as2/de/mendelson/util/clientserver/clients/filesystemview/RemoteFileBrowser.java 17    29.10.19 11:25 Heller $Revision: 1 $
package de.mendelson.util.clientserver.clients.filesystemview;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.BaseClient;
import java.io.File;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Browser widget for remote files/directories
 *
 * @author S.Heller
 * @version $Revision: 17 $
 */
public class RemoteFileBrowser extends JDialog {

    private String selectedPath = null;
    private String selectedFilename = null;
    private FileSystemViewClientServer fileView;
    private MecResourceBundle rb;
    private TreeSelectionListener treeChangeListener;
    private String preselectedPath = null;

    /**
     * Creates new form RemoteFileBrowser
     */
    public RemoteFileBrowser(JFrame parent, BaseClient client, String title) {
        super(parent, true);
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleFileBrowser.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
        this.setTitle(title);
        initComponents();
        //generate a gap to the scroll pane
        this.jTreeRemoteStructure.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        this.fileView = new FileSystemViewClientServer(client);
        List<FileObjectRoot> rootList = this.fileView.listRoots();
        this.jTreeRemoteStructure.addRoots(rootList);
        this.getRootPane().setDefaultButton(RemoteFileBrowser.this.jButtonOk);
    }

    public void setFileFilter(FileFilter fileFilter) {
        if (fileFilter != null) {
            this.fileView.setFileFilter(fileFilter);
        }
    }

    @Override
    public void setVisible(boolean flag) {
        if (flag) {
            this.treeChangeListener = new TreeSelectionListener() {
                @Override
                public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                    DefaultMutableTreeNode selectedNode = RemoteFileBrowser.this.jTreeRemoteStructure.getSelectedNode();
                    if (selectedNode != null && selectedNode.getUserObject() instanceof FileObject) {
                        RemoteFileBrowser.this.jTextFieldActualPath.setText(
                                ((FileObject) selectedNode.getUserObject()).getAbsolutePathDisplayOnServerSide());
                    }
                }
            };
            this.jTreeRemoteStructure.addTreeSelectionListener(this.treeChangeListener);
            if (this.preselectedPath != null) {
                this.jTextFieldActualPath.setText(this.preselectedPath);
                this.updateTreeSelectionForCurrentPathInTextField();
            }
        }
        super.setVisible(flag);
    }

    private void updateTreeSelectionForCurrentPathInTextField() {
        this.jTreeRemoteStructure.removeTreeSelectionListener(this.treeChangeListener);
        List<FileObject> pathList = this.fileView.getPathElements(this.jTextFieldActualPath.getText());
        for (FileObject object : pathList) {
            //if the path does NOT exist there could be nothing added - the
            //path is invalid on the server side
            if (this.jTreeRemoteStructure.nodeexists(object)) {
                List<FileObject> childList = this.fileView.listChildren(object.getAbsolutePathDisplayOnServerSide());
                this.jTreeRemoteStructure.addChildren(object, childList);
            }
        }
        if (!pathList.isEmpty()) {
            FileObject selection = pathList.get(pathList.size() - 1);
            this.jTreeRemoteStructure.setSelectedNode(selection);
        }
        this.jTreeRemoteStructure.addTreeSelectionListener(this.treeChangeListener);
    }

    /**
     * Sets a preselection on the server. Nothing will happen if the preselected
     * path does not exist on the server side
     *
     * @param path
     */
    public void setSelectedFile(String preselectedFilename) {
        this.preselectedPath = this.fileView.getAbsolutePathStr(preselectedFilename);
    }

    private void performLazyLoad(TreePath path) {
        final DefaultMutableTreeNode nodeToExpand = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (!this.jTreeRemoteStructure.isExplored(nodeToExpand)) {
            Runnable lazyLoad = new Runnable() {
                @Override
                public void run() {
                    //unexplored node, reload it from remote
                    FileObject parent = (FileObject) nodeToExpand.getUserObject();
                    List<FileObject> childList = RemoteFileBrowser.this.fileView.listChildren(parent.getAbsolutePathDisplayOnServerSide());
                    RemoteFileBrowser.this.jTreeRemoteStructure.addChildren(parent, childList);
                }
            };
            SwingUtilities.invokeLater(lazyLoad);
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

        jPanelMain = new javax.swing.JPanel();
        jScrollPaneRemote = new javax.swing.JScrollPane();
        jTreeRemoteStructure = new de.mendelson.util.clientserver.clients.filesystemview.JTreeRemoteStructure();
        jTextFieldActualPath = new javax.swing.JTextField();
        jPanelButtons = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanelMain.setLayout(new java.awt.GridBagLayout());

        jTreeRemoteStructure.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
            public void treeCollapsed(javax.swing.event.TreeExpansionEvent evt) {
            }
            public void treeExpanded(javax.swing.event.TreeExpansionEvent evt) {
                jTreeRemoteStructureTreeExpanded(evt);
            }
        });
        jScrollPaneRemote.setViewportView(jTreeRemoteStructure);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMain.add(jScrollPaneRemote, gridBagConstraints);

        jTextFieldActualPath.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        jTextFieldActualPath.setBorder(null);
        jTextFieldActualPath.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldActualPathKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanelMain.add(jTextFieldActualPath, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.ipady = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jPanelMain, gridBagConstraints);

        jPanelButtons.setLayout(new java.awt.GridBagLayout());

        jButtonOk.setText(this.rb.getResourceString( "button.ok"));
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 5);
        jPanelButtons.add(jButtonOk, gridBagConstraints);

        jButtonCancel.setText(this.rb.getResourceString( "button.cancel"));
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 10);
        jPanelButtons.add(jButtonCancel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanelButtons, gridBagConstraints);

        setSize(new java.awt.Dimension(509, 534));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTreeRemoteStructureTreeExpanded(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_jTreeRemoteStructureTreeExpanded
        this.performLazyLoad(evt.getPath());
    }//GEN-LAST:event_jTreeRemoteStructureTreeExpanded

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        DefaultMutableTreeNode selectedNode = this.jTreeRemoteStructure.getSelectedNode();
        if (selectedNode != null && selectedNode.getUserObject() instanceof FileObject) {
            this.selectedPath = this.jTextFieldActualPath.getText();
            this.selectedFilename = new File(this.jTextFieldActualPath.getText()).getName();
        }
        this.setVisible(false);
    }//GEN-LAST:event_jButtonOkActionPerformed

    private void jTextFieldActualPathKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldActualPathKeyReleased
        this.updateTreeSelectionForCurrentPathInTextField();
    }//GEN-LAST:event_jTextFieldActualPathKeyReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JScrollPane jScrollPaneRemote;
    private javax.swing.JTextField jTextFieldActualPath;
    private de.mendelson.util.clientserver.clients.filesystemview.JTreeRemoteStructure jTreeRemoteStructure;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the selectedPath
     */
    public String getSelectedPath() {
        return selectedPath;
    }

    /**
     * @return the selected filename only, without path
     */
    public String getSelectedFilename() {
        return selectedFilename;
    }

    /**
     * @param directoriesOnly the directoriesOnly to set
     */
    public void setDirectoriesOnly(boolean directoriesOnly) {
        this.jTreeRemoteStructure.setDirectoriesOnly(directoriesOnly);
    }
}
