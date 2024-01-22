//$Header: /as2/de/mendelson/comm/as2/configurationcheck/ConfigurationCheckController.java 38    24/11/22 10:22 Heller $
package de.mendelson.comm.as2.configurationcheck;

import de.mendelson.comm.as2.preferences.PreferencesAS2;
import de.mendelson.comm.as2.timing.CertificateExpireController;
import de.mendelson.comm.as2.message.MessageAccessDB;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.comm.as2.send.DirPollManager;
import de.mendelson.util.AS2Tools;
import de.mendelson.util.NamedThreadFactory;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.httpconfig.server.HTTPServerConfigInfo;
import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreCertificate;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Checks several issues of the configuration
 *
 * @author S.Heller
 * @version $Revision: 38 $
 */
public class ConfigurationCheckController {

    private final CertificateManager managerEncSign;
    private final CertificateManager managerTLS;
    private final ConfigurationCheckThread checkThread = new ConfigurationCheckThread();
    private final PreferencesAS2 preferences = new PreferencesAS2();
    private final HTTPServerConfigInfo httpServerConfigInfo;
    private final DirPollManager pollManager;
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(
            new NamedThreadFactory("configuration-check"));
    private final IDBDriverManager dbDriverManager;

    public ConfigurationCheckController(CertificateManager managerEncSign,
            CertificateManager managerTLS, HTTPServerConfigInfo httpServerConfigInfo, DirPollManager pollManager,
            IDBDriverManager dbDriverManager) {
        this.managerEncSign = managerEncSign;
        this.managerTLS = managerTLS;
        this.httpServerConfigInfo = httpServerConfigInfo;
        this.pollManager = pollManager;
        this.dbDriverManager = dbDriverManager;
    }

    /**
     * Returns all available issues that are detected by the server control
     */
    public List<ConfigurationIssue> getIssues() {
        return (this.checkThread.getIssues());
    }

    /**
     * Runs the configuration checks once - outside the thread context
     */
    public List<ConfigurationIssue> runOnce() {
        ConfigurationCheckThread testThread = new ConfigurationCheckThread();
        List<ConfigurationIssue> issueList = new ArrayList<ConfigurationIssue>();
        testThread.runAllChecks(issueList);
        return (issueList);
    }

    /**
     * Starts the embedded task that guards the log
     */
    public void start() {
        this.scheduledExecutor.scheduleWithFixedDelay(this.checkThread, 1, 30, TimeUnit.SECONDS);
    }

    public class ConfigurationCheckThread implements Runnable {

        private final List<ConfigurationIssue> allIssuesList = Collections.synchronizedList(new ArrayList<ConfigurationIssue>());
        /**
         * The list of issues that needs to be checked only once - the check
         * result will always the same as the user cannot change this
         */
        private final List<ConfigurationIssue> fixedIssuesList = Collections.synchronizedList(new ArrayList<ConfigurationIssue>());
        private boolean firstRun = true;

        public ConfigurationCheckThread() {
        }

        @Override
        public void run() {
            List<ConfigurationIssue> newIssueList = new ArrayList<ConfigurationIssue>();
            this.runAllChecks(newIssueList);
            //do block the current issue list as short as possible
            synchronized (this.allIssuesList) {
                this.allIssuesList.clear();
                this.allIssuesList.addAll(newIssueList);
            }
        }

        public List<ConfigurationIssue> getIssues() {
            List<ConfigurationIssue> issues = new ArrayList<ConfigurationIssue>();
            synchronized (this.allIssuesList) {
                issues.addAll(this.allIssuesList);
            }
            return (issues);
        }

        /**
         * Runs all checks and puts the results into the passed issue list
         *
         * @param newIssueList
         */
        public void runAllChecks(List<ConfigurationIssue> newIssueList) {
            if (this.firstRun) {
                List<ConfigurationIssue> newFixedIssuesList = new ArrayList<ConfigurationIssue>();
                this.runUnmodifyableChecks(newFixedIssuesList);
                this.firstRun = false;
                //store the unmodifyable issues once
                synchronized (this.fixedIssuesList) {
                    this.fixedIssuesList.addAll(newFixedIssuesList);
                }
            }
            //add all unmodifyable issues to the new issues list
            synchronized (this.fixedIssuesList) {
                newIssueList.addAll(this.fixedIssuesList);
            }
            this.runModifyableChecks(newIssueList);
        }

        /**
         * Runs the checks that will always return the same result - no matter
         * how often you run this check. E.g. the number of CPUs cannot be
         * changed, same to the heap memory, the data model etc. Its not
         * necessary to run this n times.
         *
         * @param newIssueList
         */
        public void runUnmodifyableChecks(List<ConfigurationIssue> newIssueList) {
            this.checkCPUCores(newIssueList);
            this.checkHeapMemory(newIssueList);
            this.checkDataModel32bit(newIssueList);
            this.checkWindowsServiceWithLocalSystemAccount(newIssueList);
        }

        /**
         * Runs the checks that could be modified by the user - e.g. setup a new
         * TLS key or something like this. It makes sense to run this
         * periodically
         *
         * @param newIssueList
         */
        public void runModifyableChecks(List<ConfigurationIssue> newIssueList) {
            this.checkCertificatesExpired(newIssueList);
            this.checkKeystore(newIssueList);
            this.checkAutoDelete(newIssueList);
            this.checkOutboundConnectionsAllowed(newIssueList);
            this.checkAllPartnersCertificatesAvailable(newIssueList);
            this.checkTLSKeystoreSettings(newIssueList);
            this.checkDirPollAmount(newIssueList);
        }

        /**
         * It is possible that a keystore has been modified by an external
         * program - or deleted?
         *
         */
        private void checkAllPartnersCertificatesAvailable(List<ConfigurationIssue> newIssueList) {
            PartnerAccessDB partnerAccess = new PartnerAccessDB(dbDriverManager);
            List<Partner> partnerList = partnerAccess.getAllPartner();
            for (Partner partner : partnerList) {
                String cryptFingerprint = partner.getCryptFingerprintSHA1();
                String signFingerprint = partner.getSignFingerprintSHA1();
                KeystoreCertificate certEncrypt = managerEncSign.getKeystoreCertificateByFingerprintSHA1(cryptFingerprint);
                if (certEncrypt == null) {
                    ConfigurationIssue issue = null;
                    if (partner.isLocalStation()) {
                        issue = new ConfigurationIssue(ConfigurationIssue.KEY_MISSING_ENC_LOCAL_STATION);
                    } else {
                        issue = new ConfigurationIssue(ConfigurationIssue.CERTIFICATE_MISSING_ENC_REMOTE_PARTNER);
                    }
                    issue.setDetails(partner.getName());
                    newIssueList.add(issue);
                }
                KeystoreCertificate certSign = managerEncSign.getKeystoreCertificateByFingerprintSHA1(signFingerprint);
                if (certSign == null) {
                    ConfigurationIssue issue = null;
                    if (partner.isLocalStation()) {
                        issue = new ConfigurationIssue(ConfigurationIssue.KEY_MISSING_SIGN_LOCAL_STATION);
                    } else {
                        issue = new ConfigurationIssue(ConfigurationIssue.CERTIFICATE_MISSING_SIGN_REMOTE_PARTNER);
                    }
                    issue.setDetails(partner.getName());
                    newIssueList.add(issue);
                }

            }
        }

        private void checkCertificatesExpired(List<ConfigurationIssue> newIssueList) {
            List<KeystoreCertificate> encSignList = managerEncSign.getKeyStoreCertificateList();
            for (KeystoreCertificate cert : encSignList) {
                if (CertificateExpireController.getCertificateExpireDuration(cert) <= 0) {
                    ConfigurationIssue issue = new ConfigurationIssue(ConfigurationIssue.CERTIFICATE_EXPIRED_ENC_SIGN);
                    issue.setDetails(cert.getAlias());
                    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
                    issue.setHintParameter(new Object[]{
                        cert.getAlias(), cert.getIssuerDN(), cert.getFingerPrintSHA1(),
                        dateFormat.format(cert.getNotBefore()),
                        dateFormat.format(cert.getNotAfter())
                    });
                    newIssueList.add(issue);
                }
            }
            List<KeystoreCertificate> sslList = managerTLS.getKeyStoreCertificateList();
            for (KeystoreCertificate cert : sslList) {
                if (CertificateExpireController.getCertificateExpireDuration(cert) <= 0) {
                    ConfigurationIssue issue = new ConfigurationIssue(ConfigurationIssue.CERTIFICATE_EXPIRED_TLS);
                    issue.setDetails(cert.getAlias());
                    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
                    issue.setHintParameter(new Object[]{
                        cert.getAlias(), cert.getIssuerDN(), cert.getFingerPrintSHA1(),
                        dateFormat.format(cert.getNotBefore()),
                        dateFormat.format(cert.getNotAfter())
                    });
                    newIssueList.add(issue);
                }
            }
        }

        private void checkDataModel32bit(List<ConfigurationIssue> newIssueList) {
            String dataModel = "";
            try {
                dataModel = System.getProperty("sun.arch.data.model");
                int bits = Integer.parseInt(dataModel);
                if (bits == 32) {
                    ConfigurationIssue issue = new ConfigurationIssue(ConfigurationIssue.JVM_32_BIT);
                    newIssueList.add(issue);
                }
            } catch (Throwable e) {
                //ignore this - it does work only on oracle VMs. If the property is not supported it will return "unknown" which could
                //not be parsed as an integer and will result in a NumberFormatException
            }
        }

        private void checkOutboundConnectionsAllowed(List<ConfigurationIssue> newIssueList) {
            int numberOfConnections = preferences.getInt(PreferencesAS2.MAX_OUTBOUND_CONNECTIONS);
            if (numberOfConnections == 0) {
                ConfigurationIssue issue = new ConfigurationIssue(ConfigurationIssue.NO_OUTBOUND_CONNECTIONS_ALLOWED);
                issue.setDetails("");
                newIssueList.add(issue);
            }
        }

        /**
         * Finds out some issues that could occur in the underlaying keystores
         */
        private void checkKeystore(List<ConfigurationIssue> newIssueList) {
            Path keystoreFileTLS = Paths.get(preferences.get(PreferencesAS2.KEYSTORE_HTTPS_SEND));
            if (!Files.isWritable(keystoreFileTLS)) {
                ConfigurationIssue issue = new ConfigurationIssue(ConfigurationIssue.KEYSTORE_TLS_RO);
                issue.setHintParameter(new Object[]{
                    keystoreFileTLS.toAbsolutePath().toString(),
                    System.getProperty("user.name")
                });
                newIssueList.add(issue);
            }
            Path keystoreFileEncryptSign = Paths.get(preferences.get(PreferencesAS2.KEYSTORE));
            if (!Files.isWritable(keystoreFileEncryptSign)) {
                ConfigurationIssue issue = new ConfigurationIssue(ConfigurationIssue.KEYSTORE_SIGN_ENCRYPT_RO);
                issue.setHintParameter(new Object[]{
                    keystoreFileEncryptSign.toAbsolutePath().toString(),
                    System.getProperty("user.name")
                });
                newIssueList.add(issue);
            }
            List<KeystoreCertificate> tlsList = managerTLS.getKeyStoreCertificateList();
            StringBuilder aliasList = new StringBuilder();
            int keyCount = 0;
            List<KeystoreCertificate> keystoreKeysList = new ArrayList<KeystoreCertificate>();
            for (KeystoreCertificate cert : tlsList) {
                if (cert.getIsKeyPair()) {
                    if (aliasList.length() > 0) {
                        aliasList.append(", ");
                    }
                    aliasList.append(cert.getAlias());
                    keystoreKeysList.add(cert);
                    keyCount++;
                }
            }
            if (keyCount == 0) {
                ConfigurationIssue issue = new ConfigurationIssue(ConfigurationIssue.NO_KEY_IN_TLS_KEYSTORE);
                newIssueList.add(issue);
            } else if (keyCount > 1) {
                ConfigurationIssue issue = new ConfigurationIssue(ConfigurationIssue.MULTIPLE_KEYS_IN_TLS_KEYSTORE);
                issue.setDetails(aliasList.toString());
                synchronized (this.allIssuesList) {
                    this.allIssuesList.add(issue);
                }
            } else {
                KeystoreCertificate usedSSLKey = keystoreKeysList.get(0);
                String foundFingerprint = usedSSLKey.getFingerPrintSHA1();
                for (String testFingerprint : KeystoreCertificate.TEST_KEYS_FINGERPRINTS_SHA1) {
                    if (foundFingerprint.equalsIgnoreCase(testFingerprint)) {
                        ConfigurationIssue issue = new ConfigurationIssue(ConfigurationIssue.USE_OF_TEST_KEYS_IN_TLS);
                        issue.setDetails(usedSSLKey.getAlias());
                        newIssueList.add(issue);
                    }
                }
            }
        }

        private void checkAutoDelete(List<ConfigurationIssue> newIssueList) {
            if (!preferences.getBoolean(PreferencesAS2.AUTO_MSG_DELETE)) {
                MessageAccessDB messageAccess = new MessageAccessDB(dbDriverManager);
                int transmissionCount = messageAccess.getMessageCount();
                if (transmissionCount > 30000) {
                    ConfigurationIssue issue = new ConfigurationIssue(ConfigurationIssue.HUGE_AMOUNT_OF_TRANSACTIONS_NO_AUTO_DELETE);
                    issue.setDetails(String.valueOf(transmissionCount));
                    newIssueList.add(issue);
                }
            }
        }

        private void checkCPUCores(List<ConfigurationIssue> newIssueList) {
            int cores = Runtime.getRuntime().availableProcessors();
            if (cores < 4) {
                ConfigurationIssue issue = new ConfigurationIssue(ConfigurationIssue.FEW_CPU_CORES);
                issue.setDetails(String.valueOf(cores));
                newIssueList.add(issue);
            }
        }

        private void checkHeapMemory(List<ConfigurationIssue> newIssueList) {
            long maxMemory = Runtime.getRuntime().maxMemory();
            long oneGB = 1073741824L;
            if (maxMemory < 4 * oneGB) {
                ConfigurationIssue issue = new ConfigurationIssue(ConfigurationIssue.LOW_MAX_HEAP_MEMORY);
                issue.setDetails(AS2Tools.getDataSizeDisplay(maxMemory));
                newIssueList.add(issue);
            }
        }

        /**
         * Checks if the underlaying http server uses the same keystore as
         * defined in the settings. This check makes just sense if a TLS receipt
         * handler is defined in the system
         *
         */
        private void checkTLSKeystoreSettings(List<ConfigurationIssue> newIssueList) {
            //its possible to start the system without the http server. In this case there are some values missing            
            if (httpServerConfigInfo == null
                    || httpServerConfigInfo.getKeystorePath() == null) {
                return;
            }
            if ((!httpServerConfigInfo.isEmbeddedHTTPServerStarted()) || (!httpServerConfigInfo.isSSLEnabled())) {
                return;
            }
            String httpsKeystorePath = Paths.get(httpServerConfigInfo.getKeystorePath()).normalize().toAbsolutePath().toString();
            String preferencesKeystorePath = Paths.get(preferences.get(PreferencesAS2.KEYSTORE_HTTPS_SEND)).normalize().toAbsolutePath().toString();
            if (!httpsKeystorePath.equals(preferencesKeystorePath)) {
                ConfigurationIssue issue = new ConfigurationIssue(ConfigurationIssue.DIFFERENT_KEYSTORES_TLS);
                issue.setHintParameter(
                        new Object[]{
                            httpsKeystorePath,
                            httpServerConfigInfo.getHTTPServerConfigFile().normalize().toAbsolutePath().toString(),
                            preferencesKeystorePath
                        });
                newIssueList.add(issue);
            }
        }

        /**
         * Checks if the system runs as service with a local system account.
         * This is a problem because Windows updates may change the rights of
         * this user and it is possible that formerly written files of this user
         * become read only or unaccessible for this user.
         */
        private void checkWindowsServiceWithLocalSystemAccount(List<ConfigurationIssue> newIssueList) {
            //check if this is a windows service with a local system account as user
            if (System.getenv("iswindowsservice") != null && System.getenv("iswindowsservice").equals("1")) {
                String serverUser = System.getProperty("user.name");
                if (serverUser != null && serverUser.contains("$")) {
                    ConfigurationIssue issue = new ConfigurationIssue(ConfigurationIssue.WINDOWS_SERVICE_LOCAL_SYSTEM_ACCOUNT);
                    issue.setDetails(serverUser);
                    issue.setHintParameter(new Object[]{serverUser});
                    newIssueList.add(issue);
                }
            }
        }

        /**
         * Checks if the number of dir polls exceeds 5 polls/sec - these are
         * 300polls/min. This might have impact on the file IO
         */
        private void checkDirPollAmount(List<ConfigurationIssue> newIssueList) {
            float pollsPerMin = ConfigurationCheckController.this.pollManager.getPollsPerMinute();
            if (pollsPerMin > 300f) {
                ConfigurationIssue issue = new ConfigurationIssue(ConfigurationIssue.TOO_MANY_DIR_POLLS);
                issue.setDetails(String.format("%.0f", pollsPerMin));
                issue.setHintParameter(new Object[]{String.format("%.0f", pollsPerMin)});
                newIssueList.add(issue);
            }
        }

    }
}
