//$Header: /as4/de/mendelson/util/xmleditorkit/XMLEditorKit.java 2     16.10.19 11:30 Heller $
package de.mendelson.util.xmleditorkit;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.DefaultEditorKit.CopyAction;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.TextAction;
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
 * @version $Revision: 2 $
 */
public class XMLEditorKit extends StyledEditorKit {

    private ViewFactory defaultFactory = new XMLEditorKitViewFactory();

    @Override
    public ViewFactory getViewFactory() {
        return defaultFactory;
    }

    @Override
    public Document createDefaultDocument() {
        return new XMLStyledDocument();
    }

    @Override
    public String getContentType() {
        return "text/xml";
    }

    @Override
    public void read(Reader in, Document document, int position) throws IOException, BadLocationException {
        BufferedReader bufferedReader = new BufferedReader(in);
        String line = bufferedReader.readLine();
        StringBuilder stringBuilder = new StringBuilder();
        while (line != null) {
            stringBuilder.append(line);
            line = bufferedReader.readLine();
        }
        int insertPosition = getInsertPosition(position, document);
        XMLEditorKitXMLReader.getInstance().read(new ByteArrayInputStream(stringBuilder.toString().getBytes()), document, insertPosition);
    }

    @Override
    public void read(InputStream in, Document document, int position) throws IOException, BadLocationException {
        int insertPosition = getInsertPosition(position, document);
        XMLEditorKitXMLReader.getInstance().read(in, document, insertPosition);
    }

    @Override
    public void write(OutputStream out, Document document, int position, int len) throws IOException, BadLocationException {
        int[] selection = new int[2];
        selection[0] = position;
        selection[1] = position + len;
        correctSelectionBounds(selection, document);
        position = selection[0];
        len = selection[1] - position;
        super.write(out, document, position, len);
    }

    @Override
    public void write(Writer out, Document document, int position, int len) throws IOException, BadLocationException {
        int[] sel = new int[2];
        sel[0] = position;
        sel[1] = position + len;
        correctSelectionBounds(sel, document);
        position = sel[0];
        len = sel[1] - position;
        super.write(out, document, position, len);
    }

    public static void correctSelectionBounds(int[] selection, Document xmlDocument) {
        if (xmlDocument instanceof XMLStyledDocument && xmlDocument.getLength() > 0) {
            XMLStyledDocument doc = (XMLStyledDocument) xmlDocument;
            int start = selection[0];
            Element root = doc.getDefaultRootElement();
            int i = root.getElementIndex(start);
            while (i >= 0 && root.getElement(i).getName().equals(XMLStyledDocument.TAG_ELEMENT)) {
                root = root.getElement(i);
                i = root.getElementIndex(start);
            }
            Element startTag = root;
            int end = selection[0];
            root = doc.getDefaultRootElement();
            i = root.getElementIndex(end);
            while (i >= 0 && root.getElement(i).getName().equals(XMLStyledDocument.TAG_ELEMENT)) {
                root = root.getElement(i);
                i = root.getElementIndex(end);
            }
            Element endTag = root;
            Element commonParent = startTag;
            while (commonParent != null
                    && !(commonParent.getStartOffset() <= endTag.getStartOffset()
                    && commonParent.getEndOffset() >= endTag.getEndOffset())) {
                commonParent = commonParent.getParentElement();
            }
            if (commonParent != null) {
                selection[0] = commonParent.getStartOffset();
                selection[1] = commonParent.getEndOffset();
            }
        }
    }

    protected int getInsertPosition(int position, Document document) {
        if (document instanceof XMLStyledDocument && document.getLength() > 0) {
            XMLStyledDocument doc = (XMLStyledDocument) document;
            Element root = doc.getDefaultRootElement();
            int i = root.getElementIndex(position);
            while (i >= 0 && root.getElement(i).getName().equals(XMLStyledDocument.TAG_ELEMENT)) {
                root = root.getElement(i);
                i = root.getElementIndex(position);
            }
            while (root.getElementCount() < 3) {
                root = root.getParentElement();
            }
            return root.getElement(0).getEndOffset();
        }
        return position;
    }

    
    private MouseListener lstCollapse = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent evt) {
            JEditorPane src = (JEditorPane) evt.getSource();

            int pos = src.viewToModel(evt.getPoint());
            View v = src.getUI().getRootView(src);
            boolean insideTagView = false;
            while (v != null && !(v instanceof XMLTagView)) {
                int i = v.getViewIndex(pos, Position.Bias.Forward);
                v = v.getView(i);
            }
            XMLTagView deepest = (XMLTagView) v;
            while (v != null && v instanceof XMLTagView) {
                deepest = (XMLTagView) v;
                int i = v.getViewIndex(pos, Position.Bias.Forward);
                v = v.getView(i);
            }

            if (deepest != null) {
                Shape a = getAllocation(deepest, src);
                if (a != null) {
                    Rectangle r = a instanceof Rectangle ? (Rectangle) a : a.getBounds();
                    r.y += XMLTagView.AREA_SHIFT / 2;
                    r.width = XMLTagView.AREA_SHIFT;
                    r.height = XMLTagView.AREA_SHIFT;

                    if (r.contains(evt.getPoint())) {
                        deepest.setExpanded(!deepest.isExpanded());

                        XMLStyledDocument doc = (XMLStyledDocument) src.getDocument();
                        try {
                            doc.setUserChanges(false);
                            pos++;
                            doc.insertString(pos, "\n", new SimpleAttributeSet());
                            doc.remove(pos, 1);
                            doc.setUserChanges(true);
                        } catch (BadLocationException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
    };

    Cursor oldCursor;
    MouseMotionListener lstMoveCollapse = new MouseMotionAdapter() {
        @Override
        public void mouseMoved(MouseEvent evt) {
            JEditorPane src = (JEditorPane) evt.getSource();
            if (oldCursor == null) {
                oldCursor = src.getCursor();
            }

            int pos = src.viewToModel(evt.getPoint());
            View v = src.getUI().getRootView(src);
            while (v != null && !(v instanceof XMLTagView)) {
                int i = v.getViewIndex(pos, Position.Bias.Forward);
                v = v.getView(i);
            }
            XMLTagView deepest = (XMLTagView) v;
            while (v != null && v instanceof XMLTagView) {
                deepest = (XMLTagView) v;
                int i = v.getViewIndex(pos, Position.Bias.Forward);
                v = v.getView(i);
            }

            if (deepest != null) {
                Shape a = getAllocation(deepest, src);
                if (a != null) {
                    Rectangle r = a instanceof Rectangle ? (Rectangle) a : a.getBounds();
                    r.y += XMLTagView.AREA_SHIFT / 2;
                    r.width = XMLTagView.AREA_SHIFT;
                    r.height = XMLTagView.AREA_SHIFT;

                    if (r.contains(evt.getPoint())) {
                        src.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        return;
                    }
                }
            }
            src.setCursor(oldCursor);
        }
    };

    @Override
    public void install(JEditorPane editorPane) {
        super.install(editorPane);
        editorPane.addMouseListener(lstCollapse);
        editorPane.addMouseMotionListener(lstMoveCollapse);
    }

    @Override
    public void deinstall(JEditorPane editorPane) {
        editorPane.removeMouseListener(lstCollapse);
        editorPane.removeMouseMotionListener(lstMoveCollapse);
        super.deinstall(editorPane);
    }

    protected static Shape getAllocation(View v, JEditorPane edit) {
        Insets ins = edit.getInsets();
        View vParent = v.getParent();
        int x = ins.left;
        int y = ins.top;
        while (vParent != null) {
            int i = vParent.getViewIndex(v.getStartOffset(), Position.Bias.Forward);
            Shape alloc = vParent.getChildAllocation(i, new Rectangle(0, 0, Short.MAX_VALUE, Short.MAX_VALUE));
            x += alloc.getBounds().x;
            y += alloc.getBounds().y;

            vParent = vParent.getParent();
        }

        if (v instanceof BoxView) {
            int ind = v.getParent().getViewIndex(v.getStartOffset(), Position.Bias.Forward);
            Rectangle r2 = v.getParent().getChildAllocation(ind, new Rectangle(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE)).getBounds();

            return new Rectangle(x, y, r2.width, r2.height);
        }

        return new Rectangle(x, y, (int) v.getPreferredSpan(View.X_AXIS), (int) v.getPreferredSpan(View.Y_AXIS));
    }

    @Override
    public Action[] getActions() {
        Action[] res = super.getActions();
        for (int i = 0; i < res.length; i++) {
            if (res[i] instanceof CopyAction) {
                res[i] = new XMLCopyAction();
            }
        }
        return res;
    }

    private static class XMLCopyAction extends TextAction {
        /**
         * Create this object with the appropriate identifier.
         */
        public XMLCopyAction() {
            super(copyAction);
        }

        /**
         * The operation to perform when this action is triggered.
         *
         * @param evt the action event
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            JTextComponent targetComponent = getTextComponent(evt);
            if (targetComponent != null) {
                //adapt selection
                int start = targetComponent.getSelectionStart();
                int end = targetComponent.getSelectionEnd();
                if (start != end) {
                    int[] selection = new int[2];
                    selection[0] = start;
                    selection[1] = end;
                    XMLEditorKit.correctSelectionBounds(selection, targetComponent.getDocument());
                    targetComponent.setSelectionStart(selection[0]);
                    targetComponent.setSelectionEnd(selection[1]);
                }
                targetComponent.copy();
            }
        }
    }
}
