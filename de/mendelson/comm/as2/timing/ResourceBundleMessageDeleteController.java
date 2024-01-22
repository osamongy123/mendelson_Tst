//$Header: /as2/de/mendelson/comm/as2/timing/ResourceBundleMessageDeleteController.java 8     1.02.21 15:53 Heller $
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
 * @version $Revision: 8 $
 */
public class ResourceBundleMessageDeleteController extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"autodelete", "{0}: This message is older than {1} {2} and has been deleted by the system maintenance process." },    
        {"transaction.deleted.user", "{0} Transaction(s) deleted by user interaction" },
        {"transaction.deleted.system", "Transaction(s) deleted by system maintenance process" },
        {"transaction.deleted.transactiondate", "Transaction date: {0}" },
        {"transaction.delete.setting.olderthan", "The process is configured to  delete transactions with green state that are older than {0}." },
        {"delete.ok", "DELETE OK" },
        {"delete.failed", "DELETE FAILED" },           
        {"delete.skipped", "DELETE SKIPPED" },           
    };
    
}