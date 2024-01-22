//$Header: /as2/de/mendelson/comm/as2/webclient2/FilePanel.java 8     11.06.21 10:13 Heller $
package de.mendelson.comm.as2.webclient2;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import de.mendelson.comm.as2.message.loggui.JPanelFileDisplay;
import de.mendelson.util.FileEncodingDetection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Dialog that display a file content
 *
 * @author S.Heller
 * @version $Revision: 8 $
 */
public class FilePanel extends TextArea {

    private String displayedFilename = "";
    /**
     * Max filesize for the display of data in the panel
     */
    private final static double MAX_FILESIZE = JPanelFileDisplay.MAX_FILESIZE;

    public FilePanel() {
        this.setRows(16);
        this.setWidth(100, Unit.PERCENTAGE);
    }

    public void displayFile(Path file, boolean detectEncoding) {
        long filesize = 0;
        try {
            filesize = Files.size(file);
        } catch (Exception e) {
            //could be also a NullPointer Exception if the file is null
        }
        boolean readOnlyStateOld = this.isReadOnly();
        //there will be displayed a new value to the panel
        this.setReadOnly(false);
        String filename = "";
        if (file == null) {
            this.setValue("No file");
        } else if (!Files.exists(file)) {
            this.setValue("File not found: " + file.toAbsolutePath().toString());
            filename = file.toAbsolutePath().toString();
        } else if (filesize > MAX_FILESIZE) {
            this.setValue("Filesize too large to display");
            filename = file.toAbsolutePath().toString();
        } else {
            try {
                if (detectEncoding) {
                    this.displayRawTextDetectEncoding(file);
                } else {
                    this.displayRawTextIgnoreEncoding(file);
                }
                filename = file.toAbsolutePath().toString();
            } catch (Exception e) {
                new Notification("Problem", e.getMessage(),
                        Notification.Type.ERROR_MESSAGE, true)
                        .show(Page.getCurrent());
            }
        }
        this.displayedFilename = filename;
        this.setReadOnly(readOnlyStateOld);
    }

    public String getDisplayedFilename() {
        return displayedFilename;
    }

    /**
     * Displays a byte array as raw text and tries to detect the encoding
     */
    private void displayRawTextDetectEncoding(Path file) throws Exception {
        FileEncodingDetection detection = new FileEncodingDetection();
        Charset encoding = detection.guessBestCharset(file);
        byte[] data = Files.readAllBytes(file);
        String dataStr = new String(data, encoding);
        //prevent JavaScript
        dataStr = AS2WebUI.replaceJavaScriptOutput(dataStr);
        this.setValue(dataStr);
    }

    /**
     * Displays a byte array as raw text and tries to detect the encoding
     */
    private void displayRawTextIgnoreEncoding(Path file) throws Exception {
        byte[] data = Files.readAllBytes(file);
        String dataStr = new String(data);
        //prevent JavaScript
        dataStr = AS2WebUI.replaceJavaScriptOutput(dataStr);
        this.setValue(dataStr);
    }

}
