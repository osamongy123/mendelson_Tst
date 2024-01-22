 //$Header: /as2/de/mendelson/util/IOFileFilterCreationDate.java 4     15.01.21 15:11 Heller $
package de.mendelson.util;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * File filter that filters the directory entries by their age
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class IOFileFilterCreationDate implements DirectoryStream.Filter {

    public static final int MODE_OLDER_THAN = 1;
    public static final int MODE_NOT_OLDER_THAN = 2;

    private int mode = MODE_OLDER_THAN;
    private boolean includeDirecories = false;
    private boolean includeFiles = true;
    private Instant instantToCompare;

    /**
     * Creates a new instance of the creation date File filter
     * @param MODE accept mode for this filter as defined in the constants of this class - either MODE_OLDER_THAN or MODE_NOT_OLDER_THAN
     * @param absoluteCreationTime The absolute creation time for the accept process in ms
     */
    public IOFileFilterCreationDate(final int MODE, long absoluteCreationTime) {
        this.mode = MODE;
        this.instantToCompare = Instant.ofEpochMilli(absoluteCreationTime);
    }

    /**
     * Returns if this file filer accepts the passed file
     */
    @Override
    public boolean accept(Object entry) {
        if (entry == null || !(entry instanceof Path)) {
            return (false);
        }
        Path path = (Path) entry;
        if( path.getFileName().toString().equals( ".")
                || path.getFileName().toString().equals( "..")){
            return( false );
        }
        if (!this.includeDirecories) {
            if (Files.isDirectory(path)) {
                return (false);
            }
        }
        if( !this.includeFiles){
            if (!Files.isDirectory(path)) {
                return (false);
            }
        }
        try {
            BasicFileAttributes view = Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
            FileTime fileTime = view.creationTime();
            boolean acceptEntry = false;
            if (this.mode == MODE_OLDER_THAN) {                
                acceptEntry = fileTime.toInstant().isBefore(this.instantToCompare);
            } else {
                acceptEntry = fileTime.toInstant().isAfter(this.instantToCompare);
            }
            return( acceptEntry );
        } catch (Exception e) {
            return (false);
        }
    }

    /**
     * Returns if the filter should return directories that match the condition
     * @return the includeDirecories
     */
    public boolean includesDirecories() {
        return includeDirecories;
    }

    /**
     * Sets if the filter should return directories that match the condition
     * @param includeDirecories the includeDirecories to set
     */
    public void setIncludeDirecories(boolean includeDirecories) {
        this.includeDirecories = includeDirecories;
    }
    
     /**
      * Returns if the filter should return files that match the condition
     * @return the includeFiles
     */
    public boolean includesFiles() {
        return includeFiles;
    }

    /**
     * Sets if the filter should return files that match the condition
     * @param includeFiles the includeFiles to set
     */
    public void setIncludeFiles(boolean includeFiles) {
        this.includeFiles = includeFiles;
    }
    
    

//    public static final void main(String[] args) {
//        IOFileFilterCreationDate fileFilter 
//                = new IOFileFilterCreationDate(MODE_OLDER_THAN, System.currentTimeMillis()
//                        - TimeUnit.DAYS.toMillis(1));
//        fileFilter.setIncludeDirecories(true);
//        Path dirPath = Paths.get("c:/temp");
//        try {
//            DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, fileFilter);
//            for (Path entry : stream) {
//                System.out.println(entry.getFileName());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

   
}
