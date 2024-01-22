//$Header: /as2/de/mendelson/comm/as2/partner/PartnerAccessDB.java 94    29/11/22 16:03 Heller $
package de.mendelson.comm.as2.partner;

import de.mendelson.comm.as2.cert.CertificateAccessDB;
import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.oauth2.OAuth2AccessDB;
import de.mendelson.util.oauth2.OAuth2Config;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManager;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.sql.Types;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Implementation of a server log for the mendelson as2 server database
 *
 * @author S.Heller
 * @version $Revision: 94 $
 */
public class PartnerAccessDB {

    /**
     * Logger to log information to
     */
    private Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    /**
     * Access the certificates
     */
    private final CertificateAccessDB certificateAccess;
    private final PartnerEventAccessDB eventAccess;
    private final OAuth2AccessDB oAuth2Access;
    private final static SystemEventManager systemEventManager = new SystemEventManagerImplAS2();

    /**
     * Returns the full partner data
     */
    public static final int DATA_COMPLETENESS_FULL = 100;
    /**
     * Return incomplete partner requests - for faster UI client-server requests
     */
    public static final int DATA_COMPLETENESS_NAMES_AS2ID_TYPE = 101;
    private final IDBDriverManager dbDriverManager;

    /**
     *
     * @param analyzeConnection Any database connection required to check the
     * metadata for the DB data types
     */
    public PartnerAccessDB(IDBDriverManager dbDriverManager) {
        this.dbDriverManager = dbDriverManager;
        this.certificateAccess = new CertificateAccessDB();
        this.eventAccess = new PartnerEventAccessDB();
        this.oAuth2Access = new OAuth2AccessDB(dbDriverManager, systemEventManager);
    }

    /**
     * Requires a query to select partners from the DB. Works in a transaction
     * context on the passed database connection
     *
     * @param dataCompleteness Allows to get partner object with lesser
     * information
     */
    private List<Partner> getPartnerByQuery(String query, String parameter, int dataCompleteness,
            Connection configConnectionNoAutoCommit) throws Exception {
        List<Partner> partnerList = new ArrayList<Partner>();
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        try {
            preparedStatement = configConnectionNoAutoCommit.prepareStatement(query);
            if (parameter != null) {
                preparedStatement.setString(1, parameter);
            }
            result = preparedStatement.executeQuery();
            while (result.next()) {
                Partner partner = new Partner();
                partner.setAS2Identification(result.getString("as2ident"));
                partner.setName(result.getString("partnername"));
                partner.setDBId(result.getInt("id"));
                partner.setLocalStation(result.getInt("islocal") == 1);
                //All partner data is requested - deliver it
                if (dataCompleteness == DATA_COMPLETENESS_FULL) {
                    partner.setSignType(result.getInt("sign"));
                    partner.setEncryptionType(result.getInt("encrypt"));
                    partner.setEmail(result.getString("email"));
                    partner.setURL(result.getString("url"));
                    partner.setMdnURL(result.getString("mdnurl"));
                    partner.setSubject(result.getString("msgsubject"));
                    partner.setContentType(result.getString("contenttype"));
                    partner.setSyncMDN(result.getInt("syncmdn") == 1);
                    partner.setPollIgnoreListString(result.getString("pollignorelist"));
                    partner.setPollInterval(result.getInt("pollinterval"));
                    partner.setCompressionType(result.getInt("msgcompression"));
                    partner.setSignedMDN(result.getInt("signedmdn") == 1);
                    partner.setKeepOriginalFilenameOnReceipt(result.getInt("keeporiginalfilenameonreceipt") == 1);
                    HTTPAuthentication authentication = partner.getAuthenticationCredentialsMessage();
                    authentication.setUser(result.getString("httpauthuser"));
                    authentication.setPassword(result.getString("httpauthpass"));
                    authentication.setEnabled(result.getInt("usehttpauth") == 1);
                    HTTPAuthentication asyncAuthentication = partner.getAuthenticationCredentialsAsyncMDN();
                    asyncAuthentication.setUser(result.getString("httpauthuserasnymdn"));
                    asyncAuthentication.setPassword(result.getString("httpauthpassasnymdn"));
                    asyncAuthentication.setEnabled(result.getInt("usehttpauthasyncmdn") == 1);
                    partner.setComment(this.dbDriverManager.readTextStoredAsJavaObject(result, "partnercomment"));
                    partner.setContactAS2(this.dbDriverManager.readTextStoredAsJavaObject(result, "partnercontact"));
                    partner.setContactCompany(this.dbDriverManager.readTextStoredAsJavaObject(result, "partneraddress"));
                    partner.setNotifyReceive(result.getInt("notifyreceive"));
                    partner.setNotifySend(result.getInt("notifysend"));
                    partner.setNotifySendReceive(result.getInt("notifysendreceive"));
                    partner.setNotifyReceiveEnabled(result.getInt("notifyreceiveenabled") == 1);
                    partner.setNotifySendEnabled(result.getInt("notifysendenabled") == 1);
                    partner.setNotifySendReceiveEnabled(result.getInt("notifysendreceiveenabled") == 1);
                    partner.setContentTransferEncoding(result.getInt("contenttransferencoding"));
                    partner.setHttpProtocolVersion(result.getString("httpversion"));
                    partner.setMaxPollFiles(result.getInt("maxpollfiles"));
                    partner.setUseAlgorithmIdentifierProtectionAttribute(result.getInt("algidentprotatt") == 1);
                    partner.setEnableDirPoll(result.getInt("enabledirpoll") == 1);
                    //ensure to have a valid partner DB id before loading the releated data
                    this.certificateAccess.loadPartnerCertificateInformation(partner, configConnectionNoAutoCommit);
                    this.loadHTTPHeaderIntoPartner(partner, configConnectionNoAutoCommit);
                    this.eventAccess.loadPartnerEvents(partner, configConnectionNoAutoCommit);
                    partner.setUseOAuth2Message(result.getInt("useoauth2message") == 1);
                    int oAuth2ReferenceMessage = result.getInt("oauth2idmessage");
                    if (!result.wasNull()) {
                        OAuth2Config oAuth2ConfigMessage = this.oAuth2Access.getOAuth2Config(oAuth2ReferenceMessage, configConnectionNoAutoCommit);
                        partner.setOAuth2Message(oAuth2ConfigMessage);
                    }
                    partner.setUseOAuth2MDN(result.getInt("useoauth2mdn") == 1);
                    int oAuth2ReferenceMDN = result.getInt("oauth2idmdn");
                    if (!result.wasNull()) {
                        OAuth2Config oAuth2ConfigMDN = this.oAuth2Access.getOAuth2Config(oAuth2ReferenceMDN, configConnectionNoAutoCommit);
                        partner.setOAuth2MDN(oAuth2ConfigMDN);
                    }
                }
                partnerList.add(partner);
            }
            Collections.sort(partnerList);
            return (partnerList);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
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
     * Requires a query to select partners from the DB. Establishes a new
     * connection to the database and gets the data transactional
     *
     * @param dataCompleteness Allows to get partner object with lesser
     * information
     */
    private List<Partner> getPartnerByQuery(String query, String parameter, int dataCompleteness) {
        List<Partner> partnerList = new ArrayList<Partner>();
        Statement statement = null;
        //a new connection to the database is required because the partner storage contains several tables and all this has to be transactional
        Connection configConnectionNoAutoCommit = null;
        String transactionName = "Partner_read";
        try {
            configConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            configConnectionNoAutoCommit.setAutoCommit(false);
            configConnectionNoAutoCommit.setReadOnly(true);
            statement = configConnectionNoAutoCommit.createStatement();
            //start transaction
            this.dbDriverManager.startTransaction(statement, transactionName);
            partnerList.addAll(this.getPartnerByQuery(query, parameter, dataCompleteness, configConnectionNoAutoCommit));
            //all ok - finish transaction and release all locks
            this.dbDriverManager.commitTransaction(statement, transactionName);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                //an error occured - rollback transaction and release all table locks
                this.dbDriverManager.rollbackTransaction(statement);
            } catch (Exception ex) {
                SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
            }
            this.logger.severe("PartnerAccessDB.getPartnerByQuery: " + e.getMessage());
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (configConnectionNoAutoCommit != null) {
                try {
                    configConnectionNoAutoCommit.close();
                } catch (Exception e) {
                    //nop
                }
            }
        }
        return (partnerList);
    }

    /**
     * Returns all partner stored in the DB, even the local station
     */
    public List<Partner> getAllPartner(int dataCompleteness) {
        return (this.getPartnerByQuery("SELECT * FROM partner", null, dataCompleteness));
    }

    /**
     * Returns all partner stored in the DB with all information, even the local
     * station
     */
    public List<Partner> getAllPartner() {
        return (this.getAllPartner(DATA_COMPLETENESS_FULL));
    }

    /**
     * Returns all partner stored in the DB with all information, even the local
     * station. The transactional context of the passed connection has to be
     * handled outside
     */
    public List<Partner> getAllPartner(int dataCompleteness, Connection configConnectionNoAutoCommit) throws Exception {
        return (this.getPartnerByQuery("SELECT * FROM partner", null, dataCompleteness, configConnectionNoAutoCommit));
    }

    /**
     * Returns all local stations stored in the DB
     */
    public List<Partner> getLocalStations(int dataCompleteness) {
        return (this.getPartnerByQuery("SELECT * FROM partner WHERE islocal=1", null, dataCompleteness));
    }

    /**
     * Returns all local stations stored in the DB
     */
    public List<Partner> getLocalStations() {
        return (this.getLocalStations(DATA_COMPLETENESS_FULL));
    }

    /**
     * Returns all partner stored in the DB, even the local station
     */
    public List<Partner> getNonLocalStations(int dataCompleteness) {
        return (this.getPartnerByQuery("SELECT * FROM partner WHERE islocal<>1", null, dataCompleteness));
    }

    /**
     * Returns all partner stored in the DB, even the local station
     */
    public List<Partner> getNonLocalStations() {
        return (this.getNonLocalStations(DATA_COMPLETENESS_FULL));
    }

    /**
     * Updates a single partner to the database by creating a new DB connection
     */
    public void updatePartner(Partner partner) {
        Connection configConnectionNoAutoCommit = null;
        Statement transactionStatement = null;
        try {
            configConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            configConnectionNoAutoCommit.setAutoCommit(false);

            String transactionName = "PartnerAccessDB_updatePartner";
            try {
                transactionStatement = configConnectionNoAutoCommit.createStatement();
                this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                this.dbDriverManager.setTableLockDELETE(
                        transactionStatement,
                        new String[]{
                            "partner",
                            "certificates",
                            "partnerevent",
                            "httpheader",
                            "partnersystem",
                            "oauth2"
                        });
                this.updatePartner(partner, configConnectionNoAutoCommit);
                this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
            } catch (Exception e) {
                try {
                    //an error occured - rollback transaction and release all locks
                    this.dbDriverManager.rollbackTransaction(transactionStatement);
                } catch (Exception ex) {
                    SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
                }
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        } finally {
            if (transactionStatement != null) {
                try {
                    transactionStatement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (configConnectionNoAutoCommit != null) {
                try {
                    configConnectionNoAutoCommit.close();
                } catch (Exception e) {
                    //nop
                }
            }
        }
    }

    /**
     * Updates a single partner in the db
     */
    /**
     * Inserts a new partner into the database
     */
    public void updatePartner(Partner partner, Connection configConnectionNoAutoCommit) throws Exception {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = configConnectionNoAutoCommit.prepareStatement(
                    "UPDATE partner SET "
                    + "as2ident=?,partnername=?,islocal=?,sign=?,encrypt=?,email=?,url=?,"
                    + "mdnurl=?,msgsubject=?,contenttype=?,syncmdn=?,pollignorelist=?,"
                    + "pollinterval=?,msgcompression=?,signedmdn=?,"
                    + "usehttpauth=?,httpauthuser=?,httpauthpass=?,"
                    + "usehttpauthasyncmdn=?,httpauthuserasnymdn=?,httpauthpassasnymdn=?,"
                    + "keeporiginalfilenameonreceipt=?,partnercomment=?,notifysend=?,"
                    + "notifyreceive=?,notifysendreceive=?,notifysendenabled=?,"
                    + "notifyreceiveenabled=?,notifysendreceiveenabled=?,"
                    + "contenttransferencoding=?,httpversion=?,"
                    + "maxpollfiles=?,partnercontact=?,partneraddress=?,algidentprotatt=?,"
                    + "enabledirpoll=?,useoauth2message=?,useoauth2mdn=?,"
                    + "oauth2idmessage=?,oauth2idmdn=? "
                    + "WHERE id=?");
            preparedStatement.setString(1, partner.getAS2Identification());
            preparedStatement.setString(2, partner.getName());
            preparedStatement.setInt(3, partner.isLocalStation() ? 1 : 0);
            preparedStatement.setInt(4, partner.getSignType());
            preparedStatement.setInt(5, partner.getEncryptionType());
            preparedStatement.setString(6, partner.getEmail());
            preparedStatement.setString(7, partner.getURL());
            preparedStatement.setString(8, partner.getMdnURL());
            preparedStatement.setString(9, partner.getSubject());
            preparedStatement.setString(10, partner.getContentType());
            preparedStatement.setInt(11, partner.isSyncMDN() ? 1 : 0);
            preparedStatement.setString(12, partner.getPollIgnoreListAsString());
            preparedStatement.setInt(13, partner.getPollInterval());
            preparedStatement.setInt(14, partner.getCompressionType());
            preparedStatement.setInt(15, partner.isSignedMDN() ? 1 : 0);
            preparedStatement.setInt(16, partner.getAuthenticationCredentialsMessage().isEnabled() ? 1 : 0);
            preparedStatement.setString(17, partner.getAuthenticationCredentialsMessage().getUser());
            preparedStatement.setString(18, partner.getAuthenticationCredentialsMessage().getPassword());
            preparedStatement.setInt(19, partner.getAuthenticationCredentialsAsyncMDN().isEnabled() ? 1 : 0);
            preparedStatement.setString(20, partner.getAuthenticationCredentialsAsyncMDN().getUser());
            preparedStatement.setString(21, partner.getAuthenticationCredentialsAsyncMDN().getPassword());
            preparedStatement.setInt(22, partner.getKeepOriginalFilenameOnReceipt() ? 1 : 0);
            this.dbDriverManager.setTextParameterAsJavaObject(preparedStatement, 23, partner.getComment());
            preparedStatement.setInt(24, partner.getNotifySend());
            preparedStatement.setInt(25, partner.getNotifyReceive());
            preparedStatement.setInt(26, partner.getNotifySendReceive());
            preparedStatement.setInt(27, partner.isNotifySendEnabled() ? 1 : 0);
            preparedStatement.setInt(28, partner.isNotifyReceiveEnabled() ? 1 : 0);
            preparedStatement.setInt(29, partner.isNotifySendReceiveEnabled() ? 1 : 0);
            preparedStatement.setInt(30, partner.getContentTransferEncoding());
            preparedStatement.setString(31, partner.getHttpProtocolVersion());
            preparedStatement.setInt(32, partner.getMaxPollFiles());
            this.dbDriverManager.setTextParameterAsJavaObject(preparedStatement, 33, partner.getContactAS2());
            this.dbDriverManager.setTextParameterAsJavaObject(preparedStatement, 34, partner.getContactCompany());
            preparedStatement.setInt(35, partner.getUseAlgorithmIdentifierProtectionAttribute() ? 1 : 0);
            preparedStatement.setInt(36, partner.isEnableDirPoll() ? 1 : 0);
            preparedStatement.setInt(37, partner.usesOAuth2Message() ? 1 : 0);
            preparedStatement.setInt(38, partner.usesOAuth2MDN() ? 1 : 0);
            if (partner.getOAuth2Message() != null) {
                this.oAuth2Access.insertOrUpdateOAuth2(partner.getOAuth2Message(), configConnectionNoAutoCommit);
                preparedStatement.setInt(39, partner.getOAuth2Message().getDBId());
            } else {
                preparedStatement.setNull(39, Types.INTEGER);
            }
            if (partner.getOAuth2MDN() != null) {
                this.oAuth2Access.insertOrUpdateOAuth2(partner.getOAuth2MDN(), configConnectionNoAutoCommit);
                preparedStatement.setInt(40, partner.getOAuth2MDN().getDBId());
            } else {
                preparedStatement.setNull(40, Types.INTEGER);
            }
            //where statement
            preparedStatement.setInt(41, partner.getDBId());
            preparedStatement.executeUpdate();
            this.storeHTTPHeader(partner, configConnectionNoAutoCommit);
            this.certificateAccess.storePartnerCertificateInformationList(partner, configConnectionNoAutoCommit);
            this.eventAccess.storePartnerEvents(partner, configConnectionNoAutoCommit);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }

    }

    /**
     * Deletes a single partner from the database by creating a new connection
     */
    public void deletePartner(Partner partner) {
        Connection configConnectionNoAutoCommit = null;
        Statement transactionStatement = null;
        String transactionName = "PartnerAccess_delete";
        try {
            configConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            configConnectionNoAutoCommit.setAutoCommit(false);
            transactionStatement = configConnectionNoAutoCommit.createStatement();
            this.dbDriverManager.startTransaction(transactionStatement, transactionName);
            this.dbDriverManager.setTableLockDELETE(
                    transactionStatement,
                    new String[]{
                        "partner",
                        "certificates",
                        "partnerevent",
                        "httpheader",
                        "partnersystem"
                    });
            try {
                this.deletePartner(partner, configConnectionNoAutoCommit);
                this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
            } catch (Exception e) {
                try {
                    //an error occured - rollback transaction and release all locks
                    this.dbDriverManager.rollbackTransaction(transactionStatement);
                } catch (Exception ex) {
                    SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
                }
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        } finally {
            if (transactionStatement != null) {
                try {
                    transactionStatement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (configConnectionNoAutoCommit != null) {
                try {
                    configConnectionNoAutoCommit.close();
                } catch (Exception e) {
                    //nop
                }
            }
        }
    }

    /**
     * Deletes a single partner from the database
     */
    public void deletePartner(Partner partner, Connection configConnectionNoAutoCommit) throws Exception {
        PreparedStatement preparedStatement = null;
        try {
            PartnerSystemAccessDB partnerSystemAccess = new PartnerSystemAccessDB(this.dbDriverManager);
            this.deleteHTTPHeader(partner, configConnectionNoAutoCommit);
            this.certificateAccess.deletePartnerCertificateInformationList(partner, configConnectionNoAutoCommit);
            this.eventAccess.deletePartnerEvents(partner, configConnectionNoAutoCommit);
            partnerSystemAccess.deletePartnerSystem(partner, configConnectionNoAutoCommit);
            preparedStatement = configConnectionNoAutoCommit.prepareStatement(
                    "DELETE FROM partner WHERE id=?");
            preparedStatement.setInt(1, partner.getDBId());
            preparedStatement.executeUpdate();
            //this worked fine - try to delete oauth2 references which might fail because they might be used somewhere else
            OAuth2Config oauth2Message = partner.getOAuth2Message();
            if (oauth2Message != null) {
                this.oAuth2Access.deleteOAuth2(oauth2Message.getDBId());
            }
            OAuth2Config oauth2MDN = partner.getOAuth2MDN();
            if (oauth2MDN != null) {
                this.oAuth2Access.deleteOAuth2(oauth2MDN.getDBId());
            }
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
    }

    /**
     * Inserts a single partner to the database by creating a new DB connection
     */
    public void insertPartner(Partner partner) {
        Connection configConnectionNoAutoCommit = null;
        Statement transactionStatement = null;
        try {
            configConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            configConnectionNoAutoCommit.setAutoCommit(false);
            String transactionName = "PartnerAccessDB_insertPartner";
            try {
                transactionStatement = configConnectionNoAutoCommit.createStatement();
                this.dbDriverManager.startTransaction(transactionStatement, transactionName);
                //start transaction - these tables have to be locked first. Delete is required because
                //the HTTP header is deleted
                this.dbDriverManager.setTableLockDELETE(transactionStatement,
                        new String[]{
                            "partner",
                            "certificates",
                            "partnerevent",
                            "httpheader",
                            "oauth2"
                        });
                this.insertPartner(partner, configConnectionNoAutoCommit);
                this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
            } catch (Exception e) {
                try {
                    //an error occured - rollback transaction and release all locks
                    this.dbDriverManager.rollbackTransaction(transactionStatement);
                } catch (Exception ex) {
                    SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
                }
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        } finally {
            if (transactionStatement != null) {
                try {
                    transactionStatement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
            if (configConnectionNoAutoCommit != null) {
                try {
                    configConnectionNoAutoCommit.close();
                } catch (Exception e) {
                    //nop
                }
            }
        }
    }

    /**
     * Inserts a new partner into the database. This has to happen transactional
     * as data is stored in multiple tables and other processes may read
     * incomplete data else
     */
    public void insertPartner(Partner partner, Connection configConnectionNoAutoCommit) throws Exception {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = configConnectionNoAutoCommit.prepareStatement(
                    "INSERT INTO partner("
                    + "as2ident,partnername,islocal,sign,encrypt,email,url,mdnurl,"
                    + "msgsubject,contenttype,syncmdn,pollignorelist,pollinterval,"
                    + "msgcompression,signedmdn,"
                    + "usehttpauth,httpauthuser,httpauthpass,usehttpauthasyncmdn,"
                    + "httpauthuserasnymdn,httpauthpassasnymdn,keeporiginalfilenameonreceipt,"
                    + "partnercomment,notifysend,notifyreceive,notifysendreceive,"
                    + "notifysendenabled,notifyreceiveenabled,notifysendreceiveenabled,"
                    + "contenttransferencoding,httpversion,"
                    + "maxpollfiles,partnercontact,partneraddress,algidentprotatt,enabledirpoll,"
                    + "useoauth2message,useoauth2mdn,oauth2idmessage,oauth2idmdn"
                    + ")VALUES("
                    + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            preparedStatement.setString(1, partner.getAS2Identification());
            preparedStatement.setString(2, partner.getName());
            preparedStatement.setInt(3, partner.isLocalStation() ? 1 : 0);
            preparedStatement.setInt(4, partner.getSignType());
            preparedStatement.setInt(5, partner.getEncryptionType());
            preparedStatement.setString(6, partner.getEmail());
            preparedStatement.setString(7, partner.getURL());
            preparedStatement.setString(8, partner.getMdnURL());
            preparedStatement.setString(9, partner.getSubject());
            preparedStatement.setString(10, partner.getContentType());
            preparedStatement.setInt(11, partner.isSyncMDN() ? 1 : 0);
            preparedStatement.setString(12, partner.getPollIgnoreListAsString());
            preparedStatement.setInt(13, partner.getPollInterval());
            preparedStatement.setInt(14, partner.getCompressionType());
            preparedStatement.setInt(15, partner.isSignedMDN() ? 1 : 0);
            preparedStatement.setInt(16, partner.getAuthenticationCredentialsMessage().isEnabled() ? 1 : 0);
            preparedStatement.setString(17, partner.getAuthenticationCredentialsMessage().getUser());
            preparedStatement.setString(18, partner.getAuthenticationCredentialsMessage().getPassword());
            preparedStatement.setInt(19, partner.getAuthenticationCredentialsAsyncMDN().isEnabled() ? 1 : 0);
            preparedStatement.setString(20, partner.getAuthenticationCredentialsAsyncMDN().getUser());
            preparedStatement.setString(21, partner.getAuthenticationCredentialsAsyncMDN().getPassword());
            preparedStatement.setInt(22, partner.getKeepOriginalFilenameOnReceipt() ? 1 : 0);
            this.dbDriverManager.setTextParameterAsJavaObject(preparedStatement, 23, partner.getComment());
            preparedStatement.setInt(24, partner.getNotifySend());
            preparedStatement.setInt(25, partner.getNotifyReceive());
            preparedStatement.setInt(26, partner.getNotifySendReceive());
            preparedStatement.setInt(27, partner.isNotifySendEnabled() ? 1 : 0);
            preparedStatement.setInt(28, partner.isNotifyReceiveEnabled() ? 1 : 0);
            preparedStatement.setInt(29, partner.isNotifySendReceiveEnabled() ? 1 : 0);
            preparedStatement.setInt(30, partner.getContentTransferEncoding());
            preparedStatement.setString(31, partner.getHttpProtocolVersion());
            preparedStatement.setInt(32, partner.getMaxPollFiles());
            this.dbDriverManager.setTextParameterAsJavaObject(preparedStatement, 33, partner.getContactAS2());
            this.dbDriverManager.setTextParameterAsJavaObject(preparedStatement, 34, partner.getContactCompany());
            preparedStatement.setInt(35, partner.getUseAlgorithmIdentifierProtectionAttribute() ? 1 : 0);
            preparedStatement.setInt(36, partner.isEnableDirPoll() ? 1 : 0);
            preparedStatement.setInt(37, partner.usesOAuth2Message() ? 1 : 0);
            preparedStatement.setInt(38, partner.usesOAuth2MDN() ? 1 : 0);
            if (partner.getOAuth2Message() != null) {
                this.oAuth2Access.insertOrUpdateOAuth2(partner.getOAuth2Message(), configConnectionNoAutoCommit);
                preparedStatement.setInt(39, partner.getOAuth2Message().getDBId());
            } else {
                preparedStatement.setNull(39, Types.INTEGER);
            }
            if (partner.getOAuth2MDN() != null) {
                this.oAuth2Access.insertOrUpdateOAuth2(partner.getOAuth2MDN(), configConnectionNoAutoCommit);
                preparedStatement.setInt(40, partner.getOAuth2MDN().getDBId());
            } else {
                preparedStatement.setNull(40, Types.INTEGER);
            }
            preparedStatement.executeUpdate();
            partner.setDBId(this.getDBIdForPartner(partner.getAS2Identification(), configConnectionNoAutoCommit));
            this.storeHTTPHeader(partner, configConnectionNoAutoCommit);
            this.certificateAccess.storePartnerCertificateInformationList(partner, configConnectionNoAutoCommit);
            this.eventAccess.storePartnerEvents(partner, configConnectionNoAutoCommit);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Exception e) {
                    this.logger.severe("PartnerAccessDB.insertPartner: " + e.getMessage());
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }

        }
    }

    /**
     * returns the internal database id for the passed partner as2
     * identification
     *
     * @param as2ident
     * @param configConnection
     * @return
     */
    private int getDBIdForPartner(String as2ident, Connection configConnection) throws Exception {
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = configConnection.prepareStatement("SELECT id FROM partner WHERE as2ident=?");
            statement.setString(1, as2ident);
            result = statement.executeQuery();
            if (result.next()) {
                return (result.getInt("id"));
            } else {
                return (-1);
            }
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (Exception e) {
                    this.logger.severe("PartnerAccessDB.getDBIdForPartner: " + e.getMessage());
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
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
     * Loads a specified partner from the DB
     *
     * @return null if the partner does not exist
     */
    public Partner getPartner(String as2ident) {
        return (this.getPartnerByAS2Id(as2ident, DATA_COMPLETENESS_FULL));
    }

    /**
     * Loads a specified partner from the DB
     *
     * @return null if the partner does not exist
     */
    public Partner getPartnerByAS2Id(String as2ident, int dataCompleteness) {
        String query = "SELECT * FROM partner WHERE as2ident=?";
        List<Partner> partner = this.getPartnerByQuery(query, as2ident, dataCompleteness);
        if (partner == null || partner.isEmpty()) {
            return (null);
        }
        return (partner.get(0));
    }

    /**
     * Loads a specified partner from the DB
     *
     * @return null if the partner does not exist
     */
    public Partner getPartnerByName(String partnerName, int dataCompleteness) {
        String query = "SELECT * FROM partner WHERE upper(partnername)=?";
        List<Partner> partner = this.getPartnerByQuery(query, partnerName.toUpperCase(), dataCompleteness);
        if (partner == null || partner.isEmpty()) {
            return (null);
        }
        return (partner.get(0));
    }

    /**
     * Loads a specified partner from the DB
     *
     * @return null if the partner does not exist
     */
    public Partner getPartner(int dbId) {
        String query = "SELECT * FROM partner WHERE id=" + dbId;
        List<Partner> partner = this.getPartnerByQuery(query, null, DATA_COMPLETENESS_FULL);
        if (partner == null || partner.isEmpty()) {
            return (null);
        }
        return (partner.get(0));
    }

    /*
     * loads the partner specific http headers from the db and assigns it to the
     * passed partner
     */
    private void loadHTTPHeaderIntoPartner(Partner partner, Connection configConnection) throws Exception {
        int partnerId = partner.getDBId();
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = configConnection.prepareStatement("SELECT * FROM httpheader WHERE partnerid=?");
            statement.setInt(1, partnerId);
            result = statement.executeQuery();
            while (result.next()) {
                PartnerHttpHeader header = new PartnerHttpHeader();
                header.setKey(result.getString("headerkey"));
                header.setValue(result.getString("headervalue"));
                partner.addHttpHeader(header);
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
     * Deletes a single partners http header from the database
     */
    private void deleteHTTPHeader(Partner partner, Connection configConnection) throws Exception {
        PreparedStatement statement = null;
        try {
            statement = configConnection.prepareStatement("DELETE FROM httpheader WHERE partnerid=?");
            statement.setInt(1, partner.getDBId());
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
     * Updates a single partners http header in the db
     */
    private void storeHTTPHeader(Partner partner, Connection configConnectionNoAutoCommit) throws Exception {
        this.deleteHTTPHeader(partner, configConnectionNoAutoCommit);
        //clear unused headers in the partner object
        partner.deleteEmptyHttpHeader();
        List<PartnerHttpHeader> headerList = partner.getHttpHeader();
        for (PartnerHttpHeader header : headerList) {
            PreparedStatement statement = null;
            try {
                statement = configConnectionNoAutoCommit.prepareStatement(
                        "INSERT INTO httpheader(partnerid,headerkey,headervalue)VALUES(?,?,?)");
                statement.setInt(1, partner.getDBId());
                statement.setString(2, header.getKey());
                statement.setString(3, header.getValue());
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
    }
    
}
