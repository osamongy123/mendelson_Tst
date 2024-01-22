//$Header: /as2/de/mendelson/comm/as2/partner/PartnerSystemAccessDB.java 27    15/11/22 9:23 Heller $
package de.mendelson.comm.as2.partner;

import de.mendelson.comm.as2.server.AS2Server;
import de.mendelson.util.database.IDBDriverManager;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Database access wrapper for partner system information. This is the
 * information that is collected if the AS2 system connects to an other AS2
 * system, it will be displayed in the partner panel
 *
 * @author S.Heller
 * @version $Revision: 27 $
 */
public class PartnerSystemAccessDB {

    /**
     * Logger to log information to
     */
    private static final Logger logger = Logger.getLogger(AS2Server.SERVER_LOGGER_NAME);
    private final PartnerAccessDB partnerAccess;
    private final IDBDriverManager dbDriverManager;

    /**
     *
     * @param analyzeConnection Any database connection just to analyze metadata
     * of the database
     */
    public PartnerSystemAccessDB(IDBDriverManager dbDriverManager) {
        this.partnerAccess = new PartnerAccessDB(dbDriverManager);
        this.dbDriverManager = dbDriverManager;
    }

    /**
     * Returns a list of all available partner system information
     *
     * @return
     */
    public List<PartnerSystem> getAllPartnerSystems() {
        List<PartnerSystem> list = new ArrayList<PartnerSystem>();
        List<Partner> allPartnerList = this.partnerAccess.getAllPartner(PartnerAccessDB.DATA_COMPLETENESS_FULL);
        Connection configConnectionAutoCommit = null;
        try {
            configConnectionAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            PreparedStatement statement = null;
            ResultSet result = null;
            try {
                statement = configConnectionAutoCommit.prepareStatement("SELECT * FROM partnersystem");
                result = statement.executeQuery();
                while (result.next()) {
                    int partnerId = result.getInt("partnerid");
                    Partner relatedPartner = null;
                    //this is really slow...
                    for (Partner partner : allPartnerList) {
                        if (partner.getDBId() == partnerId) {
                            relatedPartner = partner;
                            break;
                        }
                    }
                    if (relatedPartner != null) {
                        PartnerSystem system = new PartnerSystem();
                        system.setPartner(relatedPartner);
                        system.setAS2Version(result.getString("as2version"));
                        system.setProductName(result.getString("productname"));
                        system.setCEM(result.getInt("cem") == 1);
                        system.setCompression(result.getInt("msgcompression") == 1);
                        system.setMa(result.getInt("ma") == 1);
                        list.add(system);
                    }
                }
                return (list);
            } catch (SQLException e) {
                logger.severe("PartnerSystemAccessDB.getAllPartnerSystems: " + e.getMessage());
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statement);
                return (null);
            } catch (Exception e) {
                logger.severe("PartnerSystemAccessDB.getAllPartnerSystems: " + e.getMessage());
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                return (null);
            } finally {
                if (result != null) {
                    try {
                        result.close();
                    } catch (Exception e) {
                        logger.severe("PartnerSystemAccessDB.getAllPartnerSystems: " + e.getMessage());
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                    }
                }
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception e) {
                        this.logger.severe("PartnerSystemAccessDB.getAllPartnerSystems: " + e.getMessage());
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                    }
                }
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        } finally {
            if (configConnectionAutoCommit != null) {
                try {
                    configConnectionAutoCommit.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
        return (null);
    }

    /**
     * Returns information about the system of a single partner
     */
    public PartnerSystem getPartnerSystem(Partner partner) {
        Connection configConnectionAutoCommit = null;
        try {
            configConnectionAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            PreparedStatement statement = null;
            ResultSet result = null;
            try {
                statement = configConnectionAutoCommit.prepareStatement("SELECT * FROM partnersystem WHERE partnerid=?");
                statement.setInt(1, partner.getDBId());
                result = statement.executeQuery();
                if (result.next()) {
                    PartnerSystem system = new PartnerSystem();
                    system.setPartner(partner);
                    system.setAS2Version(result.getString("as2version"));
                    system.setProductName(result.getString("productname"));
                    system.setCEM(result.getInt("cem") == 1);
                    system.setCompression(result.getInt("msgcompression") == 1);
                    system.setMa(result.getInt("ma") == 1);
                    return (system);
                }
            } catch (SQLException e) {
                logger.severe("PartnerSystemAccessDB.getPartnerSystem: " + e.getMessage());
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY, statement);
                return (null);
            } catch (Exception e) {
                logger.severe("PartnerSystemAccessDB.getPartnerSystem: " + e.getMessage());
                SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                return (null);
            } finally {
                if (result != null) {
                    try {
                        result.close();
                    } catch (Exception e) {
                        logger.severe("PartnerSystemAccessDB.getPartnerSystem: " + e.getMessage());
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                    }
                }
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception e) {
                        logger.severe("PartnerSystemAccessDB.getPartnerSystem: " + e.getMessage());
                        SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                    }
                }
            }
        } catch (Exception e) {
            SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
        } finally {
            if (configConnectionAutoCommit != null) {
                try {
                    configConnectionAutoCommit.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e, SystemEvent.TYPE_DATABASE_ANY);
                }
            }
        }
        return (null);
    }

    /**
     * Updates a single partnersystem in the db and returns the number of
     * updated rows. If the number of updates rows is 0 there should follow an
     * insert
     */
    private int updatePartnerSystem(PartnerSystem system, Connection configConnectionNoAutoCommit) throws Exception {
        PreparedStatement statement = null;
        try {
            statement = configConnectionNoAutoCommit.prepareStatement(
                    "UPDATE partnersystem SET as2version=?,productname=?,msgcompression=?,ma=?,cem=? WHERE partnerid=?");
            statement.setString(1, system.getAS2Version());
            statement.setString(2, system.getProductName());
            statement.setInt(3, system.supportsCompression() ? 1 : 0);
            statement.setInt(4, system.supportsMA() ? 1 : 0);
            statement.setInt(5, system.supportsCEM() ? 1 : 0);
            statement.setInt(6, system.getPartner().getDBId());
            return (statement.executeUpdate());
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    //nop
                }
            }
        }
    }

    /**
     * Deletes a single partner system from the database
     */
    protected void deletePartnerSystem(Partner partner, Connection configConnectionNoAutoCommit) throws Exception {
        PreparedStatement statement = null;
        try {
            statement = configConnectionNoAutoCommit.prepareStatement("DELETE FROM partnersystem WHERE partnerid=?");
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
     * Inserts a new entry into the database or updates an existing one. This
     * has to happen in a transaction as there are two statements to check if an
     * update was successful - if not an insert will happen
     */
    public void insertOrUpdatePartnerSystem(PartnerSystem partnerSystem) {
        //a new connection to the database is required because the partner storage contains several tables and all this has to be transactional
        Connection configConnectionNoAutoCommit = null;
        Statement transactionStatement = null;
        String transactionName = "PartnerSystem_insert_update";
        try {
            configConnectionNoAutoCommit = this.dbDriverManager.getConnectionWithoutErrorHandling(IDBDriverManager.DB_CONFIG);
            configConnectionNoAutoCommit.setAutoCommit(false);
            transactionStatement = configConnectionNoAutoCommit.createStatement();
            this.dbDriverManager.startTransaction(transactionStatement, transactionName);
            //start transaction - these tables have to be locked first to forbit any write operation
            this.dbDriverManager.setTableLockINSERTAndUPDATE(transactionStatement,
                    new String[]{
                        "partnersystem",});
            int updatedRows = this.updatePartnerSystem(partnerSystem, configConnectionNoAutoCommit);
            if (updatedRows == 0) {
                this.insertPartnerSystem(partnerSystem, configConnectionNoAutoCommit);
            }
            //all ok - finish transaction and release all locks
            this.dbDriverManager.commitTransaction(transactionStatement, transactionName);
        } catch (Exception e) {
            try {
                //an error occured - rollback transaction and release all table locks
                this.dbDriverManager.rollbackTransaction(transactionStatement);
            } catch (Exception ex) {
                SystemEventManagerImplAS2.systemFailure(ex, SystemEvent.TYPE_DATABASE_ANY);
            }
            e.printStackTrace();
            this.logger.severe("PartnerSystemAccessDB.insertOrUpdatePartnerSystem: " + e.getMessage());
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
     * Inserts a new partner system into the database
     */
    private void insertPartnerSystem(PartnerSystem partnerSystem, Connection configConnectionNoAutoCommit) throws Exception {
        PreparedStatement statement = null;
        try {
            statement = configConnectionNoAutoCommit.prepareStatement(
                    "INSERT INTO partnersystem(partnerid,as2version,productname,msgcompression,ma,cem)VALUES(?,?,?,?,?,?)");
            statement.setInt(1, partnerSystem.getPartner().getDBId());
            statement.setString(2, partnerSystem.getAS2Version());
            statement.setString(3, partnerSystem.getProductName());
            statement.setInt(4, partnerSystem.supportsCompression() ? 1 : 0);
            statement.setInt(5, partnerSystem.supportsMA() ? 1 : 0);
            statement.setInt(6, partnerSystem.supportsCEM() ? 1 : 0);
            statement.executeUpdate();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    SystemEventManagerImplAS2.systemFailure(e);
                }
            }
        }
    }
}
