//$Header: /mec_as2/de/mendelson/comm/as2/statistic/ServerInteroperabilityEntry.java 2     8.01.19 9:48 Heller $
package de.mendelson.comm.as2.statistic;

import java.io.Serializable;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Single entry container for a server identification
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class ServerInteroperabilityEntry implements Serializable {

    public ServerInteroperabilityEntry() {
    }

    public int getCounter() {
        return 0;
    }

    public void setCounter(int counter) {
    }

    public int getDirection() {
        return 0;
    }

    public void setDirection(int direction) {
    }

    public int getEncryption() {
        return 0;
    }

    public void setEncryption(int encryption) {
    }

    public int getSignature() {
        return 0;
    }

    public void setSignature(int signature) {
    }

    public int getCompression() {
        return 0;
    }

    public void setCompression(int compression) {
    }

    public long getLastGood() {
        return 0L;
    }

    public void setLastGood(long lastGood) {
    }
}
