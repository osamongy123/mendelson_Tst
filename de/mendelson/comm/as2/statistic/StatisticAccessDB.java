//$Header: /mec_as2/de/mendelson/comm/as2/statistic/StatisticAccessDB.java 2     2/02/22 15:38 Heller $
package de.mendelson.comm.as2.statistic;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Implementation of a server log for the as2 server database
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class StatisticAccessDB {

    public StatisticAccessDB(Object a) {
    }

    public int deleteOlderThanAsTransaction(Object a,
            Object b, long c) {
        return (0);
    }

    public StatisticDetailEntry getDetails(long a, long b, Object c, Object d, int e, int[] f) {
        return (new StatisticDetailEntry());
    }
}
