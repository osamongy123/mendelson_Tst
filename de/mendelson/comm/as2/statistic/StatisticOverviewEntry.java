//$Header: /mec_as2/de/mendelson/comm/as2/statistic/StatisticOverviewEntry.java 2     3.07.08 16:44 Heller $
package de.mendelson.comm.as2.statistic;
import java.io.Serializable;
import java.util.Date;
/**
 * Stores a statistic overview entry
 * @author S.Heller
 * @version $Revision: 2 $
 */
public class StatisticOverviewEntry implements Serializable{

    public StatisticOverviewEntry() {
    }

    public String getLocalStationId() {
        return "";
    }

    public void setLocalStationId(String localStationId) {        
    }

    public String getPartnerId() {
        return "";
    }

    public void setPartnerId(String partnerId) {
    }

    public int getSendMessageCount() {
        return 0;
    }

    public void setSendMessageCount(int sendMessageCount) {
    }

    public int getReceivedMessageCount() {
        return 0;
    }

    public void setReceivedMessageCount(int receivedMessageCount) {
    }

    public int getSendWithFailureCount() {
        return 0;
    }

    public void setSendWithFailureCount(int sendWithFailureCount) {
    }

    public int getReceivedWithFailureCount() {
        return(0);
    }

    public void setReceivedWithFailureCount(int receivedWithFailureCount) {
    }

    public Date getResetDate() {
        return new Date();
    }

    public void setResetDate(Date resetDate) {
    }
}
