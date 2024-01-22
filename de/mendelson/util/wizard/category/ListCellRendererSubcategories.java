package de.mendelson.util.wizard.category;

import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Render the subcategories of a single category panel
 * @author S.Heller
 * @version $Revision: 6 $
 */
public class ListCellRendererSubcategories extends JLabel implements ListCellRenderer {

    /**
     * Constructs a default renderer object for an item
     * in a list.
     */
    public ListCellRendererSubcategories() {
        super();
        setOpaque(true);
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void validate() {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void revalidate() {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void repaint(Rectangle r) {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
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
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, char oldValue, char newValue) {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, short oldValue, short newValue) {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, long oldValue, long newValue) {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, float oldValue, float newValue) {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
    }

    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    }

    /**
     * A subclass of DefaultListCellRenderer that implements UIResource.
     * DefaultListCellRenderer doesn't implement UIResource
     * directly so that applications can safely override the
     * cellRenderer property with DefaultListCellRenderer subclasses.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases. The current serialization support is
     * appropriate for short term storage or RMI between applications running
     * the same version of Swing.  As of 1.4, support for long term storage
     * of all JavaBeans<sup><font size="-2">TM</font></sup>
     * has been added to the <code>java.beans</code> package.
     * Please see {@link java.beans.XMLEncoder}.
     */
    public static class UIResource extends DefaultListCellRenderer
            implements javax.swing.plaf.UIResource {
    }

    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        //setComponentOrientation(list.getComponentOrientation());
        if (isSelected) {
            this.setBackground(list.getSelectionBackground());
            this.setForeground(list.getSelectionForeground());
        } else {
            this.setBackground(list.getBackground());
            this.setForeground(list.getForeground());
        }
        this.setEnabled(list.isEnabled());
        this.setFont(list.getFont());

        Subcategory subcategory = (Subcategory) value;
        //show label, allow multiline labels
        this.setText(this.title2HTML(subcategory.getTitle()));
        this.setIcon(subcategory.getIcon());
        //Set the position of its text, relative to its icon:
        this.setVerticalTextPosition(JLabel.BOTTOM);
        this.setHorizontalTextPosition(JLabel.CENTER);
        this.setHorizontalAlignment(SwingConstants.CENTER);
        this.setBorder(new EmptyBorder(10, 20, 10, 20));
        return (this);
    }

    /**Converts a title to HTML, this allows line breaks in the label
     */
    private String title2HTML(String title) {
        final int MAXLINELENGTH = 15;
        if (title.length() < MAXLINELENGTH) {
            return (title);
        }
        StringBuilder buffer = new StringBuilder();
        buffer.append("<HTML>");
        int lineLength = 0;
        for (int i = 0; i < title.length(); i++) {
            char testChar = title.charAt(i);
            if (lineLength > MAXLINELENGTH && testChar == ' ') {
                lineLength = 0;
                buffer.append("<BR>");
            } else {
                buffer.append(testChar);
                lineLength++;
            }
        }
        buffer.append("</HTML>");
        return (buffer.toString());
    }
}
