//$Header: /as2/de/mendelson/util/clientserver/clients/filesystemview/TreeCellRendererFileBrowser.java 10    29.10.19 11:25 Heller $
package de.mendelson.util.clientserver.clients.filesystemview;

import de.mendelson.util.ColorUtil;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * TreeCellRenderer that will display the icons of the config tree
 *
 * @author S.Heller
 * @version $Revision: 10 $
 */
public class TreeCellRendererFileBrowser extends DefaultTreeCellRenderer {

    private final ImageIcon ROOT_ICON = new ImageIcon(TreeCellRendererFileBrowser.class.getResource(
            "/de/mendelson/util/clientserver/clients/filesystemview/root16x16.gif"));
    private final ImageIcon WAIT_ICON = new ImageIcon(TreeCellRendererFileBrowser.class.getResource(
            "/de/mendelson/util/clientserver/clients/filesystemview/waiting16x16.gif"));
    /**
     * Stores the currently selected node
     */
    private DefaultMutableTreeNode selectedNode = null;
    private boolean expanded = false;
    private FileSystemView clientSideFileSystemView;
    private Color COLOR_FOREGROUND_EXECUTABLE = Color.green.darker().darker();

    /**
     * Constructor to create Renderer for console tree
     */
    public TreeCellRendererFileBrowser() {
        super();
        this.clientSideFileSystemView = FileSystemView.getFileSystemView();
        Color tableBackgroundColor = UIManager.getDefaults().getColor("Table.background");
        if (tableBackgroundColor == null) {
            tableBackgroundColor = Color.WHITE;
        }
        COLOR_FOREGROUND_EXECUTABLE = ColorUtil.getBestContrastColorAroundForeground(
                tableBackgroundColor, COLOR_FOREGROUND_EXECUTABLE);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree,
            Object node, boolean selected,
            boolean expanded,
            boolean leaf,
            int row, boolean hasFocus) {
        this.selectedNode = (DefaultMutableTreeNode) node;
        this.expanded = expanded;
        Component component = super.getTreeCellRendererComponent(tree, node, selected, expanded,
                leaf, row, hasFocus);
        if (node instanceof DefaultMutableTreeNode) {
            if (((DefaultMutableTreeNode) node).getUserObject() instanceof FileObjectFile) {
                FileObjectFile selectedFileObject = (FileObjectFile) ((DefaultMutableTreeNode) node).getUserObject();
                if (selectedFileObject.isExecutable() && !selected) {
                    component.setForeground(COLOR_FOREGROUND_EXECUTABLE);
                }
            }
        }
        return (component);
    }

    /**
     * Returns the defined Icon of the entry, might be null if anything fails
     */
    private Icon getDefinedIcon() {
        Object userObject = this.selectedNode.getUserObject();
        //is this root node?
        if (userObject == null) {
            return (super.getOpenIcon());
        }
        if (userObject instanceof String) {
            return (WAIT_ICON);
        }
        if (!(userObject instanceof FileObject)) {
            return (super.getOpenIcon());
        }
        FileObject userFileObject = (FileObject) userObject;
        if (userFileObject instanceof FileObjectDir) {
            FileObjectDir dirFileObj = (FileObjectDir) userFileObject;
            if (this.expanded) {
                OpacityIcon icon = new OpacityIcon(super.getDefaultOpenIcon(), 1f);
                if (dirFileObj.isHidden()) {
                    icon.setOpacity(0.5f);
                }
                return (icon);
            } else {
                OpacityIcon icon = new OpacityIcon(super.getDefaultClosedIcon(), 1f);
                if (dirFileObj.isHidden()) {
                    icon.setOpacity(0.5f);
                }
                return (icon);
            }
        } else if (userFileObject instanceof FileObjectRoot) {
            FileObjectRoot root = (FileObjectRoot) userFileObject;
            if (root.getServersideIcon() == null) {
                return (ROOT_ICON);
            } else {
                return (root.getServersideIcon());
            }
        } else if (userFileObject instanceof FileObjectFile) {
            FileObjectFile file = (FileObjectFile) userFileObject;
            if (file.getServersideIcon() == null) {
                try {
                    Icon systemIcon = this.clientSideFileSystemView.getSystemIcon(new File(userFileObject.getFileURI()));
                    if (systemIcon == null) {
                        systemIcon = super.getLeafIcon();
                    }
                    OpacityIcon icon = new OpacityIcon(systemIcon, 1f);
                    if (file.isHidden()) {
                        icon.setOpacity(0.5f);
                    }
                    return (icon);
                } catch (Throwable e) {
                    //nop
                }
            } else {
                OpacityIcon icon = new OpacityIcon(file.getServersideIcon(), 1f);
                if (file.isHidden()) {
                    icon.setOpacity(0.5f);
                }
                return (icon);
            }
        }
        return (null);
    }

    /**
     * Gets the Icon by the type of the object
     */
    @Override
    public Icon getLeafIcon() {
        Icon icon = this.getDefinedIcon();
        if (icon != null) {
            return (icon);
        }
        //nothing found: get default
        return (super.getLeafIcon());
    }

    @Override
    public Icon getOpenIcon() {
        Icon icon = this.getDefinedIcon();
        if (icon != null) {
            return (icon);
        }
        return (super.getOpenIcon());
    }

    @Override
    public Icon getClosedIcon() {
        Icon icon = this.getDefinedIcon();
        if (icon != null) {
            return (icon);
        }
        return (super.getClosedIcon());
    }

    public int getDefaultLeafIconHeight() {
        Icon defaultIcon = super.getDefaultLeafIcon();
        if (defaultIcon != null) {
            return (defaultIcon.getIconHeight());
        }
        return (this.ROOT_ICON.getIconHeight());
    }
}
