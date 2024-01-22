//$Header: /as2/de/mendelson/comm/as2/preferences/PreferencesObjectKeyValue.java 2     22.02.06 16:21 Heller $
package de.mendelson.comm.as2.preferences;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Table model to display the keys/values to set
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class PreferencesObjectKeyValue{

    /**Key of this object used in the prefs*/
    private String key = null;
    /**Value aassigned to this key preference*/
    private String value = null;
    
    /**Creates a new Property for directory settings
     *@param key Key this preferences is stroed in the system prefs with
     *@param value Value assigned to this preference
     */
    public PreferencesObjectKeyValue( String key, String value ){
        this.key = key;
        this.value = value;
    }
    
    public String getKey(){
        return( this.key ); 
    }
    
    /**Sets a new value/directory*/
    public void setValue( String value ){
        this.value = value;   
    }
    
    public String getValue(){
        return( this.value );
    }
    
    /**Localized name for the key to display*/
    public String getName(){
        return( PreferencesAS2.getLocalizedName( this.key ) );
    }
    
    
}
