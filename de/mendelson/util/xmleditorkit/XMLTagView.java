//$Header: /as4/de/mendelson/util/xmleditorkit/XMLTagView.java 1     4/05/18 10:58a Heller $
package de.mendelson.util.xmleditorkit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.View;
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

public class XMLTagView extends BoxView {

    private boolean isExpanded = true;
    public static final int AREA_SHIFT = 10;

    public XMLTagView(Element elem) {
        super(elem, View.Y_AXIS);
        setInsets((short) 0, (short) (AREA_SHIFT + 2), (short) 0, (short) 0);
    }

    @Override
    public float getAlignment(int axis) {
        return 0;
    }

    @Override
    public void paint(Graphics g, Shape alloc) {
        Rectangle a = alloc instanceof Rectangle ? (Rectangle) alloc : alloc.getBounds();
        Shape oldClip = g.getClip();
        if (!isExpanded()) {
            Area newClip = new Area(oldClip);
            newClip.intersect(new Area(a));
            g.setClip(newClip);
        }
        super.paint(g, a);
        if (getViewCount() > 1) {
            g.setClip(oldClip);
            a.width--;
            a.height--;
            g.setColor(Color.lightGray);
            //collapse rect
            g.drawRect(a.x, a.y + AREA_SHIFT / 2, AREA_SHIFT, AREA_SHIFT);

            if (!isExpanded()) {
                g.drawLine(a.x + AREA_SHIFT / 2, a.y + AREA_SHIFT / 2 + 2, a.x + AREA_SHIFT / 2, a.y + AREA_SHIFT / 2 + AREA_SHIFT - 2);
            } else {
                g.drawLine(a.x + AREA_SHIFT / 2, a.y + 3 * AREA_SHIFT / 2, a.x + AREA_SHIFT / 2, a.y + a.height);
                g.drawLine(a.x + AREA_SHIFT / 2, a.y + a.height, a.x + AREA_SHIFT, a.y + a.height);
            }

            g.drawLine(a.x + 2, a.y + AREA_SHIFT, a.x + AREA_SHIFT - 2, a.y + AREA_SHIFT);
        }
    }

    @Override
    public float getPreferredSpan(int axis) {
        if (isExpanded() || axis != View.Y_AXIS) {
            return super.getPreferredSpan(axis);
        } else {
            View firstChild = getView(0);
            return getTopInset() + firstChild.getPreferredSpan(View.Y_AXIS);
        }
    }

    @Override
    public float getMinimumSpan(int axis) {
        if (isExpanded() || axis != View.Y_AXIS) {
            return super.getMinimumSpan(axis);
        } else {
            View firstChild = getView(0);
            return getTopInset() + firstChild.getMinimumSpan(View.Y_AXIS);
        }
    }

    @Override
    public float getMaximumSpan(int axis) {
        return getPreferredSpan(axis);
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    @Override
    protected int getNextEastWestVisualPositionFrom(int pos, Position.Bias b,
            Shape a,
            int direction,
            Position.Bias[] biasRet)
            throws BadLocationException {
        int newPos = super.getNextEastWestVisualPositionFrom(pos, b, a, direction, biasRet);
        if (!isExpanded()) {
            if (newPos >= getStartOffset() && newPos < getView(0).getView(0).getEndOffset()) {
                //first line of first child
                return newPos;
            } else if (newPos >= getView(0).getView(0).getEndOffset()) {
                if (direction == SwingConstants.EAST) {
                    newPos = Math.min(getDocument().getLength() - 1, getEndOffset());
                } else {
                    newPos = getView(0).getView(0).getEndOffset() - 1;
                }
            }
        }

        return newPos;
    }
}
