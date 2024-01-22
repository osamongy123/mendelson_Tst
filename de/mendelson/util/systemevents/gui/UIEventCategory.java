//$Header: /as2/de/mendelson/util/systemevents/gui/UIEventCategory.java 6     26.01.21 15:20 Heller $
package de.mendelson.util.systemevents.gui;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.systemevents.ResourceBundleSystemEvent;
import de.mendelson.util.systemevents.SystemEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Selectable event category in the UI
 *
 * @author S.Heller
 * @version $Revision: 6 $
 */
public class UIEventCategory implements Comparable<UIEventCategory> {

    private final int category;
    private static MecResourceBundle rbSystemEvent;

    public UIEventCategory(int category) {
        this.category = category;
        //Load resourcebundle
        try {
            this.rbSystemEvent = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleSystemEvent.class.getName());
        } //load up  resourcebundle        
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    public static List<UIEventCategory> getAllSorted(){
        List<UIEventCategory> categoryList = new ArrayList<UIEventCategory>();
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_SERVER_COMPONENTS));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_CONFIGURATION));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_CONNECTIVITY));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_CERTIFICATE));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_DATABASE));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_NOTIFICATION));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_PROCESSING));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_QUOTA));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_TRANSACTION));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_FILE_OPERATION));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_OTHER));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_LICENSE));
        categoryList.add(new UIEventCategory(SystemEvent.CATEGORY_CLIENT_OPERATION));
        Collections.sort(categoryList);
        return( categoryList );
    }
    
    
    @Override
    public String toString() {
        return (rbSystemEvent.getResourceString("category." + this.category));
    }

    @Override
    public boolean equals(Object anObject) {
        if (anObject == this) {
            return (true);
        }
        if (anObject != null && anObject instanceof UIEventCategory) {
            UIEventCategory entry = (UIEventCategory) anObject;
            return (entry.category == this.category);
        }
        return (false);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + this.category;
        return hash;
    }

    @Override
    public int compareTo(UIEventCategory otherCategory) {
        return (this.toString().compareTo(otherCategory.toString()));
    }

    public int getCategoryValue() {
        return (this.category);
    }

}
