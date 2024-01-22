//$Header: /oftp2/de/mendelson/util/clientserver/connectiontest/OFTP2Command.java 1     4/06/17 2:13p Heller $
package de.mendelson.util.clientserver.connectiontest;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Interface all OFTP2 commands have to implement
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public interface OFTP2Command {

    public static final String COMMANDNAME_AUCH = "AUCH";
    public static final String COMMANDNAME_AURP = "AURP";
    public static final String COMMANDNAME_CD = "CD";
    public static final String COMMANDNAME_CDT = "CDT";
    public static final String COMMANDNAME_EERP = "EERP";
    public static final String COMMANDNAME_EFID = "EFID";
    public static final String COMMANDNAME_EFNA = "EFNA";
    public static final String COMMANDNAME_EFPA = "EFPA";
    public static final String COMMANDNAME_ESID = "ESID";
    public static final String COMMANDNAME_NERP = "NERP";
    public static final String COMMANDNAME_RTR = "RTR";
    public static final String COMMANDNAME_SECD = "SECD";
    public static final String COMMANDNAME_SFID = "SFID";
    public static final String COMMANDNAME_SFPA = "SFPA";
    public static final String COMMANDNAME_SFNA = "SFNA";
    public static final String COMMANDNAME_SSID = "SSID";
    public static final String COMMANDNAME_SSRM = "SSRM";

    /**
     * Returns the short name of the command, e.g. "SSID"
     */
    public String getName();

    /**
     * Returns the max structure length in bytes
     */
    public int getMaxLength();

    /**
     * Returns the command description
     */
    public String getDescription();

    /**
     * Returns the name of the command, this is the first field
     */
    public String getIndicator();

    /**
     * Returns all fields of the command
     */
    public OFTP2Field[] getFields();

    /**
     * Returns a field if the name of the field is passed
     */
    public OFTP2Field getField(final String FIELD_NAME);
}
