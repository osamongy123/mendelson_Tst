//$Header: /as2/de/mendelson/comm/as2/send/ResourceBundleDirPollManager.java 19    19/01/22 16:17 Heller $
package de.mendelson.comm.as2.send;
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
 * @version $Revision: 19 $
 */
public class ResourceBundleDirPollManager extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"none", "None" },
        {"manager.status.modified", "Directory poll manager changed poll states, {0} directories are monitored" },
        {"poll.stopped", "[Directory poll manager] Poll for relationship \"{0}/{1}\" stopped." },
        {"poll.started", "[Directory poll manager] Poll for relationship \"{0}/{1}\" started. Ignore files: \"{2}\". Poll interval: {3}s" },
        {"poll.stopped.notscheduled", "[Directory poll manager] The system tried to stop the poll for relationship \"{0}/{1}\" - but this has not been scheduled." },
        {"poll.modified", "[Directory poll manager] Partner settings for the relationship \"{0}/{1}\" have been modified." },
        {"warning.ro", "[Directory poll manager] Outbox file {0} is read-only, ignoring." },
        {"warning.noread", "[Directory poll manager] No read access for outbox file {0}, ignoring." },
        {"warning.notcomplete", "[Directory poll manager] Outbox file {0} is not complete so far, ignoring." },
        {"messagefile.deleted", "The file \"{0}\" has been deleted and enqueued into the processing message queue of the server." },
        {"processing.file", "Processing the file \"{0}\" for the relationship \"{1}/{2}\"." },
        {"processing.file.error", "Error processing the file \"{0}\" for the relationship  \"{1}/{2}\": \"{3}\"." },
        {"poll.log.wait", "[Outbound directory poll] {0}->{1}: Next outbound poll process in {2}s ({3})" },
        {"poll.log.polling", "[Outbound directory poll] {0}->{1}: Polling directory \"{2}\""},
        {"title.list.polls.running", "Summary: Directories that are monitored:" },
        {"title.list.polls.stopped", "The following directory monitoring processes have been stopped" },
        {"title.list.polls.started", "The following directory monitoring processes have been started" },
    };
    
}