//$Header: /as4/de/mendelson/util/systemevents/notification/Notification.java 29    14/10/22 9:13 Heller $
package de.mendelson.util.systemevents.notification;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.systemevents.SystemEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software. Other product
 * and brand names are trademarks of their respective owners.
 */
/**
 * Performs the notification for an event
 *
 * @author S.Heller
 * @version $Revision: 29 $
 */
public abstract class Notification {

    private final String MODULE_NAME;
    private final MecResourceBundle rb;

    public Notification() {
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleNotification.class.getName());
        } //load up  resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        MODULE_NAME = rb.getResourceString("module.name");
    }

    /**
     * Sends a test notification
     *
     */
    public abstract void sendTest(String userName, String processOriginHost,
            NotificationData notificationData) throws Exception;

    /**
     * Generates a subject line addition that has the format
     * "[<product>@<hostname]"
     */
    public abstract String getNotificationSubjectServerIdentification();

    //should mainly be implemented by the code rb.getResourceString("test.message.debug")
    public abstract String getTestMessageDebugStr();

    /**
     * Returns the footer that should be added to the notification mail
     */
    public abstract String getNotificationFooter();

    /**
     * Returns the default session for the mail send process
     */
    private Session getSessionInstance(NotificationData notificationData) {
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", notificationData.getMailServer());
        properties.setProperty("mail.smtp.port", String.valueOf(notificationData.getMailServerPort()));
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.connectiontimeout", String.valueOf(TimeUnit.SECONDS.toMillis(10)));
        properties.setProperty("mail.smtp.timeout", String.valueOf(TimeUnit.SECONDS.toMillis(10)));
        if (notificationData.getConnectionSecurity() == NotificationData.SECURITY_START_TLS) {
            properties.setProperty("mail.smtp.starttls.enable", "true");
            properties.setProperty("mail.smtp.ssl.protocols", "SSLv3 TLSv1 TLSv1.1 TLSv1.2 TLSv1.3");
        } else if (notificationData.getConnectionSecurity() == NotificationData.SECURITY_TLS) {
            properties.setProperty("mail.smtp.ssl.enable", "true");
            properties.setProperty("mail.smtp.ssl.protocols", "SSLv3 TLSv1 TLSv1.1 TLSv1.2 TLSv1.3");
        }
        Session session = null;
        if (notificationData.usesSMTPAuthCredentials()) {
            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.debug.auth", "true");
            session = Session.getInstance(properties,
                    new SendMailAuthenticator(notificationData.getSMTPUser(),
                            String.valueOf(notificationData.getSMTPPass())));
        } else if (notificationData.usesSMTPAuthOAuth2() && notificationData.getOAuth2Config() != null) {
            properties.setProperty("mail.smtp.auth.mechanisms", "XOAUTH2");
            properties.setProperty("mail.smtp.auth", "true");
            properties.setProperty("mail.debug.auth", "true");
            session = Session.getInstance(properties);
        } else {
            session = Session.getInstance(properties);
        }
        return (session);
    }

    /**
     * Sends out the notification to the user
     */
    public abstract void sendNotification(List<SystemEvent> systemEventsToNotifyUserOf,
            NotificationData notificationData);

    /**
     *
     * @param productName
     * @param event
     * @param notificationData
     * @param displayTrace
     * @return The trace of the process if this is requested or just an empty
     * String if it was not requested
     * @throws Exception
     */
    @SuppressWarnings("static-access")
    protected String sendMail(String productName, SystemEvent event,
            NotificationData notificationData, boolean displayTrace) throws Exception {

        Session session = this.getSessionInstance(notificationData);
        ByteArrayOutputStream traceOut = new ByteArrayOutputStream();
        if (displayTrace) {
            PrintStream debugPrintStream = new PrintStream(traceOut);
            session.setDebugOut(debugPrintStream);
            session.setDebug(true);
        }
        // construct the message
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(notificationData.getReplyTo()));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(notificationData.getNotificationMail(), false));
        String subject = event.getSubject();
        if (subject == null) {
            subject = "";
        }
        //add the server identification to the subject before performing the notification
        if (!subject.startsWith(this.getNotificationSubjectServerIdentification())) {
            subject = this.getNotificationSubjectServerIdentification() + " " + subject;
        }
        msg.setSubject(subject);
        String bodyText = event.getBody();
        String footer = this.getNotificationFooter();
        if (footer != null && !footer.trim().isEmpty()) {
            bodyText = bodyText
                    + System.lineSeparator()
                    + System.lineSeparator()
                    + System.lineSeparator()
                    + "--"
                    + System.lineSeparator()
                    + footer;
        }
        msg.setText(bodyText);
        msg.setSentDate(new Date());
        msg.setHeader("X-Mailer", productName);
        // send the message
        Transport transport = null;
        try {
            transport = session.getTransport("smtp");
            if (notificationData.getOAuth2Config() != null) {
                transport.connect(notificationData.getOAuth2Config().getUserName(),
                        notificationData.getOAuth2Config().getAccessTokenStr());
                transport.sendMessage(msg, msg.getAllRecipients());
            } else {
                transport.send(msg);
            }
        } catch (Throwable e) {
            if (e instanceof SendFailedException) {
                SendFailedException sendFailedException = (SendFailedException) e;
                Address failedAddresses[] = sendFailedException.getInvalidAddresses();
                StringBuilder errorMessage = new StringBuilder();
                if (failedAddresses != null) {
                    errorMessage.append("The following mail addresses are invalid:").append("\n");
                    for (Address address : failedAddresses) {
                        errorMessage.append(address.toString()).append("\n");
                    }
                }
                Address validUnsentAddresses[] = sendFailedException.getValidUnsentAddresses();
                if (validUnsentAddresses != null) {
                    errorMessage.append("No mail has been sent to the following valid addresses:").append("\n");
                    for (Address address : validUnsentAddresses) {
                        errorMessage.append(address.toString()).append("\n");
                    }
                }
                StringBuilder errorLog = new StringBuilder();
                errorLog.append("[");
                errorLog.append(sendFailedException.getClass().getSimpleName());
                errorLog.append("] ");
                errorLog.append(sendFailedException.getMessage()).append("\n");
                errorLog.append(errorMessage.toString());
                String errorLogStr = MODULE_NAME + " " + this.replace(errorLog.toString(), "\n", "\n" + MODULE_NAME);
                Exception detailledException = new Exception(errorLogStr, e);
                throw (detailledException);
            } else {
                StringBuilder errorLog = new StringBuilder();
                errorLog.append(this.getTestMessageDebugStr());
                errorLog.append(traceOut.toString());
                errorLog.append("\n[");
                errorLog.append(e.getClass().getSimpleName());
                errorLog.append("] ");
                errorLog.append(e.getMessage());
                if (e.getCause() != null) {
                    errorLog.append(" - caused by [" + e.getCause().getClass().getName() + "] ");
                    errorLog.append(e.getCause().getMessage());
                    if (e.getCause() instanceof SocketTimeoutException) {
                        errorLog.append("\nThere listens a server on the SMTP host \"" + notificationData.getMailServer()
                                + ":" + notificationData.getMailServerPort() + "\" but this seems either not to be a mail server "
                                + "or it does not answer to any request.");
                    }
                }
                String errorLogStr = MODULE_NAME + " " + this.replace(errorLog.toString(), "\n", "\n" + MODULE_NAME + " ");
                Exception detailledException = new Exception(errorLogStr, e);
                throw (detailledException);
            }
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } finally {
                }
            }
            if (traceOut != null) {
                traceOut.close();
            }
        }
        return (traceOut.toString());
    }

    /**
     * Replaces the string tag by the string replacement in the sourceString
     *
     * @param source Source string
     * @param tag	String that will be replaced
     * @param replacement String that will replace the tag
     * @return String that contains the replaced values
     */
    private String replace(String source, String tag, String replacement) {
        if (source == null) {
            return null;
        }
        StringBuilder buffer = new StringBuilder();
        while (true) {
            int index = source.indexOf(tag);
            if (index == -1) {
                buffer.append(source);
                return (buffer.toString());
            }
            buffer.append(source.substring(0, index));
            buffer.append(replacement);
            source = source.substring(index + tag.length());
        }
    }

    /**
     * Used for the SMTP authentication, this is required by some mail servers
     */
    private static class SendMailAuthenticator extends Authenticator {

        private final String user;
        private final String password;

        public SendMailAuthenticator(String user, String password) {
            this.user = user;
            this.password = password;
        }

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(
                    this.user, this.password);
        }
    }
}
