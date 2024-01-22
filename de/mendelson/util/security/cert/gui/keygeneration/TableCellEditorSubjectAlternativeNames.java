//$Header: /oftp2/de/mendelson/util/security/cert/gui/keygeneration/TableCellEditorSubjectAlternativeNames.java 1     6/01/17 10:39a Heller $
package de.mendelson.util.security.cert.gui.keygeneration;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Cell editor for Columns of the class type ScriptPropertiesObject
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class TableCellEditorSubjectAlternativeNames
        extends AbstractCellEditor implements TableCellEditor {

    /**Table component the editor allows changes in
     */
    private JTable table = null;
    /** The Swing component being edited. */
    private JComponent editorComponent;
    /**
     * The delegate class which handles all methods sent from the
     * <code>CellEditor</code>.
     */
    private EditorDelegate delegate;    
    /**
     * An integer specifying the number of clicks needed to start editing.
     * Even if <code>clickCountToStart</code> is defined as zero, it
     * will not initiate until a click occurs.
     */
    private int clickCountToStart = 1;
    private List<String> allValuesList;
    private JComboBox comboBox = new JComboBox();


    public TableCellEditorSubjectAlternativeNames(List<String> allValuesList) {
        this.allValuesList = allValuesList;
        this.setEditorComponent(this.comboBox);
    }

    /**
     * Constructs a <code>DefaultCellEditor</code> object that uses a
     * combo box.
     *
     * @param comboBox  a <code>JComboBox</code> object
     */
    public void setEditorComponent(final JComboBox comboBox) {
        editorComponent = comboBox;
        this.clickCountToStart = 1;
        comboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        delegate = new EditorDelegate() {

            public void setValue(Object value) {
                comboBox.setSelectedItem(value);
            }

            @Override
            public String getCellEditorValue() {
                return (comboBox.getSelectedItem().toString());
            }

            @Override
            public boolean shouldSelectCell(EventObject anEvent) {
                if (anEvent instanceof MouseEvent) {
                    MouseEvent e = (MouseEvent) anEvent;
                    return e.getID() != MouseEvent.MOUSE_DRAGGED;
                }
                return true;
            }

            @Override
            public boolean stopCellEditing() {
                if (comboBox.isEditable()) {
                    // Commit edited value.
                    comboBox.actionPerformed(new ActionEvent(
                            TableCellEditorSubjectAlternativeNames.this, 0, ""));
                }
                return super.stopCellEditing();
            }
        };
        comboBox.addActionListener(delegate);
    }

    /**
     * Returns a reference to the editor component.
     *
     * @return the editor <code>Component</code>
     */
    public Component getComponent() {
        return editorComponent;
    }

    /**
     * Specifies the number of clicks needed to start editing.
     *
     * @param count  an int specifying the number of clicks needed to start editing
     * @see #getClickCountToStart
     */
    public void setClickCountToStart(int count) {
        clickCountToStart = count;
    }

    /**
     * Returns the number of clicks needed to start editing.
     * @return the number of clicks needed to start editing
     */
    public int getClickCountToStart() {
        return clickCountToStart;
    }

    /**
     * Forwards the message from the <code>CellEditor</code> to
     * the <code>delegate</code>.
     * @see EditorDelegate#getCellEditorValue
     */
    @Override
    public Object getCellEditorValue() {
        return delegate.getCellEditorValue();
    }

    /**
     * Forwards the message from the <code>CellEditor</code> to
     * the <code>delegate</code>.
     * @see EditorDelegate#isCellEditable(EventObject)
     */
    @Override
    public boolean isCellEditable(EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            return ((MouseEvent) anEvent).getClickCount() >= this.clickCountToStart;
        }
        return true;
    }

    /**
     * Forwards the message from the <code>CellEditor</code> to
     * the <code>delegate</code>.
     * @see EditorDelegate#shouldSelectCell(EventObject)
     */
    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return delegate.shouldSelectCell(anEvent);
    }

    /**
     * Forwards the message from the <code>CellEditor</code> to
     * the <code>delegate</code>.
     * @see EditorDelegate#stopCellEditing
     */
    @Override
    public boolean stopCellEditing() {
        return delegate.stopCellEditing();
    }

    /**
     * Forwards the message from the <code>CellEditor</code> to
     * the <code>delegate</code>.
     * @see EditorDelegate#cancelCellEditing
     */
    @Override
    public void cancelCellEditing() {
        delegate.cancelCellEditing();
    }

    /** Implements the <code>TableCellEditor</code> interface. */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        this.table = table;
        if (value instanceof String) {
            String selectionValue = (String) value;
            JComboBox comboBox = new JComboBox();
            for( String propertyType:this.allValuesList){
                comboBox.addItem(propertyType);                
            }
            comboBox.setSelectedItem(selectionValue);
            this.setEditorComponent(comboBox);
        }
        return (editorComponent);
    }

    /**
     * The protected <code>EditorDelegate</code> class.
     */
    protected class EditorDelegate implements ActionListener, ItemListener {

        /**  The value of this cell. */
        private String value;

        /**
         * Returns the value of this cell.
         * @return the value of this cell
         */
        public String getCellEditorValue() {
            return value;
        }

        /**
         * Sets the value of this cell.
         * @param value the new value of this cell
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Returns true to indicate that the editing cell may
         * be selected.
         *
         * @param   anEvent         the event
         * @return  true
         */
        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }

        /**
         * Returns true to indicate that editing has begun.
         *
         * @param anEvent          the event
         */
        public boolean startCellEditing(EventObject anEvent) {
            return true;
        }

        /**
         * Stops editing and
         * returns true to indicate that editing has stopped.
         * This method calls <code>fireEditingStopped</code>.
         *
         * @return  true
         */
        public boolean stopCellEditing() {
            fireEditingStopped();
            return true;
        }

        /**
         * Cancels editing.  This method calls <code>fireEditingCanceled</code>.
         */
        public void cancelCellEditing() {
            fireEditingCanceled();
        }

        /**
         * When an action is performed, editing is ended.
         * @param e the action event
         * @see #stopCellEditing
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            TableCellEditorSubjectAlternativeNames.this.stopCellEditing();
        }

        /**
         * When an item's state changes, editing is ended.
         * @param e the action event
         * @see #stopCellEditing
         */
        @Override
        public void itemStateChanged(ItemEvent e) {
            TableCellEditorSubjectAlternativeNames.this.stopCellEditing();
        }

        public boolean isCellEditable(EventObject anEvent) {
            if (anEvent instanceof MouseEvent) {
                return ((MouseEvent) anEvent).getClickCount() >= clickCountToStart;
            }
            return true;
        }
    }
} // End of class JCellEditor
