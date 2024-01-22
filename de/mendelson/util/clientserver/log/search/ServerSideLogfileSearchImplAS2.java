//$Header: /as2/de/mendelson/util/clientserver/log/search/ServerSideLogfileSearchImplAS2.java 1     7.12.18 12:25 Heller $
package de.mendelson.util.clientserver.log.search;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Contains all required methods for a server side log search for OFTP2
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class ServerSideLogfileSearchImplAS2 extends ServerSideLogfileSearch {

    /**Implementation of the logline for a mendelson product - extends the logline by addtional search fields
     * 
     */
    public ServerSideLogfileSearchImplAS2() {
        //minimum is [humandatetime,ms,sequence,(1 additional parameter)]
        super(4);
    }

    @Override
    /**
     * Builds up a lucene query to search for results - uses the product specific fields for the search process
     */
    protected Query buildQueryFromFilter(ServerSideLogfileFilter filter) {
        ServerSideLogfileFilterImplAS2 filterAS2 = (ServerSideLogfileFilterImplAS2) filter;
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        if (filterAS2.getMessageId() != null && !filterAS2.getMessageId().trim().isEmpty()) {
            Query subqueryMessageId = new TermQuery(new Term(LoglineImplAS2.KEY_MESSAGEID, filterAS2.getMessageId()));
            queryBuilder = queryBuilder.add(subqueryMessageId, BooleanClause.Occur.MUST);
        }else if (filterAS2.getUserdefinedId()!= null && !filterAS2.getUserdefinedId().trim().isEmpty()) {
            Query subqueryUserdefinedId = new TermQuery(new Term(LoglineImplAS2.KEY_USERDEFINED_ID, filterAS2.getUserdefinedId()));
            queryBuilder = queryBuilder.add(subqueryUserdefinedId, BooleanClause.Occur.MUST);
        }else if (filterAS2.getMDNId()!= null && !filterAS2.getMDNId().trim().isEmpty()) {
            Query subqueryMDNId = new TermQuery(new Term(LoglineImplAS2.KEY_MDNID, filterAS2.getMDNId()));
            queryBuilder = queryBuilder.add(subqueryMDNId, BooleanClause.Occur.MUST);
        }
        BooleanQuery query = queryBuilder.build();
        return (query);
    }

    @Override
    /**Instanciate an implementation of the logline that contains additional fields that are
     * required for the product specific search
     */
    protected Logline instanciateLogline(String[] headerValues, String logMessage) {
        Logline logline = new LoglineImplAS2(headerValues, logMessage);
        return (logline);
    }

    @Override
    /**Generate a implementation of the Logline from a found lucene document in the search result
     * 
     */
    protected Logline generateLoglineFromSingleSearchResult(Document document) {
        Map<String, String> map = new HashMap<String, String>();
        String logMessage = document.getField(Logline.KEY_LOGMESSAGE).stringValue();
        map.put(Logline.KEY_LOGMESSAGE, logMessage);
        String sequence = document.getField(Logline.KEY_SEQUENCE).stringValue();
        map.put(Logline.KEY_SEQUENCE, sequence);
        String ms = document.getField(Logline.KEY_MILLISECS).stringValue();
        map.put(Logline.KEY_MILLISECS, ms);
        if (document.getField(LoglineImplAS2.KEY_MESSAGEID) != null) {
            String messageId = document.getField(LoglineImplAS2.KEY_MESSAGEID).stringValue();
            if (messageId != null) {
                map.put(LoglineImplAS2.KEY_MESSAGEID, messageId);
            }
        }        
        if (document.getField(LoglineImplAS2.KEY_USERDEFINED_ID) != null) {
            String userDefinedId = document.getField(LoglineImplAS2.KEY_USERDEFINED_ID).stringValue();
            if (userDefinedId != null) {
                map.put(LoglineImplAS2.KEY_USERDEFINED_ID, userDefinedId);
            }
        }
        if (document.getField(LoglineImplAS2.KEY_MDNID) != null) {
            String mdnId = document.getField(LoglineImplAS2.KEY_MDNID).stringValue();
            if (mdnId != null) {
                map.put(LoglineImplAS2.KEY_MDNID, mdnId);
            }
        }
        Logline logLine = new LoglineImplAS2(map);
        return (logLine);
    }

//    public static final void main(String[] args) {
//        ServerSideLogfileFilterImplOFTP2 filter = new ServerSideLogfileFilterImplOFTP2();
//        Calendar calendar = Calendar.getInstance();
//        filter.setEndDate(calendar.getTimeInMillis());
//        calendar.add(Calendar.DAY_OF_YEAR, -2);
//        filter.setStartDate(calendar.getTimeInMillis());
//        filter.setTransmissionId("NOT-CATEGORIZED|20181205|1230380002");
//        System.out.println("Searching for all entries of tid " + filter.getTransmissionId());
//        ServerSideLogfileSearch search = new ServerSideLogfileSearchImplOFTP2();
//        List<Logline> result = search.performSearch(filter);
//        System.out.println(result.size() + " log line(s) found");
//        System.out.println();
//        for (Logline line : result) {
//            System.out.println("[" + line.getValue(Logline.KEY_MILLISECS) + "," + line.getValue(Logline.KEY_SEQUENCE) + "]  " + line.getValue(Logline.KEY_LOGMESSAGE));
//        }
//    }

}
