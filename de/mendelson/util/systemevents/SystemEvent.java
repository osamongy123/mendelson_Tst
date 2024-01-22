//$Header: /oftp2/de/mendelson/util/systemevents/SystemEvent.java 50    13/10/22 16:36 Heller $
package de.mendelson.util.systemevents;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.MendelsonMultiResolutionImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.UUID;
import javax.swing.ImageIcon;


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
 * @version $Revision: 50 $
 */
public class SystemEvent implements Serializable {

    public static final long serialVersionUID = 1L;

    public static final MendelsonMultiResolutionImage ICON_SEVERITY_ERROR_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/state_stopped.svg", 11, 32);
    public static final MendelsonMultiResolutionImage ICON_SEVERITY_WARNING_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/state_pending.svg", 11, 32);
    public static final MendelsonMultiResolutionImage ICON_SEVERITY_INFO_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/severity_info.svg", 11, 32);
    public static final MendelsonMultiResolutionImage ICON_ORIGIN_SYSTEM_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/origin_system.svg", 11, 32);
    public static final MendelsonMultiResolutionImage ICON_ORIGIN_TRANSACTION_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/messagedetails.svg", 11, 32);
    public static final MendelsonMultiResolutionImage ICON_ORIGIN_USER_MULTIRESOLUTION
            = MendelsonMultiResolutionImage.fromSVG(
                    "/de/mendelson/util/systemevents/gui/origin_user.svg", 11, 32);
    /**
     * Its a system shutdown, restart etc
     */
    public static final int ORIGIN_SYSTEM = 1;
    /**
     * The user changed a certificate, changed configuration etc
     */
    public static final int ORIGIN_USER = 2;
    /**
     * Any transaction related event
     */
    public static final int ORIGIN_TRANSACTION = 3;
    /**
     * The user should be notified, e.g. a new certificate via certificate
     * exchange. No problem, just a user information
     */
    public static final int SEVERITY_INFO = 1;
    /**
     * An warning occurred in the system. Non critical, e.g. a certificate will
     * expire
     */
    public static final int SEVERITY_WARNING = 2;
    /**
     * An error occurred in the system, e.g. database problem, resource problems
     * etc
     */
    public static final int SEVERITY_ERROR = 3;

    /**
     * System components
     */
    public static final int CATEGORY_SERVER_COMPONENTS = 100;
    public static final int TYPE_SERVER_COMPONENTS_ANY = 199;
    public static final int TYPE_MAIN_SERVER_SHUTDOWN = 100;
    public static final int TYPE_MAIN_SERVER_STARTUP_BEGIN = 101;
    public static final int TYPE_MAIN_SERVER_RUNNING = 102;
    public static final int TYPE_DATABASE_SERVER_STARTUP_BEGIN = 103;
    public static final int TYPE_DATABASE_SERVER_RUNNING = 104;
    public static final int TYPE_DATABASE_SERVER_SHUTDOWN = 105;
    public static final int TYPE_HTTP_SERVER_STARTUP_BEGIN = 106;
    public static final int TYPE_HTTP_SERVER_RUNNING = 107;
    public static final int TYPE_HTTP_SERVER_SHUTDOWN = 108;
    public static final int TYPE_TRFC_SERVER_STARTUP_BEGIN = 109;
    public static final int TYPE_TRFC_SERVER_RUNNING = 110;
    public static final int TYPE_TRFC_SERVER_STATE = 111;
    public static final int TYPE_TRFC_SERVER_SHUTDOWN = 112;
    public static final int TYPE_SCHEDULER_SERVER_STARTUP_BEGIN = 113;
    public static final int TYPE_SCHEDULER_SERVER_RUNNING = 114;
    public static final int TYPE_SCHEDULER_SERVER_SHUTDOWN = 115;
    public static final int TYPE_DIRECTORY_MONITORING_STATE_CHANGED = 116;
    public static final int TYPE_PORT_LISTENER = 117;

    /**
     * Connectivity
     */
    public static final int CATEGORY_CONNECTIVITY = 200;
    public static final int TYPE_CONNECTIVITY_ANY = 200;
    public static final int TYPE_CONNECTIVITY_TEST = 201;
    /**
     * Transactions
     */
    public static final int CATEGORY_TRANSACTION = 300;
    public static final int TYPE_TRANSACTION_ANY = 300;
    public static final int TYPE_TRANSACTION_ERROR = 301;
    public static final int TYPE_TRANSACTION_REJECTED_RESEND = 302;
    public static final int TYPE_TRANSACTION_DUPLICATE_MESSAGE = 303;
    public static final int TYPE_TRANSACTION_DELETE = 304;
    public static final int TYPE_TRANSACTION_CANCEL = 305;
    public static final int TYPE_TRANSACTION_RESEND = 306;
    /**
     * Certificates
     */
    public static final int CATEGORY_CERTIFICATE = 400;
    public static final int TYPE_CERTIFICATE_ANY = 400;
    public static final int TYPE_CERTIFICATE_ADD = 401;
    public static final int TYPE_CERTIFICATE_MODIFY = 402;
    public static final int TYPE_CERTIFICATE_DEL = 403;
    public static final int TYPE_CERTIFICATE_EXCHANGE_ANY = 404;
    public static final int TYPE_CERTIFICATE_EXPIRE = 405;
    public static final int TYPE_CERTIFICATE_EXCHANGE_REQUEST_RECEIVED = 406;
    /**
     * Database
     */
    public static final int CATEGORY_DATABASE = 500;
    public static final int TYPE_DATABASE_ANY = 500;
    public static final int TYPE_DATABASE_CREATION = 501;
    public static final int TYPE_DATABASE_UPDATE = 502;
    public static final int TYPE_DATABASE_INITIALIZATION = 503;
    /**
     * Configuration
     */
    public static final int CATEGORY_CONFIGURATION = 700;
    public static final int TYPE_SERVER_CONFIGURATION_ANY = 700;
    public static final int TYPE_SERVER_CONFIGURATION_CHANGED = 701;
    public static final int TYPE_SERVER_CONFIGURATION_CHECK = 702;
    public static final int TYPE_PARTNER_MODIFY = 703;
    public static final int TYPE_PARTNER_DEL = 704;
    public static final int TYPE_PARTNER_ADD = 705;
    /**
     * Quota
     */
    public static final int CATEGORY_QUOTA = 800;
    public static final int TYPE_QUOTA_ANY = 800;
    public static final int TYPE_QUOTA_SEND_EXCEEDED = 801;
    public static final int TYPE_QUOTA_RECEIVE_EXCEEDED = 802;
    public static final int TYPE_QUOTA_SEND_RECEIVE_EXCEEDED = 803;
    /**
     * Notification
     */
    public static final int CATEGORY_NOTIFICATION = 900;
    public static final int TYPE_NOTIFICATION_ANY = 900;
    public static final int TYPE_NOTIFICATION_SEND_SUCCESS = 901;
    public static final int TYPE_NOTIFICATION_SEND_FAILED = 902;
    /**
     * Processing
     */
    public static final int CATEGORY_PROCESSING = 1000;
    public static final int TYPE_PROCESSING_ANY = 1000;
    public static final int TYPE_PRE_PROCESSING = 1001;
    public static final int TYPE_POST_PROCESSING = 1002;
    /**
     * License issues
     */
    public static final int CATEGORY_LICENSE = 1100;
    public static final int TYPE_LICENSE_ANY = 1100;
    public static final int TYPE_LICENSE_UPDATE = 1101;
    public static final int TYPE_LICENSE_EXPIRE = 1102;

    /**
     * File operation
     */
    public static final int CATEGORY_FILE_OPERATION = 1200;
    public static final int TYPE_FILE_OPERATION_ANY = 1200;
    public static final int TYPE_FILE_DELETE = 1201;
    public static final int TYPE_MKDIR = 1202;
    public static final int TYPE_FILE_MOVE = 1203;
    public static final int TYPE_FILE_COPY = 1204;
    /**
     * Client-Server related operation
     */
    public static final int CATEGORY_CLIENT_OPERATION = 1300;
    public static final int TYPE_CLIENT_ANY = 1300;
    public static final int TYPE_CLIENT_LOGIN_SUCCESS = 1301;
    public static final int TYPE_CLIENT_LOGIN_FAILURE = 1302;
    public static final int TYPE_CLIENT_LOGOFF = 1303;
    /**
     * Other
     */
    public static final int CATEGORY_OTHER = 100000;
    public static final int TYPE_OTHER = 100000;

    private final DateFormat humanReadableEventDateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

    private final static String SECTION_DESCRIPTION = "[Event description]";
    private final static String SECTION_BODY = "[Details]";
    private final static String SECTION_SUBJECT = "[Summary]";

    public static final String USER_SERVER_PROCESS = "<server_process>";

    private long timestamp = System.currentTimeMillis();
    private int severity;
    private int origin;
    private int type;
    private int category;
    private String subject = "";
    private String body = "";
    private String processOriginHost = SystemEventManager.getHostname();
    private String user = USER_SERVER_PROCESS;

    private final String notificationTemplateDir = "notificationtemplates" + FileSystems.getDefault().getSeparator();

    private String id;
    private MecResourceBundle rb;
    private MecResourceBundle rbFilenames;

    public SystemEvent(int severity, int origin, int type) {
        //Load resourcebundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleSystemEvent.class.getName());
            this.rbFilenames = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleSystemEventFilenames.class.getName());
        } //load up  resourcebundle        
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.severity = severity;
        this.origin = origin;
        this.type = type;
        UUID uuid = UUID.randomUUID();
        this.id = uuid.toString();
        this.category = this.computeCategoryForType(type);
    }

    private int computeCategoryForType(int type) {
        int computedCategory = (type / 100) * 100;
        return (computedCategory);
    }

    /**
     * Reads the notification mail template file
     */
    public void readFromNotificationTemplate(String templateName, Properties replacement) throws Exception {
        String templateFilename = this.getLocalizedTemplateFilename(templateName);
        StringBuilder bodyBuffer = new StringBuilder();
        boolean inSubject = false;
        boolean inBody = false;

        BufferedReader templateReader = null;
        InputStream inStream = null;
        try {
            //prevent Files.newBufferedReader(Paths.get(templateFilename), StandardCharsets.UTF_8);
            //because this will throw a MalformedInputException if the encoding does not match!
            //The REPLACE action will replace the unreadable character with a "?"
            inStream = Files.newInputStream(Paths.get(templateFilename));
            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPLACE)
                    .onUnmappableCharacter(CodingErrorAction.REPLACE);
            templateReader = new BufferedReader(new InputStreamReader(inStream, decoder));
            String line = "";
            while (line != null) {
                line = templateReader.readLine();
                if (line != null) {
                    if (line.trim().equals("[SUBJECT]")) {
                        inSubject = true;
                        inBody = false;
                        continue;
                    } else if (line.trim().equals("[BODY]")) {
                        inSubject = false;
                        inBody = true;
                        continue;
                    }
                    if (inSubject) {
                        this.setSubject(this.replaceAllVars(line, replacement));
                        inSubject = false;
                    } else if (inBody) {
                        if (bodyBuffer.length() > 0) {
                            bodyBuffer.append("\n");
                        }
                        bodyBuffer.append(line);
                    }
                }
            }
        } finally {
            if (templateReader != null) {
                templateReader.close();
            }
            if( inStream != null ){
                inStream.close();
            }
        }
        this.setBody(this.replaceAllVars(bodyBuffer.toString(), replacement));
    }

    /**
     * Replaces all used variables in the passed source and returns them
     *
     * @param source Source string to replace the variable occurrences in
     * @param replacement container that contains the key-value pairs of
     * replacements
     * @return The replaced string
     */
    private String replaceAllVars(String source, Properties replacement) {
        Iterator iterator = replacement.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = replacement.getProperty(key);
            source = this.replace(source, key, value);
        }
        return (source);
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
     * Adds a _de _fr etc to the template name and returns it
     */
    private String getLocalizedTemplateFilename(String templateName) {
        String language = Locale.getDefault().getLanguage();
        //select language specific template
        if (Files.exists(Paths.get(this.notificationTemplateDir + templateName + "_" + language))) {
            templateName = this.notificationTemplateDir + templateName + "_" + language;
        } else {
            templateName = this.notificationTemplateDir + templateName;
        }
        return (templateName);
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the type
     */
    public int getType() {
        return this.type;
    }

    /**
     * Sets the type of this event - this will internal also compute the
     * category, there is no need to define it by parameter
     *
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
        this.setCategory(this.computeCategoryForType(type));
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    public String getHumanReadableTimestamp() {
        return (this.humanReadableEventDateFormat.format(new Date(this.getTimestamp())));
    }

    /**
     * Serializes this system event to the passed filename
     */
    public void store(Path storageDir, String storageFilePrefix, String storageFileSuffix) throws Exception {
        if (!storageDir.toFile().exists()) {
            Files.createDirectories(storageDir);
        }
        Path uniqueStorageFile = Files.createTempFile(storageDir, storageFilePrefix, storageFileSuffix);
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(uniqueStorageFile, StandardCharsets.UTF_8);
            writer.write(SECTION_DESCRIPTION);
            writer.newLine();
            writer.write("TimestampDescription=" + getHumanReadableTimestamp());
            writer.newLine();
            writer.write("SeverityDescription=" + this.severityToTextLocalized());
            writer.newLine();
            writer.write("OriginDescription=" + this.originToTextLocalized());
            writer.newLine();
            writer.write("CategoryDescription=" + this.categoryToTextLocalized());
            writer.newLine();
            writer.write("TypeDescription=" + this.typeToTextLocalized());
            writer.newLine();
            writer.write("ProcessOriginHost=" + this.getProcessOriginHost());
            writer.newLine();
            writer.write("User=" + this.getUser());
            writer.newLine();
            writer.write("Timestamp=" + this.getTimestamp());
            writer.newLine();
            writer.write("Severity=" + this.getSeverity());
            writer.newLine();
            writer.write("Origin=" + this.getOrigin());
            writer.newLine();
            writer.write("Type=" + this.getType());
            writer.newLine();
            writer.write("EventId=" + this.id);
            writer.newLine();
            writer.newLine();
            writer.newLine();
            writer.write(SECTION_SUBJECT);
            writer.newLine();
            if (this.getSubject() != null) {
                writer.write(this.getSubject());
            }
            writer.newLine();
            writer.newLine();
            writer.write(SECTION_BODY);
            writer.newLine();
            if (this.getBody() != null) {
                writer.write(this.getBody());
            }
            writer.newLine();
            writer.newLine();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * Parses a system event from a stored system event file that has been
     * stored using the store method
     */
    public static SystemEvent parse(Path eventFile) throws Exception {
        SystemEvent event = new SystemEvent(SEVERITY_INFO, ORIGIN_SYSTEM, TYPE_OTHER);
        BufferedReader reader = null;
        InputStream inStream = null;
        String section = "";
        StringBuilder body = new StringBuilder();
        StringBuilder subject = new StringBuilder();
        int sectionCount = 0;
        try {
            //prevent Files.newBufferedReader(Paths.get(templateFilename), StandardCharsets.UTF_8);
            //because this will throw a MalformedInputException if the encoding does not match!
            //The REPLACE action will replace the unreadable character with a "?"            
            inStream = Files.newInputStream(eventFile);
            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPLACE)
                    .onUnmappableCharacter(CodingErrorAction.REPLACE);
            reader = new BufferedReader(new InputStreamReader(inStream, decoder));
            String line = reader.readLine();
            while (line != null) {
                if (line.trim().equals(SECTION_DESCRIPTION)) {
                    section = SECTION_DESCRIPTION;
                    sectionCount++;
                } else if (line.trim().equals(SECTION_BODY)) {
                    section = SECTION_BODY;
                    sectionCount++;
                } else if (line.trim().equals(SECTION_SUBJECT)) {
                    section = SECTION_SUBJECT;
                    sectionCount++;
                } else {
                    try {
                        if (section.equals(SECTION_DESCRIPTION) && line.contains("=")) {
                            String[] keyValue = line.split("=");
                            if (keyValue[0].equalsIgnoreCase("user")) {
                                event.setUser(keyValue[1]);
                            } else if (keyValue[0].equalsIgnoreCase("timestamp")) {
                                event.setTimestamp(Long.valueOf(keyValue[1]).longValue());
                            } else if (keyValue[0].equalsIgnoreCase("severity")) {
                                event.setSeverity(Integer.valueOf(keyValue[1]).intValue());
                            } else if (keyValue[0].equalsIgnoreCase("origin")) {
                                event.setOrigin(Integer.valueOf(keyValue[1]).intValue());
                            } else if (keyValue[0].equalsIgnoreCase("type")) {
                                event.setType(Integer.valueOf(keyValue[1]).intValue());
                            } else if (keyValue[0].equalsIgnoreCase("processoriginhost")) {
                                event.setProcessOriginHost(keyValue[1]);
                            } else if (keyValue[0].equalsIgnoreCase("eventid")) {
                                event.setId(keyValue[1]);
                            }
                        } else if (section.equals(SECTION_BODY)) {
                            body.append(line).append("\n");
                        } else if (section.equals(SECTION_SUBJECT)) {
                            subject.append(line).append("\n");
                        }
                    } catch (Exception e) {
                        //mainly numberformat?
                        e.printStackTrace();
                    }
                }
                line = reader.readLine();
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
            if( inStream != null ){
                inStream.close();
            }
        }
        if (sectionCount != 3) {
            throw new Exception("System event parser: " + eventFile.toString() + " is no event file - bad number of sections (found " + sectionCount + ")");
        }
        event.setBody(body.toString());
        event.setSubject(subject.toString());
        return (event);
    }

    /**
     * Returns the severity of this event in a human readable form that is used
     * for the storage filename
     */
    public String severityToFilename() {
        if (this.getSeverity() == SEVERITY_ERROR) {
            return ("error");
        } else if (this.getSeverity() == SEVERITY_INFO) {
            return ("info");
        } else if (this.getSeverity() == SEVERITY_WARNING) {
            return ("warning");
        }
        return ("unknown");
    }

    /**
     * Returns the severity of this event in a human readable form that is used
     * for the storage filename
     */
    public String severityToTextLocalized() {
        return (this.rb.getResourceString("severity." + this.severity));
    }

    /**
     * Returns the category of this event in a human readable form
     */
    public String categoryToTextLocalized() {
        return (this.rb.getResourceString("category." + this.getCategory()));
    }

    /**
     * Contains a multi resolution image that displays the severity of the event
     */
    public ImageIcon getSeverityIconMultiResolution(int minResolution) {
        if (this.getSeverity() == SEVERITY_ERROR) {
            return (new ImageIcon(
                    ICON_SEVERITY_ERROR_MULTIRESOLUTION.toMinResolution(minResolution)));
        } else if (this.getSeverity() == SEVERITY_INFO) {
            return (new ImageIcon(
                    ICON_SEVERITY_INFO_MULTIRESOLUTION.toMinResolution(minResolution)));
        }
        return (new ImageIcon(
                ICON_SEVERITY_WARNING_MULTIRESOLUTION.toMinResolution(minResolution)));
    }

    /**
     * Contains a multi resolution image that displays the origin of the event
     */
    public ImageIcon getOriginIconMultiResolution(int minResolution) {
        if (this.getOrigin() == ORIGIN_SYSTEM) {
            return (new ImageIcon(
                    ICON_ORIGIN_SYSTEM_MULTIRESOLUTION.toMinResolution(minResolution)));
        } else if (this.getOrigin() == ORIGIN_TRANSACTION) {
            return (new ImageIcon(
                    ICON_ORIGIN_TRANSACTION_MULTIRESOLUTION.toMinResolution(minResolution)));
        }
        return (new ImageIcon(
                ICON_ORIGIN_USER_MULTIRESOLUTION.toMinResolution(minResolution)));
    }

    /**
     * Returns the type of this event in a human readable form that is used for
     * the storage filename
     */
    public String originToTextLocalized() {
        return (this.rb.getResourceString("origin." + this.origin));
    }

    /**
     * Returns the type of this event in a human readable form that is used for
     * the storage filename
     */
    public String originToFilename() {
        if (this.getOrigin() == ORIGIN_USER) {
            return ("user");
        } else if (this.getOrigin() == ORIGIN_TRANSACTION) {
            return ("transaction");
        } else if (this.getOrigin() == ORIGIN_SYSTEM) {
            return ("system");
        }
        return ("unknown");
    }

    public String typeToFilename() {
        String englishText = this.rbFilenames.getResourceString("type." + this.type);
        englishText = this.replace(englishText, "(", "");
        englishText = this.replace(englishText, ")", "");
        englishText = this.replace(englishText, "'", "");
        englishText = englishText.toLowerCase();
        englishText = this.replace(englishText, " ", "-");
        return (englishText);
    }

    public String typeToTextLocalized() {
        return (this.rb.getResourceString("type." + this.type));
    }

    /**
     * @return the severity
     */
    public int getSeverity() {
        return severity;
    }

    /**
     * @param severity the severity to set
     */
    public void setSeverity(int severity) {
        this.severity = severity;
    }

    /**
     * @return the origin
     */
    public int getOrigin() {
        return origin;
    }

    /**
     * @param origin the origin to set
     */
    public void setOrigin(int origin) {
        this.origin = origin;
    }

    /**
     * @return the processOriginHost
     */
    public String getProcessOriginHost() {
        return processOriginHost;
    }

    /**
     * @param processOriginHost the processOriginHost to set
     */
    public void setProcessOriginHost(String processOriginHost) {
        this.processOriginHost = processOriginHost;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Overwrite the equal method of object
     *
     * @param anObject object ot compare
     */
    @Override
    public boolean equals(Object anObject) {
        if (anObject == this) {
            return (true);
        }
        if (anObject != null && anObject instanceof SystemEvent) {
            SystemEvent event = (SystemEvent) anObject;
            return (event != null && event.getId().equals(this.id));
        }
        return (false);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
        return hash;
    }

    /**
     * @return the category
     */
    public int getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(int category) {
        this.category = category;
    }

}
