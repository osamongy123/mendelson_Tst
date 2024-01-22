//$Header: /mec_as2/de/mendelson/util/tables/hideablecolumns/TableModelHideableColumns.java 4     10/01/15 3:59p Heller $
package de.mendelson.util.tables.hideablecolumns;

import de.mendelson.util.MecResourceBundle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Model to display the columns in the columns config dialog
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class TableModelHideableColumns extends AbstractTableModel {

    /**
     * Localize the GUI
     */
    private MecResourceBundle rb = null;

    /**
     * Actual data to display, contains HideableColumns
     */
    private final List<HideableColumn> columnArray = Collections.synchronizedList(new ArrayList<HideableColumn>());
    private final List<TableColumnHiddenStateListener> listenerList = Collections.synchronizedList(new ArrayList<TableColumnHiddenStateListener>());

    /**
     * Creates new TableModelHideableColumns
     */
    public TableModelHideableColumns() {
        //load resource bundle
        try {
            this.rb
                    = (MecResourceBundle) ResourceBundle.getBundle(
                            ResourceBundleHideableColumns.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
    }

    /**
     * Passes data to the model and fires a table data update
     */
    public void passNewData(List<HideableColumn> newData) {
        synchronized (this.columnArray) {
            this.columnArray.clear();
            for (HideableColumn column : newData) {
                this.columnArray.add(column);
            }
        }
        this.fireTableDataChanged();
    }

    /**
     * Number of rows to display
     */
    @Override
    public int getRowCount() {
        synchronized (this.columnArray) {
            return (this.columnArray.size());
        }
    }

    /**
     * Number of cols to display
     */
    @Override
    public int getColumnCount() {
        return (this.getHeader().length);
    }

    /**
     * Returns a value at a specific position in the grid
     */
    @Override
    public Object getValueAt(int row, final int col) {
        synchronized (this.columnArray) {
            HideableColumn hideableColumn = this.columnArray.get(row);
            switch (col) {
                case 0:
                    return (hideableColumn.getColumn().getHeaderValue());
                case 1:
                    return (Boolean.valueOf(hideableColumn.isVisible()));
            }
        }
        return (null);
    }

    /**
     * Returns the name of every column
     *
     * @param col Column to get the header name of
     */
    @Override
    public String getColumnName(int col) {
        return (this.getHeader()[col]);
    }

    /**
     * Set how to display the grid elements
     *
     * @param col requested column
     */
    @Override
    public Class getColumnClass(int col) {
        Class[] classes
                = new Class[]{
                    Object.class,
                    Boolean.class,};
        return (classes[col]);
    }

    /**
     * Returns the table headers
     */
    private String[] getHeader() {
        return (new String[]{
            this.rb.getResourceString("header.column"),
            this.rb.getResourceString("header.visible"),});
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        synchronized (this.columnArray) {
            HideableColumn hideableColumn = this.columnArray.get(rowIndex);
            return (columnIndex == 1 && hideableColumn.isHideable());
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 1) {
            boolean boolValue = ((Boolean) aValue).booleanValue();
            HideableColumn hideableColumn = this.columnArray.get(rowIndex);
            hideableColumn.setVisible(boolValue);
            this.informColumnHiddenStateListener(hideableColumn);
        }
    }

    /**
     * Removes a listener that is informed if a column state has been changed
     */
    public void removeColumnHiddenStateListener(TableColumnHiddenStateListener hiddenStateListener) {
        synchronized (this.listenerList) {
            this.listenerList.remove(hiddenStateListener);
        }
    }

    /**
     * Adds a listener that is informed if the license has been expired
     */
    public void addColumnHiddenStateListener(TableColumnHiddenStateListener hiddenStateListener) {
        synchronized (this.listenerList) {
            this.listenerList.add(hiddenStateListener);
        }
    }

    /**
     * Informs all listeners that a column state has been changed
     */
    private void informColumnHiddenStateListener(HideableColumn column) {
        synchronized( this.listenerList){
            for( TableColumnHiddenStateListener listener:this.listenerList){
                listener.tableColumnHiddenStateChanged(new TableColumnHiddenStateListener.ColumnHiddenStateEvent(this, column));
            }
        }
    }

}
