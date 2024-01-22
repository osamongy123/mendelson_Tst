//$Header: /oftp2/de/mendelson/util/clientserver/connectiontest/OFTP2Field.java 1     4/06/17 2:13p Heller $
package de.mendelson.util.clientserver.connectiontest;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * A field in a command structure
 * @author S.Heller
 * @version $Revision: 1 $
 */
public abstract class OFTP2Field {

    public static int TYPE_AN = 1;
    public static int TYPE_N = 2;
    public static int TYPE_TXT = 3;
    public static int TYPE_BIN = 4;
    private String name;
    private int maxLength = 0;
    private String description = "";
    /**Initializesd to blanks if no default is given*/
    private byte[] defaultValue = null;
    /**If the length of this field is given by another fields content, this is stored here*/
    private OFTP2Field lengthGivenByField = null;

    public OFTP2Field(String name, int maxLength, String description) {
        this.initialize(name, maxLength, description, null);
    }

    public OFTP2Field(String name, int maxLength, String description, String defaultValue) {
        byte[] defaultValueBytes = defaultValue.getBytes();
        this.initialize(name, maxLength, description, defaultValueBytes);
    }

    public OFTP2Field(String name, int maxLength, String description, byte[] defaultValue) {
        this.initialize(name, maxLength, description, defaultValue);
    }

    /**Formats a given date in the format CCYYMMDD*/
    public static String toDateStr( Date date ){
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        return( format.format( date ));
    }

    /**Formats a given date in the format HHmmssXXXX*/
    public static String toTimeStr( Date date, String counterStr ){
        DateFormat format = new SimpleDateFormat("HHmmss");
        return( format.format( date ) + counterStr);
    }

    /**Performs a padding, depenging on the field type. 000 for N, 0x20 for AN
     * 
     * @return
     */
    public byte[] performPadding(byte[] value) {
        //no padding required
        if (value != null && value.length == this.getMaxLength()) {
            return (value);
        }
        //drop leading 0 bytes for BIN fields
        if( value != null && this.getType() == OFTP2Field.TYPE_BIN){
            BigInteger bigInt = new BigInteger( value );
            value = bigInt.toByteArray();
        }
        if (value != null && this.getMaxLength() < value.length) {
            throw new IllegalArgumentException("Field default > defined field maxLength");
        }
        byte[] newValue = new byte[this.getMaxLength()];        
        if (this.getType() == TYPE_BIN) {
            //binary: left padding with 0H bytes
            Arrays.fill(newValue, (byte) 0x00);
            if (value != null) {
                int offset = this.getMaxLength() - value.length;
                System.arraycopy(value, 0, newValue, offset, value.length);
            }
        }else if (this.getType() != TYPE_N) {
            //numeric: right fill up with blanks, up to maxlength
            Arrays.fill(newValue, (byte) 0x20);
            if (value != null) {
                System.arraycopy(value, 0, newValue, 0, value.length);
            }
        } else {
            //other types: left fill with 30H ("0")
            Arrays.fill(newValue, (byte) 0x30);
            if (value != null) {
                int offset = this.getMaxLength() - value.length;
                System.arraycopy(value, 0, newValue, offset, value.length);
            }
        }
        return( newValue );
    }

    /**initializes the field*/
    private void initialize(String name, int maxLength, String description, byte[] defaultValue) {
        this.description = description;
        this.setName(name);
        if (this.getLengthGivenByField() == null) {
            this.setMaxLength(maxLength);
            byte[] initValue = this.performPadding(defaultValue);
            this.defaultValue = initValue;
        } else {
            byte[] referedFieldsDefaultValue = this.lengthGivenByField.getDefaultValue();
            if (referedFieldsDefaultValue == null) {
                throw new RuntimeException("Unable to initialize field " + name
                        + ": field reference '"
                        + this.lengthGivenByField.getName()
                        + "' has no default value defined.");
            }
            String value = new String(referedFieldsDefaultValue);
            int lengthByContent = 0;
            try {
                lengthByContent = Integer.valueOf(value.trim());
            } catch (NumberFormatException e) {
                //nop
            }
            byte[] newDefaultValue = new byte[lengthByContent];
            Arrays.fill(newDefaultValue, (byte) 0x20);
            this.defaultValue = newDefaultValue;
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the length
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * @param maxLength the length to set
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the defaultValue
     */
    public byte[] getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(byte[] defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**Will return null if no other fields content defines this fields length
     * @return the lengthGivenByField
     */
    public OFTP2Field getLengthGivenByField() {
        return (this.lengthGivenByField);
    }

    /**
     * @param lengthGivenByField the lengthGivenByField to set
     */
    public void setLengthGivenByField(OFTP2Field lengthGivenByField) {
        this.lengthGivenByField = lengthGivenByField;
        this.initialize(this.name, this.maxLength, this.description, null);
    }

    public abstract int getType();
}
