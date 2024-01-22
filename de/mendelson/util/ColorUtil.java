//$Header: /oftp2/de/mendelson/util/ColorUtil.java 13    13/05/22 14:01 Heller $
package de.mendelson.util;

import static de.mendelson.util.ColorUtil.getBestContrastColorAroundForeground;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Tool for dealing with colors and color contrasts - the mendelson software is
 * platform independent and any color could be the background color of the OS.
 * Setting one foreground color to a fixed color without checking it against the
 * background color could result in a bad color contrast
 *
 * @author S.Heller
 * @version $Revision: 13 $
 */
public class ColorUtil {

    private static final int BEST_CONTRAST = 100;

    /**
     * Calculates the brightness of a given RGB color The formula for RGB is
     * 0.2126*R + 0.7152*G + 0.0722*B
     *
     * @return A value between 0 and 255
     */
    public static int calculateLuminance(Color color) {
        if( color == null ){
            throw new RuntimeException( "ColorUtil.calculateLuminance(Color color): Color argument must not be null");
        }
        float luminance = (float) (0.2126f * color.getRed() + 0.7152f * color.getGreen() + 0.0722f * color.getBlue());
        return (Math.round(luminance));
    }

    /**
     * Calculates the contrast of the passed foreground color to the passed
     * background color, this is the ratio of both luminance values
     *
     * @param foregroundColor
     * @param backgroundColor
     * @return
     */
    public static int calculateContrast(Color backgroundColor, Color foregroundColor) {
        int luminanceForground = calculateLuminance(foregroundColor);
        int luminanceBackground = calculateLuminance(backgroundColor);
        return (Math.abs(luminanceForground - luminanceBackground));
    }

    /**
     * Checks if the contrast between 2 colors is ok to use them in the UI
     *
     * @param color1
     * @param color2
     * @return
     */
    public static boolean contrastIsOk(Color color1, Color color2) {
        return (calculateContrast(color1, color2) >= BEST_CONTRAST);
    }
 
    /**
     * Returns the color of the list of possible colors that has the lowest contrast to the suggestedColor
     * @param suggestedColor The color which is the best suggestion - but only a list of other colors is available
     * @param possibleColors List of colors - one of them is returned (the one with the lowest contrast)
     * @return 
     */
    public static Color getColorWithLowestContrast( Color suggestedColor, List<Color> possibleColors){
        int lowestContrastSoFar = Integer.MAX_VALUE;
        int bestIndexSoFar = 0;
        for (int i = 0; i < possibleColors.size(); i++) {
            int foundContrast = calculateContrast(suggestedColor, possibleColors.get(i));
            if (foundContrast < lowestContrastSoFar) {
                bestIndexSoFar = i;
                lowestContrastSoFar = foundContrast;
            }
        }
        return (possibleColors.get(bestIndexSoFar));
    }    
    
    /**
     * Returns the best contrast color from the list of passed foreground colors for the
     * passed background color
     *
     * @param background
     * @param foregroundColors
     * @return
     */
    public static Color getBestContrastColor(Color background, List<Color> foregroundColors) {
        int bestContrastSoFar = Integer.MAX_VALUE;
        int bestIndexSoFar = 0;
        for (int i = 0; i < foregroundColors.size(); i++) {
            int foundContrast = calculateContrast(background, foregroundColors.get(i));
            int foundContrastDiff = Math.abs(BEST_CONTRAST - foundContrast);
            if (foundContrastDiff < bestContrastSoFar) {
                bestIndexSoFar = i;
                bestContrastSoFar = foundContrastDiff;
            }
        }
        return (foregroundColors.get(bestIndexSoFar));
    }

    /**
     * Auto corrects the foreground color of the passed text component to keep
     * good color contrast
     *
     * @return The new foreground color
     */
    public static Color autoCorrectForegroundColor(Component component) {
        Color bestColor = getBestContrastColorAroundForeground(component.getBackground(), component.getForeground());
        component.setForeground(bestColor);
        return (bestColor);
    }

    /**
     * Returns the a contrast color from the passed foreground color that is
     * even the same if the contrast is ok or a color that is brighter or darker
     *
     * @param background
     * @param foregroundColors
     * @return
     */
    public static Color getBestContrastColorAroundForeground(Color background, Color foregroundColor) {
        //keep color if contrast is ok
        if (contrastIsOk(background, foregroundColor)) {
            return (foregroundColor);
        }
        List<Color> foregroundColors = null;
        //its a dark background: prefer light colors
        if (calculateLuminance(background) < 128) {
            foregroundColors = new ArrayList<Color>();
            foregroundColors.add(foregroundColor);
            for (int i = 0; i < 20; i++) {
                foregroundColor = lightenColor(foregroundColor, 0.05f);
                foregroundColors.add(foregroundColor);
            }
        } else {
            //its a light foreground: prefer dark colors
            foregroundColors = new ArrayList<Color>();
            foregroundColors.add(foregroundColor);
            for (int i = 0; i < 20; i++) {
                foregroundColor = darkenColor(foregroundColor, 0.05f);
                foregroundColors.add(foregroundColor);
            }
        }
        Color newForegroundColor = getBestContrastColor(background, foregroundColors);
        return (newForegroundColor);
    }

    /**
     * Lightens a color by a given amount
     *
     * @param color The color to lighten
     * @param amount The amount to lighten the color. 0 will leave the color
     * unchanged; 1 will make the color completely white. Sample: 0.1f will lighten the color by 10% - the
     * standard java Color.lighten() does around 25%
     * @return The bleached color
     */
    public static Color lightenColor(Color color, float amount) {
        if (amount > 1f) {
            amount = 1f;
        } else if (amount < 0f) {
            amount = 0f;
        }
        //afterwards lighten the color        
        int red = (int) ((color.getRed() * (1 - amount) / 255 + amount) * 255);
        int green = (int) ((color.getGreen() * (1 - amount) / 255 + amount) * 255);
        int blue = (int) ((color.getBlue() * (1 - amount) / 255 + amount) * 255);
        return (new Color(red, green, blue));
    }

    /**
     * Darkens a color by a given amount
     *
     * @param color The color to darken
     * @param amount The amount to darken the color. 0 will leave the color
     * unchanged; 1 will make the color completely black. Sample: 0.1f will darken the color by 10% - the
     * standard java Color.darken() does around 25%
     * @return The bleached color
     */
    public static Color darkenColor(Color color, float amount) {
        if (amount > 1f) {
            amount = 1f;
        } else if (amount < 0f) {
            amount = 0f;
        }
        //afterwards darken the color        
        int red = (int) ((color.getRed() * (1 - amount) / 255) * 255);    
        int green = (int) ((color.getGreen() * (1 - amount) / 255) * 255);
        int blue = (int) ((color.getBlue() * (1 - amount) / 255) * 255);
        return (new Color(red, green, blue));
    }

    /**Mix two colors
     * @param ration [0..1]
     */
    public static Color blend(Color color1, Color color2, float ratio) {
        //huh???
        if( color1 == null && color2 == null ){
            return( Color.BLACK);            
        }
        if( color1 == null ){
            return( color2 );
        }
        if( color2 == null ){
            return( color1 );
        }
        if (ratio > 1f) {
            ratio = 1f;
        } else if (ratio < 0f) {
            ratio = 0f;
        }
        float iRatio = 1.0f - ratio;
        int i1 = color1.getRGB();
        int i2 = color2.getRGB();
        int a1 = (i1 >> 24 & 0xff);
        int r1 = ((i1 & 0xff0000) >> 16);
        int g1 = ((i1 & 0xff00) >> 8);
        int b1 = (i1 & 0xff);
        int a2 = (i2 >> 24 & 0xff);
        int r2 = ((i2 & 0xff0000) >> 16);
        int g2 = ((i2 & 0xff00) >> 8);
        int b2 = (i2 & 0xff);
        int a = (int) ((a1 * iRatio) + (a2 * ratio));
        int r = (int) ((r1 * iRatio) + (r2 * ratio));
        int g = (int) ((g1 * iRatio) + (g2 * ratio));
        int b = (int) ((b1 * iRatio) + (b2 * ratio));
        return new Color(a << 24 | r << 16 | g << 8 | b);
    }

    
    /**
     * Returns the color in the format "#rrggbb"
     * @param color
     * @return 
     */
    public static String toHex(Color color){
        String hex = String.format("#%02x%02x%02x", 
                color.getRed(), 
                color.getGreen(), 
                color.getBlue());  
        return( hex.toUpperCase());
    }
    
}
