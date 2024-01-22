//$Header: /as2/de/mendelson/comm/as2/send/DirPollManager.java 58    28/07/22 13:06 Heller $
package de.mendelson.comm.as2.send;

import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.NamedThreadFactory;
import de.mendelson.util.clientserver.ClientServer;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Manager that polls the outbox directories of the partners, creates messages
 * and sends them
 *
 * @author S.Heller
 * @version $Revision: 58 $
 */
public class DirPollManager {

    private Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private CertificateManager certificateManager;
    /**
     * Stores all poll threads key: partner DB id, value: pollThread
     */
    private final Map<String, DirPollThread> mapPollThread = Collections.synchronizedMap(new HashMap<String, DirPollThread>());
    /**
     * Executor service for all poll threads, with n poll threads at the same
     * time
     */
    private ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(5,
            new NamedThreadFactory("dir-poll"));
    /**
     * Localize the GUI
     */
    private MecResourceBundle rb = null;
    private ClientServer clientserver;
    private IDBDriverManager dbDriverManager;

    public DirPollManager(CertificateManager certificateManager, 
            ClientServer clientserver, IDBDriverManager dbDriverManager) throws Exception {
        this.clientserver = clientserver;
        this.dbDriverManager = dbDriverManager;
        //Load default resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleDirPollManager.class.getName());
        } //load up resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.certificateManager = certificateManager;
        //Remove all threads from the scheduler queue once they are canceled. 
        //The threads are canceled by canceling their future.
        this.scheduledExecutor.setRemoveOnCancelPolicy(true);
    }

    /**
     * Start all poll threads
     */
    public void start() {
        this.partnerConfigurationChanged();
        this.logger.info(this.rb.getResourceString("manager.status.modified", String.valueOf(this.getPollThreadCount())));
    }

    /**
     * Returns the list of poll threads that are currently active
     */
    private List<DirPollThread> getPollThreads() {
        List<DirPollThread> pollThreadList = new ArrayList<DirPollThread>();
        synchronized (this.mapPollThread) {
            pollThreadList.addAll(this.mapPollThread.values());
        }
        return (pollThreadList);
    }

    /**
     * Returns the number of directory poll attempts per minute. If the user has
     * set the poll intervals really small with a huge amount of partners this
     * will have impact on the file IO
     *
     * @return
     */
    public float getPollsPerMinute() {
        float totalPollsPerMinute = 0;
        synchronized (this.mapPollThread) {
            Collection<DirPollThread> threadList = this.mapPollThread.values();
            for (DirPollThread singleThread : threadList) {
                long pollIntervalInMS = singleThread.getPollIntervalInMS();
                float pollsPerMinute = 60000f / (float) pollIntervalInMS;
                totalPollsPerMinute += pollsPerMinute;
            }
        }
        return (totalPollsPerMinute);
    }

    /**
     * Returns the number of poll threads for the system information. This might
     * be important as the number of poll threads grows per local station. E.g.:
     * If you have 4 local station and 100 remote partners you will have 400
     * poll threads which might have impact on the file IO
     */
    public int getPollThreadCount() {
        synchronized (this.mapPollThread) {
            return (this.mapPollThread.size());
        }
    }

    /**
     * Indicates that the partner configuration has been changed: This should
     * stop now unused tasks and start other
     */
    public void partnerConfigurationChanged() {
        List<String> pollStopLines = new ArrayList<String>();
        List<String> pollStartLines = new ArrayList<String>();

        PartnerAccessDB access = new PartnerAccessDB(dbDriverManager);
        List<Partner> allPartnerList = access.getAllPartner();
        if (allPartnerList == null) {
            this.logger.severe("partnerConfigurationChanged: Unable to load partner");
            return;
        }
        List<Partner> localStationList = new ArrayList<Partner>();
        for (Partner partner : allPartnerList) {
            if (partner.isLocalStation()) {
                localStationList.add(partner);
            }
        }
        synchronized (this.mapPollThread) {
            for (Partner sender : localStationList) {
                for (Partner receiver : allPartnerList) {
                    String id = sender.getDBId() + "_" + receiver.getDBId();
                    //add partner task if it does not exist so far and if the receiver is no local station and the dir poll is enabled
                    if (!this.mapPollThread.containsKey(id) && !receiver.isLocalStation() && receiver.isEnableDirPoll()) {
                        DirPollThread newPoll = this.addPartnerPollThread(sender, receiver);
                        pollStartLines.add(newPoll.getLogLine());
                    } else if (this.mapPollThread.containsKey(id)) {
                        DirPollThread thread = (DirPollThread) this.mapPollThread.get(id);
                        if (!receiver.isLocalStation()) {
                            if (thread.hasBeenModified(sender, receiver)) {
                                //restart a poll thread - it has been modified
                                thread.requestStop();
                                this.mapPollThread.remove(id);
                                //restart the poll thread with the new values
                                if (receiver.isEnableDirPoll()) {
                                    DirPollThread restartPoll = this.addPartnerPollThread(sender, receiver);
                                } else {
                                    //no restart - means it has been stopped/deleted
                                    pollStopLines.add(thread.getLogLine());
                                }
                            }
                        } else {
                            //its a local station now: stop the task and remove it
                            pollStopLines.add(thread.getLogLine());
                            thread.requestStop();
                            this.mapPollThread.remove(id);
                        }
                    }
                }
            }
            //still running task that is not in the configuration any more: stop and remove
            List<String> idList = new ArrayList<String>();
            Iterator iterator = this.mapPollThread.keySet().iterator();
            while (iterator.hasNext()) {
                idList.add((String) iterator.next());
            }
            for (String id : idList) {
                boolean idFound = false;
                for (Partner sender : localStationList) {
                    for (Partner receiver : allPartnerList) {
                        String relationShipId = sender.getDBId() + "_" + receiver.getDBId();
                        if (id.equals(relationShipId)) {
                            idFound = true;
                        }
                    }
                }
                //old still running taks, has been deleted in the config: stop and remove
                if (!idFound) {
                    DirPollThread thread = this.mapPollThread.get(id);
                    pollStopLines.add(thread.getLogLine());
                    thread.requestStop();
                    this.mapPollThread.remove(id);
                }
            }
        }
        //all done - now fire a system event
        if (!pollStopLines.isEmpty() || !pollStartLines.isEmpty()) {
            SystemEvent event = new SystemEvent(
                    SystemEvent.SEVERITY_INFO,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_DIRECTORY_MONITORING_STATE_CHANGED);
            List<DirPollThread> threadList = this.getPollThreads();
            event.setSubject(this.rb.getResourceString("manager.status.modified", String.valueOf(threadList.size())));

            StringBuilder bodyBuilder = new StringBuilder();
            //display stopped polls
            bodyBuilder.append(rb.getResourceString("title.list.polls.stopped")).append("\n");
            bodyBuilder.append("------").append("\n");
            Collections.sort(pollStopLines);
            for (String line : pollStopLines) {
                bodyBuilder.append(line).append("\n");
            }
            if (pollStopLines.isEmpty()) {
                bodyBuilder.append(rb.getResourceString("none")).append("\n");
            }
            bodyBuilder.append("\n\n");
            //display started polls
            bodyBuilder.append(rb.getResourceString("title.list.polls.started")).append("\n");
            bodyBuilder.append("------").append("\n");
            Collections.sort(pollStartLines);
            for (String line : pollStartLines) {
                bodyBuilder.append(line).append("\n");
            }
            if (pollStartLines.isEmpty()) {
                bodyBuilder.append(rb.getResourceString("none")).append("\n");
            }
            bodyBuilder.append("\n\n");
            //display all current polls
            bodyBuilder.append(rb.getResourceString("title.list.polls.running")).append("\n");
            bodyBuilder.append("------").append("\n");
            List<String> pollRunningLines = new ArrayList<String>();
            for (DirPollThread thread : threadList) {
                pollRunningLines.add(thread.getLogLine() + "\n");
            }
            Collections.sort(pollRunningLines);
            for (String line : pollRunningLines) {
                bodyBuilder.append(line);
            }
            if (pollRunningLines.isEmpty()) {
                bodyBuilder.append(rb.getResourceString("none")).append("\n");
            }
            event.setBody(bodyBuilder.toString());
            SystemEventManagerImplAS2.newEvent(event);
        }
    }

    /**
     * Adds a new partner to the poll thread list
     *
     */
    private DirPollThread addPartnerPollThread(Partner localStation, Partner partner) {
        DirPollThread thread = new DirPollThread(this.dbDriverManager, 
                this.clientserver, this.certificateManager,
                localStation, partner);
        synchronized (this.mapPollThread) {
            this.mapPollThread.put(localStation.getDBId() + "_" + partner.getDBId(), thread);
            thread.initializeThread();
            ScheduledFuture future = this.scheduledExecutor.scheduleWithFixedDelay(thread, 5000, thread.getPollIntervalInMS(), TimeUnit.MILLISECONDS);
            //set the future to the thread to have the possibility to cancel it later and 
            //remove it from the schedulers internal queue
            thread.setFuture(future);
        }
        return (thread);
    }

}
