//$Header: /as2/de/mendelson/util/AS2Tools.java 12    1/06/22 14:37 Heller $
package de.mendelson.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Some programming tools for mendelson business integration
 *
 * @author S.Heller
 * @version $Revision: 12 $
 */
public class AS2Tools {

    private final static DateFormat DATE_FORMAT_TEMP_FILE = new SimpleDateFormat("yyyyMMdd");        
    
    /**
     * Replaces the string tag by the string replacement in the sourceString
     *
     * @param source Source string
     * @param tag	String that will be replaced
     * @param replacement String that will replace the tag
     * @return String that contains the replaced values
     */
    public static String replace(String source, String tag, String replacement) {
        if (source == null) {
            return null;
        }
        StringBuilder buffer = new StringBuilder();
        while (true) {
            int index = source.indexOf(tag);
            if (index == -1) {
                buffer.append(source);
                return (buffer.toString());
            }
            buffer.append(source.substring(0, index));
            buffer.append(replacement);
            source = source.substring(index + tag.length());
        }
    }

    /**
     * Folds a string using the passed delimiter where the max line length is
     * the passed lineLenght
     *
     * @param source Source string to use
     * @param delimiter Delimiter to add at the folding point
     * @param lineLength Max line length of the result
     */
    public static final String fold(String source, String delimiter, int lineLength) {
        if( source == null ){
            return( "null" );
        }
        StringBuilder result = new StringBuilder();
        int linePos = 0;
        for( int i = 0; i < source.length(); i++ ){
            char singleChar = source.charAt(i);
            if( singleChar == ' ' && linePos >= lineLength){
                result.append( delimiter );
                linePos = 0;
            }else{
                result.append( singleChar );
                linePos++;
            }
        }        
        return(result.toString());
    }

    /**
     * Creates a temp file in a data stamped folder below the directory temp
     */
    public static synchronized Path createTempFile(String prefix, String suffix) throws IOException {
        String tempDirStr = Paths.get("temp").toAbsolutePath().toString();
        Path targetDir = Paths.get(tempDirStr, DATE_FORMAT_TEMP_FILE.format(new Date()));
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }
        //create a unique file in the temp subdirectory
        Path tempFile = Files.createTempFile(targetDir, prefix, suffix);
        return (tempFile);
    }

    /**
     * Displays the passed data size in a proper format
     */
    public static String getDataSizeDisplay(long size) {
        StringBuilder builder = new StringBuilder();
        Formatter formatter = new Formatter(builder);
        if (size > 1.048E6) {
            formatter.format(Locale.getDefault(), "%.2f", Float.valueOf((float) size / (float) 1.048E6));
            builder.append(" ").append("MB");
            return (builder.toString());
        } else if (size > 1024L) {
            formatter.format(Locale.getDefault(), "%.2f", Float.valueOf((float) size / 1024f));
            builder.append(" ").append("KB");
            return (builder.toString());
        }
        return (String.valueOf(size) + " Byte");
    }

    /**
     * Displays a duration
     */
    public static String getTimeDisplay(long duration) {
        NumberFormat formatter = new DecimalFormat("0.00");
        if (duration < 1000) {
            return (duration + "ms");
        }
        float timeInSecs = (float) ((float) duration / 1000f);
        return (formatter.format(timeInSecs) + "s");
    }

}
