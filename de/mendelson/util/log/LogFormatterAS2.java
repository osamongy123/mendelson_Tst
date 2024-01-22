//$Header: /as2/de/mendelson/util/log/LogFormatterAS2.java 7     27/06/22 12:05 Heller $
package de.mendelson.util.log;

import de.mendelson.comm.as2.message.AS2MDNInfo;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.MessageAccessDB;
import de.mendelson.util.clientserver.log.search.LoglineImplAS2;
import de.mendelson.util.database.IDBDriverManager;
import java.sql.Connection;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Formatter to format the log of mendelson AS2 log file messages
 *
 * @author S.Heller
 */
public class LogFormatterAS2 extends LogFormatter {

    private MessageAccessDB messageAccess = null;

    public LogFormatterAS2(int formatType) {
        super(formatType);
    }

    /**
     * Allows references to the user defined processing id for MDNs
     */
    public LogFormatterAS2(int formatType, IDBDriverManager dbDriverManager) {
        super(formatType);
        this.messageAccess = new MessageAccessDB(dbDriverManager);
    }

    /**
     * Overwrite this method to add product specific parameter to the header
     */
    @Override
    protected void addOutputToLog(int formatType, StringBuilder builder, Object[] recordParameter) {
        if (formatType == LogFormatter.FORMAT_LOGFILE) {
            for (Object parameter : recordParameter) {
                if (parameter instanceof AS2MessageInfo) {
                    AS2MessageInfo messageInfo = (AS2MessageInfo) parameter;
                    builder.append(",");
                    builder.append(LoglineImplAS2.KEY_MESSAGEID).append("=");
                    builder.append(messageInfo.getMessageId());
                    if (messageInfo.getUserdefinedId() != null
                            && !messageInfo.getUserdefinedId().equals("--")) {
                        builder.append(",");
                        builder.append(LoglineImplAS2.KEY_USERDEFINED_ID).append("=");
                        builder.append(messageInfo.getUserdefinedId());
                    }
                } else if (parameter instanceof AS2MDNInfo) {
                    AS2MDNInfo mdnInfo = (AS2MDNInfo) parameter;
                    builder.append(",");
                    builder.append(LoglineImplAS2.KEY_MDNID).append("=");
                    builder.append(mdnInfo.getMessageId());
                    if (mdnInfo.getRelatedMessageId() != null) {
                        builder.append(",");
                        builder.append(LoglineImplAS2.KEY_MESSAGEID).append("=");
                        builder.append(mdnInfo.getRelatedMessageId());
                        //this MDN is related to an outbound message - add this related messages user defined id to the
                        //MDN log
                        if (this.messageAccess != null) {
                            AS2MessageInfo relatedMessageInfo = this.messageAccess.getLastMessageEntry(mdnInfo.getRelatedMessageId());
                            if (relatedMessageInfo != null) {
                                if (relatedMessageInfo.getUserdefinedId() != null
                                        && !relatedMessageInfo.getUserdefinedId().equals("--")) {
                                    builder.append(",");
                                    builder.append(LoglineImplAS2.KEY_USERDEFINED_ID).append("=");
                                    builder.append(relatedMessageInfo.getUserdefinedId());
                                }
                            }
                        }
                    }
                }
            }
        } else if (formatType == LogFormatter.FORMAT_CONSOLE 
                || formatType == LogFormatter.FORMAT_CONSOLE_COLORED) {
            for (Object parameter : recordParameter) {
                if (parameter instanceof AS2MessageInfo) {
                    AS2MessageInfo messageInfo = (AS2MessageInfo) parameter;
                    builder.append("[");
                    builder.append(messageInfo.getMessageId());
                    builder.append("] ");
                } else if (parameter instanceof AS2MDNInfo) {
                    AS2MDNInfo mdnInfo = (AS2MDNInfo) parameter;
                    builder.append("[");
                    builder.append(mdnInfo.getMessageId());
                    builder.append("] ");
                }
            }
        }
    }
}
