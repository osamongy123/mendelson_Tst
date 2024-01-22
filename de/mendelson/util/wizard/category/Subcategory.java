//$Header: /converteride/de/mendelson/util/wizard/category/Subcategory.java 3     30.03.05 11:55 Heller $
package de.mendelson.util.wizard.category;

import javax.swing.ImageIcon;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Object that stores the contents of a category. A category is a tab in the
 * JDialogCategory panel
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class Subcategory{
    
    public final static ImageIcon ICON_NO_CATEGORY_ICON
            = new ImageIcon( Subcategory.class.getResource(
            "/de/mendelson/util/wizard/category/emptycategory32x32.gif"));
    
    private String description = null;    
    /**The categories title
     */
    private String title = null;
    
    private ImageIcon icon = null;
    
    /**Action command linked with this sub category*/
    private String actionCommand = null;
    
    public Subcategory(){
    }
    
    public void setDescription( String description ){
        this.description = description;
    }
    
    public String getDescription(){
        return( this.description );
    }
    
    /**The subcategories item*/
    public void setIcon( ImageIcon icon ){
        this.icon = icon;
    }
    
    public ImageIcon getIcon(){
        if( this.icon == null )
            return( ICON_NO_CATEGORY_ICON );
        return( this.icon );
    }
    
    /**The subcategories title
     */
    public String getTitle(){
        return( this.title );
    }
    
    public void setTitle( String title ){
        this.title = title;
    }
    
    /**Action command that is linked with this subcategory
     */
    public void setActionCommand( String actionCommand ){
        this.actionCommand = actionCommand;
    }
    
    /**Returns the action command that is linked with this subcategory
     */
    public String getActionCommand(){
        return( this.actionCommand );
    }
    
}