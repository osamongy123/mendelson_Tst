//$Header: /as2/de/mendelson/comm/as2/partner/gui/ListCellRendererPartner.java 10    15.08.19 10:46 Heller $
package de.mendelson.comm.as2.partner.gui;

import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.util.MendelsonMultiResolutionImage;
import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

/**
 * Renderer to render the workflows that could be selected
 *
 * @author S.Heller
 * @version $Revision: 10 $
 */
public class ListCellRendererPartner extends JLabel implements ListCellRenderer {

    public static final MendelsonMultiResolutionImage IMAGE_LOCALSTATION
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/partner/gui/localstation.svg", 16, 32);
    public static final MendelsonMultiResolutionImage IMAGE_REMOTESTATION
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/partner/gui/singlepartner.svg", 16, 32);
    public static final MendelsonMultiResolutionImage IMAGE_REMOTESTATION_CONFIGERROR
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/partner/gui/singlepartner_error.svg", 16, 32);
    public static final MendelsonMultiResolutionImage IMAGE_LOCALSTATION_CONFIGERROR
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/partner/gui/localstation_error.svg", 16, 32);

    public static final ImageIcon ICON_LOCALSTATION
            = new ImageIcon(IMAGE_LOCALSTATION.toMinResolution(16));
    public static final ImageIcon ICON_REMOTESTATION
            = new ImageIcon(IMAGE_REMOTESTATION.toMinResolution(16));

    /**
     * Constructs a default renderer object for an item in a list.
     */
    public ListCellRendererPartner() {
        super();
        setOpaque(true);
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void validate() {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void revalidate() {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void repaint(Rectangle r) {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        // Strings get interned...
        if (propertyName.equals("text")) {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, char oldValue, char newValue) {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, short oldValue, short newValue) {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, long oldValue, long newValue) {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, float oldValue, float newValue) {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    }

    /**
     * A subclass of DefaultListCellRenderer that implements UIResource.
     * DefaultListCellRenderer doesn't implement UIResource directly so that
     * applications can safely override the cellRenderer property with
     * DefaultListCellRenderer subclasses.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with future Swing
     * releases. The current serialization support is appropriate for short term
     * storage or RMI between applications running the same version of Swing. As
     * of 1.4, support for long term storage of all
     * JavaBeans<sup><font size="-2">TM</font></sup>
     * has been added to the <code>java.beans</code> package. Please see
     * {@link java.beans.XMLEncoder}.
     */
    public static class UIResource extends DefaultListCellRenderer
            implements javax.swing.plaf.UIResource {
    }

    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        setComponentOrientation(list.getComponentOrientation());
        if (isSelected) {
            this.setBackground(list.getSelectionBackground());
            this.setForeground(list.getSelectionForeground());

        } else {
            this.setBackground(list.getBackground());
            this.setForeground(list.getForeground());
        }
        this.setFont(list.getFont());
        //Linux sets the value to null if nothing has been selected in the combobox
        if (value != null) {
            if (value instanceof Partner) {
                Partner partner = (Partner) value;
                if (partner.isLocalStation()) {
                    this.setIcon(ICON_LOCALSTATION);
                } else {
                    this.setIcon(ICON_REMOTESTATION);
                }
                this.setEnabled(list.isEnabled());
                this.setText(partner.toString());
            } else if (value instanceof String) {
                this.setEnabled(list.isEnabled());
                this.setIcon(null);
                this.setText(value.toString());
            }
        }
        this.setHorizontalAlignment(SwingConstants.LEADING);
        this.setHorizontalTextPosition(SwingConstants.RIGHT);

        return (this);
    }
}
