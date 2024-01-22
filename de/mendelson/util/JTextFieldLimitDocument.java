//$Header: /mendelson_business_integration/de/mendelson/util/JTextFieldLimitDocument.java 2     8.04.15 13:54 Heller $
package de.mendelson.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Document that limits the input document length of a JTextField, use it in the
 * following way: 
 * JTextFieldLimitDocument limit = new JTextFieldLimitDocument();
 * limit.setLengthLimit(26); 
 * textfield.setDocument(limit);
 * 
 * or simpler:
 * 
 * jTextFieldXX.setDocument( new JTextFieldLimitDocument(255));
 * 
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class JTextFieldLimitDocument extends PlainDocument {

    private int lengthLimit = 20;

    public JTextFieldLimitDocument() {
        super();
    }

    public JTextFieldLimitDocument(int lengthLimit) {
        super();
        this.lengthLimit = lengthLimit;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attr)
            throws BadLocationException {
        if (str == null) {
            return;
        }
        if ((getLength() + str.length()) <= getLengthLimit()) {
            super.insertString(offset, str, attr);
        }
    }

    /**
     * @return the lengthLimit
     */
    public int getLengthLimit() {
        return lengthLimit;
    }

    /**
     * @param lengthLimit the lengthLimit to set
     */
    public void setLengthLimit(int lengthLimit) {
        this.lengthLimit = lengthLimit;
    }
}
