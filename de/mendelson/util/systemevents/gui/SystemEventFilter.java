//$Header: /as4/de/mendelson/util/systemevents/gui/SystemEventFilter.java 3     10.10.18 12:18 Heller $
package de.mendelson.util.systemevents.gui;

import de.mendelson.util.systemevents.SystemEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Filter to display system events in the user interface
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class SystemEventFilter {

    private final List<Integer> originList = Collections.synchronizedList(new ArrayList<Integer>());
    private final List<Integer> severityList = Collections.synchronizedList(new ArrayList<Integer>());
    private int acceptedCategory = -1;

    public SystemEventFilter(){
        //default: no not filter any entry
        synchronized( this.originList){
            this.originList.add( SystemEvent.ORIGIN_SYSTEM);
            this.originList.add( SystemEvent.ORIGIN_TRANSACTION);
            this.originList.add( SystemEvent.ORIGIN_USER);
        }
        synchronized( this.severityList ){
            this.severityList.add( SystemEvent.SEVERITY_ERROR);
            this.severityList.add( SystemEvent.SEVERITY_INFO);
            this.severityList.add( SystemEvent.SEVERITY_WARNING);
        }
    }
    
    public void setAcceptedCategory( int category ){
        this.acceptedCategory = category;
    }
    
    public int getAcceptedCategory(){
        return( this.acceptedCategory );
    }
    
    public void addAcceptedOrigin(final int ORIGIN) {
        synchronized (this.originList) {
            this.originList.add(ORIGIN);
        }
    }

    public void addAcceptedSeverity(final int SEVERITY) {
        synchronized (this.severityList) {
            this.severityList.add(SEVERITY);
        }
    }

    private List<Integer> getOriginList() {
        List<Integer> tempList = new ArrayList<Integer>();
        synchronized (this.originList) {
            tempList.addAll(this.originList);
        }
        return (tempList);
    }

    private List<Integer> getSeverityList() {
        List<Integer> tempList = new ArrayList<Integer>();
        synchronized (this.severityList) {
            tempList.addAll(this.severityList);
        }
        return (tempList);
    }
    
    public void setValues(SystemEventFilter filter) {
        synchronized (this.originList) {
            this.originList.clear();
            this.originList.addAll(filter.getOriginList());
        }
        synchronized (this.severityList) {
            this.severityList.clear();
            this.severityList.addAll( filter.getSeverityList() );
        }
        this.acceptedCategory = filter.getAcceptedCategory();
    }

    public void clear() {
        synchronized (this.originList) {
            this.originList.clear();
        }
        synchronized (this.severityList) {
            this.severityList.clear();
        }
        this.acceptedCategory = -1;
    }

    /**
     * Accepts a system event - or not
     */
    public boolean accept(SystemEvent systemEvent) {
        boolean severityAccepted = false;
        synchronized (this.severityList) {
            severityAccepted = this.severityList.contains(Integer.valueOf(systemEvent.getSeverity()));
        }
        boolean originAccepted = false;
        synchronized (this.originList) {
            originAccepted = this.originList.contains(Integer.valueOf(systemEvent.getOrigin()));
        }
        boolean categoryAccepted = true;
        if( this.acceptedCategory != -1 && systemEvent.getCategory() != this.acceptedCategory ){
            categoryAccepted = false;
        }
        return (severityAccepted && originAccepted && categoryAccepted);
    }

}
