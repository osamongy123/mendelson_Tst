//$Header: /as2/de/mendelson/util/tables/hideablecolumns/HideableColumn.java 1     10.07.15 13:01 Heller $
package de.mendelson.util.tables.hideablecolumns;
import javax.swing.table.TableColumn;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Column in a table that could be hidden
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class HideableColumn{
    private int position = 0;
    private TableColumn column = null;
    private boolean visible = true;
    private boolean hideable = true;
    
    public int getPosition() {
        return position;
    }
    
    public void setPosition(int position) {
        this.position = position;
    }
    
    public TableColumn getColumn() {
        return column;
    }
    
    public void setColumn(TableColumn column) {
        this.column = column;
    }
    
    public boolean isVisible() {
        return(this.visible);
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isHideable() {
        return hideable;
    }

    public void setHideable(boolean hideable) {
        this.hideable = hideable;
    }
}
