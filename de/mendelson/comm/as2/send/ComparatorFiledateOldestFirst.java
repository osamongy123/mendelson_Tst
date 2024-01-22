//$Header: /as2/de/mendelson/comm/as2/send/ComparatorFiledateOldestFirst.java 2     24.10.18 11:30 Heller $
package de.mendelson.comm.as2.send;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Comparator to use to compare file dates by their age
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class ComparatorFiledateOldestFirst implements Comparator<Path> {

    /**
     * Compares tow object of the type Path by their last modification date
     */
    @Override
    public int compare(Path o1, Path o2) {
        try {
            BasicFileAttributes view1 = Files.getFileAttributeView(o1, BasicFileAttributeView.class).readAttributes();
            long lastModified1 = view1.lastModifiedTime().toMillis();
            BasicFileAttributes view2 = Files.getFileAttributeView(o2, BasicFileAttributeView.class).readAttributes();            
            long lastModified2 = view2.lastModifiedTime().toMillis();
            if (lastModified1 < lastModified2) {
                return (-1);
            }
            if (lastModified1 > lastModified2) {
                return (1);
            }
        } catch (Exception e) {
            //returns 0 now
        }
        return (0);
    }
}
