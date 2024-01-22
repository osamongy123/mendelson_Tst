//$Header: /oftp2/de/mendelson/util/clientserver/clients/fileoperation/FileOperationProcessing.java 1     2.11.18 10:01 Heller $
package de.mendelson.util.clientserver.clients.fileoperation;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Performs the file operations on the server - could be included in the server processing
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class FileOperationProcessing {

    /**
     * Non blocking file directory list
     */
    private List<Path> listFilesNIO(Path dir, DirectoryStream.Filter fileFilter) throws Exception {
        List<Path> result = new ArrayList<Path>();
        DirectoryStream<Path> stream = null;
        try {
            stream = Files.newDirectoryStream(dir, fileFilter);
            for (Path entry : stream) {
                result.add(entry);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return result;
    }

    /**
     * Deletes a directory with all subdirectories even if they are not empty
     */
    public void deleteDirectoryWithSubdirectories(Path path)throws Exception {
        if (Files.exists(path)) {            
                List<Path> files = this.listFilesNIO(path, new DirectoryStream.Filter<Path>() {
                    @Override
                    public boolean accept(Path entry) throws IOException {
                        return (true);
                    }
                });
                for (Path file : files) {
                    if (Files.isDirectory(file)) {
                        this.deleteDirectoryWithSubdirectories(file);
                    } else {
                        Files.delete(file);
                    }
                }
        }
        Files.delete(path);
    }
    
}
