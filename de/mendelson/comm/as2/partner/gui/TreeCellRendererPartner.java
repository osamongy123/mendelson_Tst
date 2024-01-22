//$Header: /as2/de/mendelson/comm/as2/partner/gui/TreeCellRendererPartner.java 6     15.08.19 10:46 Heller $
package de.mendelson.comm.as2.partner.gui;

import de.mendelson.comm.as2.partner.Partner;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
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
 * @version $Revision: 6 $
 */
public class TreeCellRendererPartner extends DefaultTreeCellRenderer {

    public final static int ICON_HEIGHT = JTreePartner.ICON_HEIGHT;

    private final static ImageIcon ICON_REMOTE
            = new ImageIcon(ListCellRendererPartner.IMAGE_REMOTESTATION.toMinResolution(ICON_HEIGHT));
    private final static ImageIcon ICON_LOCAL
            = new ImageIcon(ListCellRendererPartner.IMAGE_LOCALSTATION.toMinResolution(ICON_HEIGHT));
    private final static ImageIcon ICON_LOCAL_ERROR
            = new ImageIcon(ListCellRendererPartner.IMAGE_LOCALSTATION_CONFIGERROR.toMinResolution(ICON_HEIGHT));
    private final static ImageIcon ICON_REMOTE_ERROR
            = new ImageIcon(ListCellRendererPartner.IMAGE_REMOTESTATION_CONFIGERROR.toMinResolution(ICON_HEIGHT));

    /**
     * Stores the selected node
     */
    private DefaultMutableTreeNode selectedNode = null;

    /**
     * Constructor to create Renderer for console tree
     */
    public TreeCellRendererPartner() {
        super();
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree,
            Object selectedObject, boolean sel,
            boolean expanded,
            boolean leaf,
            int row, boolean hasFocus) {
        this.selectedNode = (DefaultMutableTreeNode) selectedObject;
        return (super.getTreeCellRendererComponent(tree, selectedObject, sel, expanded,
                leaf, row, hasFocus));
    }

    /**
     * Returns the defined Icon of the entry to be rendered
     */
    private Icon getDefinedIcon() {
        ImageIcon icon = null;
        Object object = this.selectedNode.getUserObject();
        //is this root node?
        if (object == null || !(object instanceof Partner)) {
            return (super.getOpenIcon());
        }
        Partner partner = (Partner) object;
        if (partner.isLocalStation()) {
            if (partner.hasConfigError()) {
                icon = this.ICON_LOCAL_ERROR;
            } else {
                icon = this.ICON_LOCAL;
            }
        } else {
            if (partner.hasConfigError()) {
                icon = this.ICON_REMOTE_ERROR;
            } else {
                icon = this.ICON_REMOTE;
            }
        }

        return (icon);
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
}
