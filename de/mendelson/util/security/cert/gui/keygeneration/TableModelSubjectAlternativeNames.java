//$Header: /as2/de/mendelson/util/security/cert/gui/keygeneration/TableModelSubjectAlternativeNames.java 4     25.02.20 14:20 Heller $
package de.mendelson.util.security.cert.gui.keygeneration;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.uinotification.UINotification;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.GeneralName;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Table model
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class TableModelSubjectAlternativeNames extends AbstractTableModel {

    private final List<GeneralName> parameter = Collections.synchronizedList(new ArrayList<GeneralName>());
    private JFrame parentComponent = null;
    private MecResourceBundle rb = null;

    /**
     * Load resources
     */
    public TableModelSubjectAlternativeNames() {
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleDialogSubjectAlternativeNames.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle "
                    + e.getClassName() + " not found.");
        }
    }

    public List<GeneralName> getNamesList() {
        synchronized (this.parameter) {
            List<GeneralName> list = new ArrayList<GeneralName>();
            list.addAll(this.parameter);
            return (list);
        }
    }

    public void passNewData(JFrame parentComponent, List<GeneralName> parameter) {
        synchronized (this.parameter) {
            this.parameter.clear();
            this.parameter.addAll(parameter);
        }
    }

    /**
     * Number of rows to display
     */
    @Override
    public int getRowCount() {
        synchronized (this.parameter) {
            return (this.parameter.size());
        }
    }

    /**
     * Number of cols to display
     */
    @Override
    public int getColumnCount() {
        return (2);
    }

    /**
     * Returns a value at a specific position in the grid
     */
    @Override
    public Object getValueAt(int row, int col) {
        synchronized (this.parameter) {
            int tagNoInt = this.parameter.get(row).getTagNo();
            if (col == 0) {
                String tagNoStr = TagNo.intValueToString(tagNoInt);
                return (tagNoStr);
            }
            //ip addresses are stored sometimes in hex in a DEROctetString, this has to be decoded first
            ASN1Primitive primitive = this.parameter.get(row).getName().toASN1Primitive();
            if (primitive instanceof DEROctetString && tagNoInt == GeneralName.iPAddress) {
                DEROctetString str = (DEROctetString) primitive;
                StringBuilder decStr = new StringBuilder();
                byte[] octets = str.getOctets();
                for (byte octet : octets) {
                    if (decStr.length() > 0) {
                        decStr.append(".");
                    }
                    decStr.append((int) (octet & 0xFF));
                }
                return (decStr);
            }
            return (this.parameter.get(row).getName());
        }
    }

    protected void addRow() {
        synchronized (this.parameter) {
            this.parameter.add(new GeneralName(GeneralName.uniformResourceIdentifier, "oftp://O0123456789ABC"));
            this.fireTableRowsInserted(this.parameter.size() - 1, this.parameter.size() - 1);
        }
    }

    protected void delRow(int row) {
        synchronized (this.parameter) {
            this.parameter.remove(row);
            this.fireTableRowsDeleted(row, row);
        }
    }

    /**
     * Returns the name of every column
     *
     * @param col Column to get the header name of
     */
    @Override
    public String getColumnName(int col) {
        return (new String[]{
            this.rb.getResourceString("header.name"), this.rb.getResourceString("header.value")
        })[col];

    }

    /**
     * Set how to display the grid elements
     *
     * @param col requested column
     */
    @Override
    public Class getColumnClass(int col) {
        return (new Class[]{
            String.class,
            String.class
        }[col]);
    }

    /**
     * Swing GUI checks which cols are editable.
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        return (true);
    }

    /**
     * This is automatically called if a cell value is changed..
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        synchronized (this.parameter) {
            GeneralName nameSoFar = this.parameter.get(row);
            int existingType = nameSoFar.getTagNo();
            ASN1Encodable existingNameValue = nameSoFar.getName();
            GeneralName newName = null;
            try {
                if (col == 0) {
                    //The toString() on ASN1Encodable is required to perform the format check of the General Name generation process
                    newName = new GeneralName(new TagNo(value.toString()).getValue(), existingNameValue.toString());
                } else if (col == 1) {
                    newName = new GeneralName(existingType, value.toString());
                }
                this.parameter.remove(row);
                this.parameter.add(row, newName);
            } catch (Exception e) {
                StringBuilder additionalInfo = new StringBuilder();
                int relatedTagNo = -1;
                if (col == 0) {
                    relatedTagNo = new TagNo(value.toString()).getValue();
                } else if (col == 1) {
                    relatedTagNo = nameSoFar.getTagNo();
                }
                if (e instanceof IllegalArgumentException) {
                    if (relatedTagNo == GeneralName.rfc822Name) {
                        additionalInfo.append("Sample format: \"username@myhost.com\"");
                    }
                    if (relatedTagNo == GeneralName.iPAddress) {
                        additionalInfo.append("Sample format: \"192.168.0.1\"");
                    }
                    if (relatedTagNo == GeneralName.uniformResourceIdentifier) {
                        additionalInfo.append("Sample format: \"www.mendelson.de\"");
                    }
                    if (relatedTagNo == GeneralName.registeredID) {
                        additionalInfo.append("Sample format: \"1.3.6.1.4.1\"");
                    }
                }
                String errorMessage = e.getMessage();
                if (errorMessage == null) {
                    errorMessage = NullPointerException.class.getSimpleName();
                }
                if (additionalInfo.length() > 0) {
                    errorMessage = errorMessage + "\n" + additionalInfo.toString();
                }
                UINotification.instance().addNotification(null,
                        UINotification.TYPE_ERROR,
                        "Error generating General Name",
                        errorMessage);
            }
        }
    }

}
