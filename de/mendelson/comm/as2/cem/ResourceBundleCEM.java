//$Header: /as2/de/mendelson/comm/as2/cem/ResourceBundleCEM.java 13    7.12.18 11:54 Heller $
package de.mendelson.comm.as2.cem;

import de.mendelson.comm.as2.cem.messages.TrustResponse;
import de.mendelson.util.MecResourceBundle;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize gui entries
 *
 * @author S.Heller
 * @version $Revision: 13 $
 */
public class ResourceBundleCEM extends MecResourceBundle {

    public static final long serialVersionUID = 1L;

    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

    /**
     * List of messages in the specific language
     */
    static final Object[][] CONTENTS = {
        {"trustrequest.rejected", "The Trust Request answer has set to the state \"" + TrustResponse.STATUS_REJECTED_STR + "\"." },
        {"trustrequest.accepted", "The Trust Request answer has set to the state \"" + TrustResponse.STATUS_ACCEPTED_STR + "\"." },
        {"trustrequest.working.on", "Working on trust request {0}." },
        {"trustrequest.certificates.found", "Number of transmitted certificates found: {0}." },
        {"cem.validated.schema", "The inbound CEM has been validated successfully."},        
        {"cem.structure.info", "Number of trust requests in the inbound CEM: {0}" },
        {"transmitted.certificate.info", "The transmitted certificate has the IssuerDN=\"{0}\" and the serial number \"{1}\"." },
        {CEMReceiptController.KEYSTORE_TYPE_ENC_SIGN + ".cert.already.imported", "The submitted CEM certificate does already exist in the underlaying enc/sign keystore (alias {0}), the import has been skipped."},
        {CEMReceiptController.KEYSTORE_TYPE_SSL + ".cert.already.imported", "The submitted CEM certificate does already exist in the underlaying SSL/TLS keystore (alias {0}), the import has been skipped."},
        {CEMReceiptController.KEYSTORE_TYPE_ENC_SIGN + ".cert.imported.success", "The submitted CEM certificate has been imported sucessfully to the underlaying enc/sign keystore (alias {0})."},
        {CEMReceiptController.KEYSTORE_TYPE_SSL + ".cert.imported.success", "The submitted CEM certificate has been imported sucessfully to the underlaying SSL/TLS keystore (alias {0})."},
        {"category." + CEMEntry.CATEGORY_CRYPT, "Encryption"},
        {"category." + CEMEntry.CATEGORY_SIGN, "Signature"},
        {"category." + CEMEntry.CATEGORY_SSL, "SSL"},
        {"state." + CEMEntry.STATUS_ACCEPTED_INT, "Accepted by {0}"},
        {"state." + CEMEntry.STATUS_PENDING_INT, "No answer so far from {0}"},
        {"state." + CEMEntry.STATUS_REJECTED_INT, "Rejected by {0}"},
        {"state." + CEMEntry.STATUS_CANCELED_INT, "Canceled"},
        {"state." + CEMEntry.STATUS_PROCESSING_ERROR_INT, "Processing error"},
        {"cemtype.response", "The CEM message is a certificate response"},
        {"cemtype.request", "The CEM message is a certificate request"},
        {"cem.response.relatedrequest.found", "The CEM response refers to the existing request \"{0}\""},
        {"cem.response.prepared", "CEM response message has been created for the request {0}"},
        {"cem.created.request", "Generated CEM request from \"{0}\" to \"{1}\". It includes the certificate with the issuerDN \"{2}\" and the serial \"{3}\". The defined purpose is {4}." },
    };

}
