//$Header: /converteride/de/mendelson/util/wizard/category/CategorySelectionListener.java 2     30.03.05 12:24 Heller $
package de.mendelson.util.wizard.category;

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
 * Listener interface every class has to implement that listens to a category selection
 * @author S.Heller
 * @version $Revision: 2 $
 */
public interface CategorySelectionListener extends EventListener {
    
    public void selectionPerformed( CategorySelectionEvent evt );
    
    public class CategorySelectionEvent extends EventObject {
        
        private int index = -1;
        private Subcategory subcategory = null;
        
        /**@param source source category
         *@param subcategory Subcategory that has been selected in the category
         */
        public CategorySelectionEvent(Object source, Subcategory subcategory,
                int index ){
            super( source );
            this.index = index;
            this.subcategory = subcategory;
        }
        
        public Category getCategory(){
            return( (Category)this.getSource() );
        }
        
        public Subcategory getSubcategory(){
            return( this.subcategory );
        }
        
        public int getIndex(){
            return( this.index );
        }
    }
    
}