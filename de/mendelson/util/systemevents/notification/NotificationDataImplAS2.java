//$Header: /as2/de/mendelson/util/systemevents/notification/NotificationDataImplAS2.java 9     13/10/22 10:05 Heller $
package de.mendelson.util.systemevents.notification;

import de.mendelson.util.oauth2.OAuth2Config;
import java.io.Serializable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Implementation of a server log for the as2 server database
 * @author S.Heller
 * @version $Revision: 9 $
 */
public class NotificationDataImplAS2 extends NotificationData implements Serializable{
   
    public static final long serialVersionUID = 1L;
    
    private String notificationMail = null;
    private String mailServer = null;
    private int mailServerPort = 25;
    private boolean notifyCertExpire = false;
    private boolean notifyTransactionError = false;
    private boolean notifyCEM = false;
    private boolean notifySystemFailure = false;
    private boolean notifyResendDetected = true;
    private boolean notifyConnectionProblem = false;
    private boolean notifyPostprocessingProblem = false;
    /**Makes no sense but some mail servers require this to be a valid email from the same host to prevent SPAM sending*/
    private String replyTo = null;
    private boolean useSMTPAuthCredentials = false;
    private boolean useSMTPAuthOAuth2 = false;
    private String smtpUser = null;
    private char[] smtpPass = null;
    private int connectionSecurity = SECURITY_PLAIN;
    private int maxNotificationsPerMin = 2;
    private OAuth2Config oAuth2Config = null;

    @Override
    public String getNotificationMail() {
        return notificationMail;
    }

    public void setNotificationMail(String notificationMail) {
        this.notificationMail = notificationMail;
    }

    @Override
    public String getMailServer() {
        return mailServer;
    }

    public void setMailServer(String mailServer) {
        this.mailServer = mailServer;
    }

    @Override
    public int getMailServerPort() {
        return mailServerPort;
    }

    public void setMailServerPort(int mailServerPort) {
        this.mailServerPort = mailServerPort;
    }


    public boolean notifyCertExpire() {
        return notifyCertExpire;
    }

    public void setNotifyCertExpire(boolean notifyCertExpire) {
        this.notifyCertExpire = notifyCertExpire;
    }

    public boolean notifyTransactionError() {
        return notifyTransactionError;
    }

    public void setNotifyTransactionError(boolean notifyTransactionError) {
        this.notifyTransactionError = notifyTransactionError;
    }

    @Override
    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }
    
    /**Serializes this notification data object to XML
     * @param level level in the XML hierarch for the XML beautifying
     */
    public String toXML(int level) {
        StringBuilder builder = new StringBuilder();
        String offset = "";
        for (int i = 0; i < level; i++) {
            offset += "\t";
        }
        builder.append(offset).append("<notification>\n");
        builder.append(offset).append("\t<mailserver>").append(this.toCDATA(this.mailServer)).append("</mailserver>\n");
        builder.append(offset).append("\t<mailserverport>").append(this.mailServerPort).append("</mailserverport>\n");
        builder.append(offset).append("\t<connectionsecurity>").append(this.connectionSecurity).append("</connectionsecurity>\n");
        builder.append(offset).append("\t<notificationmail>").append(this.toCDATA(this.notificationMail)).append("</notificationmail>\n");
        builder.append(offset).append("\t<notifycertexpire>").append(String.valueOf(this.notifyCertExpire)).append("</notifycertexpire>\n");
        builder.append(offset).append("\t<notifytransactionerror>").append(String.valueOf(this.notifyTransactionError)).append("</notifytransactionerror>\n");
        builder.append(offset).append("\t<notifysystemfailure>").append(String.valueOf(this.notifySystemFailure)).append("</notifysystemfailure>\n");
        builder.append(offset).append("\t<notifycem>").append(String.valueOf(this.notifyCEM)).append("</notifycem>\n");
        builder.append(offset).append("\t<notifyconnectionproblem>").append(String.valueOf(this.notifyConnectionProblem)).append("</notifyconnectionproblem>\n");
        builder.append(offset).append("\t<replyto>").append(this.toCDATA(this.replyTo)).append("</replyto>\n");
        builder.append(offset).append("\t<maxnotificationspermin>").append(this.toCDATA(String.valueOf(this.maxNotificationsPerMin))).append("</maxnotificationspermin>\n");
        builder.append(offset).append("\t<useauthorizationcredentials>").append(this.toCDATA(String.valueOf(this.useSMTPAuthCredentials))).append("</useauthorizationcredentials>\n");
        builder.append(offset).append("\t<authorizationcredentialsuser>").append(this.toCDATA(String.valueOf(this.smtpUser==null?"":this.smtpUser))).append("</authorizationcredentialsuser>\n");
        builder.append(offset).append("\t<authorizationcredentialspass>").append(this.toCDATA(String.valueOf(this.smtpPass==null?"":new String(this.smtpPass)))).append("</authorizationcredentialspass>\n");
        builder.append(offset).append("\t<useauthorizationoauth2>").append(this.toCDATA(String.valueOf(this.useSMTPAuthOAuth2))).append("</useauthorizationoauth2>\n");
        if( this.oAuth2Config != null ){
            builder.append(offset).append(this.oAuth2Config.toXML(level+1, offset, this.useSMTPAuthOAuth2));
        }
        builder.append(offset).append("</notification>\n");
        return (builder.toString());
    }

    /**Adds a cdata indicator to xml data*/
    private String toCDATA(String data) {
        return ("<![CDATA[" + data + "]]>");
    }

    /**Deserializes a notification from an XML node*/
    public static NotificationData fromXML(Element element) {
        NotificationDataImplAS2 notification = new NotificationDataImplAS2();
        NodeList notificationNodeList = element.getChildNodes();
        for (int i = 0; i < notificationNodeList.getLength(); i++) {
            if (notificationNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element property = (Element) notificationNodeList.item(i);
                String key = property.getTagName();
                String value = property.getTextContent();
                if (key.equals("mailserver")) {
                    notification.setMailServer(value);
                } else if (key.equals("mailserverport")) {
                    notification.setMailServerPort(Integer.valueOf(value).intValue());
                } else if (key.equals("notificationmail")) {
                    notification.setNotificationMail(value);
                } else if (key.equals("notifycertexpire")) {
                    notification.setNotifyCertExpire(value.equalsIgnoreCase("true"));
                } else if (key.equals("notifytransactionerror")) {
                    notification.setNotifyTransactionError(value.equalsIgnoreCase("true"));
                }else if (key.equals("notifysystemfailure")) {
                    notification.setNotifySystemFailure(value.equalsIgnoreCase("true"));
                }else if (key.equals("notifyconnectionproblem")) {
                    notification.setNotifyConnectionProblem(value.equalsIgnoreCase("true"));
                }else if (key.equals("notifycem")) {
                    notification.setNotifyCEM(value.equalsIgnoreCase("true"));
                } else if (key.equals("replyto")) {
                    notification.setReplyTo(value);
                }else if (key.equals("connectionsecurity")) {
                    notification.setConnectionSecurity(Integer.valueOf(value).intValue());
                }else if (key.equals("maxnotificationspermin")) {
                    notification.setMaxNotificationsPerMin(Integer.valueOf(value).intValue());
                }
            }
        }
        return (notification);
    }

    /**
     * @return the notifyCEM
     */
    public boolean notifyCEM() {
        return notifyCEM;
    }

    /**
     * @param notifyCEM the notifyCEM to set
     */
    public void setNotifyCEM(boolean notifyCEM) {
        this.notifyCEM = notifyCEM;
    }

    @Override
    public boolean usesSMTPAuthCredentials() {
        return useSMTPAuthCredentials;
    }
 
    /**
     * @param useSMTHAuth the useSMTHAuth to set
     */    
    public void setUsesSMTPAuthCredentials(boolean useSMTPAuthCredentials) {
        this.useSMTPAuthCredentials = useSMTPAuthCredentials;
    }
    
    @Override
    public boolean usesSMTPAuthOAuth2() {
        return useSMTPAuthOAuth2;
    }
 
    /**
     * @param useSMTHAuth the useSMTHAuth to set
     */    
    public void setUsesSMTPAuthOAuth2(boolean useSMTPAuthOAuth2) {
        this.useSMTPAuthOAuth2 = useSMTPAuthOAuth2;
    }
    
    /**
     * @return the smtpUser
     */
    @Override
    public String getSMTPUser() {
        return smtpUser;
    }

    /**
     * @param smtpUser the smtpUser to set
     */
    public void setSMTPUser(String smtpUser) {
        this.smtpUser = smtpUser;
    }

    /**
     * @return the smtpPass
     */
    @Override
    public char[] getSMTPPass() {
        return smtpPass;
    }

    /**
     * @param smtpPass the smtpPass to set
     */
    public void setSMTPPass(char[] smtpPass) {
        this.smtpPass = smtpPass;
    }

    /**
     * @return the notifySystemFailure
     */
    public boolean notifySystemFailure() {
        return notifySystemFailure;
    }

    /**
     * @param notifySystemFailure the notifySystemFailure to set
     */
    public void setNotifySystemFailure(boolean notifySystemFailure) {
        this.notifySystemFailure = notifySystemFailure;
    }

    /**
     * @return the notifyResendDetected
     */
    public boolean notifyResendDetected() {
        return( this.notifyResendDetected);
    }

    /**
     * @param notifyResendDetected the notifyResendDetected to set
     */
    public void setNotifyResendDetected(boolean notifyResendDetected) {
        this.notifyResendDetected = notifyResendDetected;
    }

    /**
     * @return the security
     */
    @Override
    public int getConnectionSecurity() {
        return connectionSecurity;
    }

    /**
     * @param connectionSecurity the connection security to set
     */
    public void setConnectionSecurity(int connectionSecurity) {
        this.connectionSecurity = connectionSecurity;
    }

    /**
     * @return the maxNotificationsPerMin
     */
    @Override
    public int getMaxNotificationsPerMin() {
        return maxNotificationsPerMin;
    }

    /**
     * @param maxNotificationsPerMin the maxNotificationsPerMin to set
     */
    public void setMaxNotificationsPerMin(int maxNotificationsPerMin) {
        this.maxNotificationsPerMin = maxNotificationsPerMin;
    }
    
    /**
     * @return the notifyConnectionProblems
     */
    public boolean notifyConnectionProblem() {
        return notifyConnectionProblem;
    }

    /**
     * @param notifyConnectionProblems the notifyConnectionProblems to set
     */
    public void setNotifyConnectionProblem(boolean notifyConnectionProblem) {
        this.notifyConnectionProblem = notifyConnectionProblem;
    }

     /**
     * @return the notifyPostprocessingProblem
     */
    public boolean notifyPostprocessingProblem() {
        return notifyPostprocessingProblem;
    }

    /**
     * @param notifyPostprocessingProblem the notifyPostprocessingProblem to set
     */
    public void setNotifyPostprocessingProblem(boolean notifyPostprocessingProblem) {
        this.notifyPostprocessingProblem = notifyPostprocessingProblem;
    }

    /**Returns the OAuth2 config - might be null*/
    @Override
    public OAuth2Config getOAuth2Config() {
        return( this.oAuth2Config);
    }
    
    public void setOAuth2Config(OAuth2Config oAuth2Config){
        this.oAuth2Config = oAuth2Config;
    }

    
    
}
