//$Header: /mec_as2/de/mendelson/comm/as2/statistic/StatisticDetailEntry.java 1     2/02/22 15:36 Heller $
package de.mendelson.comm.as2.statistic;
import de.mendelson.comm.as2.message.AS2MessageInfo;
import java.io.Serializable;
/**
 * Stores a statistic overview entry
 * @author S.Heller
 * @version $Revision: 1 $
 */
public class StatisticDetailEntry implements Serializable{
        
    public static final long serialVersionUID = 1L;
    public static final int DIRECTION_ALL = 23;
    public static final int DIRECTION_IN = AS2MessageInfo.DIRECTION_IN;
    public static final int DIRECTION_OUT = AS2MessageInfo.DIRECTION_OUT;
        
    private int counter = 0;    
    private String localStation = null;
    private String partner = null;
    private long startTime = 0;
    private long endTime = 0;
    private int direction = DIRECTION_ALL;
    private String seriesName = "";
    
    public StatisticDetailEntry() {
    }
    
    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getLocalStation() {
        return localStation;
    }

    public void setLocalStation(String localStation) {
        this.localStation = localStation;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    /**
     * @return the seriesName
     */
    public String getSeriesName() {
        return seriesName;
    }

    /**
     * @param seriesName the seriesName to set
     */
    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

}
