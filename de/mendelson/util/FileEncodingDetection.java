//$Header: /converteride/de/mendelson/util/FileEncodingDetection.java 14    25.11.20 12:37 Heller $
package de.mendelson.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Checks the encoding of a file by just trying all available encodings on it
 *
 * @author S.Heller
 * @version $Revision: 14 $
 */
public class FileEncodingDetection {

    /**
     * @deprecated
     */
    @Deprecated(since = "2020")
    public List<Charset> detectCharsets(File file) {
        return (this.detectCharsets(file.toPath()));
    }

    /**
     * Returns all encodings that match the passed file. If the file does use
     * just ASCII characters these could be a lot.
     */
    public List<Charset> detectCharsets(Path file) {
        List<Charset> matchingFileEncodingList = new ArrayList<Charset>();
        List<Charset> availableSystemEncodingList = new ArrayList<Charset>();
        //get all supported encodings of the operation system
        Map<String, Charset> map = Charset.availableCharsets();
        Iterator<Entry<String, Charset>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, Charset> entry = iterator.next();
            availableSystemEncodingList.add(entry.getValue());
        }
        for (Charset encoding : availableSystemEncodingList) {
            if (this.encodingMatches(file, encoding)) {
                matchingFileEncodingList.add(encoding);
            }
        }        
        //sort this list a little bit. There are popular encodings and they should be in the
        //first places
        List<Charset> popularCharsets = new ArrayList<Charset>();
        popularCharsets.add(StandardCharsets.UTF_8);
        popularCharsets.add(Charset.defaultCharset());
        popularCharsets.add(StandardCharsets.UTF_16);
        popularCharsets.add(StandardCharsets.UTF_16BE);
        popularCharsets.add(StandardCharsets.UTF_16LE);
        popularCharsets.add(StandardCharsets.ISO_8859_1);
        Collections.reverse(popularCharsets);
        for (Charset popularCharset : popularCharsets) {
            if (matchingFileEncodingList.contains(popularCharset)) {
                matchingFileEncodingList.remove(popularCharset);
                matchingFileEncodingList.add(0, popularCharset);
            }
        }
        return (matchingFileEncodingList);
    }

    /**
     * @deprecated
     */
    @Deprecated(since = "2020")
    public Charset guessBestCharset(File file) {
        return (this.guessBestCharset(file.toPath()));
    }

    /**
     * Analyzes a passed file and returns the best encoding to display it
     * without error. If multiple char sets are possible (which is nearly always
     * the case) the following order is preferred: 1. Any encoding signaled by a
     * BOM (UTF-8, UTF-16 etc) 2. Default Encoding of system 3. UTF-8 4. UTF-16
     * 5. ISO-8859* 6. ISO-*
     *
     * If no charset has been found the default system charset is returned
     */
    public Charset guessBestCharset(Path path) {
        //even if the file encoding matches the standard system encoding a BOM 
        //may signal that the requested encoding is another. Using one of these
        //encodings prevents seeing the BOM in a text editor
        Charset encodingByBOM = this.getEncodingByBOM(path);
        if (encodingByBOM != null && this.encodingMatches(path, encodingByBOM)) {
            return (encodingByBOM);
        }

        //check if the default file encoding does already match. This is the
        //choice the user expects if it matches - e.g. on windows systems every user expects
        //windows-1225
        if (this.encodingMatches(path, Charset.defaultCharset())) {
            return (Charset.defaultCharset());
        }
        //UTF-8 is very common if it did not match so far. Try this
        if (this.encodingMatches(path, StandardCharsets.UTF_8)) {
            return (StandardCharsets.UTF_8);
        }
        //UTF-16 is very common if it did not match so far. Try this
        if (this.encodingMatches(path, StandardCharsets.UTF_16)) {
            return (StandardCharsets.UTF_16);
        }
        //Now this becomes time consuming. Check all available encodings if they match, then
        //return the most preferred (all are valid but mainly unusal or not expected by the user.
        List<Charset> possibleCharsets = this.detectCharsets(path);
        for (Charset charset : possibleCharsets) {
            if (charset.toString().toUpperCase().startsWith("ISO-8859")) {
                return (charset);
            }
        }
        for (Charset charset : possibleCharsets) {
            if (charset.toString().toUpperCase().startsWith("ISO-")) {
                return (charset);
            }
        }
        if (possibleCharsets.isEmpty()) {
            return (Charset.defaultCharset());
        }
        return (possibleCharsets.get(0));
    }

    /**
     * Checks if the passed encoding matches the passed file. An encoding
     * matches if every character found in the file could be matched to a
     * character of the passed charset. The less special characters are in the
     * file the more likely is it to match an encoding
     *
     * @param file
     * @param charset
     * @return
     */
    public boolean encodingMatches(Path file, Charset charset) {
        Reader decoderReader = null;
        try {
            CharsetDecoder decoder = charset.newDecoder();
            decoder.reset();
            decoder.onMalformedInput(CodingErrorAction.REPORT);
            decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
            decoderReader = new InputStreamReader(Files.newInputStream(file), decoder);
            //no problem occured with character matching
            this.streamData(decoderReader);
            return (true);
        } catch (Exception e) {
            return (false);
        } finally {
            try {
                if (decoderReader != null) {
                    decoderReader.close();
                }
            } catch (Exception e) {
                //nop
            }
        }
    }

    /**
     * Returns an encoding that is identified by the passed files BOM or null if
     * no BOM matches or there is no BOM identified
     *
     * @param file
     * @return
     */
    private Charset getEncodingByBOM(Path file) {
        final Charset[] POSSIBLE_ENCODINGS_DETECTED_BY_BOM = new Charset[]{
            StandardCharsets.UTF_8,
            StandardCharsets.UTF_16,
            StandardCharsets.UTF_16BE,
            StandardCharsets.UTF_16LE,
            Charset.forName("UTF-32"),
            Charset.forName("UTF-32BE"),
            Charset.forName("UTF-32LE"),};
        for (Charset testCharset : POSSIBLE_ENCODINGS_DETECTED_BY_BOM) {
            byte[] bom = this.getBOMForEncoding(testCharset);
            if (bom == null) {
                continue;
            }
            try {
                if (Files.size(file) < bom.length) {
                    continue;
                }
            } catch (Exception e) {
                continue;
            }
            byte[] fileStartBytes = new byte[bom.length];
            InputStream inStream = null;
            try {
                inStream = Files.newInputStream(file);
                inStream.readNBytes(fileStartBytes, 0, bom.length);
            } catch (Exception e) {
                continue;
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
            boolean match = true;
            for (int i = 0; i < fileStartBytes.length; i++) {
                if (fileStartBytes[i] != bom[i]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return (testCharset);
            }
        }
        return (null);
    }

    /**
     * Reads a character stream and just does nothing with it. It is expected to
     * have a character encoder on the reader that checks if every character is
     * matchable
     *
     * @param decoderReaderIn
     * @throws IOException
     */
    private void streamData(Reader decoderReaderIn) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(decoderReaderIn);
        //copy the contents to an output stream
        char[] buffer = new char[2048];
        int read = 2048;
        //a read of 0 must be allowed, sometimes it takes time to
        //extract data from the input
        while (read != -1) {
            read = bufferedReader.read(buffer);
        }
    }

    /**
     * Returns the right BOM header for the passed encoding or null if there is
     * none
     *
     * @param encoding
     */
    private byte[] getBOMForEncoding(Charset encoding) {
        byte[] bomUTF8 = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] bomUTF16BE = new byte[]{(byte) 0xFE, (byte) 0xFF};
        byte[] bomUTF16LE = new byte[]{(byte) 0xFF, (byte) 0xFE};
        byte[] bomUTF32BE = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xFE};
        byte[] bomUTF32LE = new byte[]{(byte) 0xFF, (byte) 0xFE, (byte) 0x00, (byte) 0x00};
        if (encoding.equals(StandardCharsets.UTF_8)) {
            return (bomUTF8);
        } else if (encoding.equals(StandardCharsets.UTF_16BE) || encoding.equals(StandardCharsets.UTF_16)) {
            //UTF-16BE is the default UTF-16
            return (bomUTF16BE);
        } else if (encoding.equals(StandardCharsets.UTF_16LE)) {
            return (bomUTF16LE);
        } else if (encoding.toString().equalsIgnoreCase("UTF-32BE") || encoding.toString().equalsIgnoreCase("UTF-32")) {
            //UTF-32BE is the default UTF-32
            return (bomUTF32BE);
        } else if (encoding.toString().equalsIgnoreCase("UTF-32LE")) {
            return (bomUTF32LE);
        }
        return (null);
    }

}
