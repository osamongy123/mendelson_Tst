//$Header: /oftp2/de/mendelson/util/clientserver/connectiontest/OFTP2FieldAN.java 1     4/06/17 2:13p Heller $
package de.mendelson.util.clientserver.connectiontest;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * An alphanumeric field in a command structure
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class OFTP2FieldAN extends OFTP2Field {

    public OFTP2FieldAN(String name, int maxLength, String description) {
        super(name, maxLength, description);
    }

    public OFTP2FieldAN(String name, int maxLength, String description, String defaultValue) {
        super(name, maxLength, description, defaultValue);
    }

    public OFTP2FieldAN(String name, int maxLength, String description, byte[] defaultValue) {
        super(name, maxLength, description, defaultValue);
    }

    @Override
    public int getType() {
        return (OFTP2Field.TYPE_AN);
    }

    /**Checks if the value contains valid chars. These are 0-9, A-Z and the
     * special chars "/ - . & ( ) space" where space may not be embedded
     * @param value
     * @return
     */
    public static final boolean containsValidChars(String value) {
        value = value.trim();
        for (int i = 0; i < value.length(); i++) {
            char testchar = value.charAt(i);
            if (!Character.isUpperCase(testchar)
                    && !Character.isDigit(testchar)
                    && testchar != '/'
                    && testchar != '-'
                    && testchar != '.'
                    && testchar != '&'
                    && testchar != '('
                    && testchar != ')'
                    //embedded blanks are NOT a valid character - but some OFTP products seem
                    //to allow them - added for compatibility reasons
                    && testchar != ' '
                    //the underscore character is NOT a valid character - but its used
                    //in the virtual filenames for the certificate exchange
                    && testchar != '_') {
                return (false);
            }
        }
        return (true);
    }

    /**Replaces all invalid chars by others to become a valie value*/
    public static String replaceToBecomeValid(String value) {
        StringBuilder result = new StringBuilder();
        value = value.trim();
        for (int i = 0; i < value.length(); i++) {
            char testchar = value.charAt(i);
            if (Character.isLetter(testchar) && !Character.isUpperCase(testchar)) {
                result.append(String.valueOf(testchar).toUpperCase());
            } else if( Character.isSpaceChar(testchar)){
                //this is disabled - but embedded spaces are not allowed in the protocol.
                //modify the following line to replace embedded spaces by an other character
                result.append(' ');
            }else {
                boolean isValid = containsValidChars(String.valueOf(testchar));
                if (isValid) {
                    result.append(testchar);
                } else {
                    result.append('-');
                }
            }
        }
        return (result.toString());
    }
}
