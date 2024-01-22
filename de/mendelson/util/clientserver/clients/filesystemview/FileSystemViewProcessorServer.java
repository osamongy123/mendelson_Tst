//$Header: /mendelson_business_integration/de/mendelson/util/clientserver/clients/filesystemview/FileSystemViewProcessorServer.java 21    3.1 $
package de.mendelson.util.clientserver.clients.filesystemview;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Processes file system view requests
 *
 * @author S.Heller
 * @version $Revision: 21 $
 */
public class FileSystemViewProcessorServer {

    private Logger logger;
    private boolean serverSideFileSystemSupportsDOS = false;
    private boolean serverSideFileSystemSupportsPOSIX = false;

    public FileSystemViewProcessorServer(Logger logger) {
        this.logger = logger;
        this.serverSideFileSystemSupportsDOS = this.fileSystemSupportsDOS();
        this.serverSideFileSystemSupportsPOSIX = this.fileSystemSupportsPosix();
    }

    /**
     * Returns the implementation specific name of the file storage assigned to
     * the passed root
     */
    private String getRootFileStorageName(URI rootURI) {
        try {
            Path rootPath = Paths.get(rootURI);
            FileStore store = Files.getFileStore(rootPath);
            return (store.name());
        } catch (IOException e) {
            //e.g. device not ready for CD, DVD drives
        }
        return (null);
    }

    /**
     * Returns the system icon - may return null if this is not accessible
     */
    private Icon getSystemIcon(URI fileURI) {
        FileSystemView view = FileSystemView.getFileSystemView();
        Icon systemIcon = null;
        try {
            systemIcon = view.getSystemIcon(new File(fileURI));
        } catch (Throwable e) {
            //it is possible that the process owner has no access to the root file
            //system - e.g. if its LocalSystem under Windows. In this case there will be no root
            //icon and this method should return null
            this.logger.warning("Client Request (FileSystemViewProcessorServer.getSystemIcon): Unable to access the system icon for URL \""
                    + fileURI.toString() + "\" - will use the fallback icon at the client.");
        }
        return (systemIcon);
    }

    private boolean fileSystemSupportsPosix() {
        boolean isPosix = FileSystems.getDefault().supportedFileAttributeViews().contains("posix");
        return (isPosix);
    }

    private boolean fileSystemSupportsDOS() {
        boolean isPosix = FileSystems.getDefault().supportedFileAttributeViews().contains("dos");
        return (isPosix);
    }

    /**
     * Works just for DOS but thats ok as this is just a render feature
     */
    private boolean isHidden(Path file) {
        if (this.serverSideFileSystemSupportsDOS) {
            try {
                DosFileAttributes dosFileAttributes = Files.readAttributes(file, DosFileAttributes.class);
                return (dosFileAttributes.isHidden());
            } catch (Exception e) {
                //nop
            }
        }
        try {
            return (Files.isHidden(file));
        } catch (Exception e) {
            //nop
        }
        return (false);
    }

    /**
     * Works just for UNIX like OS but thats ok as this is just a render option
     */
    private boolean isExecutable(Path file) {
        if (this.serverSideFileSystemSupportsPOSIX) {
            try {
                Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(file, LinkOption.NOFOLLOW_LINKS);
                return (permissions.contains(PosixFilePermission.OWNER_EXECUTE)
                        || permissions.contains(PosixFilePermission.GROUP_EXECUTE)
                        || permissions.contains(PosixFilePermission.OTHERS_EXECUTE));
            } catch (Exception e) {
                //nop
            }
        }
        //weird but under DOS isExecutable always returns true :/
        return (false);
    }

    /**
     * Lists the system roots
     */
    private List<FileObject> listRoots() {
        List<FileObject> rootList = new ArrayList<FileObject>();
        try {
            File[] roots = File.listRoots();
            for (File root : roots) {
                String storeName = this.getRootFileStorageName(root.toURI());
                Icon icon = this.getSystemIcon(root.toURI());
                rootList.add(new FileObjectRoot(root.toURI(), storeName, icon));
            }
        } catch (Throwable e) {
            this.logger.warning("Client Request (FileSystemViewProcessorServer [LIST_ROOTS]): Unable to determine file system roots. "
                    + "[" + e.getClass().getSimpleName() + "] " + e.getMessage());
        }
        return (rootList);
    }

    /**
     * Lists the children for a specific path
     */
    private List<FileObject> listChildren(String requestPath, FileFilter filter) {
        Path parentDir = Paths.get(requestPath);
        List<FileObject> childList = new ArrayList<FileObject>();
        try {
            List<Path> children = this.listFilesNIOAcceptAll(parentDir);
            for (Path child : children) {
                if (Files.isDirectory(child)) {
                    FileObjectDir fileObject = new FileObjectDir(child.toUri());
                    fileObject.setHidden(this.isHidden(child));
                    boolean isSymbolicLink = Files.isSymbolicLink(child);
                    if (isSymbolicLink) {
                        fileObject.setIsSymbolikLink(true);
                        try {
                            Path symbolicLinkTarget = Files.readSymbolicLink(child);
                            fileObject.setSymbolicLinkTarget(symbolicLinkTarget.toAbsolutePath().toString());
                        } catch (Exception e) {
                            //java.nio.file.NotLinkException: The file or directory is not a reparse point.
                        }
                    }
                    childList.add(fileObject);
                } else {
                    if (filter.displayFile(child)) {
                        Icon icon = this.getSystemIcon(child.toUri());
                        FileObjectFile fileObject = new FileObjectFile(child.toUri(), icon);
                        fileObject.setHidden(this.isHidden(child));
                        fileObject.setExecutable(this.isExecutable(child));
                        fileObject.setReadOnly(!Files.isWritable(child));
                        boolean isSymbolicLink = Files.isSymbolicLink(child);
                        if (isSymbolicLink) {
                            fileObject.setIsSymbolikLink(true);
                            try {
                                Path symbolicLinkTarget = Files.readSymbolicLink(child);
                                fileObject.setSymbolicLinkTarget(symbolicLinkTarget.toAbsolutePath().toString());
                            } catch (Exception e) {
                                //java.nio.file.NotLinkException: The file or directory is not a reparse point.
                            }
                        }
                        childList.add(fileObject);
                    }
                }
            }
            Collections.sort(childList);
        } catch (Throwable e) {
            this.logger.warning("Client Request (FileSystemViewProcessorServer [LIST_CHILDREN]): Unable to list children for path \"" + requestPath
                    + "\". [" + e.getClass().getSimpleName() + "] " + e.getMessage());
        }
        return (childList);
    }

    private List<FileObject> listPathElements(String pathString) {
        List<FileObject> elements = new ArrayList<FileObject>();
        try {
            if (pathString != null) {
                Path pathFile = Paths.get(pathString);
                while (pathFile != null) {
                    if (Files.exists(pathFile)) {
                        if (pathFile.getParent() == null) {
                            String storeName = this.getRootFileStorageName(pathFile.toUri());
                            Icon icon = this.getSystemIcon(pathFile.toUri());
                            elements.add(0, new FileObjectRoot(pathFile.toUri(), storeName, icon));
                        } else if (Files.isDirectory(pathFile)) {
                            FileObjectDir fileObjectDir = new FileObjectDir(pathFile.toUri());
                            fileObjectDir.setHidden(this.isHidden(pathFile));
                            boolean isSymbolicLink = Files.isSymbolicLink(pathFile);
                            if (isSymbolicLink) {
                                fileObjectDir.setIsSymbolikLink(true);
                                try {
                                    Path symbolicLinkTarget = Files.readSymbolicLink(pathFile);
                                    fileObjectDir.setSymbolicLinkTarget(symbolicLinkTarget.toAbsolutePath().toString());
                                } catch (Exception e) {
                                    //java.nio.file.NotLinkException: The file or directory is not a reparse point.
                                }
                            }
                            elements.add(0, fileObjectDir);
                        }
                    }
                    pathFile = pathFile.getParent();
                }
            }
        } catch (Throwable e) {
            this.logger.warning("Client Request (FileSystemViewProcessorServer [LIST_PATH_ELEMENT]): Unable to list path elements for path \"" + pathString
                    + "\". [" + e.getClass().getSimpleName() + "] " + e.getMessage());
        }
        return (elements);
    }

    public FileSystemViewResponse performRequest(FileSystemViewRequest request) {
        FileFilter filter = request.getFileFilter();
        FileSystemViewResponse response = new FileSystemViewResponse(request);
        final int requestType = request.getRequestType();
        switch (requestType) {
            case FileSystemViewRequest.TYPE_LIST_ROOTS:
                response.setParameterFileArray(this.listRoots());
                break;
            case FileSystemViewRequest.TYPE_LIST_CHILDREN:
                String requestPath = request.getRequestFilePath();
                response.setParameterFileArray(this.listChildren(requestPath, filter));
                break;
            case FileSystemViewRequest.TYPE_GET_ABSOLUTE_PATH_STR:
                String pathStr = "";
                try {
                    String requestedPath = request.getRequestFilePath();
                    pathStr = Paths.get(requestedPath).normalize().toAbsolutePath().toString();
                } catch (Throwable e) {
                    //this.logger.warning("FileSystemViewProcessorServer [GET_PATH_STR]: Unable to get path: " + e.getMessage());
                } finally {
                    response.setParameterString(pathStr);
                }
                break;
            case FileSystemViewRequest.TYPE_GET_PATH_ELEMENTS:
                response.setParameterFileArray(this.listPathElements(request.getRequestFilePath()));
                break;
            case FileSystemViewRequest.TYPE_GET_FILE_SEPARATOR:
                response.setParameterString(FileSystems.getDefault().getSeparator());
                break;
        }
        return (response);
    }

    /**
     * Non blocking file directory list - accepting all files and do not follow
     * symbolic links
     */
    private List<Path> listFilesNIOAcceptAll(Path dir) throws Exception {
        List<Path> files = this.listFilesNIO(dir, new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path entry) throws IOException {
                return (true);
            }
        });
        return (files);
    }

    /**
     * Non blocking file directory list with a user defined filter
     */
    private List<Path> listFilesNIO(Path dir, DirectoryStream.Filter fileFilter) throws Exception {
        List<Path> result = new ArrayList<Path>();
        //do not follow symbolic links - if the user requests this just return an empty list
        if (Files.isSymbolicLink(dir)) {
            return (result);
        }
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

}
