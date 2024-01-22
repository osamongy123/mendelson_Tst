//$Header: /as2/de/mendelson/util/clientserver/clients/filesystemview/JTreeRemoteStructure.java 11    30.11.21 11:08 Heller $
package de.mendelson.util.clientserver.clients.filesystemview;

import de.mendelson.util.MecResourceBundle;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
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
 * Tree to display remote file structure
 *
 * @author S.Heller
 * @version $Revision: 11 $
 */
public class JTreeRemoteStructure extends JTree {

    private final DefaultMutableTreeNode root;
    private final Map<FileObject, DefaultMutableTreeNode> map = new ConcurrentHashMap<FileObject, DefaultMutableTreeNode>();
    private boolean directoriesOnly = false;
    private MecResourceBundle rb;

    /**
     * Holds a new partner ID for every created partner that is always negativ
     * but unique in this lifecycle
     */
    /**
     * Tree constructor
     */
    public JTreeRemoteStructure() {
        super(new DefaultMutableTreeNode());
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleFileBrowser.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
        this.setRootVisible(false);
        this.root = (DefaultMutableTreeNode) this.getModel().getRoot();
        TreeCellRendererFileBrowser treeCellRenderer = new TreeCellRendererFileBrowser();
        this.setCellRenderer(treeCellRenderer);
        int iconHeight = treeCellRenderer.getDefaultLeafIconHeight();
        this.setRowHeight(Math.max(this.getRowHeight(), iconHeight + 5));
    }

    public void addRoots(List<FileObjectRoot> roots) {
        this.map.clear();
        this.root.removeAllChildren();
        for (FileObject remoteRoot : roots) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(remoteRoot);
            this.root.add(node);
            this.map.put(remoteRoot, node);
            //add a dummy node below - indicates that the roow has not been expanded so far
            node.add(new DefaultMutableTreeNode(this.rb.getResourceString("wait")));
        }
        ((DefaultTreeModel) this.getModel()).nodeStructureChanged(this.root);
        this.expand(this.root);
    }

    /**
     * Expands a node
     */
    private void expand(DefaultMutableTreeNode node) {
        TreePath treePath = null;
        synchronized (node) {
            treePath = new TreePath(node.getPath());
            this.expandPath(treePath);
        }
        this.fireTreeExpanded(treePath);
    }

    public boolean isExplored(DefaultMutableTreeNode node) {
        synchronized (node) {
            if (node.getChildCount() == 1) {
                DefaultMutableTreeNode firstChild = (DefaultMutableTreeNode) node.getFirstChild();
                if (firstChild.getUserObject() instanceof String) {
                    //its the "wait" indicator
                    return (false);
                }
            }
        }
        return (true);
    }

    public boolean nodeexists(FileObject node) {
        DefaultMutableTreeNode parentNode = this.map.get(node);
        return (parentNode != null);
    }

    public void addChildren(FileObject parent, List<FileObject> children) {
        DefaultMutableTreeNode parentNode = null;
        parentNode = this.map.get(parent);
        //remove dummy node
        parentNode.removeAllChildren();
        ((DefaultTreeModel) this.getModel()).nodeStructureChanged(parentNode);
        for (FileObject child : children) {
            if (child instanceof FileObjectDir
                    || !this.directoriesOnly) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(child);
                parentNode.add(node);
                this.map.put(child, node);
                if (child instanceof FileObjectDir) {
                    node.add(new DefaultMutableTreeNode(this.rb.getResourceString("wait")));
                }
            }
        }
        ((DefaultTreeModel) this.getModel()).nodeStructureChanged(parentNode);
        this.expand(parentNode);
        this.setSelectedNode(parent);
    }

    /**
     * Returns the Tree node for a passed FileObject
     */
    public void setSelectedNode(FileObject selection) {
        DefaultMutableTreeNode selectionNode = null;
            selectionNode = this.map.get(selection);
        if (selectionNode != null) {
            TreePath selectionPath = new TreePath(selectionNode.getPath());
            this.scrollPathToVisible(selectionPath);
            this.setSelectionPath(selectionPath);
        }
    }

    /**
     * Returns the selected node of the Tree
     */
    public DefaultMutableTreeNode getSelectedNode() {
        TreePath path = this.getSelectionPath();
        if (path != null) {
            return ((DefaultMutableTreeNode) path.getLastPathComponent());
        }
        return (null);
    }

    /**
     * @param directoriesOnly the directoriesOnly to set
     */
    public void setDirectoriesOnly(boolean directoriesOnly) {
        this.directoriesOnly = directoriesOnly;
    }
}
