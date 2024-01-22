//$Header: /as2/de/mendelson/util/clientserver/log/search/LoglineImplAS2.java 1     7.12.18 12:25 Heller $
package de.mendelson.util.clientserver.log.search;

import java.io.Serializable;
import java.util.Map;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Stores the information about an event
 *
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class LoglineImplAS2 extends Logline implements Serializable {

    public static final long serialVersionUID = 1L;

    public static final String KEY_MESSAGEID = "msgid";
    public static final String KEY_MDNID = "mdnid";
    public static final String KEY_USERDEFINED_ID = "uid";

    /**
     * Pre parsed data
     */
    public LoglineImplAS2(Map<String, String> map) {
        super(map);
    }

    public LoglineImplAS2(String[] header, String logMessage) {
        super(header, logMessage);
    }

    @Override
    public void addAdditionalFieldsToDocument(Document luceneDocument) {
        if (this.getValue(KEY_MESSAGEID) != null) {
            luceneDocument.add(new StringField(
                    KEY_MESSAGEID, this.getValue(KEY_MESSAGEID),
                    Field.Store.YES));
        }
        if (this.getValue(KEY_MDNID) != null) {
            luceneDocument.add(new StringField(
                    KEY_MDNID, this.getValue(KEY_MDNID),
                    Field.Store.YES));
        }
        if (this.getValue(KEY_USERDEFINED_ID) != null) {
            luceneDocument.add(new StringField(
                    KEY_USERDEFINED_ID, this.getValue(KEY_USERDEFINED_ID),
                    Field.Store.YES));
        }
    }

}
