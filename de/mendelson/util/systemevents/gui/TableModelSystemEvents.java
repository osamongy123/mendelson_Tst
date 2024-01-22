//$Header: /oftp2/de/mendelson/util/systemevents/gui/TableModelSystemEvents.java 12    4.06.19 10:57 Heller $
package de.mendelson.util.systemevents.gui;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.systemevents.SystemEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Model to display all files that are open and save/close them
 *
 * @author S.Heller
 * @version $Revision: 12 $
 */
public class TableModelSystemEvents extends AbstractTableModel {

    public static final int ROW_HEIGHT = 20;
    private static final int IMAGE_HEIGHT = ROW_HEIGHT-3;
    private final List<SystemEvent> systemEventList = Collections.synchronizedList(new ArrayList<SystemEvent>());

    private MecResourceBundle rb;

    /**
     * Load resources
     */
    public TableModelSystemEvents() {
        //Load resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleDialogSystemEvent.class.getName());
        } //load up  resourcebundle        
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    public void passNewData(List<SystemEvent> newSystemEvents) {
        synchronized (this.systemEventList) {
            this.systemEventList.clear();
            this.systemEventList.addAll(newSystemEvents);
        }
        this.fireTableDataChanged();
    }


    /**
     * Number of rows to display
     */
    @Override
    public int getRowCount() {
        synchronized (this.systemEventList) {
            return (this.systemEventList.size());
        }
    }

    public SystemEvent getEventAt(int row) {
        synchronized (this.systemEventList) {
            if (row >= 0 && row < this.systemEventList.size()) {
                return (this.systemEventList.get(row));
            } else {
                return (null);
            }
        }
    }

    /**Returns -1 if the event is currently not displayed*/
    public int getDisplayRowOfEvent(SystemEvent event) {
        synchronized (this.systemEventList) {            
            return (this.systemEventList.indexOf(event));
        }
    }

    /**
     * Number of cols to display
     */
    @Override
    public int getColumnCount() {
        return (5);
    }

    /**
     * Returns a value at a specific position in the grid
     */
    @Override
    public Object getValueAt(int row, int col
    ) {
        SystemEvent event = null;
        synchronized (this.systemEventList) {
            event = this.systemEventList.get(row);
        }
        if (col == 0) {
            return (event.getSeverityIconMultiResolution(IMAGE_HEIGHT));
        } else if (col == 1) {
            return (event.getOriginIconMultiResolution(IMAGE_HEIGHT));
        } else if (col == 2) {
            return (new Date(event.getTimestamp()));
        } else if (col == 3) {
            return (event.categoryToTextLocalized());
        }else {
            return (event.typeToTextLocalized());
        }
    }

    /**
     * Returns the name of every column
     *
     * @param col Column to get the header name of
     */
    @Override
    public String getColumnName(int col
    ) {
        return (new String[]{
            " ", "  ", 
            this.rb.getResourceString("header.timestamp"), 
            this.rb.getResourceString("header.category"), 
            this.rb.getResourceString("header.type")
        }[col]);
    }

    /**
     * Set how to display the grid elements
     *
     * @param col requested column
     */
    @Override
    public Class getColumnClass(int col
    ) {
        return (new Class[]{
            ImageIcon.class,
            ImageIcon.class,
            Date.class,
            String.class,
            String.class,}[col]);
    }

}
