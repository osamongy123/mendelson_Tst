//$Header: /oftp2/de/mendelson/util/balloontip/BalloonToolTip.java 9     14/06/22 10:05 Heller $
package de.mendelson.util.balloontip;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.IllegalComponentStateException;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Tooltip implementation for balloon tool tips that helps explaining details
 * direct in the UI
 *
 * @author S.Heller
 * @version $Revision: 9 $
 */
public class BalloonToolTip extends JToolTip {

    protected static final int BORDER_GAP = 10;
    private final int TRIANGLE_SIZE = 10;
    private final int ARC = 10;
    protected static final int BORDER_STROKE_SIZE = 1;
    private Font font;
    private Color borderColor = Color.DARK_GRAY;
    private Color backgoundColor = Color.LIGHT_GRAY;
    private Color foregoundColor = Color.BLACK;

    public BalloonToolTip() {
        super();
        this.font = UIManager.getFont("ToolTip.font");
        if (this.font == null) {
            this.font = new Font(Font.DIALOG, Font.PLAIN, 12);
        }
        if (UIManager.getColor("controlText") != null) {
            foregoundColor = UIManager.getColor("controlText");
        }
        if (UIManager.getColor("controlHighlight") != null) {
            backgoundColor = UIManager.getColor("controlHighlight");
        }
        if (UIManager.getColor("controlDkShadow") != null) {
            borderColor = UIManager.getColor("controlDkShadow");
        }
        this.setBorder(new EmptyBorder(
                BORDER_GAP,
                BORDER_GAP,
                BORDER_GAP,
                BORDER_GAP + TRIANGLE_SIZE));
        this.setOpaque(true);
        /**2022-05: There seems to be a bug in the java tooltip management. If the user manages 
         * to bring the mouse onto the tooltip while it is displayed (requires a really fast mouse), the tooltip
         * will not be disabled and stay visible until the user moves the mouse again to a new tool
         * tip element. This listener will simply set the visibility to false if the user manages 
         * it to move the mouse that fast
         */
        this.addMouseListener(new MouseAdapter() {            

            @Override            
            public void mouseEntered(MouseEvent e) {
                BalloonToolTip.this.setVisible(false);
            }
            
        });
    }

    /**
     * Sets the colors of this component
     */
    public void setColors(Color backgoundColor, Color foregoundColor, Color borderColor) {
        this.backgoundColor = backgoundColor;
        this.foregoundColor = foregoundColor;
        this.borderColor = borderColor;
    }

    @Override
    /**
     * Makes this Container displayable by connecting it to a native screen
     * resource. This is overwritten to make all parent components transparent
     * and to set a transparent background to the parent window
     *
     */
    public void addNotify() {
        super.addNotify();
        setOpaque(false);
        Component parent = this.getParent();
        if (parent != null) {
            if (parent instanceof JComponent) {
                JComponent jparent = (JComponent) parent;
                jparent.setOpaque(false);
            }
        }
        Window window = SwingUtilities.windowForComponent(this);
        if (window != null) {
            try {
                window.setBackground(new Color(255, 255, 255, 0));
            } catch (IllegalComponentStateException e) {
                //Do nothing
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        //do not display a tooltip for a disabled component
        if( !this.getComponent().isEnabled()){
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        //clear
        BufferedImage bufferedImage = new BufferedImage(this.getWidth(), this.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        // Create a graphics contents on the buffered image
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        g2d.dispose();
        g2.drawImage(bufferedImage, 0, 0, this);
        g.drawImage(bufferedImage, 0, 0, this);
        // create the balloon shape
        Shape balloonArea = this.createBalloonShape();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        //set the background color of the tooltip. Its also possible to make it
        //transparent in the future, with something like
        //Color transparentColor = new Color( 
        //        this.backgoundColor.getRed(),
        //        this.backgoundColor.getGreen(),
        //        this.backgoundColor.getBlue(),
        //        200);
        // - where the alpha channel is a value from 0 (full transparency) to 255 (solid)
        g2.setColor(this.backgoundColor);
        g2.fill(balloonArea);

        // draw the balloon border
        g2.setColor(this.borderColor);
        g2.setStroke(new BasicStroke(BORDER_STROKE_SIZE));
        g2.draw(balloonArea);
        g2.dispose();

        //generate the text label and render it
        String toolTipText = this.getComponent().getToolTipText();
        JLabel textLabel = new JLabel(toolTipText);
        textLabel.setFont(this.font);
        textLabel.setSize(textLabel.getPreferredSize());
        Graphics2D g2Text = (Graphics2D) g.create(BORDER_GAP + TRIANGLE_SIZE, BORDER_GAP,
                this.getWidth() - BORDER_GAP - TRIANGLE_SIZE, this.getHeight() - BORDER_GAP);
        g2Text.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2Text.setColor(this.foregoundColor);
        textLabel.setForeground(this.foregoundColor);
        textLabel.paint(g2Text);
        g2Text.dispose();

    }

    /**
     * Creates the shape for the balloon
     */
    private Shape createBalloonShape() {
        Shape balloonShape = new RoundRectangle2D.Float(TRIANGLE_SIZE, BORDER_STROKE_SIZE,
                this.getWidth() - TRIANGLE_SIZE - 2 * BORDER_STROKE_SIZE,
                this.getHeight() - 2 * BORDER_STROKE_SIZE,
                ARC, ARC);
        Polygon triangle = new Polygon();
        int triangleX = (int) (balloonShape.getBounds2D().getX());
        int triangleY = (int) (balloonShape.getBounds2D().getCenterY());
        triangle.addPoint(triangleX
                - TRIANGLE_SIZE, triangleY);
        triangle.addPoint(triangleX, triangleY
                - TRIANGLE_SIZE);
        triangle.addPoint(triangleX, triangleY
                + TRIANGLE_SIZE);
        Area balloonArea = new Area(balloonShape);
        balloonArea.add(new Area(triangle));
        return (balloonArea);
    }

}
