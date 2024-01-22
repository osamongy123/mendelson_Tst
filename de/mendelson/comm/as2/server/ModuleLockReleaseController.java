//$Header: /as2/de/mendelson/comm/as2/server/ModuleLockReleaseController.java 7     27/01/22 11:34 Heller $
package de.mendelson.comm.as2.server;

import de.mendelson.util.NamedThreadFactory;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.modulelock.ModuleLock;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.sql.Connection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Heartbeat control for the exclusive modules: If a client does not refresh its
 * lock all n seconds and this is detected by this class the exclusive lock is
 * deleted in the server. This might be necessary if a client has been shut down
 * without deleting it's exclusive lock on a module (or a connection has been
 * cut or something else)
 *
 * @author S.Heller
 * @version $Revision: 7 $
 */
public class ModuleLockReleaseController {

    /**
     * Logger to log information to
     */
    private Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private LockReleaseThread releaseThread;
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(
            new NamedThreadFactory("module-lock-watchdog"));
    private final IDBDriverManager dbDriverManager;

    public ModuleLockReleaseController(IDBDriverManager dbDriverManager) throws Exception {
        this.dbDriverManager = dbDriverManager;
    }

    /**
     * Starts the embedded task that guards the log
     */
    public void startLockReleaseControl() {
        this.releaseThread = new LockReleaseThread();
        this.scheduledExecutor.scheduleWithFixedDelay(this.releaseThread, 0, 1, TimeUnit.MINUTES);
    }

    public class LockReleaseThread implements Runnable {

        public LockReleaseThread() {
        }

        @Override
        public void run() {
            Connection runtimeConnectionNoAutoCommit = null;
            try {
                runtimeConnectionNoAutoCommit = dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_RUNTIME);
                runtimeConnectionNoAutoCommit.setAutoCommit(false);
                ModuleLock.releaseAllLocksOlderThan(dbDriverManager, runtimeConnectionNoAutoCommit, TimeUnit.MINUTES.toMillis(2));
            } catch (Throwable e) {
                //nop
            } finally {
                if (runtimeConnectionNoAutoCommit != null) {
                    try {
                        runtimeConnectionNoAutoCommit.close();
                    } catch (Exception e) {
                        SystemEventManagerImplAS2.systemFailure(e);
                    }
                }
            }
        }
    }
}
