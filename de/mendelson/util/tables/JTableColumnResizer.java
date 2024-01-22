//$Header: /as2/de/mendelson/util/tables/JTableColumnResizer.java 8     15/06/22 9:31 Heller $
package de.mendelson.util.tables;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Looks at the content of the columns of a JTable and sets the preferred widths
 * JTableColumnResizer.adjustColumnWidthByContent(myJTableObject);
 * JTableColumnResizer.adjustColumnWidthByContent(myJTableObject, new
 * int[]{1,3});
 *
 */
public class JTableColumnResizer {

    /**
     * Resizes the columns of a table by their content
     */
    public static void adjustColumnWidthByContent(final JTable table) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (table.getModel()) {
                    for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
                        if (!table.getColumnModel().getColumn(columnIndex).getResizable()) {
                            continue;
                        }
                        int maxWidth = 0;
                        for (int row = 0; row < table.getRowCount(); row++) {
                            TableCellRenderer renderer = table.getCellRenderer(row, columnIndex);
                            Object value = table.getValueAt(row, columnIndex);
                            Component comp
                                    = renderer.getTableCellRendererComponent(table,
                                            value,
                                            false,
                                            false,
                                            row,
                                            columnIndex);
                            maxWidth = Math.max(comp.getPreferredSize().width, maxWidth);
                        }
                        TableColumn tableColumn = table.getColumnModel().getColumn(columnIndex);
                        //it's possible to craete tables without headers by just setting the table header to null
                        if (table.getTableHeader() != null) {
                            TableCellRenderer headerRenderer = tableColumn.getHeaderRenderer();
                            if (headerRenderer == null) {
                                headerRenderer = table.getTableHeader().getDefaultRenderer();
                            }
                            Object headerValue = tableColumn.getHeaderValue();
                            Component headerComponent
                                    = headerRenderer.getTableCellRendererComponent(table,
                                            headerValue,
                                            false,
                                            false,
                                            0,
                                            columnIndex);
                            maxWidth = Math.max(maxWidth, headerComponent.getPreferredSize().width);
                        }
                        tableColumn.setPreferredWidth(maxWidth);
                    }
                }
            }
        };
        try {
            SwingUtilities.invokeLater(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
