//$Header: /as2/de/mendelson/comm/as2/timing/CertificateExpireController.java 24    12/10/22 14:57 Heller $
package de.mendelson.comm.as2.timing;

import de.mendelson.util.security.cert.CertificateManager;
import de.mendelson.util.security.cert.KeystoreCertificate;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.NamedThreadFactory;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.util.Calendar;
import java.util.Date;
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
 * Controlls the certificates and checks if they will expire soon
 *
 * @author S.Heller
 * @version $Revision: 24 $
 */
public class CertificateExpireController {

    private int[] daysToExpire = new int[]{10, 3, 1};
    /**
     * Logger to log information to
     */
    private Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private CertificateManager managerSSL;
    private CertificateManager managerEncSign;
    private CertificationExpireThread expireThread;
    private final ScheduledExecutorService expireCheckScheduler = Executors.newSingleThreadScheduledExecutor(
            new NamedThreadFactory("certificate-expire-check"));

    public CertificateExpireController(CertificateManager managerEncSign, CertificateManager managerSSL) {        
        this.managerEncSign = managerEncSign;
        this.managerSSL = managerSSL;
    }

    /**
     * Starts the embedded task that guards the log
     */
    public void startCertExpireControl() {
        this.expireThread = new CertificationExpireThread();
        this.expireCheckScheduler.scheduleWithFixedDelay(this.expireThread, 0, 1, TimeUnit.DAYS);
    }

    /**
     * Computes and returns the number of days between the two passed dates
     *
     * @param date1 first date
     * @param date2 second date
     * @param dateFormat1 Format of the first date
     * @param dateFormat2 Format of the second date
     */
    private static int getDayDiff(Date firstDate, Date secondDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstDate);
        int dayOfYear1 = calendar.get(Calendar.DAY_OF_YEAR);
        int year1 = calendar.get(Calendar.YEAR);
        calendar.setTime(secondDate);
        int dayOfYear2 = calendar.get(Calendar.DAY_OF_YEAR);
        int year2 = calendar.get(Calendar.YEAR);
        return ((year2 - year1) * 365 + (dayOfYear2 - dayOfYear1));
    }

    public static int getCertificateExpireDuration(KeystoreCertificate certificate) {
        return (getDayDiff(new Date(), certificate.getNotAfter()));
    }

    public class CertificationExpireThread implements Runnable {

        public CertificationExpireThread() {
        }

        @Override
        public void run() {
            try {
                List<KeystoreCertificate> encSignList = managerEncSign.getKeyStoreCertificateList();
                this.checkCertificates(encSignList);
                List<KeystoreCertificate> sslList = managerSSL.getKeyStoreCertificateList();
                this.checkCertificates(sslList);
            } catch (Throwable e) {
                SystemEventManagerImplAS2.systemFailure(e);
            }
        }

        /**
         * Checks if a certificate is expire or is up to expire
         */
        private void checkCertificates(List<KeystoreCertificate> list) {
            for (KeystoreCertificate certificate : list) {
                int certificateExpireDuration = CertificateExpireController.getCertificateExpireDuration(certificate);
                //The certificate has not been expired so far
                for (int expireDuration : daysToExpire) {
                    if (certificateExpireDuration == expireDuration) {
                        SystemEventManagerImplAS2 manager
                                = new SystemEventManagerImplAS2();
                        try {
                            manager.newEventCertificateWillExpire(certificate, certificateExpireDuration);
                        } catch (Exception e) {
                            String exceptionClass = "[" + e.getClass().getName() + "]";
                            logger.severe("CertificateExpireThread: " + exceptionClass + " " + e.getMessage());
                            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_PROCESSING_ANY);
                        }
                    }
                }
                //The certificate has been already expired
                if (certificateExpireDuration <= 0) {
                    SystemEventManagerImplAS2 manager = new SystemEventManagerImplAS2();
                    try {
                        manager.newEventCertificateWillExpire(certificate, certificateExpireDuration);
                    } catch (Exception e) {
                        String exceptionClass = "[" + e.getClass().getName() + "]";
                        logger.severe("CertificateExpireThread: " + exceptionClass + " " + e.getMessage());
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_PROCESSING_ANY);
                    }
                }
            }
        }

    }
}
