//$Header: /as2/de/mendelson/comm/as2/database/ISQLQueryModifier.java 1     5.10.20 11:12 Heller $
package de.mendelson.comm.as2.database;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Interface for all classes that modify SQL statements
 *
 * @author S.Heller
 * @version $Revision: 1 $
 * @since build 70
 */
public interface ISQLQueryModifier {

    public String modifyQuery(String query);

}
