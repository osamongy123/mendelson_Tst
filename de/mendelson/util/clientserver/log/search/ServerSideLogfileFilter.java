//$Header: /oftp2/de/mendelson/util/clientserver/log/search/ServerSideLogfileFilter.java 3     5.12.18 16:16 Heller $
package de.mendelson.util.clientserver.log.search;

import java.io.Serializable;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Filter the client could define to perform a server side system log file search
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class ServerSideLogfileFilter implements Serializable {

    public static final long serialVersionUID = 1L;

    private long startDate = System.currentTimeMillis();
    private long endDate = System.currentTimeMillis();
    /**This will return the last 10k lines of the full result*/
    private int maxResults = 10000;

    public ServerSideLogfileFilter() {
    }

    /**
     * @return the startDate
     */
    public long getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public long getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }


    /**
     * @return the maxResults
     */
    public int getMaxResults() {
        return maxResults;
    }

    /**
     * @param maxResults the maxResults to set
     */
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

}
