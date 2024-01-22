//$Header: /mec_as2/de/mendelson/comm/as2/client/about/ResourceBundleAboutDialog.java 3     29.03.06 15:58 Heller $ 
package de.mendelson.comm.as2.client.about;
import de.mendelson.util.MecResourceBundle;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/** 
 * ResourceBundle to localize gui entries
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class ResourceBundleAboutDialog extends MecResourceBundle{

  public Object[][] getContents() {
    return contents;
  }

  /**List of messages in the specific language*/
  static final Object[][] contents = {
        
    {"title", "About" },  
    {"builddate", "Build date: {0}" },
    {"button.ok", "Ok" },
    {"tab.about", "Version" },
    {"tab.license", "License" },
  };		
  
}