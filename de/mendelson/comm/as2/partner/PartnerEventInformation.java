//$Header: /as2/de/mendelson/comm/as2/partner/PartnerEventInformation.java 10    10/01/23 9:23 Heller $
package de.mendelson.comm.as2.partner;

import de.mendelson.comm.as2.message.postprocessingevent.ProcessingEvent;
import de.mendelson.comm.as2.partner.gui.event.PartnerEventResource;
import de.mendelson.util.MendelsonMultiResolutionImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
 * Stores event information of a partner
 *
 * @author S.Heller
 * @version $Revision: 10 $
 */
public class PartnerEventInformation implements Serializable {

    public static final long serialVersionUID = 1L;

    public static final int PROCESS_EXECUTE_SHELL = ProcessingEvent.PROCESS_EXECUTE_SHELL;
    public static final int PROCESS_MOVE_TO_PARTNER = ProcessingEvent.PROCESS_MOVE_TO_PARTNER;
    public static final int PROCESS_MOVE_TO_DIR = ProcessingEvent.PROCESS_MOVE_TO_DIR;

    public static final int TYPE_ON_RECEIPT = ProcessingEvent.TYPE_RECEIPT_SUCCESS;
    public static final int TYPE_ON_SENDERROR = ProcessingEvent.TYPE_SEND_FAILURE;
    public static final int TYPE_ON_SENDSUCCESS = ProcessingEvent.TYPE_SEND_SUCCESS;

    private boolean useonreceipt = false;
    private boolean useonsenderror = false;
    private boolean useonsendsuccess = false;
    private int processOnReceipt = PROCESS_EXECUTE_SHELL;
    private int processOnSenderror = PROCESS_EXECUTE_SHELL;
    private int processOnSendsuccess = PROCESS_EXECUTE_SHELL;

    private final List<String> parameteronreceipt = new ArrayList<String>();
    private final List<String> parameteronsenderror = new ArrayList<String>();
    private final List<String> parameteronsendsuccess = new ArrayList<String>();

    /**
     * Creates an empty entry
     */
    public PartnerEventInformation() {

    }

    /**
     * Returns the related image that matches the requested process
     *
     * @param PROCESS_TYPE
     * @return
     */
    public static MendelsonMultiResolutionImage getImageForProcess(int PROCESS_TYPE) {
        if (PROCESS_TYPE == PROCESS_MOVE_TO_DIR) {
            return (PartnerEventResource.IMAGE_PROCESS_MOVE_TO_DIR);
        }
        if (PROCESS_TYPE == PROCESS_MOVE_TO_PARTNER) {
            return (PartnerEventResource.IMAGE_PROCESS_MOVE_TO_PARTNER);
        }
        return (PartnerEventResource.IMAGE_PROCESS_EXECUTE_SHELL);
    }

    /**
     * Serializes these partner event to XML
     *
     * @param level level in the XML hierarchy for the xml beautifying
     */
    public String toXML(int level) {
        String offset = "";
        for (int i = 0; i < level; i++) {
            offset += "\t";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(offset).append("<events>\n");
        builder.append(offset).append("\t<useonreceipt>").append(String.valueOf(this.useonreceipt)).append("</useonreceipt>\n");
        builder.append(offset).append("\t<typeonreceipt>").append(String.valueOf(this.processOnReceipt)).append("</typeonreceipt>\n");        
        if (this.hasParameterOnReceipt()) {
            builder.append(offset).append("\t<onreceiptvalues>\n");
            for (String value : this.parameteronreceipt) {
                builder.append(offset).append("\t\t<value>").append(this.toCDATA(value)).append("</value>\n");
            }            
            builder.append(offset).append("\t</onreceiptvalues>\n");
        }        
        builder.append(offset).append("\t<useonsenderror>").append(String.valueOf(this.useonsenderror)).append("</useonsenderror>\n");
        builder.append(offset).append("\t<typeonsenderror>").append(String.valueOf(this.processOnSenderror)).append("</typeonsenderror>\n");
        if (this.hasParameterOnSenderror()) {
            builder.append(offset).append("\t<onsenderrorvalues>\n");
            for (String value : this.parameteronsenderror) {
                builder.append(offset).append("\t\t<value>").append(this.toCDATA(value)).append("</value>\n");
            }
            builder.append(offset).append("\t</onsenderrorvalues>\n");
        }
        builder.append(offset).append("\t<useonsendsuccess>").append(String.valueOf(this.useonsendsuccess)).append("</useonsendsuccess>\n");
        builder.append(offset).append("\t<typeonsendsuccess>").append(String.valueOf(this.processOnSendsuccess)).append("</typeonsendsuccess>\n");
        if (this.hasParameterOnSendsuccess()) {
            builder.append(offset).append("\t<onsendsuccessvalues>\n");
            for (String value : this.parameteronsendsuccess) {
                builder.append(offset).append("\t\t<value>").append(this.toCDATA(value)).append("</value>\n");
            }
            builder.append(offset).append("\t</onsendsuccessvalues>\n");
        }
        builder.append(offset).append("</events>\n");
        return (builder.toString());
    }

    /**
     * Adds a cdata indicator to xml data
     */
    private String toCDATA(String data) {
        return ("<![CDATA[" + data + "]]>");
    }

    public static void fromXML(Partner partner, Element element) {
        PartnerEventInformation eventInfo = partner.getPartnerEvents();
        NodeList propertiesNodeList = element.getChildNodes();
        for (int i = 0; i < propertiesNodeList.getLength(); i++) {
            if (propertiesNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element property = (Element) propertiesNodeList.item(i);
                String key = property.getTagName();
                String value = property.getTextContent();
                if (key.equals("useonreceipt")) {
                    eventInfo.setUseOnReceipt(Boolean.valueOf(value).booleanValue());
                }
                if (key.equals("useonsenderror")) {
                    eventInfo.setUseOnSenderror(Boolean.valueOf(value).booleanValue());
                }
                if (key.equals("useonsendsuccess")) {
                    eventInfo.setUseOnSendsuccess(Boolean.valueOf(value).booleanValue());
                }
                if (key.equals("typeonreceipt")) {
                    eventInfo.setProcessOnReceipt(Integer.valueOf(value).intValue());
                }
                if (key.equals("typeonsenderror")) {
                    eventInfo.setProcessOnSenderror(Integer.valueOf(value).intValue());
                }
                if (key.equals("typeonsendsuccess")) {
                    eventInfo.setProcessOnSendsuccess(Integer.valueOf(value).intValue());
                }
                if (key.equals("onreceiptvalues")) {
                    collectXMLValues(eventInfo.parameteronreceipt, property);
                }
                if (key.equals("onsenderrorvalues")) {
                    collectXMLValues(eventInfo.parameteronsenderror, property);
                }
                if (key.equals("onsendsuccessvalues")) {
                    collectXMLValues(eventInfo.parameteronsendsuccess, property);
                }
            }
        }
    }

    private static void collectXMLValues(List<String> list, Element element) {
        list.clear();        
        NodeList propertiesNodeList = element.getChildNodes();
        for (int i = 0; i < propertiesNodeList.getLength(); i++) {
            if (propertiesNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element valueElement = (Element) propertiesNodeList.item(i);
                String valueTag = valueElement.getTagName();
                if (valueTag.equals("value")) {
                    String propertyValue = "";
                    if( valueElement.getTextContent() != null ){
                        propertyValue = valueElement.getTextContent();
                    }
                    list.add(propertyValue);
                }
            }
        }
    }

    /**
     * Overwrite the equal method of object
     *
     * @param anObject object to compare
     */
    @Override
    public boolean equals(Object anObject) {
        if (anObject == this) {
            return (true);
        }
        if (anObject != null && anObject instanceof PartnerEventInformation) {
            PartnerEventInformation entry = (PartnerEventInformation) anObject;
            return (entry.getProcessOnReceipt() == this.getProcessOnReceipt()
                    && entry.getProcessOnSenderror() == this.getProcessOnSenderror()
                    && entry.getProcessOnSendsuccess() == this.getProcessOnSendsuccess()
                    && this.parameterAreEqual(entry.getParameterOnReceipt(), this.getParameterOnReceipt())
                    && this.parameterAreEqual(entry.getParameterOnSenderror(), this.getParameterOnSenderror())
                    && this.parameterAreEqual(entry.getParameterOnSendsuccess(), this.getParameterOnSendsuccess()));

        }
        return (false);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + (this.useOnReceipt() ? 1 : 0);
        hash = 83 * hash + (this.useOnSendsuccess() ? 1 : 0);
        hash = 83 * hash + this.getProcessOnReceipt();
        hash = 83 * hash + this.getProcessOnSenderror();
        hash = 83 * hash + this.getProcessOnSendsuccess();
        return hash;
    }

    private boolean parameterAreEqual(List<String> listA, List<String> listB) {
        StringBuilder builderA = new StringBuilder();
        for (String listAStr : listA) {
            builderA.append(listAStr);
        }
        StringBuilder builderB = new StringBuilder();
        for (String listBStr : listB) {
            builderB.append(listBStr);
        }
        return (builderA.toString().equals(builderB.toString()));
    }

    /**
     * @return the useonreceipt
     */
    public boolean useOnReceipt() {
        return useonreceipt;
    }

    public void setUse(final int EVENT_TYPE, boolean flag) {
        if (EVENT_TYPE == TYPE_ON_RECEIPT) {
            this.setUseOnReceipt(flag);
        } else if (EVENT_TYPE == TYPE_ON_SENDERROR) {
            this.setUseOnSenderror(flag);
        } else if (EVENT_TYPE == TYPE_ON_SENDSUCCESS) {
            this.setUseOnSendsuccess(flag);
        }
    }

    /**
     * @param useonreceipt the useonreceipt to set
     */
    public void setUseOnReceipt(boolean useonreceipt) {
        this.useonreceipt = useonreceipt;
    }

    /**
     * @return the useonsenderror
     */
    public boolean useOnSenderror() {
        return useonsenderror;
    }

    /**
     * @param useonsenderror the useonsenderror to set
     */
    public void setUseOnSenderror(boolean useonsenderror) {
        this.useonsenderror = useonsenderror;
    }

    /**
     * @return the useonsendsuccess
     */
    public boolean useOnSendsuccess() {
        return useonsendsuccess;
    }

    /**
     * @param useonsendsuccess the useonsendsuccess to set
     */
    public void setUseOnSendsuccess(boolean useonsendsuccess) {
        this.useonsendsuccess = useonsendsuccess;
    }

    public void setProcess(final int EVENT_TYPE, final int PROCESS_TYPE) {
        if (EVENT_TYPE == TYPE_ON_RECEIPT) {
            this.setProcessOnReceipt(PROCESS_TYPE);
        } else if (EVENT_TYPE == TYPE_ON_SENDERROR) {
            this.setProcessOnSenderror(PROCESS_TYPE);
        } else if (EVENT_TYPE == TYPE_ON_SENDSUCCESS) {
            this.setProcessOnSendsuccess(PROCESS_TYPE);
        }
    }

    public int getProcess(final int EVENT_TYPE) {
        if (EVENT_TYPE == TYPE_ON_RECEIPT) {
            return (this.getProcessOnReceipt());
        } else if (EVENT_TYPE == TYPE_ON_SENDERROR) {
            return (this.getProcessOnSenderror());
        } else if (EVENT_TYPE == TYPE_ON_SENDSUCCESS) {
            return (this.getProcessOnSendsuccess());
        } else {
            throw new IllegalArgumentException("PartnerEventInformation.getProcess(): Undefined event type " + EVENT_TYPE);
        }
    }
    
    /**
     * @return the typeonreceipt
     */
    private int getProcessOnReceipt() {
        return processOnReceipt;
    }

    /**
     * @param processonreceipt the typeonreceipt to set
     */
    private void setProcessOnReceipt(int processonreceipt) {
        this.processOnReceipt = processonreceipt;
    }

    /**
     * @return the typeonsenderror
     */
    private int getProcessOnSenderror() {
        return processOnSenderror;
    }

    /**
     * @param processonsenderror the typeonsenderror to set
     */
    private void setProcessOnSenderror(int processonsenderror) {
        this.processOnSenderror = processonsenderror;
    }

    /**
     * @return the typeonsendsuccess
     */
    private int getProcessOnSendsuccess() {
        return processOnSendsuccess;
    }

    /**
     * @param processonsendsuccess the typeonsendsuccess to set
     */
    private void setProcessOnSendsuccess(int processonsendsuccess) {
        this.processOnSendsuccess = processonsendsuccess;
    }

    public List<String> getParameter(final int EVENT_TYPE) {
        if (EVENT_TYPE == TYPE_ON_RECEIPT) {
            return (this.getParameterOnReceipt());
        } else if (EVENT_TYPE == TYPE_ON_SENDERROR) {
            return (this.getParameterOnSenderror());
        } else if (EVENT_TYPE == TYPE_ON_SENDSUCCESS) {
            return (this.getParameterOnSendsuccess());
        } else {
            throw new IllegalArgumentException("PartnerEventInformation.getParameter(): Undefined event type " + EVENT_TYPE);
        }
    }

    /**
     * @return the parameteronreceipt
     */
    private List<String> getParameterOnReceipt() {
        return parameteronreceipt;
    }

    public void setParameter(final int EVENT_TYPE, List<String> parameter) {
        if (EVENT_TYPE == TYPE_ON_RECEIPT) {
            this.setParameterOnReceipt(parameter);
        } else if (EVENT_TYPE == TYPE_ON_SENDERROR) {
            this.setParameterOnSenderror(parameter);
        } else if (EVENT_TYPE == TYPE_ON_SENDSUCCESS) {
            this.setParameterOnSendsuccess(parameter);
        }
    }

    public void setParameter(final int EVENT_TYPE, String parameter) {
        if (EVENT_TYPE == TYPE_ON_RECEIPT) {
            this.setParameterOnReceipt(parameter);
        } else if (EVENT_TYPE == TYPE_ON_SENDERROR) {
            this.setParameterOnSenderror(parameter);
        } else if (EVENT_TYPE == TYPE_ON_SENDSUCCESS) {
            this.setParameterOnSendsuccess(parameter);
        }
    }
    
    /**
     * @param parameteronreceipt the parameteronreceipt to set
     */
    public void setParameterOnReceipt(List<String> parameteronreceipt) {
        this.parameteronreceipt.clear();
        this.parameteronreceipt.addAll(parameteronreceipt);
    }

    /**
     * @param parameteronreceipt the parameteronreceipt to set
     */
    public void setParameterOnReceipt(String parameteronreceipt) {
        this.parameteronreceipt.clear();
        this.parameteronreceipt.add(parameteronreceipt);
    }

    /**
     * @return the parameteronsenderror
     */
    private List<String> getParameterOnSenderror() {
        return parameteronsenderror;
    }

    /**
     * @param parameteronsenderror the parameteronsenderror to set
     */
    private void setParameterOnSenderror(List<String> parameteronsenderror) {
        this.parameteronsenderror.clear();
        this.parameteronsenderror.addAll(parameteronsenderror);
    }

    private void setParameterOnSenderror(String parameteronsenderror) {
        this.parameteronsenderror.clear();
        this.parameteronsenderror.add(parameteronsenderror);
    }

    /**
     * @return the parameteronsendsuccess
     */
    private List<String> getParameterOnSendsuccess() {
        return parameteronsendsuccess;
    }

    /**
     * @param parameteronsendsuccess the parameteronsendsuccess to set
     */
    private void setParameterOnSendsuccess(List<String> parameteronsendsuccess) {
        this.parameteronsendsuccess.clear();
        this.parameteronsendsuccess.addAll(parameteronsendsuccess);
    }

    private void setParameterOnSendsuccess(String parameteronsendsuccess) {
        this.parameteronsendsuccess.clear();
        this.parameteronsendsuccess.add(parameteronsendsuccess);
    }

    private boolean hasParameterOnSendsuccess() {
        if (this.parameteronsendsuccess.isEmpty()) {
            return (false);
        }
        for (String parameter : this.parameteronsendsuccess) {
            if (parameter != null && parameter.trim().length() > 0) {
                return (true);
            }
        }
        return (false);
    }

    private boolean hasParameterOnSenderror() {
        if (this.parameteronsenderror.isEmpty()) {
            return (false);
        }
        for (String parameter : this.parameteronsenderror) {
            if (parameter != null && parameter.trim().length() > 0) {
                return (true);
            }
        }
        return (false);
    }

    private boolean hasParameterOnReceipt() {
        if (this.parameteronreceipt.isEmpty()) {
            return (false);
        }
        for (String parameter : this.parameteronreceipt) {
            if (parameter != null && parameter.trim().length() > 0) {
                return (true);
            }
        }
        return (false);
    }
    
    public boolean hasParameter( final int EVENT_TYPE){
        if (EVENT_TYPE == TYPE_ON_RECEIPT) {
            return (this.hasParameterOnReceipt());
        } else if (EVENT_TYPE == TYPE_ON_SENDERROR) {
            return (this.hasParameterOnSenderror());
        } else if (EVENT_TYPE == TYPE_ON_SENDSUCCESS) {
            return (this.hasParameterOnSendsuccess());
        } else {
            throw new IllegalArgumentException("PartnerEventInformation.hasParameter(): Undefined event type " + EVENT_TYPE);
        }
    }
    
}
