//$Header: /as2/de/mendelson/comm/as2/datasheet/DatasheetBuilder.java 17    16.09.21 15:30 Heller $
package de.mendelson.comm.as2.datasheet;

import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.content.common.CSCreator;
import de.intarsys.pdf.encoding.WinAnsiEncoding;
import de.intarsys.pdf.font.PDFontType1;
import de.intarsys.pdf.pd.PDAFChoiceField;
import de.intarsys.pdf.pd.PDAFTextField;
import de.intarsys.pdf.pd.PDAcroForm;
import de.intarsys.pdf.pd.PDAppearanceCharacteristics;
import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.pdf.pd.PDEmbeddedFile;
import de.intarsys.pdf.pd.PDFileAttachmentAnnotation;
import de.intarsys.pdf.pd.PDFileSpecification;
import de.intarsys.pdf.pd.PDPage;
import de.intarsys.pdf.pd.PDWidgetAnnotation;
import de.intarsys.tools.locator.FileLocator;
import de.mendelson.comm.as2.AS2ServerVersion;
import de.mendelson.comm.as2.message.AS2Message;
import de.mendelson.comm.as2.message.ResourceBundleAS2Message;
import de.mendelson.comm.as2.partner.Partner;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.security.encryption.EncryptionConstantsAS2;
import de.mendelson.util.security.signature.SignatureConstantsAS2;
import java.awt.Rectangle;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Class that is responsible for the creation of a PDF file
 *
 * @author S.Heller
 * @version $Revision: 17 $
 */
public class DatasheetBuilder {

    private PDDocument document;
    private Partner localStation;
    private Partner remotePartner;
    private PDAcroForm form = null;
    private final int LEFT_MARGIN = 60;
    private final int MAX_LINE_LENGTH = 32;
    private DatasheetInformation localInformation;
    private DateFormat format = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);
    private MecResourceBundle rbMessage;

    public DatasheetBuilder(Partner localStation, Partner remotePartner, DatasheetInformation localInformation) {
        this.localInformation = localInformation;
        // First create a new document.
        this.document = (PDDocument.createNew());
        this.document.setTitle("AS2 Data and Parameter");
        this.document.setAuthor(System.getProperty("user.name"));
        this.document.setCreator(AS2ServerVersion.getCompany());
        this.document.setProducer(AS2ServerVersion.getFullProductName());
        this.localStation = localStation;
        this.remotePartner = remotePartner;
        //load resource bundle
        try {
            this.rbMessage = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleAS2Message.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
    }

    /**
     * Adds a new page to the PDF and returns a creator on it
     */
    public PDPage openNewPage() {
        // Then create the first page.
        PDPage page = (PDPage) PDPage.META.createNew();
        // add page to the document.
        this.document.addPageNode(page);
        return (page);
    }

    public void closePage(CSCreator creator) {
        creator.close();
    }

    /**
     * Adds an attachment to the PDF
     */
    public void addAttachment(PDPage page, String filename, String subject, byte[] content) {
        PDFileAttachmentAnnotation annotation = (PDFileAttachmentAnnotation) PDFileAttachmentAnnotation.META.createNew();
        page.addAnnotation(annotation);
        annotation.setPrint(false);
        annotation.setColor(new float[]{1, 1, 0});
        PDFileSpecification fileSpec = (PDFileSpecification) PDFileSpecification.META.createNew();
        fileSpec.setFileSpecificationString(PDFileSpecification.DK_F, filename);
        annotation.setFileSpecification(fileSpec);
        annotation.setContents(filename);
        annotation.setSubject(subject);
        PDEmbeddedFile embeddedFile = (PDEmbeddedFile) PDEmbeddedFile.META.createNew();
        embeddedFile.setBytes(content);
        fileSpec.setEmbeddedFile(PDFileSpecification.DK_F, embeddedFile);
    }

    /**
     * Writes the pages header
     */
    public void writeHeader(CSCreator creator) {
        this.writeTextBold(creator, LEFT_MARGIN, 720, 12, "AS2 Data and Parameter Sheet");
        this.writeText(creator, LEFT_MARGIN, 700, 10, "Please fill in these information to establish the AS2 connection");
        this.writeText(creator, LEFT_MARGIN + 430, 780, 10, this.format.format(new Date()));
    }

    private String[] getSignatureAlgorithms() {
        List<String> algorithms = new ArrayList<String>();
        algorithms.add(this.rbMessage.getResourceString("signature." + SignatureConstantsAS2.SIGNATURE_NONE));
        algorithms.add(this.rbMessage.getResourceString("signature." + SignatureConstantsAS2.SIGNATURE_SHA1));
        algorithms.add(this.rbMessage.getResourceString("signature." + SignatureConstantsAS2.SIGNATURE_MD5));
        algorithms.add(this.rbMessage.getResourceString("signature." + SignatureConstantsAS2.SIGNATURE_SHA256));
        algorithms.add(this.rbMessage.getResourceString("signature." + SignatureConstantsAS2.SIGNATURE_SHA384));
        algorithms.add(this.rbMessage.getResourceString("signature." + SignatureConstantsAS2.SIGNATURE_SHA512));
        algorithms.add(this.rbMessage.getResourceString("signature." + SignatureConstantsAS2.SIGNATURE_SHA1_RSASSA_PSS));
        algorithms.add(this.rbMessage.getResourceString("signature." + SignatureConstantsAS2.SIGNATURE_SHA224_RSASSA_PSS));
        algorithms.add(this.rbMessage.getResourceString("signature." + SignatureConstantsAS2.SIGNATURE_SHA256_RSASSA_PSS));
        algorithms.add(this.rbMessage.getResourceString("signature." + SignatureConstantsAS2.SIGNATURE_SHA384_RSASSA_PSS));
        algorithms.add(this.rbMessage.getResourceString("signature." + SignatureConstantsAS2.SIGNATURE_SHA512_RSASSA_PSS));
        algorithms.add(this.rbMessage.getResourceString("signature." + SignatureConstantsAS2.SIGNATURE_SHA3_224));
        algorithms.add(this.rbMessage.getResourceString("signature." + SignatureConstantsAS2.SIGNATURE_SHA3_256));
        algorithms.add(this.rbMessage.getResourceString("signature." + SignatureConstantsAS2.SIGNATURE_SHA3_384));
        algorithms.add(this.rbMessage.getResourceString("signature." + SignatureConstantsAS2.SIGNATURE_SHA3_512));
        algorithms.add(this.rbMessage.getResourceString("signature." + SignatureConstantsAS2.SIGNATURE_SHA3_224_RSASSA_PSS));
        algorithms.add(this.rbMessage.getResourceString("signature." + SignatureConstantsAS2.SIGNATURE_SHA3_256_RSASSA_PSS));
        algorithms.add(this.rbMessage.getResourceString("signature." + SignatureConstantsAS2.SIGNATURE_SHA3_384_RSASSA_PSS));
        algorithms.add(this.rbMessage.getResourceString("signature." + SignatureConstantsAS2.SIGNATURE_SHA3_512_RSASSA_PSS));
        String[] algorithmArray = new String[algorithms.size()];
        algorithmArray = algorithms.toArray(algorithmArray);
        return (algorithmArray);
    }

    private String[] getEncryptionAlgorithms() {
        List<String> algorithms = new ArrayList<String>();
        algorithms.add(this.rbMessage.getResourceString("encryption." + EncryptionConstantsAS2.ENCRYPTION_NONE));
        algorithms.add(this.rbMessage.getResourceString("encryption." + EncryptionConstantsAS2.ENCRYPTION_3DES));
        algorithms.add(this.rbMessage.getResourceString("encryption." + EncryptionConstantsAS2.ENCRYPTION_DES));
        algorithms.add(this.rbMessage.getResourceString("encryption." + EncryptionConstantsAS2.ENCRYPTION_RC2_40));
        algorithms.add(this.rbMessage.getResourceString("encryption." + EncryptionConstantsAS2.ENCRYPTION_RC2_64));
        algorithms.add(this.rbMessage.getResourceString("encryption." + EncryptionConstantsAS2.ENCRYPTION_RC2_128));
        algorithms.add(this.rbMessage.getResourceString("encryption." + EncryptionConstantsAS2.ENCRYPTION_RC2_196));
        algorithms.add(this.rbMessage.getResourceString("encryption." + EncryptionConstantsAS2.ENCRYPTION_RC4_40));
        algorithms.add(this.rbMessage.getResourceString("encryption." + EncryptionConstantsAS2.ENCRYPTION_RC4_56));
        algorithms.add(this.rbMessage.getResourceString("encryption." + EncryptionConstantsAS2.ENCRYPTION_RC4_128));
        algorithms.add(this.rbMessage.getResourceString("encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_128));
        algorithms.add(this.rbMessage.getResourceString("encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_192));
        algorithms.add(this.rbMessage.getResourceString("encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_256));
        algorithms.add(this.rbMessage.getResourceString("encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_128_RSAES_AOEP));
        algorithms.add(this.rbMessage.getResourceString("encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_192_RSAES_AOEP));
        algorithms.add(this.rbMessage.getResourceString("encryption." + EncryptionConstantsAS2.ENCRYPTION_AES_256_RSAES_AOEP));
        String[] algorithmArray = new String[algorithms.size()];
        algorithmArray = algorithms.toArray(algorithmArray);
        return (algorithmArray);
    }

    private void writeText(CSCreator creator, int x, int y, int fontSize, String text) {
        this.writeText(creator, x, y, fontSize, text, false);
    }

    private void writeTextBold(CSCreator creator, int x, int y, int fontSize, String text) {
        this.writeText(creator, x, y, fontSize, text, true);
    }

    private void writeText(CSCreator creator, int x, int y, int fontSize, String text, boolean bold) {
        if (text == null) {
            return;
        }
        String[] lines = text.split("[\\n]");
        creator.setNonStrokeColorRGB(0, 0, 0);
        PDFontType1 font = null;
        if (bold) {
            font = PDFontType1.createNew(PDFontType1.FONT_Helvetica_Bold);
        } else {
            font = PDFontType1.createNew(PDFontType1.FONT_Helvetica);
        }
        //allow german umlaute
        font.setEncoding(WinAnsiEncoding.UNIQUE);
        creator.textSetFont(null, font, fontSize);
        int ygap = 2;
        for (int i = 0; i < lines.length; i++) {
            creator.textLineMoveTo(x, y - fontSize * (i + 1) - (ygap * i));
            creator.textShow(lines[i]);
        }
    }

    public void writeYesNo(CSCreator creator, int x, int y, int fontSize, boolean yesSelected, boolean noSelected) {
        int xgap = 3;
        creator.penRectangle(new Rectangle(x, y - fontSize, fontSize, fontSize));
        if (yesSelected) {
            creator.penMoveTo(x, y);
            creator.penLineTo(x + fontSize, y - fontSize);
            creator.penMoveTo(x + fontSize, y);
            creator.penLineTo(x, y - fontSize);
        }
        this.writeText(creator, x + fontSize + xgap, y, fontSize, "Yes");
        x += 50;
        creator.penRectangle(new Rectangle(x, y - fontSize, fontSize, fontSize));
        if (noSelected) {
            creator.penMoveTo(x, y);
            creator.penLineTo(x + fontSize, y - fontSize);
            creator.penMoveTo(x + fontSize, y);
            creator.penLineTo(x, y - fontSize);
        }
        this.writeText(creator, x + fontSize + xgap, y, fontSize, "No");
        //print the path
        creator.pathStroke();
    }

    public int drawTable(CSCreator creator, PDPage page) {
        //---------------
        //Table constants
        int x = LEFT_MARGIN;
        int y = 660;
        int rowWidth1 = 140;
        int rowWidth2 = 175;
        int rowWidth3 = 175;
        //---------------
        int xgap = 5;
        int ygap = 5;
        int rowX1Abs = x + xgap;
        int rowX2Abs = x + rowWidth1 + xgap;
        int rowX3Abs = x + rowWidth1 + rowWidth2 + xgap;
        int height = 30;
        this.drawTableRow(creator, x, y, height, new int[]{rowWidth1, rowWidth2, rowWidth3});
        this.writeTextBold(creator, rowX2Abs, y - ygap, 12, "Our parameters");
        this.writeTextBold(creator, rowX3Abs, y - ygap, 12, "Your parameters");
        y -= height;
        height = 70;
        this.drawTableRow(creator, x, y, height, new int[]{rowWidth1, rowWidth2, rowWidth3});
        this.writeTextBold(creator, rowX1Abs, y - ygap, 12, "Company address");
        this.writeText(creator, rowX1Abs, y - ygap - 15, 10, "(Name and address)");
        this.writeText(creator, rowX2Abs, y - ygap, 10, this.localStation.getContactCompany());
        this.addFormFieldText(page, rowX3Abs, y - ygap, rowWidth3 - 2 * xgap, 56, 10, true, "company_address",
                this.remotePartner == null ? "<Your company address>" : this.remotePartner.getContactCompany());
        y -= height;
        height = 63;
        this.drawTableRow(creator, x, y, height, new int[]{rowWidth1, rowWidth2, rowWidth3});
        this.writeTextBold(creator, rowX1Abs, y - ygap, 12, "Contact");
        this.writeText(creator, rowX1Abs, y - ygap - 15, 10, "Data exchange/infrastructure");
        this.writeText(creator, rowX1Abs, y - ygap - 28, 10, "(Name, telephone, mail, ..)");
        this.writeText(creator, rowX2Abs, y - ygap, 10, this.localStation.getContactAS2());
        this.addFormFieldText(page, rowX3Abs, y - ygap, rowWidth3 - 2 * xgap, 48, 10, true, "dataex_contact",
                this.remotePartner == null ? "<Your data exchange/infrastructure contact person>" : this.remotePartner.getContactAS2());
        y -= height;
        height = 120;
        int lineY = 17;
        this.drawTableRow(creator, x, y, height, new int[]{rowWidth1, rowWidth2, rowWidth3});
        this.writeTextBold(creator, rowX1Abs, y - ygap, 12, "AS2 parameter");
        this.writeText(creator, rowX1Abs, y - ygap - lineY, 10, "AS2 id");
        this.writeText(creator, rowX2Abs, y - ygap - lineY, 10, this.localStation.getAS2Identification());
        this.addFormFieldText(page, rowX3Abs, y - ygap - lineY, rowWidth3 - 2 * xgap, 10, 10, false, "AS2_ID",
                this.remotePartner == null ? "<Your AS2 id>"
                        : this.remotePartner.getAS2Identification());
        lineY = 32;
        this.writeText(creator, rowX1Abs, y - ygap - lineY, 10, "Data encryption");
        this.writeText(creator, rowX2Abs, y - ygap - lineY, 10,
                this.rbMessage.getResourceString("encryption." + this.localInformation.getEncryption()));
        String defaultEncryptionRemote = this.remotePartner == null
                ? this.rbMessage.getResourceString("encryption." + AS2Message.ENCRYPTION_3DES)
                : this.rbMessage.getResourceString("encryption." + this.remotePartner.getEncryptionType());
        this.addFormFieldChoice(page, rowX3Abs, y - ygap - lineY, rowWidth3 - 2 * xgap, 10, 10, "ENCRYPTION",
                this.getEncryptionAlgorithms(), defaultEncryptionRemote);
        lineY = 47;
        this.writeText(creator, rowX1Abs, y - ygap - lineY, 10, "Digital signature");
        this.writeText(creator, rowX2Abs, y - ygap - lineY, 10,
                this.rbMessage.getResourceString("signature." + this.localInformation.getSignature()));
        String defaultSignatureRemote = this.remotePartner == null
                ? this.rbMessage.getResourceString("signature." + AS2Message.SIGNATURE_SHA1)
                : this.rbMessage.getResourceString("signature." + this.remotePartner.getSignType());
        this.addFormFieldChoice(page, rowX3Abs, y - ygap - lineY, rowWidth3 - 2 * xgap, 10, 10, "SIGNATURE",
                this.getSignatureAlgorithms(), defaultSignatureRemote);
        lineY = 62;
        this.writeText(creator, rowX1Abs, y - ygap - lineY, 10, "Compression");
        boolean crossYes = false;
        boolean crossNo = false;
        if (this.localInformation.getCompression() == AS2Message.COMPRESSION_ZLIB) {
            crossYes = true;
        } else {
            crossNo = true;
        }
        this.writeYesNo(creator, rowX2Abs, y - ygap - lineY, 10, crossYes, crossNo);
        boolean presetYes = false;
        if (this.remotePartner != null) {
            presetYes = true;
        }
        this.addFormFieldChoiceYesNo(page, rowX3Abs, y - ygap - lineY, rowWidth3 - 2 * xgap, 10, 10, "COMPRESSION", presetYes);
        lineY = 77;
        this.writeText(creator, rowX1Abs, y - ygap - lineY, 10, "Request sync MDN");
        crossYes = false;
        crossNo = false;
        if (this.localInformation.requestsSyncMDN()) {
            crossYes = true;
        } else {
            crossNo = true;
        }
        this.writeYesNo(creator, rowX2Abs, y - ygap - lineY, 10, crossYes, crossNo);
        this.addFormFieldChoiceYesNo(page, rowX3Abs, y - ygap - lineY, rowWidth3 - 2 * xgap, 10, 10, "MDN_SYNC", presetYes);
        lineY = 92;
        this.writeText(creator, rowX1Abs, y - ygap - lineY, 10, "Request signed MDN");
        crossYes = false;
        crossNo = false;
        if (this.localInformation.requestsSignedMDN()) {
            crossYes = true;
        } else {
            crossNo = true;
        }
        this.writeYesNo(creator, rowX2Abs, y - ygap - lineY, 10, crossYes, crossNo);
        presetYes = false;
        if (this.remotePartner != null) {
            presetYes = true;
        }
        this.addFormFieldChoiceYesNo(page, rowX3Abs, y - ygap - lineY, rowWidth3 - 2 * xgap, 10, 10, "MDN_SIGN", presetYes);
        y -= height;
        height = 27;
        this.drawTableRow(creator, x, y, height, new int[]{rowWidth1, rowWidth2, rowWidth3});
        this.writeTextBold(creator, rowX1Abs, y - ygap, 12, "AS2 Product");
        this.addFormFieldText(page, rowX3Abs, y - ygap, rowWidth3 - 2 * xgap, 10, 10, false, "Product", "<Your AS2 product>");
        this.writeText(creator, rowX2Abs, y - ygap, 10, AS2ServerVersion.getProductName());
        y -= height;
        height = 80;
        this.drawTableRow(creator, x, y, height, new int[]{rowWidth1, rowWidth2, rowWidth3});
        this.writeTextBold(creator, rowX1Abs, y - ygap, 12, "Internet parameter");
        lineY = 17;
        this.writeText(creator, rowX1Abs, y - ygap - lineY, 10, "AS2 receipt URL\n(Single line)");
        String localReceiptURL = this.localInformation.getReceiptURL();
        StringBuilder localReceiptURLTmp = new StringBuilder();
        for (int i = 0; i < localReceiptURL.length(); i++) {
            localReceiptURLTmp.append(localReceiptURL.charAt(i));
            if (i > 0 && i % MAX_LINE_LENGTH == 0) {
                localReceiptURLTmp.append("\n");
            }
        }
        String remotePartnerReceiptURL = this.remotePartner == null ? "<Your AS receipt URL>" : this.remotePartner.getURL();
        StringBuilder remotePartnerReceiptURLTmp = new StringBuilder();
        for (int i = 0; i < remotePartnerReceiptURL.length(); i++) {
            remotePartnerReceiptURLTmp.append(remotePartnerReceiptURL.charAt(i));
            if (i > 0 && i % MAX_LINE_LENGTH == 0) {
                remotePartnerReceiptURLTmp.append("\n");
            }
        }
        this.writeText(creator, rowX2Abs, y - ygap - lineY, 10, localReceiptURLTmp.toString());
        this.addFormFieldText(page, rowX3Abs, y - ygap - lineY, rowWidth3 - 2 * xgap, 50, 10, true, "RECEIPT_URL",
                remotePartnerReceiptURLTmp.toString());
        y -= height;
        height = 130;
        this.drawTableRow(creator, x, y, height, new int[]{rowWidth1, rowWidth2, rowWidth3});
        this.writeTextBold(creator, rowX1Abs, y - ygap, 12, "Comment");
        String comment = this.wrapString(this.localInformation.getComment(), "\n", MAX_LINE_LENGTH);
        this.writeText(creator, rowX2Abs, y - ygap, 10, comment);
        this.addFormFieldText(page, rowX3Abs, y - ygap, rowWidth3 - 2 * xgap, 115, 10, true, "comment", "<Comment>");
        y -= height;
        return (y);
    }

    /**
     * Wordwise wrap a string
     */
    private String wrapString(String s, String deliminator, int length) {
        String result = "";
        int lastdelimPos = 0;
        for (String token : s.split(" ", -1)) {
            if (result.length() - lastdelimPos + token.length() > length) {
                result = result + deliminator + token;
                lastdelimPos = result.length() + 1;
            } else {
                result += (result.isEmpty() ? "" : " ") + token;
            }
        }
        return result;
    }

    private void drawTableRow(CSCreator creator, int x, int y, int height, int[] rowWidth) {
        int width = 0;
        for (int i = 0; i < rowWidth.length; i++) {
            width += rowWidth[i];
        }
        int xposCounter = x;
        int[] rowXPosAbsolute = new int[rowWidth.length + 1];
        rowXPosAbsolute[0] = xposCounter;
        for (int i = 0; i < rowWidth.length; i++) {
            xposCounter += rowWidth[i];
            rowXPosAbsolute[i + 1] = xposCounter;
        }
        //upper line
        creator.penMoveTo(x, y);
        creator.penLineTo(x + width, y);
        //lower line
        creator.penMoveTo(x, y - height);
        creator.penLineTo(x + width, y - height);
        //horizontal lines
        for (int i = 0; i < rowXPosAbsolute.length; i++) {
            creator.penMoveTo(rowXPosAbsolute[i], y);
            creator.penLineTo(rowXPosAbsolute[i], y - height);
        }
        //print the path
        creator.pathStroke();
    }

    private void addFormFieldText(PDPage page, int x, int y, int width, int height, int fontSize, boolean multiline, String formName, String defaultValue) {
        int ygap = 2;
        y = y - height - 2 * ygap;
        if (this.form == null) {
            this.form = this.document.createAcroForm();
        }
        // creating appearances is a hard task.
        // for now let the viewer do the work
        form.setNeedAppearances(true);
        // an annotation is the physical presentation of a field
        PDWidgetAnnotation annotation = (PDWidgetAnnotation) PDWidgetAnnotation.META.createNew();
        annotation.setPrint(true);
        // make it visible on our page
        page.addAnnotation(annotation);
        // at this position.
        CDSRectangle rect = new CDSRectangle(x, y + ygap, x + width + ygap, y - height);
        annotation.setRectangle(rect);
        annotation.setBorderStyleWidth(1);
        PDAppearanceCharacteristics apc = (PDAppearanceCharacteristics) PDAppearanceCharacteristics.META.createNew();
        apc.setBorderColor(new float[]{0, 0, 0});
        apc.setBackgroundColor(new float[]{1, 1, 1});
        annotation.setAppearanceCharacteristics(apc);
        // a text input field with this annotation.
        PDAFTextField field = (PDAFTextField) PDAFTextField.META.createNew();
        // add the field to the form immediately - maybe we propagate information
        form.addField(field);
        // add the visible field
        field.addAnnotation(annotation);
        field.setLocalName(formName);
        field.setDefaultAppearanceFont(PDFontType1.createNew(PDFontType1.FONT_Helvetica));
        field.setDefaultAppearanceFontSize(fontSize);
        field.setDefaultAppearanceFontColor(new float[]{0, 0, 0});
        field.setMultiline(multiline);
        field.setValueString(defaultValue);
        field.setNoExport(false);
    }

    private void addFormFieldChoiceYesNo(PDPage page, int x, int y, int width, int height, int fontSize, String formName,
            boolean presetYes) {
        if (presetYes) {
            String[] options = new String[]{"Yes", "No"};
            this.addFormFieldChoice(page, x, y, width, height, fontSize, formName, options, "Yes");
        } else {
            String[] options = new String[]{"No", "Yes"};
            this.addFormFieldChoice(page, x, y, width, height, fontSize, formName, options, "No");
        }
    }

    private void addFormFieldChoice(PDPage page, int x, int y, int width, int height, int fontSize, String formName,
            String[] options, String selection) {
        int ygap = 2;
        y = y - height - 2 * ygap;
        if (this.form == null) {
            this.form = this.document.createAcroForm();
        }
        // creating appearances is a hard task - for now let the viewer do the work
        form.setNeedAppearances(true);
        // an annotation is the physical presentation of a field
        PDWidgetAnnotation annotation = (PDWidgetAnnotation) PDWidgetAnnotation.META.createNew();
        // make it visible on our page
        page.addAnnotation(annotation);
        // at this position.
        CDSRectangle rect = new CDSRectangle(x, y + ygap, x + width + ygap, y - height);
        annotation.setPrint(true);
        annotation.setRectangle(rect);
        annotation.setBorderStyleWidth(1);
        PDAppearanceCharacteristics apc = (PDAppearanceCharacteristics) PDAppearanceCharacteristics.META.createNew();
        apc.setBorderColor(new float[]{0, 0, 0});
        apc.setBackgroundColor(new float[]{1, 1, 1});
        annotation.setAppearanceCharacteristics(apc);
        // a text input field with this annotation.
        PDAFChoiceField field = (PDAFChoiceField) PDAFChoiceField.META.createNew();
        // add the field to the form immediately - maybe we propagate information
        form.addField(field);
        // add the visible field
        field.addAnnotation(annotation);
        field.setLocalName(formName);
        field.setDefaultAppearanceFont(PDFontType1.createNew(PDFontType1.FONT_Helvetica));
        field.setDefaultAppearanceFontSize(fontSize);
        field.setDefaultAppearanceFontColor(new float[]{0, 0, 0});
        String[] sortedOptions = new String[options.length];
        sortedOptions[0] = selection;
        int index = 1;
        for (String singleOption : options) {
            if (!singleOption.equals(selection)) {
                sortedOptions[index] = singleOption;
                index++;
            }
        }
        field.setOptions(sortedOptions, sortedOptions);
        field.setNoExport(false);
    }

    /**
     * Finally writes the in memory document to the harddisk
     */
    private void saveDocument(String absolutePath) throws IOException {
        FileLocator locator = new FileLocator(absolutePath);
        this.document.save(locator, null);
    }

    public void create(Path outFile) throws Exception {
        PDPage page = this.openNewPage();
        CSCreator creator = CSCreator.createNew(page);
        this.writeHeader(creator);
        int tableEndY = this.drawTable(creator, page);
        this.writeText(creator, this.LEFT_MARGIN, tableEndY - 5, 10, "Please find attached to this document the required certificates for the AS2 security.");
        if (this.localInformation.getCertEncryptData() != null) {
            this.addAttachment(page, "encryptdata.p7b", "Encrypt data to us using this certificate", this.localInformation.getCertEncryptData());
        }
        if (this.localInformation.getCertSSL() != null) {
            this.addAttachment(page, "ssl.p7b", "Use this certificate for the SSL connection", this.localInformation.getCertSSL());
        }
        if (this.localInformation.getCertVerifySignature() != null) {
            this.addAttachment(page, "verifysignature.p7b", "Verify our data signature using this certificate", this.localInformation.getCertVerifySignature());
        }
        this.closePage(creator);
        this.saveDocument(outFile.toAbsolutePath().toString());
    }
}
