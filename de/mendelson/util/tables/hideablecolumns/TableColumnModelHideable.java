//$Header: /as2/de/mendelson/util/tables/hideablecolumns/TableColumnModelHideable.java 3     10.07.15 16:08 Heller $
package de.mendelson.util.tables.hideablecolumns;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Wrapper to a table model, allows to hide columns
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class TableColumnModelHideable implements TableColumnModel{
    
    private TableColumnModel model = null;
    
    private Map<Object,HideableColumn>columnMap = new HashMap<Object,HideableColumn>();
    private Map<Integer,HideableColumn>positionMap = new HashMap<Integer,HideableColumn>();
    
    public TableColumnModelHideable( TableColumnModel model ){
        this.model = model;
        //store all columns
        for( int i = 0; i < model.getColumnCount(); i++ ){
            TableColumn col = model.getColumn(i);
            Object header = col.getHeaderValue();
            HideableColumn hiddenColumn = new HideableColumn();
            hiddenColumn.setColumn( col );
            hiddenColumn.setVisible( true );
            hiddenColumn.setHideable( true );
            hiddenColumn.setPosition( i );
            columnMap.put( header, hiddenColumn );
            positionMap.put( Integer.valueOf(i), hiddenColumn );
        }
    }
    
    @Override
    public void addColumn(TableColumn aColumn){
        this.model.addColumn( aColumn );
    }
    
    @Override
    public void addColumnModelListener(TableColumnModelListener x){
        this.model.addColumnModelListener(x);
    }
    
    @Override
    public TableColumn getColumn(int columnIndex){
        return( this.model.getColumn(columnIndex));
    }
    
    @Override
    public int getColumnCount(){
        return( this.model.getColumnCount() );
    }
    
    @Override
    public int getColumnIndex(Object columnIdentifier){
        return( this.model.getColumnIndex(columnIdentifier));
    }
    
    @Override
    public int getColumnIndexAtX(int xPosition){
        return( this.model.getColumnIndexAtX(xPosition));
    }
    
    @Override
    public int getColumnMargin(){
        return( this.model.getColumnMargin());
    }
    
    @Override
    public Enumeration getColumns(){
        return( this.model.getColumns());
    }
    
    @Override
    public boolean getColumnSelectionAllowed(){
        return( this.model.getColumnSelectionAllowed());
    }
    
    @Override
    public int getSelectedColumnCount(){
        return( this.model.getSelectedColumnCount());
    }
    
    @Override
    public int[] getSelectedColumns(){
        return( this.model.getSelectedColumns());
    }
    
    @Override
    public ListSelectionModel getSelectionModel(){
        return( this.model.getSelectionModel());
    }
    
    @Override
    public int getTotalColumnWidth(){
        return( this.model.getTotalColumnWidth());
    }
    
    @Override
    public void moveColumn(int columnIndex, int newIndex){
        this.model.moveColumn( columnIndex, newIndex );
    }
    
    @Override
    public void removeColumn(TableColumn column){
        this.model.removeColumn( column );
    }
    
    @Override
    public void removeColumnModelListener(TableColumnModelListener x){
        this.model.removeColumnModelListener( x );
    }
    
    @Override
    public void setColumnMargin(int newMargin){
        this.model.setColumnMargin( newMargin );
    }
    
    @Override
    public void setColumnSelectionAllowed(boolean flag){
        this.model.setColumnSelectionAllowed( flag );
    }
    
    @Override
    public void setSelectionModel(ListSelectionModel newModel){
        this.model.setSelectionModel( newModel );
    }
    
    public void setVisible( Object header, boolean flag ){
        HideableColumn column = this.columnMap.get( header );
        //ups column does not exist
        if( column == null ){
            return;
        }
        //do nothing if the column is already in this state
        if( column.isVisible() == flag ){
            return;
        }
        //do nothing if the column is not hideable
        if( !column.isHideable() ){
            return;
        }
        column.setVisible( flag );
        if( flag ){
            this.model.removeColumn(column.getColumn());
        }else{
            this.updateState();
        }
    }
    
    /**Updates the table model by the actual state of the hidden columns
     */
    public void updateState(){        
        HideableColumn[] columns = this.getColumnsSorted();    
        //remove all cols
        while( this.model.getColumnCount() > 0 ){
            this.model.removeColumn( this.model.getColumn(0));
        }
        //add all cols, now in right order
        for (HideableColumn column : columns) {
            this.model.addColumn(column.getColumn());
        }
        //remove all cols that have to be hidden
        for( int i = columns.length-1; i >= 0; i-- ){
            if( !columns[i].isVisible() ){
                this.model.removeColumn( columns[i].getColumn() );
            }
        }
    }    
    
    /**Returns all columns, sorted by their position*/
    public HideableColumn[] getColumnsSorted(){
        HideableColumn[] columns = new HideableColumn[this.positionMap.size()];
        for( int i = 0, size = this.positionMap.size(); i < size; i++ ){
            HideableColumn newColumn = this.positionMap.get(Integer.valueOf(i));
            columns[i] = newColumn;
        }
        return( columns );
    }
    
}
