//$Header: /oftp2/de/mendelson/util/log/ANSI.java 7     13/05/22 16:28 Heller $
package de.mendelson.util.log;

import java.awt.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * ANSI codes for colors and text attributes to use in the log. To create
 * messages that are colorized in using ANSI sequences just add these constants
 * to the data, like
 * <pre>
 * String myMessage = ANSI.COLOR_RED + "Error: " + ANSI.RESET + "An error occured.";
 * </pre> 
 *
 * Colors COLOR_SYSTEM_xxx should be supported in all ANSI supporting devices
 * Colors COLOR_SYSTEM_xxx_BRIGHT are the first extension and might supported in
 * ANSI supporting devices
 * All other colors are the 256 color extension, the related CSS3 names are for example available
 * via https://www.farb-tabelle.de
 *
 *
 * @author S.Heller
 * @version $Revision: 7 $
 */
public class ANSI {

    /**
     * Control Sequence Inducer (CSI)
     */
    public final static String CSI = "\u001B[";

    /**
     * All Attributes reset
     */
    public static final String RESET = CSI + "0m";
    /**
     * Font attribute BOLD
     */
    public static final String BOLD = CSI + "1m";
    /**
     * Font attribute FAINT
     */
    public static final String FAINT = CSI + "2m";
    /**
     * Font attribute ITALIC
     */
    public static final String ITALIC = CSI + "3m";
    /**
     * Font attribute UNDERLINE
     */
    public static final String UNDERLINE = CSI + "4m";

    /**
     * default terminal color
     */
    public static final String COLOR_SYSTEM_BLACK = CSI + "0;30m";
    /**
     * default terminal color
     */
    public static final String COLOR_SYSTEM_RED = CSI + "0;31m";
    /**
     * default terminal color
     */
    public static final String COLOR_SYSTEM_GREEN = CSI + "0;32m";
    /**
     * default terminal color
     */
    public static final String COLOR_SYSTEM_YELLOW = CSI + "0;33m";
    /**
     * default terminal color
     */
    public static final String COLOR_SYSTEM_BLUE = CSI + "0;34m";
    /**
     * default terminal color
     */
    public static final String COLOR_SYSTEM_PURPLE = CSI + "0;35m";
    /**
     * default terminal color
     */
    public static final String COLOR_SYSTEM_CYAN = CSI + "0;36m";
    /**
     * default terminal color
     */
    public static final String COLOR_SYSTEM_GREY = CSI + "0;37m";
    /**
     * default terminal color (bright)
     */
    public static final String COLOR_SYSTEM_GREY_BRIGHT = CSI + "0;90m";
    /**
     * default terminal color (bright)
     */
    public static final String COLOR_SYSTEM_RED_BRIGHT = CSI + "0;91m";
    /**
     * default terminal color (bright)
     */
    public static final String COLOR_SYSTEM_GREEN_BRIGHT = CSI + "0;92m";
    /**
     * default terminal color (bright)
     */
    public static final String COLOR_SYSTEM_YELLOW_BRIGHT = CSI + "0;93m";
    /**
     * default terminal color (bright)
     */
    public static final String COLOR_SYSTEM_BLUE_BRIGHT = CSI + "0;94m";
    /**
     * default terminal color (bright)
     */
    public static final String COLOR_SYSTEM_PURPLE_BRIGHT = CSI + "0;95m";
    /**
     * default terminal color (bright)
     */
    public static final String COLOR_SYSTEM_CYAN_BRIGHT = CSI + "0;96m";
    /**
     * default terminal color (bright)
     */
    public static final String COLOR_SYSTEM_WHITE_BRIGHT = CSI + "0;97m";
    /**
     * Extended 256bit color, names are taken from CSS3 Color names.
     */
    public static final String COLOR_GREY0 = CSI + "38;5;16m";
    public static final String COLOR_NAVYBLUE = CSI + "38;5;17m";
    public static final String COLOR_BLUE4 = CSI + "38;5;18m";
    public static final String COLOR_BLUE3_1 = CSI + "38;5;19m";
    public static final String COLOR_BLUE3_2 = CSI + "38;5;20m";
    public static final String COLOR_BLUE1 = CSI + "38;5;21m";
    public static final String COLOR_DARKGREEN = CSI + "38;5;22m";
    public static final String COLOR_DEEPSKYBLUE4_1 = CSI + "38;5;23m";
    public static final String COLOR_DEEPSKYBLUE4_2 = CSI + "38;5;24m";
    public static final String COLOR_DEEPSKYBLUE4_3 = CSI + "38;5;25m";
    public static final String COLOR_DODGERBLUE3 = CSI + "38;5;26m";
    public static final String COLOR_DODGERBLUE2 = CSI + "38;5;27m";
    public static final String COLOR_GREEN4 = CSI + "38;5;28m";
    public static final String COLOR_SPRINGGREEN4 = CSI + "38;5;29m";
    public static final String COLOR_TURQUOISE4 = CSI + "38;5;30m";
    public static final String COLOR_DEEPSKYBLUE3_1 = CSI + "38;5;31m";
    public static final String COLOR_DEEPSKYBLUE3_2 = CSI + "38;5;32m";
    public static final String COLOR_DODGERBLUE1 = CSI + "38;5;33m";
    public static final String COLOR_GREEN3_1 = CSI + "38;5;34m";
    public static final String COLOR_SPRINGGREEN3_1 = CSI + "38;5;35m";
    public static final String COLOR_CYAN4 = CSI + "38;5;36m";
    public static final String COLOR_LIGHTSEAGREEN = CSI + "38;5;37m";
    public static final String COLOR_DEEPSKYBLUE2 = CSI + "38;5;38m";
    public static final String COLOR_DEEPSKYBLUE1 = CSI + "38;5;39m";
    public static final String COLOR_GREEN3_2 = CSI + "38;5;40m";
    public static final String COLOR_SPRINGGREEN3_2 = CSI + "38;5;41m";
    public static final String COLOR_SPRINGGREEN2_1 = CSI + "38;5;42m";
    public static final String COLOR_CYAN3 = CSI + "38;5;43m";
    public static final String COLOR_DARKTURQUOISE = CSI + "38;5;44m";
    public static final String COLOR_TURQUOISE2 = CSI + "38;5;45m";
    public static final String COLOR_GREEN1 = CSI + "38;5;46m";
    public static final String COLOR_SPRINGGREEN2_2 = CSI + "38;5;47m";
    public static final String COLOR_SPRINGGREEN1 = CSI + "38;5;48m";
    public static final String COLOR_MEDIUMSPRINGGREEN = CSI + "38;5;49m";
    public static final String COLOR_CYAN2 = CSI + "38;5;50m";
    public static final String COLOR_CYAN1 = CSI + "38;5;51m";
    public static final String COLOR_RED4_1 = CSI + "38;5;52m";
    public static final String COLOR_DEEPPINK4_1 = CSI + "38;5;53m";
    public static final String COLOR_PURPLE4_1 = CSI + "38;5;54m";
    public static final String COLOR_PURPLE4_2 = CSI + "38;5;55m";
    public static final String COLOR_PURPLE3 = CSI + "38;5;56m";
    public static final String COLOR_BLUEVIOLET = CSI + "38;5;57m";
    public static final String COLOR_ORANGE4_1 = CSI + "38;5;58m";
    public static final String COLOR_GREY37 = CSI + "38;5;59m";
    public static final String COLOR_MEDIUMPURPLE4 = CSI + "38;5;60m";
    public static final String COLOR_SLATEBLUE3_1 = CSI + "38;5;61m";
    public static final String COLOR_SLATEBLUE3_2 = CSI + "38;5;62m";
    public static final String COLOR_ROYALBLUE1 = CSI + "38;5;63m";
    public static final String COLOR_CHARTREUSE4 = CSI + "38;5;64m";
    public static final String COLOR_DARKSEAGREEN4_1 = CSI + "38;5;65m";
    public static final String COLOR_PALETURQUOISE4 = CSI + "38;5;66m";
    public static final String COLOR_STEELBLUE = CSI + "38;5;67m";
    public static final String COLOR_STEELBLUE3 = CSI + "38;5;68m";
    public static final String COLOR_CORNFLOWERBLUE = CSI + "38;5;69m";
    public static final String COLOR_CHARTREUSE3_1 = CSI + "38;5;70m";
    public static final String COLOR_DARKSEAGREEN4_2 = CSI + "38;5;71m";
    public static final String COLOR_CADETBLUE_1 = CSI + "38;5;72m";
    public static final String COLOR_CADETBLUE_2 = CSI + "38;5;73m";
    public static final String COLOR_SKYBLUE3 = CSI + "38;5;74m";
    public static final String COLOR_STEELBLUE1_1 = CSI + "38;5;75m";
    public static final String COLOR_CHARTREUSE3_2 = CSI + "38;5;76m";
    public static final String COLOR_PALEGREEN3_1 = CSI + "38;5;77m";
    public static final String COLOR_SEAGREEN3 = CSI + "38;5;78m";
    public static final String COLOR_AQUAMARINE3 = CSI + "38;5;79m";
    public static final String COLOR_MEDIUMTURQUOISE = CSI + "38;5;80m";
    public static final String COLOR_STEELBLUE1_2 = CSI + "38;5;81m";
    public static final String COLOR_CHARTREUSE2_1 = CSI + "38;5;82m";
    public static final String COLOR_SEAGREEN2 = CSI + "38;5;83m";
    public static final String COLOR_SEAGREEN1_1 = CSI + "38;5;84m";
    public static final String COLOR_SEAGREEN1_2 = CSI + "38;5;85m";
    public static final String COLOR_AQUAMARINE1_1 = CSI + "38;5;86m";
    public static final String COLOR_DARKSLATEGRAY2 = CSI + "38;5;87m";
    public static final String COLOR_RED4_2 = CSI + "38;5;88m";
    public static final String COLOR_DEEPPINK4_2 = CSI + "38;5;89m";
    public static final String COLOR_MAGENTA4_1 = CSI + "38;5;90m";
    public static final String COLOR_MAGENTA4_2 = CSI + "38;5;91m";
    public static final String COLOR_DARKVIOLET_1 = CSI + "38;5;92m";
    public static final String COLOR_PURPLE_1 = CSI + "38;5;93m";
    public static final String COLOR_ORANGE4_2 = CSI + "38;5;94m";
    public static final String COLOR_LIGHTPINK4 = CSI + "38;5;95m";
    public static final String COLOR_PLUM4 = CSI + "38;5;96m";
    public static final String COLOR_MEDIUMPURPLE3_1 = CSI + "38;5;97m";
    public static final String COLOR_MEDIUMPURPLE3_2 = CSI + "38;5;98m";
    public static final String COLOR_SLATEBLUE1 = CSI + "38;5;99m";
    public static final String COLOR_YELLOW4_1 = CSI + "38;5;100m";
    public static final String COLOR_WHEAT4 = CSI + "38;5;101m";
    public static final String COLOR_GREY53 = CSI + "38;5;102m";
    public static final String COLOR_LIGHTSLATEGREY = CSI + "38;5;103m";
    public static final String COLOR_MEDIUMPURPLE = CSI + "38;5;104m";
    public static final String COLOR_LIGHTSLATEBLUE = CSI + "38;5;105m";
    public static final String COLOR_YELLOW4_2 = CSI + "38;5;106m";
    public static final String COLOR_DARKOLIVEGREEN3_1 = CSI + "38;5;107m";
    public static final String COLOR_DARKSEAGREEN = CSI + "38;5;108m";
    public static final String COLOR_LIGHTSKYBLUE3_1 = CSI + "38;5;109m";
    public static final String COLOR_LIGHTSKYBLUE3_2 = CSI + "38;5;110m";
    public static final String COLOR_SKYBLUE2 = CSI + "38;5;111m";
    public static final String COLOR_CHARTREUSE2_2 = CSI + "38;5;112m";
    public static final String COLOR_DARKOLIVEGREEN3_2 = CSI + "38;5;113m";
    public static final String COLOR_PALEGREEN3_2 = CSI + "38;5;114m";
    public static final String COLOR_DARKSEAGREEN3_1 = CSI + "38;5;115m";
    public static final String COLOR_DARKSLATEGRAY3 = CSI + "38;5;116m";
    public static final String COLOR_SKYBLUE1 = CSI + "38;5;117m";
    public static final String COLOR_CHARTREUSE1 = CSI + "38;5;118m";
    public static final String COLOR_LIGHTGREEN_1 = CSI + "38;5;119m";
    public static final String COLOR_LIGHTGREEN_2 = CSI + "38;5;120m";
    public static final String COLOR_PALEGREEN1_1 = CSI + "38;5;121m";
    public static final String COLOR_AQUAMARINE1_2 = CSI + "38;5;122m";
    public static final String COLOR_DARKSLATEGRAY1 = CSI + "38;5;123m";
    public static final String COLOR_RED3_1 = CSI + "38;5;124m";
    public static final String COLOR_DEEPPINK4_3 = CSI + "38;5;125m";
    public static final String COLOR_MEDIUMVIOLETRED = CSI + "38;5;126m";
    public static final String COLOR_MAGENTA3_1 = CSI + "38;5;127m";
    public static final String COLOR_DARKVIOLET_2 = CSI + "38;5;128m";
    public static final String COLOR_PURPLE_2 = CSI + "38;5;129m";
    public static final String COLOR_DARKORANGE3_1 = CSI + "38;5;130m";
    public static final String COLOR_INDIANRED_1 = CSI + "38;5;131m";
    public static final String COLOR_HOTPINK3_1 = CSI + "38;5;132m";
    public static final String COLOR_MEDIUMORCHID3 = CSI + "38;5;133m";
    public static final String COLOR_MEDIUMORCHID = CSI + "38;5;134m";
    public static final String COLOR_MEDIUMPURPLE2_1 = CSI + "38;5;135m";
    public static final String COLOR_DARKGOLDENROD = CSI + "38;5;136m";
    public static final String COLOR_LIGHTSALMON3_1 = CSI + "38;5;137m";
    public static final String COLOR_ROSYBROWN = CSI + "38;5;138m";
    public static final String COLOR_GREY63 = CSI + "38;5;139m";
    public static final String COLOR_MEDIUMPURPLE2_2 = CSI + "38;5;140m";
    public static final String COLOR_MEDIUMPURPLE1 = CSI + "38;5;141m";
    public static final String COLOR_GOLD3_1 = CSI + "38;5;142m";
    public static final String COLOR_DARKKHAKI = CSI + "38;5;143m";
    public static final String COLOR_NAVAJOWHITE3 = CSI + "38;5;144m";
    public static final String COLOR_GREY69 = CSI + "38;5;145m";
    public static final String COLOR_LIGHTSTEELBLUE3 = CSI + "38;5;146m";
    public static final String COLOR_LIGHTSTEELBLUE = CSI + "38;5;147m";
    public static final String COLOR_YELLOW3_1 = CSI + "38;5;148m";
    public static final String COLOR_DARKOLIVEGREEN3_3 = CSI + "38;5;149m";
    public static final String COLOR_DARKSEAGREEN3_2 = CSI + "38;5;150m";
    public static final String COLOR_DARKSEAGREEN2_1 = CSI + "38;5;151m";
    public static final String COLOR_LIGHTCYAN3 = CSI + "38;5;152m";
    public static final String COLOR_LIGHTSKYBLUE1 = CSI + "38;5;153m";
    public static final String COLOR_GREENYELLOW = CSI + "38;5;154m";
    public static final String COLOR_DARKOLIVEGREEN2 = CSI + "38;5;155m";
    public static final String COLOR_PALEGREEN1_2 = CSI + "38;5;156m";
    public static final String COLOR_DARKSEAGREEN2_2 = CSI + "38;5;157m";
    public static final String COLOR_DARKSEAGREEN1_1 = CSI + "38;5;158m";
    public static final String COLOR_PALETURQUOISE1 = CSI + "38;5;159m";
    public static final String COLOR_RED3_2 = CSI + "38;5;160m";
    public static final String COLOR_DEEPPINK3_1 = CSI + "38;5;161m";
    public static final String COLOR_DEEPPINK3_2 = CSI + "38;5;162m";
    public static final String COLOR_MAGENTA3_2 = CSI + "38;5;163m";
    public static final String COLOR_MAGENTA3_3 = CSI + "38;5;164m";
    public static final String COLOR_MAGENTA2_1 = CSI + "38;5;165m";
    public static final String COLOR_DARKORANGE3_2 = CSI + "38;5;166m";
    public static final String COLOR_INDIANRED_2 = CSI + "38;5;167m";
    public static final String COLOR_HOTPINK3_2 = CSI + "38;5;168m";
    public static final String COLOR_HOTPINK2 = CSI + "38;5;169m";
    public static final String COLOR_ORCHID = CSI + "38;5;170m";
    public static final String COLOR_MEDIUMORCHID1_1 = CSI + "38;5;171m";
    public static final String COLOR_ORANGE3 = CSI + "38;5;172m";
    public static final String COLOR_LIGHTSALMON3_2 = CSI + "38;5;173m";
    public static final String COLOR_LIGHTPINK3 = CSI + "38;5;174m";
    public static final String COLOR_PINK3 = CSI + "38;5;175m";
    public static final String COLOR_PLUM3 = CSI + "38;5;176m";
    public static final String COLOR_VIOLET = CSI + "38;5;177m";
    public static final String COLOR_GOLD3_2 = CSI + "38;5;178m";
    public static final String COLOR_LIGHTGOLDENROD3 = CSI + "38;5;179m";
    public static final String COLOR_TAN = CSI + "38;5;180m";
    public static final String COLOR_MISTYROSE3 = CSI + "38;5;181m";
    public static final String COLOR_THISTLE3 = CSI + "38;5;182m";
    public static final String COLOR_PLUM2 = CSI + "38;5;183m";
    public static final String COLOR_YELLOW3_2 = CSI + "38;5;184m";
    public static final String COLOR_KHAKI3 = CSI + "38;5;185m";
    public static final String COLOR_LIGHTGOLDENROD2_1 = CSI + "38;5;186m";
    public static final String COLOR_LIGHTYELLOW3 = CSI + "38;5;187m";
    public static final String COLOR_GREY84 = CSI + "38;5;188m";
    public static final String COLOR_LIGHTSTEELBLUE1 = CSI + "38;5;189m";
    public static final String COLOR_YELLOW2 = CSI + "38;5;190m";
    public static final String COLOR_DARKOLIVEGREEN1_1 = CSI + "38;5;191m";
    public static final String COLOR_DARKOLIVEGREEN1_2 = CSI + "38;5;192m";
    public static final String COLOR_DARKSEAGREEN1_2 = CSI + "38;5;193m";
    public static final String COLOR_HONEYDEW2 = CSI + "38;5;194m";
    public static final String COLOR_LIGHTCYAN1 = CSI + "38;5;195m";
    public static final String COLOR_RED1 = CSI + "38;5;196m";
    public static final String COLOR_DEEPPINK2 = CSI + "38;5;197m";
    public static final String COLOR_DEEPPINK1_1 = CSI + "38;5;198m";
    public static final String COLOR_DEEPPINK1_2 = CSI + "38;5;199m";
    public static final String COLOR_MAGENTA2_2 = CSI + "38;5;200m";
    public static final String COLOR_MAGENTA1 = CSI + "38;5;201m";
    public static final String COLOR_ORANGERED1 = CSI + "38;5;202m";
    public static final String COLOR_INDIANRED1_1 = CSI + "38;5;203m";
    public static final String COLOR_INDIANRED1_2 = CSI + "38;5;204m";
    public static final String COLOR_HOTPINK_1 = CSI + "38;5;205m";
    public static final String COLOR_HOTPINK_2 = CSI + "38;5;206m";
    public static final String COLOR_MEDIUMORCHID1_2 = CSI + "38;5;207m";
    public static final String COLOR_DARKORANGE = CSI + "38;5;208m";
    public static final String COLOR_SALMON1 = CSI + "38;5;209m";
    public static final String COLOR_LIGHTCORAL = CSI + "38;5;210m";
    public static final String COLOR_PALEVIOLETRED1 = CSI + "38;5;211m";
    public static final String COLOR_ORCHID2 = CSI + "38;5;212m";
    public static final String COLOR_ORCHID1 = CSI + "38;5;213m";
    public static final String COLOR_ORANGE1 = CSI + "38;5;214m";
    public static final String COLOR_SANDYBROWN = CSI + "38;5;215m";
    public static final String COLOR_LIGHTSALMON1 = CSI + "38;5;216m";
    public static final String COLOR_LIGHTPINK1 = CSI + "38;5;217m";
    public static final String COLOR_PINK1 = CSI + "38;5;218m";
    public static final String COLOR_PLUM1 = CSI + "38;5;219m";
    public static final String COLOR_GOLD1 = CSI + "38;5;220m";
    public static final String COLOR_LIGHTGOLDENROD2_2 = CSI + "38;5;221m";
    public static final String COLOR_LIGHTGOLDENROD2_3 = CSI + "38;5;222m";
    public static final String COLOR_NAVAJOWHITE1 = CSI + "38;5;223m";
    public static final String COLOR_MISTYROSE1 = CSI + "38;5;224m";
    public static final String COLOR_THISTLE1 = CSI + "38;5;225m";
    public static final String COLOR_YELLOW1 = CSI + "38;5;226m";
    public static final String COLOR_LIGHTGOLDENROD1 = CSI + "38;5;227m";
    public static final String COLOR_KHAKI1 = CSI + "38;5;228m";
    public static final String COLOR_WHEAT1 = CSI + "38;5;229m";
    public static final String COLOR_CORNSILK1 = CSI + "38;5;230m";
    public static final String COLOR_GREY100 = CSI + "38;5;231m";
    public static final String COLOR_GREY3 = CSI + "38;5;232m";
    public static final String COLOR_GREY7 = CSI + "38;5;233m";
    public static final String COLOR_GREY11 = CSI + "38;5;234m";
    public static final String COLOR_GREY15 = CSI + "38;5;235m";
    public static final String COLOR_GREY19 = CSI + "38;5;236m";
    public static final String COLOR_GREY23 = CSI + "38;5;237m";
    public static final String COLOR_GREY27 = CSI + "38;5;238m";
    public static final String COLOR_GREY30 = CSI + "38;5;239m";
    public static final String COLOR_GREY35 = CSI + "38;5;240m";
    public static final String COLOR_GREY39 = CSI + "38;5;241m";
    public static final String COLOR_GREY42 = CSI + "38;5;242m";
    public static final String COLOR_GREY46 = CSI + "38;5;243m";
    public static final String COLOR_GREY50 = CSI + "38;5;244m";
    public static final String COLOR_GREY54 = CSI + "38;5;245m";
    public static final String COLOR_GREY58 = CSI + "38;5;246m";
    public static final String COLOR_GREY62 = CSI + "38;5;247m";
    public static final String COLOR_GREY66 = CSI + "38;5;248m";
    public static final String COLOR_GREY70 = CSI + "38;5;249m";
    public static final String COLOR_GREY74 = CSI + "38;5;250m";
    public static final String COLOR_GREY78 = CSI + "38;5;251m";
    public static final String COLOR_GREY82 = CSI + "38;5;252m";
    public static final String COLOR_GREY85 = CSI + "38;5;253m";
    public static final String COLOR_GREY89 = CSI + "38;5;254m";
    public static final String COLOR_LIGHTGREY = CSI + "38;5;255m";

    private final static Map<String, Color> COLOR_MAP = new ConcurrentHashMap<String, Color>();

    static {
        //basic system colors
        COLOR_MAP.put(COLOR_SYSTEM_BLACK, Color.decode("#000000"));
        COLOR_MAP.put(COLOR_SYSTEM_RED, Color.decode("#800000"));
        COLOR_MAP.put(COLOR_SYSTEM_GREEN, Color.decode("#008000"));
        COLOR_MAP.put(COLOR_SYSTEM_YELLOW, Color.decode("#808000"));
        COLOR_MAP.put(COLOR_SYSTEM_BLUE, Color.decode("#000080"));
        COLOR_MAP.put(COLOR_SYSTEM_PURPLE, Color.decode("#800080"));
        COLOR_MAP.put(COLOR_SYSTEM_CYAN, Color.decode("#008080"));
        COLOR_MAP.put(COLOR_SYSTEM_GREY, Color.decode("#c0c0c0"));
        //extended system colors
        COLOR_MAP.put(COLOR_SYSTEM_GREY_BRIGHT, Color.decode("#808080"));
        COLOR_MAP.put(COLOR_SYSTEM_RED_BRIGHT, Color.decode("#FF0000"));
        COLOR_MAP.put(COLOR_SYSTEM_GREEN_BRIGHT, Color.decode("#00FF00"));
        COLOR_MAP.put(COLOR_SYSTEM_YELLOW_BRIGHT, Color.decode("#FFFF00"));
        COLOR_MAP.put(COLOR_SYSTEM_BLUE_BRIGHT, Color.decode("#0000FF"));
        COLOR_MAP.put(COLOR_SYSTEM_PURPLE_BRIGHT, Color.decode("#FF00FF"));
        COLOR_MAP.put(COLOR_SYSTEM_CYAN_BRIGHT, Color.decode("#00FFFF"));
        COLOR_MAP.put(COLOR_SYSTEM_WHITE_BRIGHT, Color.decode("#FFFFFF"));
        //additional 256bit colors, the constants are also used in CSS
        COLOR_MAP.put(COLOR_GREY0, Color.decode("#000000"));
        COLOR_MAP.put(COLOR_NAVYBLUE, Color.decode("#00005F"));
        COLOR_MAP.put(COLOR_BLUE4, Color.decode("#000087"));
        COLOR_MAP.put(COLOR_BLUE3_1, Color.decode("#0000AF"));
        COLOR_MAP.put(COLOR_BLUE3_2, Color.decode("#0000D7"));
        COLOR_MAP.put(COLOR_BLUE1, Color.decode("#0000FF"));
        COLOR_MAP.put(COLOR_DARKGREEN, Color.decode("#005F00"));
        COLOR_MAP.put(COLOR_DEEPSKYBLUE4_1, Color.decode("#005F5F"));
        COLOR_MAP.put(COLOR_DEEPSKYBLUE4_2, Color.decode("#005F87"));
        COLOR_MAP.put(COLOR_DEEPSKYBLUE4_3, Color.decode("#005FAF"));
        COLOR_MAP.put(COLOR_DODGERBLUE3, Color.decode("#005FD7"));
        COLOR_MAP.put(COLOR_DODGERBLUE2, Color.decode("#005FFF"));
        COLOR_MAP.put(COLOR_GREEN4, Color.decode("#008700"));
        COLOR_MAP.put(COLOR_SPRINGGREEN4, Color.decode("#00875F"));
        COLOR_MAP.put(COLOR_TURQUOISE4, Color.decode("#008787"));
        COLOR_MAP.put(COLOR_DEEPSKYBLUE3_1, Color.decode("#0087AF"));
        COLOR_MAP.put(COLOR_DEEPSKYBLUE3_2, Color.decode("#0087D7"));
        COLOR_MAP.put(COLOR_DODGERBLUE1, Color.decode("#0087FF"));
        COLOR_MAP.put(COLOR_GREEN3_1, Color.decode("#00AF00"));
        COLOR_MAP.put(COLOR_SPRINGGREEN3_1, Color.decode("#00AF5F"));
        COLOR_MAP.put(COLOR_CYAN4, Color.decode("#00AF87"));
        COLOR_MAP.put(COLOR_LIGHTSEAGREEN, Color.decode("#00AFAF"));
        COLOR_MAP.put(COLOR_DEEPSKYBLUE2, Color.decode("#00AFD7"));
        COLOR_MAP.put(COLOR_DEEPSKYBLUE1, Color.decode("#00AFFF"));
        COLOR_MAP.put(COLOR_GREEN3_2, Color.decode("#00D700"));
        COLOR_MAP.put(COLOR_SPRINGGREEN3_2, Color.decode("#00D75F"));
        COLOR_MAP.put(COLOR_SPRINGGREEN2_1, Color.decode("#00D787"));
        COLOR_MAP.put(COLOR_CYAN3, Color.decode("#00D7AF"));
        COLOR_MAP.put(COLOR_DARKTURQUOISE, Color.decode("#00D7D7"));
        COLOR_MAP.put(COLOR_TURQUOISE2, Color.decode("#00D7FF"));
        COLOR_MAP.put(COLOR_GREEN1, Color.decode("#00FF00"));
        COLOR_MAP.put(COLOR_SPRINGGREEN2_2, Color.decode("#00FF5F"));
        COLOR_MAP.put(COLOR_SPRINGGREEN1, Color.decode("#00FF87"));
        COLOR_MAP.put(COLOR_MEDIUMSPRINGGREEN, Color.decode("#00FFAF"));
        COLOR_MAP.put(COLOR_CYAN2, Color.decode("#00FFD7"));
        COLOR_MAP.put(COLOR_CYAN1, Color.decode("#00FFFF"));
        COLOR_MAP.put(COLOR_RED4_1, Color.decode("#5F0000"));
        COLOR_MAP.put(COLOR_DEEPPINK4_1, Color.decode("#5F005F"));
        COLOR_MAP.put(COLOR_PURPLE4_1, Color.decode("#5F0087"));
        COLOR_MAP.put(COLOR_PURPLE4_2, Color.decode("#5F00AF"));
        COLOR_MAP.put(COLOR_PURPLE3, Color.decode("#5F00D7"));
        COLOR_MAP.put(COLOR_BLUEVIOLET, Color.decode("#5F00FF"));
        COLOR_MAP.put(COLOR_ORANGE4_1, Color.decode("#5F5F00"));
        COLOR_MAP.put(COLOR_GREY37, Color.decode("#5F5F5F"));
        COLOR_MAP.put(COLOR_MEDIUMPURPLE4, Color.decode("#5F5F87"));
        COLOR_MAP.put(COLOR_SLATEBLUE3_1, Color.decode("#5F5FAF"));
        COLOR_MAP.put(COLOR_SLATEBLUE3_2, Color.decode("#5F5FD7"));
        COLOR_MAP.put(COLOR_ROYALBLUE1, Color.decode("#5F5FFF"));
        COLOR_MAP.put(COLOR_CHARTREUSE4, Color.decode("#5F8700"));
        COLOR_MAP.put(COLOR_DARKSEAGREEN4_1, Color.decode("#5F875F"));
        COLOR_MAP.put(COLOR_PALETURQUOISE4, Color.decode("#5F8787"));
        COLOR_MAP.put(COLOR_STEELBLUE, Color.decode("#5F87AF"));
        COLOR_MAP.put(COLOR_STEELBLUE3, Color.decode("#5F87D7"));
        COLOR_MAP.put(COLOR_CORNFLOWERBLUE, Color.decode("#5F87FF"));
        COLOR_MAP.put(COLOR_CHARTREUSE3_1, Color.decode("#5FAF00"));
        COLOR_MAP.put(COLOR_DARKSEAGREEN4_2, Color.decode("#5FAF5F"));
        COLOR_MAP.put(COLOR_CADETBLUE_1, Color.decode("#5FAF87"));
        COLOR_MAP.put(COLOR_CADETBLUE_2, Color.decode("#5FAFAF"));
        COLOR_MAP.put(COLOR_SKYBLUE3, Color.decode("#5FAFD7"));
        COLOR_MAP.put(COLOR_STEELBLUE1_1, Color.decode("#5FAFFF"));
        COLOR_MAP.put(COLOR_CHARTREUSE3_2, Color.decode("#5FD700"));
        COLOR_MAP.put(COLOR_PALEGREEN3_1, Color.decode("#5FD75F"));
        COLOR_MAP.put(COLOR_SEAGREEN3, Color.decode("#5FD787"));
        COLOR_MAP.put(COLOR_AQUAMARINE3, Color.decode("#5FD7AF"));
        COLOR_MAP.put(COLOR_MEDIUMTURQUOISE, Color.decode("#5FD7D7"));
        COLOR_MAP.put(COLOR_STEELBLUE1_2, Color.decode("#5FD7FF"));
        COLOR_MAP.put(COLOR_CHARTREUSE2_1, Color.decode("#5FFF00"));
        COLOR_MAP.put(COLOR_SEAGREEN2, Color.decode("#5FFF5F"));
        COLOR_MAP.put(COLOR_SEAGREEN1_1, Color.decode("#5FFF87"));
        COLOR_MAP.put(COLOR_SEAGREEN1_2, Color.decode("#5FFFAF"));
        COLOR_MAP.put(COLOR_AQUAMARINE1_1, Color.decode("#5FFFD7"));
        COLOR_MAP.put(COLOR_DARKSLATEGRAY2, Color.decode("#5FFFFF"));
        COLOR_MAP.put(COLOR_RED4_2, Color.decode("#870000"));
        COLOR_MAP.put(COLOR_DEEPPINK4_2, Color.decode("#87005F"));
        COLOR_MAP.put(COLOR_MAGENTA4_1, Color.decode("#870087"));
        COLOR_MAP.put(COLOR_MAGENTA4_2, Color.decode("#8700AF"));
        COLOR_MAP.put(COLOR_DARKVIOLET_1, Color.decode("#8700D7"));
        COLOR_MAP.put(COLOR_PURPLE_1, Color.decode("#8700FF"));
        COLOR_MAP.put(COLOR_ORANGE4_2, Color.decode("#875F00"));
        COLOR_MAP.put(COLOR_LIGHTPINK4, Color.decode("#875F5F"));
        COLOR_MAP.put(COLOR_PLUM4, Color.decode("#875F87"));
        COLOR_MAP.put(COLOR_MEDIUMPURPLE3_1, Color.decode("#875FAF"));
        COLOR_MAP.put(COLOR_MEDIUMPURPLE3_2, Color.decode("#875FD7"));
        COLOR_MAP.put(COLOR_SLATEBLUE1, Color.decode("#875FFF"));
        COLOR_MAP.put(COLOR_YELLOW4_1, Color.decode("#878700"));
        COLOR_MAP.put(COLOR_WHEAT4, Color.decode("#87875F"));
        COLOR_MAP.put(COLOR_GREY53, Color.decode("#878787"));
        COLOR_MAP.put(COLOR_LIGHTSLATEGREY, Color.decode("#8787AF"));
        COLOR_MAP.put(COLOR_MEDIUMPURPLE, Color.decode("#8787D7"));
        COLOR_MAP.put(COLOR_LIGHTSLATEBLUE, Color.decode("#8787FF"));
        COLOR_MAP.put(COLOR_YELLOW4_2, Color.decode("#87AF00"));
        COLOR_MAP.put(COLOR_DARKOLIVEGREEN3_1, Color.decode("#87AF5F"));
        COLOR_MAP.put(COLOR_DARKSEAGREEN, Color.decode("#87AF87"));
        COLOR_MAP.put(COLOR_LIGHTSKYBLUE3_1, Color.decode("#87AFAF"));
        COLOR_MAP.put(COLOR_LIGHTSKYBLUE3_2, Color.decode("#87AFD7"));
        COLOR_MAP.put(COLOR_SKYBLUE2, Color.decode("#87AFFF"));
        COLOR_MAP.put(COLOR_CHARTREUSE2_2, Color.decode("#87D700"));
        COLOR_MAP.put(COLOR_DARKOLIVEGREEN3_2, Color.decode("#87D75F"));
        COLOR_MAP.put(COLOR_PALEGREEN3_2, Color.decode("#87D787"));
        COLOR_MAP.put(COLOR_DARKSEAGREEN3_1, Color.decode("#87D7AF"));
        COLOR_MAP.put(COLOR_DARKSLATEGRAY3, Color.decode("#87D7D7"));
        COLOR_MAP.put(COLOR_SKYBLUE1, Color.decode("#87D7FF"));
        COLOR_MAP.put(COLOR_CHARTREUSE1, Color.decode("#87FF00"));
        COLOR_MAP.put(COLOR_LIGHTGREEN_1, Color.decode("#87FF5F"));
        COLOR_MAP.put(COLOR_LIGHTGREEN_2, Color.decode("#87FF87"));
        COLOR_MAP.put(COLOR_PALEGREEN1_1, Color.decode("#87FFAF"));
        COLOR_MAP.put(COLOR_AQUAMARINE1_2, Color.decode("#87FFD7"));
        COLOR_MAP.put(COLOR_DARKSLATEGRAY1, Color.decode("#87FFFF"));
        COLOR_MAP.put(COLOR_RED3_1, Color.decode("#AF0000"));
        COLOR_MAP.put(COLOR_DEEPPINK4_3, Color.decode("#AF005F"));
        COLOR_MAP.put(COLOR_MEDIUMVIOLETRED, Color.decode("#AF0087"));
        COLOR_MAP.put(COLOR_MAGENTA3_1, Color.decode("#AF00AF"));
        COLOR_MAP.put(COLOR_DARKVIOLET_2, Color.decode("#AF00D7"));
        COLOR_MAP.put(COLOR_PURPLE_2, Color.decode("#AF00FF"));
        COLOR_MAP.put(COLOR_DARKORANGE3_1, Color.decode("#AF5F00"));
        COLOR_MAP.put(COLOR_INDIANRED_1, Color.decode("#AF5F5F"));
        COLOR_MAP.put(COLOR_HOTPINK3_1, Color.decode("#AF5F87"));
        COLOR_MAP.put(COLOR_MEDIUMORCHID3, Color.decode("#AF5FAF"));
        COLOR_MAP.put(COLOR_MEDIUMORCHID, Color.decode("#AF5FD7"));
        COLOR_MAP.put(COLOR_MEDIUMPURPLE2_1, Color.decode("#AF5FFF"));
        COLOR_MAP.put(COLOR_DARKGOLDENROD, Color.decode("#AF8700"));
        COLOR_MAP.put(COLOR_LIGHTSALMON3_1, Color.decode("#AF875F"));
        COLOR_MAP.put(COLOR_ROSYBROWN, Color.decode("#AF8787"));
        COLOR_MAP.put(COLOR_GREY63, Color.decode("#AF87AF"));
        COLOR_MAP.put(COLOR_MEDIUMPURPLE2_2, Color.decode("#AF87D7"));
        COLOR_MAP.put(COLOR_MEDIUMPURPLE1, Color.decode("#AF87FF"));
        COLOR_MAP.put(COLOR_GOLD3_1, Color.decode("#AFAF00"));
        COLOR_MAP.put(COLOR_DARKKHAKI, Color.decode("#AFAF5F"));
        COLOR_MAP.put(COLOR_NAVAJOWHITE3, Color.decode("#AFAF87"));
        COLOR_MAP.put(COLOR_GREY69, Color.decode("#AFAFAF"));
        COLOR_MAP.put(COLOR_LIGHTSTEELBLUE3, Color.decode("#AFAFD7"));
        COLOR_MAP.put(COLOR_LIGHTSTEELBLUE, Color.decode("#AFAFFF"));
        COLOR_MAP.put(COLOR_YELLOW3_1, Color.decode("#AFD700"));
        COLOR_MAP.put(COLOR_DARKOLIVEGREEN3_3, Color.decode("#AFD75F"));
        COLOR_MAP.put(COLOR_DARKSEAGREEN3_2, Color.decode("#AFD787"));
        COLOR_MAP.put(COLOR_DARKSEAGREEN2_1, Color.decode("#AFD7AF"));
        COLOR_MAP.put(COLOR_LIGHTCYAN3, Color.decode("#AFD7D7"));
        COLOR_MAP.put(COLOR_LIGHTSKYBLUE1, Color.decode("#AFD7FF"));
        COLOR_MAP.put(COLOR_GREENYELLOW, Color.decode("#AFFF00"));
        COLOR_MAP.put(COLOR_DARKOLIVEGREEN2, Color.decode("#AFFF5F"));
        COLOR_MAP.put(COLOR_PALEGREEN1_2, Color.decode("#AFFF87"));
        COLOR_MAP.put(COLOR_DARKSEAGREEN2_2, Color.decode("#AFFFAF"));
        COLOR_MAP.put(COLOR_DARKSEAGREEN1_1, Color.decode("#AFFFD7"));
        COLOR_MAP.put(COLOR_PALETURQUOISE1, Color.decode("#AFFFFF"));
        COLOR_MAP.put(COLOR_RED3_2, Color.decode("#D70000"));
        COLOR_MAP.put(COLOR_DEEPPINK3_1, Color.decode("#D7005F"));
        COLOR_MAP.put(COLOR_DEEPPINK3_2, Color.decode("#D70087"));
        COLOR_MAP.put(COLOR_MAGENTA3_2, Color.decode("#D700AF"));
        COLOR_MAP.put(COLOR_MAGENTA3_3, Color.decode("#D700D7"));
        COLOR_MAP.put(COLOR_MAGENTA2_1, Color.decode("#D700FF"));
        COLOR_MAP.put(COLOR_DARKORANGE3_2, Color.decode("#D75F00"));
        COLOR_MAP.put(COLOR_INDIANRED_2, Color.decode("#D75F5F"));
        COLOR_MAP.put(COLOR_HOTPINK3_2, Color.decode("#D75F87"));
        COLOR_MAP.put(COLOR_HOTPINK2, Color.decode("#D75FAF"));
        COLOR_MAP.put(COLOR_ORCHID, Color.decode("#D75FD7"));
        COLOR_MAP.put(COLOR_MEDIUMORCHID1_1, Color.decode("#D75FFF"));
        COLOR_MAP.put(COLOR_ORANGE3, Color.decode("#D78700"));
        COLOR_MAP.put(COLOR_LIGHTSALMON3_2, Color.decode("#D7875F"));
        COLOR_MAP.put(COLOR_LIGHTPINK3, Color.decode("#D78787"));
        COLOR_MAP.put(COLOR_PINK3, Color.decode("#D787AF"));
        COLOR_MAP.put(COLOR_PLUM3, Color.decode("#D787D7"));
        COLOR_MAP.put(COLOR_VIOLET, Color.decode("#D787FF"));
        COLOR_MAP.put(COLOR_GOLD3_2, Color.decode("#D7AF00"));
        COLOR_MAP.put(COLOR_LIGHTGOLDENROD3, Color.decode("#D7AF5F"));
        COLOR_MAP.put(COLOR_TAN, Color.decode("#D7AF87"));
        COLOR_MAP.put(COLOR_MISTYROSE3, Color.decode("#D7AFAF"));
        COLOR_MAP.put(COLOR_THISTLE3, Color.decode("#D7AFD7"));
        COLOR_MAP.put(COLOR_PLUM2, Color.decode("#D7AFFF"));
        COLOR_MAP.put(COLOR_YELLOW3_2, Color.decode("#D7D700"));
        COLOR_MAP.put(COLOR_KHAKI3, Color.decode("#D7D75F"));
        COLOR_MAP.put(COLOR_LIGHTGOLDENROD2_1, Color.decode("#D7D787"));
        COLOR_MAP.put(COLOR_LIGHTYELLOW3, Color.decode("#D7D7AF"));
        COLOR_MAP.put(COLOR_GREY84, Color.decode("#D7D7D7"));
        COLOR_MAP.put(COLOR_LIGHTSTEELBLUE1, Color.decode("#D7D7FF"));
        COLOR_MAP.put(COLOR_YELLOW2, Color.decode("#D7FF00"));
        COLOR_MAP.put(COLOR_DARKOLIVEGREEN1_1, Color.decode("#D7FF5F"));
        COLOR_MAP.put(COLOR_DARKOLIVEGREEN1_2, Color.decode("#D7FF87"));
        COLOR_MAP.put(COLOR_DARKSEAGREEN1_2, Color.decode("#D7FFAF"));
        COLOR_MAP.put(COLOR_HONEYDEW2, Color.decode("#D7FFD7"));
        COLOR_MAP.put(COLOR_LIGHTCYAN1, Color.decode("#D7FFFF"));
        COLOR_MAP.put(COLOR_RED1, Color.decode("#FF0000"));
        COLOR_MAP.put(COLOR_DEEPPINK2, Color.decode("#FF005F"));
        COLOR_MAP.put(COLOR_DEEPPINK1_1, Color.decode("#FF0087"));
        COLOR_MAP.put(COLOR_DEEPPINK1_2, Color.decode("#FF00AF"));
        COLOR_MAP.put(COLOR_MAGENTA2_2, Color.decode("#FF00D7"));
        COLOR_MAP.put(COLOR_MAGENTA1, Color.decode("#FF00FF"));
        COLOR_MAP.put(COLOR_ORANGERED1, Color.decode("#FF5F00"));
        COLOR_MAP.put(COLOR_INDIANRED1_1, Color.decode("#FF5F5F"));
        COLOR_MAP.put(COLOR_INDIANRED1_2, Color.decode("#FF5F87"));
        COLOR_MAP.put(COLOR_HOTPINK_1, Color.decode("#FF5FAF"));
        COLOR_MAP.put(COLOR_HOTPINK_2, Color.decode("#FF5FD7"));
        COLOR_MAP.put(COLOR_MEDIUMORCHID1_2, Color.decode("#FF5FFF"));
        COLOR_MAP.put(COLOR_DARKORANGE, Color.decode("#FF8700"));
        COLOR_MAP.put(COLOR_SALMON1, Color.decode("#FF875F"));
        COLOR_MAP.put(COLOR_LIGHTCORAL, Color.decode("#FF8787"));
        COLOR_MAP.put(COLOR_PALEVIOLETRED1, Color.decode("#FF87AF"));
        COLOR_MAP.put(COLOR_ORCHID2, Color.decode("#FF87D7"));
        COLOR_MAP.put(COLOR_ORCHID1, Color.decode("#FF87FF"));
        COLOR_MAP.put(COLOR_ORANGE1, Color.decode("#FFAF00"));
        COLOR_MAP.put(COLOR_SANDYBROWN, Color.decode("#FFAF5F"));
        COLOR_MAP.put(COLOR_LIGHTSALMON1, Color.decode("#FFAF87"));
        COLOR_MAP.put(COLOR_LIGHTPINK1, Color.decode("#FFAFAF"));
        COLOR_MAP.put(COLOR_PINK1, Color.decode("#FFAFD7"));
        COLOR_MAP.put(COLOR_PLUM1, Color.decode("#FFAFFF"));
        COLOR_MAP.put(COLOR_GOLD1, Color.decode("#FFD700"));
        COLOR_MAP.put(COLOR_LIGHTGOLDENROD2_2, Color.decode("#FFD75F"));
        COLOR_MAP.put(COLOR_LIGHTGOLDENROD2_3, Color.decode("#FFD787"));
        COLOR_MAP.put(COLOR_NAVAJOWHITE1, Color.decode("#FFD7AF"));
        COLOR_MAP.put(COLOR_MISTYROSE1, Color.decode("#FFD7D7"));
        COLOR_MAP.put(COLOR_THISTLE1, Color.decode("#FFD7FF"));
        COLOR_MAP.put(COLOR_YELLOW1, Color.decode("#FFFF00"));
        COLOR_MAP.put(COLOR_LIGHTGOLDENROD1, Color.decode("#FFFF5F"));
        COLOR_MAP.put(COLOR_KHAKI1, Color.decode("#FFFF87"));
        COLOR_MAP.put(COLOR_WHEAT1, Color.decode("#FFFFAF"));
        COLOR_MAP.put(COLOR_CORNSILK1, Color.decode("#FFFFD7"));
        COLOR_MAP.put(COLOR_GREY100, Color.decode("#FFFFFF"));
        COLOR_MAP.put(COLOR_GREY3, Color.decode("#080808"));
        COLOR_MAP.put(COLOR_GREY7, Color.decode("#121212"));
        COLOR_MAP.put(COLOR_GREY11, Color.decode("#1C1C1C"));
        COLOR_MAP.put(COLOR_GREY15, Color.decode("#262626"));
        COLOR_MAP.put(COLOR_GREY19, Color.decode("#303030"));
        COLOR_MAP.put(COLOR_GREY23, Color.decode("#3A3A3A"));
        COLOR_MAP.put(COLOR_GREY27, Color.decode("#444444"));
        COLOR_MAP.put(COLOR_GREY30, Color.decode("#4E4E4E"));
        COLOR_MAP.put(COLOR_GREY35, Color.decode("#585858"));
        COLOR_MAP.put(COLOR_GREY39, Color.decode("#626262"));
        COLOR_MAP.put(COLOR_GREY42, Color.decode("#6C6C6C"));
        COLOR_MAP.put(COLOR_GREY46, Color.decode("#767676"));
        COLOR_MAP.put(COLOR_GREY50, Color.decode("#808080"));
        COLOR_MAP.put(COLOR_GREY54, Color.decode("#8A8A8A"));
        COLOR_MAP.put(COLOR_GREY58, Color.decode("#949494"));
        COLOR_MAP.put(COLOR_GREY62, Color.decode("#9E9E9E"));
        COLOR_MAP.put(COLOR_GREY66, Color.decode("#A8A8A8"));
        COLOR_MAP.put(COLOR_GREY70, Color.decode("#B2B2B2"));
        COLOR_MAP.put(COLOR_GREY74, Color.decode("#BCBCBC"));
        COLOR_MAP.put(COLOR_GREY78, Color.decode("#C6C6C6"));
        COLOR_MAP.put(COLOR_GREY82, Color.decode("#D0D0D0"));
        COLOR_MAP.put(COLOR_GREY85, Color.decode("#DADADA"));
        COLOR_MAP.put(COLOR_GREY89, Color.decode("#E4E4E4"));
        COLOR_MAP.put(COLOR_LIGHTGREY, Color.decode("#EEEEEE"));
    }
    

    /**
     * Displays a bundle of byte arrays as hex string, for debug purpose only
     */
    private static String toHexDisplay(byte[] data) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            result.append(Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1));
            result.append(" ");
        }
        return result.toString();
    }

    /**
     * Checks if the passed ANSI sequence is a color sequence
     *
     * @param ansiCode
     * @return
     */
    public static final boolean isColorSequence(String ansiSequence) {
        return (ansiSequence.startsWith(CSI + "0;")
                || ansiSequence.startsWith(CSI + "38;"));
    }

    /**
     * Returns a java color object for the passed ANSI constant
     */
    public static Color toColor(String ansiColor) {
        return (COLOR_MAP.getOrDefault(ansiColor, Color.WHITE));
    }

    /**
     * Returns a matching IRC color to the passed java color - has to be correct
     * match, else WHITE is returned
     *
     * @param ansiColor A color that has a ANSI pendant
     * @return
     */
    public static String toColorStr(Color ansiColor) {
        for (String key : COLOR_MAP.keySet()) {
            if (COLOR_MAP.get(key).equals(ansiColor)) {
                return (key);
            }
        }
        return (COLOR_SYSTEM_WHITE_BRIGHT);
    }

    /**
     * Removes all formatting from a line that contains ANSI style formatting
     */
    public static String stripANSI(String line) {
        StringBuilder builder = new StringBuilder();
        boolean inSequence = false;
        for (int i = 0; i < line.length(); i++) {
            if (!inSequence) {
                if (i < line.length() - 1) {
                    String possibleCSI = line.substring(i, i + 2);
                    if (possibleCSI.equals(CSI)) {
                        inSequence = true;
                    }
                }
            }
            char foundChar = line.charAt(i);
            if (!inSequence) {
                builder.append(foundChar);
            } else {
                if (foundChar == 'm');
                inSequence = false;
            }
        }
        return (builder.toString());
    }
}
