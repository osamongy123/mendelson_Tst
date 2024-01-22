//$Header: /mec_as2/de/mendelson/comm/as2/statistic/ServerInteroperabilityContainer.java 2     8.01.19 9:48 Heller $
package de.mendelson.comm.as2.statistic;

import java.io.Serializable;
import java.util.List;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class ServerInteroperabilityContainer implements Serializable{

    public static final long serialVersionUID = 1L;

    public ServerInteroperabilityContainer(String serverId) {
    }

    public List<ServerInteroperabilityEntry> getEntries() {
        return (null);
    }

    public void addEntry(ServerInteroperabilityEntry entry) {
    }
}
