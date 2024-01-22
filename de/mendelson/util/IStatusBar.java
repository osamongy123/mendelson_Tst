//$Header: /as2/de/mendelson/util/IStatusBar.java 1     12/07/17 9:19a Heller $
package de.mendelson.util;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Status bar interface for the mendelson GUIs
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public interface IStatusBar{

    public void startProgressIndeterminate(String progressDetails, String uniqueId);

    public void stopProgressIfExists(String uniqueId);


}
