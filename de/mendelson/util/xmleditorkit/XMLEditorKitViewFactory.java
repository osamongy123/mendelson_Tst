//$Header: /as4/de/mendelson/util/xmleditorkit/XMLEditorKitViewFactory.java 1     4/05/18 10:58a Heller $
package de.mendelson.util.xmleditorkit;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * XML Editor Kit - based on code from Stanislav Lapitsky
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
class XMLEditorKitViewFactory implements ViewFactory {

    @Override
    public View create(Element elem) {
        String kind = elem.getName();
        if (kind != null) {
            if (kind.equals(AbstractDocument.ContentElementName)) {
                return new LabelView(elem);
            } else if (kind.equals(XMLStyledDocument.TAG_ELEMENT)) {
                return new XMLTagView(elem);
            } else if (kind.equals(XMLStyledDocument.TAG_ROW_START_ELEMENT)
                    || kind.equals(XMLStyledDocument.TAG_ROW_END_ELEMENT)) {
                return new BoxView(elem, View.X_AXIS) {
                    @Override
                    public float getAlignment(int axis) {
                        return 0;
                    }

                    @Override
                    public float getMaximumSpan(int axis) {
                        return getPreferredSpan(axis);
                    }
                };
            } else if (kind.equals(AbstractDocument.SectionElementName)) {
                return new BoxView(elem, View.Y_AXIS);
            } else if (kind.equals(StyleConstants.ComponentElementName)) {
                return new ComponentView(elem);
            } else if (kind.equals(StyleConstants.IconElementName)) {
                return new IconView(elem);
            }
        }

        // default to text display
        return new LabelView(elem);
    }
}
