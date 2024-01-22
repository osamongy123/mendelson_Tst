//$Header: /as2/de/mendelson/comm/as2/message/loggui/TableModelMessageOverview.java 35    22/11/22 16:38 Heller $
package de.mendelson.comm.as2.message.loggui;

import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import de.mendelson.comm.as2.message.AS2Payload;
import de.mendelson.comm.as2.message.ResourceBundleAS2Message;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.util.ImageUtil;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Model to display the message overview
 *
 * @author S.Heller
 * @version $Revision: 35 $
 */
public class TableModelMessageOverview extends AbstractTableModel {

    public static final int ROW_HEIGHT = 20;
    protected static final int IMAGE_HEIGHT = ROW_HEIGHT - 3;

    public static final ImageIcon ICON_IN
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/in.svg", IMAGE_HEIGHT, IMAGE_HEIGHT * 3));
    public static final ImageIcon ICON_OUT
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/out.svg", IMAGE_HEIGHT, IMAGE_HEIGHT * 3));
    public static final ImageIcon ICON_PENDING
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/state_pending.svg", IMAGE_HEIGHT, IMAGE_HEIGHT * 3));
    public static final ImageIcon ICON_STOPPED
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/state_stopped.svg", IMAGE_HEIGHT, IMAGE_HEIGHT * 3));
    public static final ImageIcon ICON_FINISHED
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/state_finished.svg", IMAGE_HEIGHT, IMAGE_HEIGHT * 3));
    public static final ImageIcon ICON_RESEND_OVERLAY
            = new ImageIcon(MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/comm/as2/message/loggui/resend_overlay.svg", IMAGE_HEIGHT, IMAGE_HEIGHT * 3));

    /**
     * ResourceBundle to localize the headers
     */
    private final MecResourceBundle rb;
    /**
     * ResourceBundle to localize the enc/signature stuff
     */
    private final MecResourceBundle rbMessage;
    /**
     * Stores all partner ids and the corresponding partner objects
     */
    private final Map<String, Partner> partnerMap = new ConcurrentHashMap<String, Partner>();
    /**
     * Data to display
     */
    private final List<AS2Message> data = Collections.synchronizedList(new ArrayList<AS2Message>());
    /**
     * Format the date display
     */
    private final DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    /**
     * Creates new LogTableModel
     */
    public TableModelMessageOverview() {
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleMessageOverview.class.getName());
            this.rbMessage = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleAS2Message.class.getName());
        } //load up  resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
    }

    /**
     * Passes a list of partner ot this table
     *
     */
    public void passPartner(Map<String, Partner> partnerMap) {
        this.partnerMap.putAll(partnerMap);
        this.fireTableDataChanged();
    }

    /**
     * Passes data to the model and fires a table data update. If there has been already loaded
     * a payload for the message (if it existed already and the new message hasnt one - copy
     * the payload from the existing one. No need to request if again from the server
     */
    public void passNewData(List<AS2Message> newData) {
        synchronized (this.data) {
            List<AS2Message> existingData = new ArrayList<AS2Message>();
            existingData.addAll(this.data);            
            this.data.clear();
            for( AS2Message newMessage:newData){
                this.data.add(newMessage);
                if( newMessage.getPayloadCount() == 0 ){
                    int existingIndex = existingData.indexOf(newMessage);
                    if( existingIndex >= 0 ){
                        AS2Message existingMessage = existingData.get(existingIndex);
                        if( existingMessage.getPayloadCount() > 0){
                            newMessage.setPayloads(existingMessage.getPayloads());
                        }
                    }                    
                }
            }
        }
        ((AbstractTableModel) this).fireTableDataChanged();
    }

    public void passPayload(AS2Message message, List<AS2Payload> payloads) {
        int index = -1;
        synchronized (this.data) {
            index = this.data.indexOf(message);
            if (index != -1) {
                AS2Message foundMessage = this.data.get(index);
                foundMessage.setPayloads(payloads);
            }
        }
        if (index != -1) {
            ((AbstractTableModel) this).fireTableRowsUpdated(index, index);
        }
    }

    /**Returns a list of messages with already updated/existing payloads*/
    public List<AS2Message> getMessagesWithoutPassedPayloads(){
        List<AS2Message> messagePayloadList = new ArrayList<AS2Message>();
        synchronized( this.data ){
            for( AS2Message message:this.data ){
                if( message.getPayloadCount() == 0 ){
                    messagePayloadList.add( message );
                }
            }
        }
        return( messagePayloadList );
    }
    
    
    /**
     * Returns the data stored in the specific row
     *
     * @param row Row to look into
     */
    public AS2Message getRow(int row) {
        synchronized (this.data) {
            if (row > this.data.size() - 1) {
                return (null);
            }
            return (this.data.get(row));
        }
    }

    /**
     * Returns the data stored in specific rows
     *
     * @param row Rows to look into
     */
    public AS2Message[] getRows(int[] row) {
        AS2Message[] rows = new AS2Message[row.length];
        synchronized (this.data) {
            for (int i = 0; i < row.length; i++) {
                rows[i] = this.data.get(row[i]);
            }
            return (rows);
        }
    }

    /**
     * Number of rows to display
     */
    @Override
    public int getRowCount() {
        synchronized (this.data) {
            return (this.data.size());
        }
    }

    /**
     * Number of cols to display
     */
    @Override
    public int getColumnCount() {
        return (13);
    }

    /**
     * Returns a value at a specific position in the grid
     */
    @Override
    public Object getValueAt(int row, int col) {
        AS2Message overviewRow = null;
        synchronized (this.data) {
            overviewRow = this.data.get(row);
        }
        AS2MessageInfo info = (AS2MessageInfo) overviewRow.getAS2Info();
        switch (col) {
            case 0:
                if (info.getState() == AS2Message.STATE_FINISHED) {
                    if (info.getResendCounter() == 0) {
                        return (ICON_FINISHED);
                    } else {
                        return (ImageUtil.mixImages(ICON_FINISHED, ICON_RESEND_OVERLAY));
                    }
                } else if (info.getState() == AS2Message.STATE_STOPPED) {
                    if (info.getResendCounter() == 0) {
                        return (ICON_STOPPED);
                    } else {
                        return (ImageUtil.mixImages(ICON_STOPPED, ICON_RESEND_OVERLAY));
                    }
                }
                return (ICON_PENDING);
            case 1:
                if (info.getDirection() == AS2MessageInfo.DIRECTION_IN) {
                    return (ICON_IN);
                } else {
                    return (ICON_OUT);
                }
            case 2:
                return (info.getInitDate());
            case 3:
                if (info.getDirection() != AS2MessageInfo.DIRECTION_IN) {
                    String id = info.getSenderId();
                    Partner sender = this.partnerMap.get(id);
                    if (sender != null) {
                        return (sender.getName());
                    } else {
                        return (id);
                    }
                } else {
                    String id = info.getReceiverId();
                    Partner receiver = this.partnerMap.get(id);
                    if (receiver != null) {
                        return (receiver.getName());
                    } else {
                        return (id);
                    }
                }
            case 4:
                if (info.getDirection() == AS2MessageInfo.DIRECTION_IN) {
                    String id = info.getSenderId();
                    Partner sender = this.partnerMap.get(id);
                    if (sender != null) {
                        return (sender.getName());
                    } else {
                        return (id);
                    }
                } else {
                    String id = info.getReceiverId();
                    Partner receiver = this.partnerMap.get(id);
                    if (receiver != null) {
                        return (receiver.getName());
                    } else {
                        return (id);
                    }
                }
            case 5:
                return (info.getMessageId());
            case 6:
                if (overviewRow.getPayloadCount() == 0
                        || (overviewRow.getPayloadCount() == 1 && overviewRow.getPayload(0).getOriginalFilename() == null)) {
                    return ("--");
                } else if (overviewRow.getPayloadCount() == 1) {
                    return (overviewRow.getPayload(0).getOriginalFilename());
                } else {
                    return (this.rb.getResourceString("number.of.attachments", String.valueOf(overviewRow.getPayloadCount())));
                }
            case 7:
                return (this.rbMessage.getResourceString("encryption." + info.getEncryptionType()));
            case 8:
                return (this.rbMessage.getResourceString("signature." + info.getSignType()));
            case 9:
                if (info.requestsSyncMDN()) {
                    return ("SYNC");
                } else {
                    return ("ASYNC");
                }
            case 10:
                if (info.getUserdefinedId() == null) {
                    return ("--");
                } else {
                    return (info.getUserdefinedId());
                }
            case 11:
                if (info.getSubject() == null) {
                    return ("");
                } else {
                    return (info.getSubject());
                }
            case 12:
                if (info.getCompressionType() == AS2Message.COMPRESSION_ZLIB) {
                    return (Boolean.TRUE);
                } else {
                    return (Boolean.FALSE);
                }
        }
        return (null);
    }

    /**
     * Returns the name of every column
     *
     * @param col Column to get the header name of
     */
    @Override
    public String getColumnName(int col) {
        switch (col) {
            case 0:
                return (" ");
            case 1:
                return ("  ");
            case 2:
                return (this.rb.getResourceString("header.timestamp"));
            case 3:
                return (this.rb.getResourceString("header.localstation"));
            case 4:
                return (this.rb.getResourceString("header.partner"));
            case 5:
                return (this.rb.getResourceString("header.messageid"));
            case 6:
                return (this.rb.getResourceString("header.payload"));
            case 7:
                return (this.rb.getResourceString("header.encryption"));
            case 8:
                return (this.rb.getResourceString("header.signature"));
            case 9:
                return (this.rb.getResourceString("header.mdn"));
            case 10:
                return (this.rb.getResourceString("header.userdefinedid"));
            case 11:
                return (this.rb.getResourceString("header.subject"));
            case 12:
                return (this.rb.getResourceString("header.compression"));
        }
        return (null);
    }

    /**
     * Set how to display the grid elements
     *
     * @param col requested column
     */
    @Override
    public Class getColumnClass(int col) {
        return (new Class[]{
            ImageIcon.class,
            ImageIcon.class,
            Date.class,
            String.class,
            String.class,
            String.class,
            String.class,
            String.class,
            String.class,
            String.class,
            String.class,
            String.class,
            Boolean.class,}[col]);
    }
}
