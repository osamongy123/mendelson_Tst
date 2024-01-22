//$Header: /as4/de/mendelson/util/xmleditorkit/XMLStyledDocument.java 3     16.10.19 12:49 Heller $
package de.mendelson.util.xmleditorkit;

import de.mendelson.util.ColorUtil;
import java.awt.Color;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.DefaultStyledDocument.ElementSpec;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * XML Editor Kit - based on code from Stanislav Lapitsky
 * This class requires the ColorUtil class to determine the colors with the best 
 * contrast for the UIs editor pane background color
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */

public class XMLStyledDocument extends DefaultStyledDocument {

    public static String TAG_ELEMENT = "tag_element";
    public static String TAG_ROW_START_ELEMENT = "tag_row_start_element";
    public static String TAG_ROW_END_ELEMENT = "tag_row_end_element";

    public static SimpleAttributeSet BRACKET_ATTRIBUTES = new SimpleAttributeSet();
    public static SimpleAttributeSet TAGNAME_ATTRIBUTES = new SimpleAttributeSet();
    public static SimpleAttributeSet ATTRIBUTENAME_ATTRIBUTES = new SimpleAttributeSet();
    public static SimpleAttributeSet ATTRIBUTEVALUE_ATTRIBUTES = new SimpleAttributeSet();
    public static SimpleAttributeSet PLAIN_ATTRIBUTES = new SimpleAttributeSet();
    public static SimpleAttributeSet COMMENT_ATTRIBUTES = new SimpleAttributeSet();

    static {
        Color editorPaneBackgroundColor = UIManager.getColor("EditorPane.background");
        if( editorPaneBackgroundColor == null ){
            editorPaneBackgroundColor = Color.WHITE;
        }        
        
        //TAG NAME
        StyleConstants.setBold(TAGNAME_ATTRIBUTES, true);        
        StyleConstants.setForeground(TAGNAME_ATTRIBUTES, 
                ColorUtil.getBestContrastColorAroundForeground(
                        editorPaneBackgroundColor, Color.GREEN.darker().darker()));
        
        //ATTRIBUTENAME
        StyleConstants.setBold(ATTRIBUTENAME_ATTRIBUTES, true);
        StyleConstants.setForeground(ATTRIBUTENAME_ATTRIBUTES, 
                ColorUtil.getBestContrastColorAroundForeground(
                        editorPaneBackgroundColor, Color.BLACK));        
        
        //ATTRIBUTEVALUE
        StyleConstants.setItalic(ATTRIBUTEVALUE_ATTRIBUTES, true);
        StyleConstants.setForeground(ATTRIBUTEVALUE_ATTRIBUTES, 
                ColorUtil.getBestContrastColorAroundForeground(
                        editorPaneBackgroundColor, Color.BLUE));
        
        //PLAIN ATTRIBUTES
        StyleConstants.setFontSize(PLAIN_ATTRIBUTES, StyleConstants.getFontSize(PLAIN_ATTRIBUTES) - 1);
        StyleConstants.setForeground(PLAIN_ATTRIBUTES, 
                ColorUtil.getBestContrastColorAroundForeground(
                        editorPaneBackgroundColor, Color.DARK_GRAY));
        
        //COMMENT ATTRIBUTES
        StyleConstants.setFontSize(COMMENT_ATTRIBUTES, StyleConstants.getFontSize(COMMENT_ATTRIBUTES) - 1);
        StyleConstants.setForeground(COMMENT_ATTRIBUTES, ColorUtil.getBestContrastColorAroundForeground(
                        editorPaneBackgroundColor, Color.GRAY));
        StyleConstants.setItalic(COMMENT_ATTRIBUTES, true);
        
        
        //BRACKET ATTRIBUTES
        StyleConstants.setForeground(BRACKET_ATTRIBUTES, ColorUtil.getBestContrastColorAroundForeground(
                        editorPaneBackgroundColor, Color.BLACK));
    }

    private boolean isUserChanges = true;

    public XMLStyledDocument() {

    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (!isUserChanges()) {
            super.insertString(offs, str, a);
        }
    }

    @Override
    public void remove(int offs, int len) throws BadLocationException {
        if (!isUserChanges()) {
            super.remove(offs, len);
        }
    }

    public boolean isUserChanges() {
        return isUserChanges;
    }

    public void setUserChanges(boolean userChanges) {
        isUserChanges = userChanges;
    }

    @Override
    protected void insert(int offset, ElementSpec[] data) throws BadLocationException {
        super.insert(offset, data);
    }

}
