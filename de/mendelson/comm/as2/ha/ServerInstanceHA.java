//$Header: /mec_as2/de/mendelson/comm/as2/ha/ServerInstanceHA.java 1     2/02/22 15:13 Heller $
package de.mendelson.comm.as2.ha;

import java.io.Serializable;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Stores information about this any server instance found in the HA
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class ServerInstanceHA implements Serializable {

    public static final long serialVersionUID = 1L;

    private String uniqueId = "";
    private String localIP = "";
    private String publicIP = null;
    private String cloudInstanceId = null;
    private String host = "";
    private int numberOfClients = 0;
    private String os = "";
    private String productVersion = "";
    private long startTime = 0;
    private long lastSeenTime = 0;

    public ServerInstanceHA() {
    }

    /**
     * @return the uniqueDd
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * @param uniqueDd the uniqueDd to set
     */
    public void setUniqueId(String uniqueDd) {
        this.uniqueId = uniqueDd;
    }

    /**
     * @return the ip
     */
    public String getLocalIP() {
        return localIP;
    }

    /**
     * @param ip the ip to set
     */
    public void setLocalIP(String ip) {
        this.localIP = ip;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the numberOfClients
     */
    public int getNumberOfClients() {
        return numberOfClients;
    }

    /**
     * @param numberOfClients the numberOfClients to set
     */
    public void setNumberOfClients(int numberOfClients) {
        this.numberOfClients = numberOfClients;
    }

    /**
     * @return the os
     */
    public String getOS() {
        return os;
    }

    /**
     * @param os the os to set
     */
    public void setOS(String os) {
        this.os = os;
    }

    /**
     * @return the productVersion
     */
    public String getProductVersion() {
        return productVersion;
    }

    /**
     * @param productVersion the productVersion to set
     */
    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    /**
     * @return the startTime
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the lastSeenTime
     */
    public long getLastSeenTime() {
        return lastSeenTime;
    }

    /**
     * @param lastSeenTime the lastSeenTime to set
     */
    public void setLastSeenTime(long lastSeenTime) {
        this.lastSeenTime = lastSeenTime;
    }

    /**
     * @return the publicIP
     */
    public String getPublicIP() {
        return publicIP;
    }

    /**
     * @param publicIP the publicIP to set
     */
    public void setPublicIP(String publicIP) {
        this.publicIP = publicIP;
    }

    /**
     * @return the cloudInstanceId
     */
    public String getCloudInstanceId() {
        return cloudInstanceId;
    }

    /**
     * @param cloudInstanceId the cloudInstanceId to set
     */
    public void setCloudInstanceId(String cloudInstanceId) {
        this.cloudInstanceId = cloudInstanceId;
    }

}
