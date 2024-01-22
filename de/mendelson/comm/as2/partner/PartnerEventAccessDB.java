//$Header: /as2/de/mendelson/comm/as2/partner/PartnerEventAccessDB.java 8     29/11/22 10:07 Heller $
package de.mendelson.comm.as2.partner;

import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.security.Base64;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Access the partner events in the database
 *
 * @author S.Heller
 * @version $Revision: 8 $
 */
public class PartnerEventAccessDB {

    /**
     * Logger to log information to
     */
    private Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);

    public PartnerEventAccessDB() {
    }

    /**
     * Populates the passed partner with the stored event data
     */
    public void loadPartnerEvents(Partner partner, Connection configConnection) throws Exception {
        PreparedStatement statement = null;
        ResultSet result = null;
        PartnerEventInformation eventInfo = partner.getPartnerEvents();
        try {
            statement = configConnection.prepareStatement("SELECT * FROM partnerevent WHERE partnerid=?");
            statement.setInt(1, partner.getDBId());
            result = statement.executeQuery();
            while (result.next()) {
                eventInfo.setProcess(PartnerEventInformation.TYPE_ON_RECEIPT, result.getInt("typeonreceipt"));
                eventInfo.setProcess(PartnerEventInformation.TYPE_ON_SENDERROR, result.getInt("typeonsenderror"));
                eventInfo.setProcess(PartnerEventInformation.TYPE_ON_SENDSUCCESS, result.getInt("typeonsendsuccess"));
                eventInfo.setUseOnReceipt(result.getInt("useonreceipt") != 0);
                eventInfo.setUseOnSenderror(result.getInt("useonsenderror") != 0);
                eventInfo.setUseOnSendsuccess(result.getInt("useonsendsuccess") != 0);
                eventInfo.setParameter(PartnerEventInformation.TYPE_ON_RECEIPT, this.deserializeList(result.getString("parameteronreceipt")));
                eventInfo.setParameter(PartnerEventInformation.TYPE_ON_SENDERROR, this.deserializeList(result.getString("parameteronsenderror")));
                eventInfo.setParameter(PartnerEventInformation.TYPE_ON_SENDSUCCESS, this.deserializeList(result.getString("parameteronsendsuccess")));
            }
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
     * Serializes a list to a single string
     *
     * @param list
     * @return
     */
    private String serializeList(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String entry : list) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(Base64.encode(entry.getBytes(StandardCharsets.UTF_8)));
        }
        return (builder.toString());
    }

    /**
     * Serializes a list to a single string
     *
     * @param list
     * @return
     */
    private List<String> deserializeList(String entry) {
        List<String> list = new ArrayList<String>();
        if (entry == null) {
            return (list);
        }
        String[] entryArray = entry.split(" ");
        for (String singleEntry : entryArray) {
            byte[] decodedBytes = Base64.decode(singleEntry);
            //base64 decoding failed...return empty parameters
            if (decodedBytes == null) {
                return (new ArrayList<String>());
            }
            list.add(new String(decodedBytes, StandardCharsets.UTF_8));
        }
        return (list);
    }

    /**
     * Stores the events of a partner
     *
     * @param configConnectionNoAutoCommit passed database connection - required
     * for transactional operation
     *
     */
    public void storePartnerEvents(Partner partner, Connection configConnectionNoAutoCommit) throws Exception {
        this.deletePartnerEvents(partner, configConnectionNoAutoCommit);
        PartnerEventInformation partnerEvents = partner.getPartnerEvents();
        PreparedStatement statement = null;
        try {
            statement = configConnectionNoAutoCommit.prepareStatement(
                    "INSERT INTO partnerevent(partnerid,useonreceipt,useonsenderror,useonsendsuccess,"
                    + "typeonreceipt,typeonsenderror,typeonsendsuccess,"
                    + "parameteronreceipt,parameteronsenderror,parameteronsendsuccess)"
                    + "VALUES(?,?,?,?,?,?,?,?,?,?)");
            statement.setInt(1, partner.getDBId());
            statement.setInt(2, partnerEvents.useOnReceipt() ? 1 : 0);
            statement.setInt(3, partnerEvents.useOnSenderror() ? 1 : 0);
            statement.setInt(4, partnerEvents.useOnSendsuccess() ? 1 : 0);
            statement.setInt(5, partnerEvents.getProcess(PartnerEventInformation.TYPE_ON_RECEIPT));
            statement.setInt(6, partnerEvents.getProcess(PartnerEventInformation.TYPE_ON_SENDERROR));
            statement.setInt(7, partnerEvents.getProcess(PartnerEventInformation.TYPE_ON_SENDSUCCESS));
            if (partnerEvents.hasParameter(PartnerEventInformation.TYPE_ON_RECEIPT)) {
                statement.setString(8, this.serializeList(partnerEvents.getParameter(PartnerEventInformation.TYPE_ON_RECEIPT)));
            } else {
                statement.setNull(8, java.sql.Types.VARCHAR);
            }
            if (partnerEvents.hasParameter(PartnerEventInformation.TYPE_ON_SENDERROR)) {
                statement.setString(9, this.serializeList(partnerEvents.getParameter(PartnerEventInformation.TYPE_ON_SENDERROR)));
            } else {
                statement.setNull(9, java.sql.Types.VARCHAR);
            }
            if (partnerEvents.hasParameter(PartnerEventInformation.TYPE_ON_SENDSUCCESS)) {
                statement.setString(10, this.serializeList(partnerEvents.getParameter(PartnerEventInformation.TYPE_ON_SENDSUCCESS)));
            } else {
                statement.setNull(10, java.sql.Types.VARCHAR);
            }
            statement.executeUpdate();
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

    /**
     * Deletes a single partners event entry in the database
     *
     * @param configConnectionNoAutoCommit passed database connection - required
     * for transactional operation
     */
    public void deletePartnerEvents(Partner partner, Connection configConnectionNoAutoCommit) throws Exception {
        PreparedStatement statement = null;
        try {
            statement = configConnectionNoAutoCommit.prepareStatement("DELETE FROM partnerevent WHERE partnerid=?");
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
