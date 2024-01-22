//$Header: /as2/de/mendelson/comm/as2/partner/gui/TableCellRendererPartner.java 5     27.07.21 15:33 Heller $
package de.mendelson.comm.as2.partner.gui;

import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.clientserver.PartnerListRequest;
import de.mendelson.comm.as2.partner.clientserver.PartnerListResponse;
import de.mendelson.util.MendelsonMultiResolutionImage;
import de.mendelson.util.clientserver.BaseClient;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/** 
 * Renders a partner in a JTable column
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class TableCellRendererPartner extends DefaultTableCellRenderer implements TableCellRenderer {

    private static final ImageIcon ICON_LOCAL
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/localstation.svg", 16, 32));
    private static final ImageIcon ICON_REMOTE
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/singlepartner.svg", 16, 32));
    private BaseClient baseClient;

    /**
     * Creates a default table cell renderer.
     */
    public TableCellRendererPartner(BaseClient baseClient) {
        super();
        this.baseClient = baseClient;
        this.setOpaque(true);
    }
    // implements javax.swing.table.TableCellRenderer

    /**
     *
     * Returns the default table cell renderer.
     *
     * @param table  the <code>JTable</code>
     * @param value  the value to assign to the cell at
     *			<code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus true if cell has focus
     * @param row  the row of the cell to render
     * @param column the column of the cell to render
     * @return the default table cell renderer
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
            this.setForeground(table.getSelectionForeground());

        } else {
            this.setBackground(table.getBackground());
            this.setForeground(table.getForeground());
        }

        this.setEnabled(table.isEnabled());
        this.setFont(table.getFont());
        if (value instanceof Partner) {
            Partner partner = (Partner) value;
            this.renderPartner(partner);
        } else if (value instanceof String) {
            //expecting AS2 id
            PartnerListRequest request = new PartnerListRequest(PartnerListRequest.LIST_BY_AS2_ID);
            request.setAdditionalListOptionStr((String) value);
            List<Partner> partnerList = ((PartnerListResponse)this.baseClient.sendSync(request, Partner.TIMEOUT_PARTNER_REQUEST)).getList();
            if( partnerList != null && partnerList.size() > 0 ){
                this.renderPartner(partnerList.get(0));
            }else {
                //partner does not exist: just display the AS2 id
                this.setIcon(this.ICON_LOCAL);
                this.setText((String) value);
            }            
        }
        return (this);
    }

    /**Renders the partner entry in the table*/
    private void renderPartner(Partner partner) {
        if (partner.isLocalStation()) {
            this.setIcon(this.ICON_LOCAL);
        } else {
            this.setIcon(this.ICON_REMOTE);
        }
        this.setText(partner.toString());
    }


    /*
     * The following methods are overridden as a performance measure to
     * to prune code-paths are often called in the case of renders
     * but which we know are unnecessary.  Great care should be taken
     * when writing your own renderer to weigh the benefits and
     * drawbacks of overriding methods like these.
     */
    /**
     * Overridden for performance reasons.
     * See the <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public boolean isOpaque() {
        Color back = getBackground();
        Component p = getParent();
        if (p != null) {
            p = p.getParent();
        }
        // p should now be the JTable.
        boolean colorMatch = (back != null) && (p != null)
                && back.equals(p.getBackground())
                && p.isOpaque();
        return !colorMatch && super.isOpaque();
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
    @Override
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    }
}


