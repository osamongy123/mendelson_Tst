//$Header: /as2/de/mendelson/comm/as2/cert/CertificateAccessDB.java 24    29/11/22 13:17 Heller $
package de.mendelson.comm.as2.cert;

import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.comm.as2.partner.PartnerCertificateInformation;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Access the certificate lists in the database
 *
 * @author S.Heller
 * @version $Revision: 24 $
 */
public class CertificateAccessDB {

    /**
     * Logger to log information to
     */
    private Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);

    public CertificateAccessDB() {
    }

    /**
     * Returns the list of certificates used by the passed partner
     */
    public void loadPartnerCertificateInformation(Partner partner, Connection configConnection) {
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = configConnection.prepareStatement("SELECT * FROM certificates WHERE partnerid=?");
            statement.setInt(1, partner.getDBId());
            statement.setEscapeProcessing(true);
            result = statement.executeQuery();
            while (result.next()) {
                String fingerprint = result.getString("fingerprintsha1");
                PartnerCertificateInformation information = new PartnerCertificateInformation(
                        fingerprint, result.getInt("category"));
                partner.setCertificateInformation(information);
            }
        } catch (Exception e) {
            this.logger.severe("CertificateAccessDB.loadPartnerCertificateInformation: " + e.getMessage());
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statement);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (result != null) {
                try {
                    result.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
    }

    /**
     * Stores the actual partner certificate list of a partner
     */
    public void storePartnerCertificateInformationList(Partner partner, Connection configConnection) throws Exception {
        this.deletePartnerCertificateInformationList(partner, configConnection);
        Collection<PartnerCertificateInformation> list = partner.getPartnerCertificateInformationList().asList();
        for (PartnerCertificateInformation certInfo : list) {
            PreparedStatement statement = null;
            try {
                statement = configConnection.prepareStatement("INSERT INTO certificates(partnerid,fingerprintsha1,category)VALUES(?,?,?)");
                statement.setEscapeProcessing(true);
                statement.setInt(1, partner.getDBId());
                statement.setString(2, certInfo.getFingerprintSHA1());
                statement.setInt(3, certInfo.getCategory());
                statement.execute();
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception e) {
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                    }
                }
            }
        }
    }

    /**
     * Deletes the actual partner certificate list of a partner. A config
     * connection is passed to allow the storing process in a transactional way
     */
    public void deletePartnerCertificateInformationList(Partner partner, Connection configConnection) throws Exception {
        PreparedStatement statement = null;
        try {
            statement = configConnection.prepareStatement("DELETE FROM certificates WHERE partnerid=?");
            statement.setInt(1, partner.getDBId());
            statement.execute();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
    }
}
