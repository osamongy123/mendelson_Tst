//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ResourceBundleExecuteShellCommand.java 4     10.09.20 12:57 Heller $
package de.mendelson.comm.as2.message.postprocessingevent;
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
 * @version $Revision: 4 $
 */
public class ResourceBundleExecuteShellCommand extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"executing.receipt", "[Postprocessing] ({0} --> {1}) Executing postprocessing event after receipt." },
        {"executing.send", "[Postprocessing] ({0} --> {1}) Executing postprocessing event after send." },
        {"executing.command", "[Postprocessing] Shell command: \"{0}\"." },
        {"executed.command", "[Postprocessing] Shell command executed, returncode={0}." },
        {"messageid.nolonger.exist", "[Postprocessing] Unable to execute a postprocessing event for the message \"{0}\" - this message does no longer exist. Skipping.." },
    };
    
}