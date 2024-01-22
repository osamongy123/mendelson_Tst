//$Header: /as2/de/mendelson/comm/as2/database/DBClientInformation.java 2     24/08/22 12:55 Heller $
package de.mendelson.comm.as2.database;

import java.io.Serializable;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Stores some information of the used jdbc driver - just for information
 * purpose
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class DBClientInformation implements Serializable {
    public static final long serialVersionUID = 1L;
    
    private String productName = "UNKNOWN";
    private String productVersion = "UNKNOWN";

    public DBClientInformation() {
    }

    /**
     * @return the productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @return the productVersion
     */
    public String getProductVersion() {
        return productVersion;
    }

    /**
     * @param productName the productName to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * @param productVersion the productVersion to set
     */
    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }
}
