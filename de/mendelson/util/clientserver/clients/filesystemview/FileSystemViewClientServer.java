//$Header: /as2/de/mendelson/util/clientserver/clients/filesystemview/FileSystemViewClientServer.java 7     28.10.19 16:31 Heller $
package de.mendelson.util.clientserver.clients.filesystemview;

import de.mendelson.util.clientserver.BaseClient;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Handles the access to remote directories
 *
 * @author S.Heller
 * @version $Revision: 7 $
 */
public class FileSystemViewClientServer {

    private BaseClient baseClient;
    private FileFilter fileFilter = null;

    public FileSystemViewClientServer(BaseClient baseClient) {
        this.baseClient = baseClient;
    }

    public void setFileFilter(FileFilter fileFilter) {
        this.fileFilter = fileFilter;
    }

    public List<FileObject> getPathElements(String path) {
        FileSystemViewRequest request = new FileSystemViewRequest(FileSystemViewRequest.TYPE_GET_PATH_ELEMENTS);
        request.setRequestFilePath(path);
        request.setFileFilter(this.fileFilter);
        FileSystemViewResponse response = this.sendSyncFileSystemViewRequest(request);
        if( response != null ){
            return( response.getParameterFileArray());
        }else{
            return( new ArrayList<FileObject>());
        }
    }

    public String getAbsolutePathStr(String path) {
        FileSystemViewRequest request = new FileSystemViewRequest(FileSystemViewRequest.TYPE_GET_ABSOLUTE_PATH_STR);
        request.setRequestFilePath(path);
        request.setFileFilter(this.fileFilter);
        FileSystemViewResponse response = this.sendSyncFileSystemViewRequest(request);
        if( response != null ){
            return( response.getParameterString());
        }else{
            return( path);
        }
    }

    public List<FileObjectRoot> listRoots() {
        FileSystemViewRequest request = new FileSystemViewRequest(FileSystemViewRequest.TYPE_LIST_ROOTS);
        request.setFileFilter(this.fileFilter);
        List<FileObjectRoot> rootList = new ArrayList<FileObjectRoot>();
        FileSystemViewResponse response = this.sendSyncFileSystemViewRequest(request);
        if (response != null) {
            List<FileObject> fileObjectList = response.getParameterFileArray();
            for (FileObject entry : fileObjectList) {
                if (entry instanceof FileObjectRoot) {
                    rootList.add((FileObjectRoot) entry);
                }
            }
        }
        return (rootList);
    }

    public List<FileObject> listChildren(String path) {
        FileSystemViewRequest request = new FileSystemViewRequest(FileSystemViewRequest.TYPE_LIST_CHILDREN);
        request.setRequestFilePath(path);
        request.setFileFilter(this.fileFilter);
        FileSystemViewResponse response = this.sendSyncFileSystemViewRequest(request);
        if (response != null) {
            return( response.getParameterFileArray());
        }else{
            return( new ArrayList<FileObject>());
        }
    }

    /**
     * May return null if the sync response fails, 30s timeout for a server
     * answer
     */
    private FileSystemViewResponse sendSyncFileSystemViewRequest(FileSystemViewRequest request) {
        //there could be a IO timeout, e.g. for an unused CD drive
        FileSystemViewResponse response = (FileSystemViewResponse) this.baseClient.sendSync(request, TimeUnit.SECONDS.toMillis(30));
        return (response);
    }
}
