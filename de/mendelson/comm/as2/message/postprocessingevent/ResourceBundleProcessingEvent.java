//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/ResourceBundleProcessingEvent.java 3     10.09.20 12:57 Heller $
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
 * @version $Revision: 3 $
 */
public class ResourceBundleProcessingEvent extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"event.enqueued", "The defined postprocessing event ({0}) has been enqueued and will be executed in some seconds." },
        {"processtype." + ProcessingEvent.PROCESS_EXECUTE_SHELL, "Execute shell command" },
        {"processtype." + ProcessingEvent.PROCESS_MOVE_TO_DIR, "Move message to directory" },
        {"processtype." + ProcessingEvent.PROCESS_MOVE_TO_PARTNER, "Forward message to partner" },
        {"eventtype." + ProcessingEvent.TYPE_RECEIPT_SUCCESS, "Receipt" },
        {"eventtype." + ProcessingEvent.TYPE_SEND_FAILURE, "Send (failed)" },
        {"eventtype." + ProcessingEvent.TYPE_SEND_SUCCESS, "Send (success)" },
    };
    
}