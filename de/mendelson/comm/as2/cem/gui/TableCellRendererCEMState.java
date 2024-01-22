//$Header: /as2/de/mendelson/comm/as2/cem/gui/TableCellRendererCEMState.java 8     14.05.13 10:23 Heller $
package de.mendelson.comm.as2.cem.gui;

import de.mendelson.comm.as2.cem.CEMEntry;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.comm.as2.partner.clientserver.PartnerListRequest;
import de.mendelson.comm.as2.partner.clientserver.PartnerListResponse;
import de.mendelson.util.clientserver.BaseClient;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/** 
 * Renders the CEM state column
 * @author S.Heller
 * @version $Revision: 8 $
 */
public class TableCellRendererCEMState extends DefaultTableCellRenderer implements TableCellRenderer {

    private DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    private Color colorAccepted = new Color(166, 247, 164);
    private Color colorRejected = new Color(255, 145, 145);
    private Color colorPending = new Color(255, 255, 183);

    private BaseClient baseClient;

    /**
     * Creates a default table cell renderer.
     */
    public TableCellRendererCEMState(BaseClient baseClient) {
        super();
        this.setOpaque(true);
        this.baseClient = baseClient;
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

        if (value instanceof CEMEntry) {
            Color backgroundColor = table.getBackground();
            CEMEntry entry = (CEMEntry) value;
            String receiverName = entry.getReceiverAS2Id();
            PartnerListRequest request = new PartnerListRequest(PartnerListRequest.LIST_BY_AS2_ID);
            request.setAdditionalListOptionStr(receiverName);
            List<Partner> partnerList = ((PartnerListResponse)this.baseClient.sendSync(request)).getList();
            if( partnerList != null && partnerList.size() > 0 ){
                receiverName = partnerList.get(0).getName();
            }
            int state = entry.getCemState();            
            if (state == CEMEntry.STATUS_ACCEPTED_INT) {
                backgroundColor = this.colorAccepted;
                this.setText(CEMEntry.getStateLocalized(state, receiverName)
                        + " (" + this.format.format( new Date(entry.getResponseMessageOriginated()))
                        + ")");
            } else if (state == CEMEntry.STATUS_PENDING_INT) {
                backgroundColor = this.colorPending;
                this.setText(CEMEntry.getStateLocalized(state, receiverName));
            } else if (state == CEMEntry.STATUS_REJECTED_INT) {
                backgroundColor = this.colorRejected;
                this.setText(CEMEntry.getStateLocalized(state, receiverName)
                        + " (" + this.format.format( new Date(entry.getResponseMessageOriginated()))
                        + ")");
            } else if (state == CEMEntry.STATUS_CANCELED_INT) {
                backgroundColor = this.colorRejected;
                this.setText(CEMEntry.getStateLocalized(state, receiverName));
            }else if (state == CEMEntry.STATUS_PROCESSING_ERROR_INT) {
                backgroundColor = this.colorRejected;
                this.setText(CEMEntry.getStateLocalized(state, receiverName));
            }
            if( isSelected){
                this.setBackground( backgroundColor.darker());
            }else{
                this.setBackground( backgroundColor);
            }
        }
        return (this);
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


