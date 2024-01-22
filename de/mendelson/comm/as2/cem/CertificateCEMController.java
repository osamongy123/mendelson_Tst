//$Header: /as2/de/mendelson/comm/as2/cem/CertificateCEMController.java 17    28/07/22 9:23 Heller $
package de.mendelson.comm.as2.cem;

import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.comm.as2.clientserver.message.RefreshClientCEMDisplay;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.MessageAccessDB;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerAccessDB;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.NamedThreadFactory;
import de.mendelson.util.clientserver.ClientServer;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.sql.Connection;
import java.util.List;
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
 * Controller that executes CEM events
 *
 * @author S.Heller
 * @version $Revision: 17 $
 */
public class CertificateCEMController {

    /**
     * Logger to log information to
     */
    private Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private IDBDriverManager dbDriverManager;
    /**
     * Stores the certificates
     */
    private CertificateManager certificateManager;
    private ClientServer clientserver;
    private CEMControllerThread checkThread;
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(
            new NamedThreadFactory("cem-controller"));

    public CertificateCEMController(ClientServer clientserver,
            IDBDriverManager dbDriverManager,
            CertificateManager certificateManager) {
        this.certificateManager = certificateManager;
        this.dbDriverManager = dbDriverManager;
        this.clientserver = clientserver;
    }

    public void start() {
        this.checkThread = new CEMControllerThread();
        this.scheduledExecutor.scheduleWithFixedDelay(this.checkThread, 1, 1, TimeUnit.MINUTES);
    }

    public class CEMControllerThread implements Runnable {

        public CEMControllerThread() {
        }

        @Override
        public void run() {

            try {
                this.handleRequestProcessingErrors();
                this.handleCertificateConfigChangeShouldHappenNow();
            } catch (Throwable e) {
                e.printStackTrace();
                logger.severe("CertificateCEMController: " + e.getMessage());
                SystemEventManagerImplAS2.systemFailure(e);
            }
        }

        /**
         * In CEM the certifiates for signatures and ssl may contain a respond
         * by date to enable them. If the bydate is not given in the request
         * nothing will happen until a response comes.
         */
        private void handleCertificateConfigChangeShouldHappenNow() {
            PartnerAccessDB partnerAccess
                    = new PartnerAccessDB(dbDriverManager);
            CEMAccessDB cemAccess = new CEMAccessDB(dbDriverManager);
            List<CEMEntry> certificateChangeList = cemAccess.getCertificatesToChange();
            SystemEventManagerImplAS2 eventManager
                    = new SystemEventManagerImplAS2();
            for (CEMEntry entry : certificateChangeList) {
                Partner partner = partnerAccess.getPartner(entry.getInitiatorAS2Id());
                KeystoreCertificate referencedCert = certificateManager.getKeystoreCertificateByIssuerDNAndSerial(
                        entry.getIssuername(), entry.getSerialId());
                if (referencedCert == null) {
                    throw new RuntimeException("The CEM entry references a certificate with issuer " + entry.getIssuername()
                            + " and serial " + entry.getSerialId() + " that is not found in the system");
                }
                cemAccess.markAsProcessed(entry.getRequestId(), entry.getCategory());
                //a state has changed: inform the user
                logger.fine(partner.getPartnerCertificateInformationList()
                        .getCertificatePurposeDescription(certificateManager, partner, entry.getCategory()));
                try {
                    eventManager.newEventCertificateChangeShouldHappenNowByCEM(certificateManager, partner, entry.getCategory());
                } catch (Exception e) {
                    logger.warning("CertificateCEMController: Notification@handleCertificateChanges " + e.getMessage());
                }
            }
            if (clientserver != null) {
                clientserver.broadcastToClients(new RefreshClientCEMDisplay());
            }
        }

        /**
         * Checks if a CEM request came back with a failure MDN. In this case
         * the whole request should be set to processing error
         */
        private void handleRequestProcessingErrors() {
            CEMAccessDB cemAccess = new CEMAccessDB(dbDriverManager);
            MessageAccessDB messageAccess
                    = new MessageAccessDB(dbDriverManager);
            List<CEMEntry> pendingCEM = cemAccess.getCEMEntriesPending();
            for (CEMEntry entry : pendingCEM) {
                if (entry.getRequestMessageid() != null) {
                    AS2MessageInfo messageInfo = messageAccess.getLastMessageEntry(entry.getRequestMessageid());
                    //it could happen that the pending request message no longer exists
                    if (messageInfo != null && messageInfo.getState() == AS2Message.STATE_STOPPED) {
                        cemAccess.setPendingRequestsToState(entry.getInitiatorAS2Id(),
                                entry.getReceiverAS2Id(), CEMEntry.CATEGORY_CRYPT,
                                entry.getRequestId(), CEMEntry.STATUS_PROCESSING_ERROR_INT);
                        cemAccess.setPendingRequestsToState(entry.getInitiatorAS2Id(),
                                entry.getReceiverAS2Id(), CEMEntry.CATEGORY_SIGN,
                                entry.getRequestId(), CEMEntry.STATUS_PROCESSING_ERROR_INT);
                        cemAccess.setPendingRequestsToState(entry.getInitiatorAS2Id(),
                                entry.getReceiverAS2Id(), CEMEntry.CATEGORY_SSL,
                                entry.getRequestId(), CEMEntry.STATUS_PROCESSING_ERROR_INT);
                    }
                }
            }
            if (clientserver != null) {
                clientserver.broadcastToClients(new RefreshClientCEMDisplay());
            }
        }
    }
}
