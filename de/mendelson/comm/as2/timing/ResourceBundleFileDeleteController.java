//$Header: /as2/de/mendelson/comm/as2/timing/ResourceBundleFileDeleteController.java 5     15.01.21 15:11 Heller $
package de.mendelson.comm.as2.timing;
import de.mendelson.util.MecResourceBundle;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize a mendelson product
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class ResourceBundleFileDeleteController extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"autodelete", "{0}: The file has been deleted by the system maintenance process." },
        {"delete.title", "File delete operation by system maintenance" },
        {"delete.title.log", "Log dir delete operation by system maintenance" },
        {"delete.title.tempfiles", "Temporary files" },
        {"delete.title._rawincoming", "Inbound file from _rawincoming" },
        {"delete.header.logfiles", "Delete log and system event files older than {0} days" },
        {"success", "SUCCESS" },
        {"failure", "FAILURE" },
        {"no.entries", "{0}: No entries found" },
    };
    
}