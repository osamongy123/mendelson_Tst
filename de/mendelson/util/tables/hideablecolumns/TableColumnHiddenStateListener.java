//$Header: /as2/de/mendelson/util/tables/hideablecolumns/TableColumnHiddenStateListener.java 1     14.07.15 10:01 Heller $
package de.mendelson.util.tables.hideablecolumns;
import java.util.EventListener;
import java.util.EventObject;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Listener that is informed if the hidden state of a column has been changed by the user
 * @author S.Heller
 * @version $Revision: 1 $
 */
public interface TableColumnHiddenStateListener extends EventListener {
    
    public void tableColumnHiddenStateChanged( ColumnHiddenStateEvent e );
    
    public class ColumnHiddenStateEvent extends EventObject {    
              
        private HideableColumn column;
        
        /**@param source source that has thrown the event
         */
        public ColumnHiddenStateEvent( Object source, HideableColumn column ){
            super(source);
            this.column = column;
        }

        /**
         * @return the column
         */
        public HideableColumn getColumn() {
            return column;
        }

        /**
         * @param column the column to set
         */
        public void setColumn(HideableColumn column) {
            this.column = column;
        }

    }
    
}
