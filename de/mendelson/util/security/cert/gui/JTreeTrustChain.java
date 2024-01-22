//$Header: /as4/de/mendelson/util/security/cert/gui/JTreeTrustChain.java 7     25/08/22 10:38 Heller $
package de.mendelson.util.security.cert.gui;

import de.mendelson.util.security.DNUtil;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.tree.SortableTreeNode;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Tree to display the trust chain of a certificate
 *
 * @author S.Heller
 * @version $Revision: 7 $
 */
public class JTreeTrustChain extends JTree {

    /**
     * This is the root node
     */
    private SortableTreeNode root;

    /**
     * Tree constructor
     */
    public JTreeTrustChain() {
        super(new SortableTreeNode());
        this.setRootVisible(true);
        this.root = (SortableTreeNode) this.getModel().getRoot();
        this.setCellRenderer(new TreeCellRendererTrustChain());
        this.setRowHeight(TreeCellRendererTrustChain.ROW_HEIGHT);
    }

    /**
     * Builds up the tree
     */
    public void buildTree(List<KeystoreCertificate> trustChain) {
        this.root.removeAllChildren();
        ((DefaultTreeModel) this.getModel()).nodeStructureChanged(this.root);
        //check if first cert is untrusted
        SortableTreeNode parent = null;
        KeystoreCertificate firstCert = trustChain.get(0);
        if (!firstCert.getIssuerDN().equals(firstCert.getSubjectDN())) {
            StringBuilder text = new StringBuilder();
            text.append(DNUtil.getCommonName(firstCert.getX509Certificate(), DNUtil.ISSUER));
            text.append( " [");
            text.append(DNUtil.getOrganization(firstCert.getX509Certificate(), DNUtil.ISSUER));
            text.append( "]");
            this.root.setUserObject(text.toString());
            SortableTreeNode child = new SortableTreeNode(firstCert);
            this.root.add(child);
            ((DefaultTreeModel) this.getModel()).nodeStructureChanged(this.root);
            parent = child;
        } else {
            this.root.setUserObject(trustChain.get(0));
            parent = this.root;
        }
        for (int i = 1; i < trustChain.size(); i++) {
            SortableTreeNode child = new SortableTreeNode(trustChain.get(i));
            parent.add(child);
            ((DefaultTreeModel) this.getModel()).nodeStructureChanged(parent);
            parent = child;
        }
        ((DefaultTreeModel) this.getModel()).nodeStructureChanged(parent);
        this.expandPath(new TreePath(parent.getPath()));
        this.setSelectionPath(new TreePath(parent.getPath()));
    }

    /**
     * Returns the selected node of the Tree
     */
    public SortableTreeNode getSelectedNode() {
        TreePath path = this.getSelectionPath();
        if (path != null) {
            return ((SortableTreeNode) path.getLastPathComponent());
        }
        return (null);
    }

    public void partnerChanged(KeystoreCertificate certificate) {
        synchronized (this.getModel()) {
            for (int i = 0; i < this.root.getChildCount(); i++) {
                SortableTreeNode child = (SortableTreeNode) root.getChildAt(i);
                KeystoreCertificate foundCertificate = (KeystoreCertificate) child.getUserObject();
                if (foundCertificate.equals(certificate)) {
                    ((DefaultTreeModel) this.getModel()).nodeChanged(child);
                }
            }
        }
    }

}
