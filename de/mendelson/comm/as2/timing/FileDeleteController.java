//$Header: /as2/de/mendelson/comm/as2/timing/FileDeleteController.java 18    1/06/22 14:49 Heller $
package de.mendelson.comm.as2.timing;

import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.IOFileFilterCreationDate;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.NamedThreadFactory;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Controls the timed deletion of AS2 file entries from the file system
 *
 * @author S.Heller
 * @version $Revision: 18 $
 */
public class FileDeleteController {

    /**
     * Logger to log information to
     */
    private Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private PreferencesAS2 preferences = new PreferencesAS2();
    private TmpFileDeleteThread tempFileDeleteThread;
    private LogFileDeleteThread logFileDeleteThread;
    private MecResourceBundle rb = null;
    private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1,
            new NamedThreadFactory("file-delete-control"));

    public FileDeleteController() {
        //Load default resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleFileDeleteController.class.getName());
        } //load up resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    /**
     * Starts the embedded task that guards the files to delete
     */
    public void startAutoDeleteControl() {
        this.tempFileDeleteThread = new TmpFileDeleteThread();
        //schedule the thread
        int initialDelay = 15;
        int period = 30;
        this.scheduledExecutor.scheduleWithFixedDelay(this.tempFileDeleteThread, initialDelay, period, TimeUnit.MINUTES);
        this.logFileDeleteThread = new LogFileDeleteThread();
        //schedule the thread
        initialDelay = 30;
        period = 30;
        this.scheduledExecutor.scheduleWithFixedDelay(this.logFileDeleteThread, initialDelay, period, TimeUnit.MINUTES);
    }

    /**
     * Non blocking file directory list
     */
    private List<Path> listFilesNIO(Path dir, DirectoryStream.Filter fileFilter) throws Exception {
        List<Path> result = new ArrayList<Path>();
        DirectoryStream<Path> stream = null;
        try {
            stream = Files.newDirectoryStream(dir, fileFilter);
            for (Path entry : stream) {
                result.add(entry);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return result;
    }

    /**
     * Non blocking subdir list
     */
    private List<Path> listDirsNIO(Path parentDir, long olderThanTimeAbsolute) throws Exception {
        IOFileFilterCreationDate dirOnlyFilter
                = new IOFileFilterCreationDate(IOFileFilterCreationDate.MODE_OLDER_THAN, olderThanTimeAbsolute);
        dirOnlyFilter.setIncludeFiles(false);
        dirOnlyFilter.setIncludeDirecories(true);
        List<Path> result = new ArrayList<Path>();
        DirectoryStream<Path> stream = null;
        try {
            stream = Files.newDirectoryStream(parentDir, dirOnlyFilter);
            for (Path entry : stream) {
                result.add(entry);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return result;
    }

    /**
     * Deletes a path recursive and throws an exception with details if this
     * fails
     */
    public void deleteDirectoryRecursive(Path dir) throws IOException {
        List<Path> pathsToDelete = Files.walk(dir)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        for (Path path : pathsToDelete) {
            Files.deleteIfExists(path);
        }
    }

    /**
     * Deletes all log and event files of the system
     */
    public class LogFileDeleteThread implements Runnable {

        @Override
        public void run() {
            if (preferences.getBoolean(PreferencesAS2.AUTO_LOGDIR_DELETE)) {
                long maxAgeInDays = preferences.getInt(PreferencesAS2.AUTO_LOGDIR_DELETE_OLDERTHAN);
                long olderThanTimeAbsolute = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(maxAgeInDays);
                StringBuilder deleteLog = new StringBuilder();
                int foundEntries = 0;
                int eventSeverity = SystemEvent.SEVERITY_INFO;
                try {
                    Path logDirRoot = Paths.get("log");
                    List<Path> subDirList = listDirsNIO(logDirRoot, olderThanTimeAbsolute);
                    foundEntries = subDirList.size();
                    deleteLog.append(rb.getResourceString("delete.header.logfiles", String.valueOf(maxAgeInDays)));
                    deleteLog.append(System.lineSeparator()).append("---");
                    for (Path logDir : subDirList) {
                        deleteLog.append(System.lineSeparator());
                        deleteLog.append(logDir.toAbsolutePath());
                        try {
                            deleteDirectoryRecursive(logDir);
                            deleteLog.append(" [").append(rb.getResourceString("success")).append("]");
                        } catch (Exception e) {
                            deleteLog.append(" [").append(rb.getResourceString("failure")).append("]");
                            eventSeverity = SystemEvent.SEVERITY_WARNING;
                            deleteLog.append(" [").append(e.getClass().getSimpleName()).append("]: ").append(e.getMessage());
                        }
                    }
                } catch (Throwable e) {
                    eventSeverity = SystemEvent.SEVERITY_WARNING;
                    deleteLog.append(System.lineSeparator());
                    deleteLog.append("[").append(e.getClass().getSimpleName()).append("]: ").append(e.getMessage());
                }
                if (foundEntries > 0 || eventSeverity != SystemEvent.SEVERITY_INFO) {
                    SystemEvent event = new SystemEvent(
                            eventSeverity, SystemEvent.ORIGIN_SYSTEM, SystemEvent.TYPE_FILE_DELETE);
                    event.setSubject(rb.getResourceString("delete.title.log"));
                    event.setBody(deleteLog.toString());
                    SystemEventManagerImplAS2.newEvent(event);
                }
            }
        }
    }

    /**
     * Deletes all tmp files of the system
     */
    public class TmpFileDeleteThread implements Runnable {

        public TmpFileDeleteThread() {
        }

        @Override
        public void run() {
            if (preferences.getBoolean(PreferencesAS2.AUTO_MSG_DELETE)) {
                Path rawIncomingDir
                        = Paths.get(Paths.get(preferences.get(PreferencesAS2.DIR_MSG)).toAbsolutePath().toString(),
                                "_rawincoming");
                //delete all files that are older than MDN wait time + delete log time
                long maxAgeInS = (preferences.getInt(PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN)
                        * preferences.getInt(PreferencesAS2.AUTO_MSG_DELETE_OLDERTHAN_MULTIPLIER_S))
                        + TimeUnit.MINUTES.toSeconds(preferences.getInt(PreferencesAS2.ASYNC_MDN_TIMEOUT));
                long olderThanTimeAbsolute = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(maxAgeInS);
                //substract one additional day as buffer. There is another thread that deletes the transmissions which has priority to this file cleanup process.
                //This is to ensure that this task will not delete
                //any transmission file if it runs first and the second task runs directly afterwards. 
                //In this case the other thread would complain about
                //missing files for transmissions and throw system event errors
                olderThanTimeAbsolute = olderThanTimeAbsolute - TimeUnit.DAYS.toMillis(1);
                int eventSeverity = SystemEvent.SEVERITY_INFO;
                IOFileFilterCreationDate fileFilter = new IOFileFilterCreationDate(IOFileFilterCreationDate.MODE_OLDER_THAN, olderThanTimeAbsolute);
                StringBuilder deleteLog = new StringBuilder();
                AtomicInteger foundEntries = new AtomicInteger(0);
                try {
                    deleteLog.append(rb.getResourceString("delete.title._rawincoming"));
                    deleteLog.append(System.lineSeparator()).append("---").append(System.lineSeparator());
                    //delete _rawincoming entries
                    int severity = this.deleteFilesInDirectory(rawIncomingDir, fileFilter, deleteLog, foundEntries);
                    if (severity == SystemEvent.SEVERITY_WARNING) {
                        eventSeverity = SystemEvent.SEVERITY_WARNING;
                    }
                } catch (Exception e) {
                    eventSeverity = SystemEvent.SEVERITY_WARNING;
                    deleteLog.append("[").append(e.getClass().getSimpleName()).append("]: ").append(e.getMessage());
                }
                try {
                    //delete temp dir entries and subdirectories
                    fileFilter.setIncludeDirecories(true);
                    deleteLog.append(System.lineSeparator());
                    deleteLog.append(System.lineSeparator());
                    deleteLog.append(rb.getResourceString("delete.title.tempfiles"));
                    deleteLog.append(System.lineSeparator()).append("---").append(System.lineSeparator());
                    int severity = this.deleteFilesInDirectory(Paths.get("temp"), fileFilter, deleteLog, foundEntries);
                    if (severity == SystemEvent.SEVERITY_WARNING) {
                        eventSeverity = SystemEvent.SEVERITY_WARNING;
                    }
                } catch (Exception e) {
                    eventSeverity = SystemEvent.SEVERITY_WARNING;
                    deleteLog.append("[").append(e.getClass().getSimpleName()).append("]: ").append(e.getMessage());
                }
                if (foundEntries.intValue() > 0) {
                    SystemEvent event = new SystemEvent(
                            eventSeverity, SystemEvent.ORIGIN_SYSTEM, SystemEvent.TYPE_FILE_DELETE);
                    event.setSubject(rb.getResourceString("delete.title"));
                    event.setBody(deleteLog.toString());
                    SystemEventManagerImplAS2.newEvent(event);
                }
            }
        }

        /**
         * Deletes all files found in the passed path that matches the file
         * filter
         *
         * @return The severity of the operation
         */
        private int deleteFilesInDirectory(Path directory, IOFileFilterCreationDate fileFilter,
                StringBuilder deleteLog, AtomicInteger foundEntries) throws Exception {
            int eventSeverity = SystemEvent.SEVERITY_INFO;
            List<Path> fileList = listFilesNIO(directory, fileFilter);
            if (fileList.isEmpty()) {
                deleteLog.append(rb.getResourceString("no.entries", directory.toAbsolutePath().toString()));
                deleteLog.append(System.lineSeparator());
            }
            for (Path singlePath : fileList) {
                foundEntries.incrementAndGet();
                //if its a directory descent into it and delete it first
                if (Files.isDirectory(singlePath)) {
                    int severity = this.deleteFilesInDirectory(singlePath, fileFilter, deleteLog, foundEntries);
                    if (severity == SystemEvent.SEVERITY_WARNING) {
                        eventSeverity = SystemEvent.SEVERITY_WARNING;
                    }
                }
                try {
                    Files.delete(singlePath);
                    deleteLog.append(rb.getResourceString("success") + ": ");
                    deleteLog.append(singlePath.toAbsolutePath().toString());
                    deleteLog.append(System.lineSeparator());
                    logger.config(rb.getResourceString("autodelete", singlePath.toAbsolutePath().toString()));
                } catch (Exception delEx) {
                    deleteLog.append(rb.getResourceString("failure") + " [" + delEx.getClass().getSimpleName() + "]: ");
                    deleteLog.append(singlePath.toAbsolutePath().toString());
                    deleteLog.append(System.lineSeparator());
                    eventSeverity = SystemEvent.SEVERITY_WARNING;
                }
            }
            return (eventSeverity);
        }

    }
}
