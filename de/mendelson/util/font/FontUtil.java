//$Header: /converteride/de/mendelson/util/font/FontUtil.java 9     23/03/22 13:28 Heller $
package de.mendelson.util.font;

import java.awt.Color;
import java.awt.Font;
import java.io.InputStream;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Utility class to deliver and handle the standard fonts used in the mendelson
 * products
 *
 * @author S.Heller
 * @version $Revision: 9 $
 */
public class FontUtil {

    public static final String STYLE_LOGO = "LOGO_PLAIN";
    public static final String STYLE_PRODUCT_PLAIN = "PRODUCT_PLAIN";
    public static final String STYLE_PRODUCT_BOLD = "PRODUCT_BOLD";
    public static final String STYLE_PLAIN = "PLAIN";
    public static final String STYLE_BOLD = "BOLD";
    

    public static final String PRODUCT_MBI = "mbi";
    public static final String PRODUCT_AS2 = "AS2";
    public static final String PRODUCT_OFTP2 = "OFTP2";
    public static final String PRODUCT_AS4 = "AS4";
    public static final String PRODUCT_EDIPAD = "EDIPAD";
    public static final String PRODUCT_AS4_COMMUNITY = "AS4_COMMUNITY";
    public static final String PRODUCT_AS2_COMMUNITY = "AS2_COMMUNITY";
    public static final String PRODUCT_OFTP2_COMMUNITY = "OFTP2_COMMUNITY";
    public static final String PRODUCT_CONVERTER_IDE = "CONVERTER_IDE";

    private static Font fontProductPlain;
    private static Font fontProductBold;
    private static Font fontPlain;
    private static Font fontBold;
    private static Font fontLogo;

    static {
        String fontResourceProductPlain = "/de/mendelson/util/font/Square721ExtendedBT.ttf";
        InputStream fontInStream = null;
        try {
            fontInStream = FontUtil.class.getResourceAsStream(fontResourceProductPlain);
            fontProductPlain = Font.createFont(Font.TRUETYPE_FONT, fontInStream);
        } catch (Exception e) {
            fontProductPlain = new Font("SansSerif", Font.BOLD, 18);
        } finally {
            if (fontInStream != null) {
                try {
                    fontInStream.close();
                } catch (Exception e) {
                }
            }
        }
        fontInStream = null;
        String fontResourceProductBold = "/de/mendelson/util/font/Square721BoldExtendedBT.ttf";
        try {
            fontInStream = FontUtil.class.getResourceAsStream(fontResourceProductBold);
            fontProductBold = Font.createFont(Font.TRUETYPE_FONT, fontInStream);
        } catch (Exception e) {
            fontProductBold = new Font("SansSerif", Font.BOLD, 18);
        } finally {
            if (fontInStream != null) {
                try {
                    fontInStream.close();
                } catch (Exception e) {
                }
            }
        }
        fontInStream = null;
        String fontResourcePlain = "/de/mendelson/util/font/Square721BTRoman.ttf";
        try {
            fontInStream = FontUtil.class.getResourceAsStream(fontResourcePlain);
            fontPlain = Font.createFont(Font.TRUETYPE_FONT, fontInStream);
        } catch (Exception e) {
            fontPlain = new Font("SansSerif", Font.PLAIN, 18);
        } finally {
            if (fontInStream != null) {
                try {
                    fontInStream.close();
                } catch (Exception e) {
                }
            }
        }
        fontInStream = null;
        String fontResourceBold = "/de/mendelson/util/font/Square721DMNormal.ttf";
        try {
            fontInStream = FontUtil.class.getResourceAsStream(fontResourceBold);
            fontBold = Font.createFont(Font.TRUETYPE_FONT, fontInStream);
        } catch (Exception e) {
            fontBold = new Font("SansSerif", Font.PLAIN, 18);
        } finally {
            if (fontInStream != null) {
                try {
                    fontInStream.close();
                } catch (Exception e) {
                }
            }
        }
        fontInStream = null;
        String fontResourceLogo = "/de/mendelson/util/font/NewDetroitRegular.ttf";
        try {
            fontInStream = FontUtil.class.getResourceAsStream(fontResourceLogo);
            fontLogo = Font.createFont(Font.TRUETYPE_FONT, fontInStream);
        } catch (Exception e) {
            fontBold = new Font("SansSerif", Font.PLAIN, 18);
        } finally {
            if (fontInStream != null) {
                try {
                    fontInStream.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     *
     * @param PRODUCT One of PRODUCT_MBI, [..]
     * @return
     */
    public static Color getFontColor(final String PRODUCT) {
        if (PRODUCT.equals(PRODUCT_MBI)
                || PRODUCT.equals(PRODUCT_EDIPAD)) {
            return (Color.decode("#577076"));
        } else if (PRODUCT.equals(PRODUCT_AS2)
                || PRODUCT.equals(PRODUCT_OFTP2)
                || PRODUCT.equals(PRODUCT_AS4)) {
            return (Color.decode("#274B18"));
        } else if (PRODUCT.equals(PRODUCT_CONVERTER_IDE)) {
            return (Color.decode("#897C69"));
        } else if (PRODUCT.equals(PRODUCT_AS4_COMMUNITY)
                || PRODUCT.equals(PRODUCT_AS2_COMMUNITY)
                || PRODUCT.equals(PRODUCT_OFTP2_COMMUNITY)) {
            return (Color.decode("#8E8E8E"));
        }
        return (Color.decode("#577076"));
    }

    /**
     * Returns the default product font
     *
     * @param STYLE one of FontUtil.STYLE_PLAIN, FontUtil.STYPE_BOLD
     * @param size
     */
    public static Font getProductFont(final String STYLE, int size) {
        if (!STYLE.equals(STYLE_PRODUCT_PLAIN)
                && !STYLE.equals(STYLE_PRODUCT_BOLD)
                && !STYLE.equals(STYLE_PLAIN)
                && !STYLE.equals(STYLE_BOLD)
                && !STYLE.equals(STYLE_LOGO)) {
            throw new IllegalArgumentException("FontUtil.getProductFont(): Unknown font style " + STYLE);
        }
        if (STYLE.equals(STYLE_PRODUCT_PLAIN)) {
            return (fontProductPlain.deriveFont((float) size));
        } else if (STYLE.equals(STYLE_PRODUCT_BOLD)) {
            return (fontProductBold.deriveFont((float) size));
        } else if (STYLE.equals(STYLE_BOLD)) {
            return (fontBold.deriveFont((float) size));
        }else if (STYLE.equals(STYLE_LOGO)) {
            return (fontLogo.deriveFont(Font.PLAIN, (float) size));
        } else {
            return (fontPlain.deriveFont((float) size));
        }

    }
}
