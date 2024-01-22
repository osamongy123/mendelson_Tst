//$Header: /as4/de/mendelson/util/systemevents/search/ServerSideEventFilter.java 2     12.10.18 12:19 Heller $
package de.mendelson.util.systemevents.search;

import java.io.Serializable;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Filter the client could define to perform a server side system event search
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class ServerSideEventFilter implements Serializable {

    public static final long serialVersionUID = 1L;

    private long startDate = System.currentTimeMillis();
    private long endDate = System.currentTimeMillis();
    private boolean acceptSeverityWarning = false;
    private boolean acceptSeverityError = false;
    private boolean acceptSeverityInfo = false;
    private String subjectText = null;
    private String bodyText = null;
    private String eventid = null;
    private int acceptCategory = -1;
    private int acceptType = -1;
    private boolean acceptOriginSystem = false;
    private boolean acceptOriginUser = false;
    private boolean acceptOriginTransaction = false;
    private int maxResults = 100;

    public ServerSideEventFilter() {
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
     * @return the acceptWarning
     */
    public boolean getAcceptSeverityWarning() {
        return acceptSeverityWarning;
    }

    /**
     * @param acceptWarning the acceptWarning to set
     */
    public void setAcceptSeverityWarning(boolean acceptWarning) {
        this.acceptSeverityWarning = acceptWarning;
    }

    /**
     * @return the acceptError
     */
    public boolean getAcceptSeverityError() {
        return acceptSeverityError;
    }

    /**
     * @param acceptError the acceptError to set
     */
    public void setAcceptSeverityError(boolean acceptError) {
        this.acceptSeverityError = acceptError;
    }

    /**
     * @return the acceptInfo
     */
    public boolean getAcceptSeverityInfo() {
        return acceptSeverityInfo;
    }

    /**
     * @param acceptInfo the acceptInfo to set
     */
    public void setAcceptSeverityInfo(boolean acceptInfo) {
        this.acceptSeverityInfo = acceptInfo;
    }

    /**
     * @return the subjectText
     */
    public String getSubjectSearchText() {
        return subjectText;
    }

    /**
     * @param subjectText the subjectText to set
     */
    public void setSubjectSearchText(String subjectText) {
        this.subjectText = subjectText;
    }

    /**
     * @return the bodyText
     */
    public String getBodySearchText() {
        return bodyText;
    }

    /**
     * @param bodyText the bodyText to set
     */
    public void setBodySearchText(String bodyText) {
        this.bodyText = bodyText;
    }

    /**
     * @return the acceptCategory
     */
    public int getAcceptCategory() {
        return acceptCategory;
    }

    /**
     * @param acceptCategory the acceptCategory to set
     */
    public void setAcceptCategory(int acceptCategory) {
        this.acceptCategory = acceptCategory;
    }

    /**
     * @return the acceptType
     */
    public int getAcceptType() {
        return acceptType;
    }

    /**
     * @param acceptType the acceptType to set
     */
    public void setAcceptType(int acceptType) {
        this.acceptType = acceptType;
    }

    /**
     * @return the acceptOriginSystem
     */
    public boolean getAcceptOriginSystem() {
        return acceptOriginSystem;
    }

    /**
     * @param acceptOriginSystem the acceptOriginSystem to set
     */
    public void setAcceptOriginSystem(boolean acceptOriginSystem) {
        this.acceptOriginSystem = acceptOriginSystem;
    }

    /**
     * @return the acceptOriginUser
     */
    public boolean getAcceptOriginUser() {
        return acceptOriginUser;
    }

    /**
     * @param acceptOriginUser the acceptOriginUser to set
     */
    public void setAcceptOriginUser(boolean acceptOriginUser) {
        this.acceptOriginUser = acceptOriginUser;
    }

    /**
     * @return the acceptOriginTransaction
     */
    public boolean getAcceptOriginTransaction() {
        return acceptOriginTransaction;
    }

    /**
     * @param acceptOriginTransaction the acceptOriginTransaction to set
     */
    public void setAcceptOriginTransaction(boolean acceptOriginTransaction) {
        this.acceptOriginTransaction = acceptOriginTransaction;
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

    /**
     * @return the eventid
     */
    public String getSearchEventid() {
        return eventid;
    }

    /**
     * @param eventid the eventid to set
     */
    public void setSearchEventid(String eventid) {
        this.eventid = eventid;
    }

}
