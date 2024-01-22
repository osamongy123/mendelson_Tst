//$Header: /as2/de/mendelson/util/security/cert/gui/keygeneration/ResourceBundleGenerateKey.java 13    24/10/22 12:52 Heller $
package de.mendelson.util.security.cert.gui.keygeneration;
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
 * @author S.Heller
 * @version $Revision: 13 $
 */
public class ResourceBundleGenerateKey extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {

        {"title", "Generate key" },
        {"button.ok", "Ok" },
        {"button.cancel", "Cancel" },
        {"label.keytype", "Key type" },
        {"label.keytype.help", "<HTML><strong>Key type</strong><br><br>"
            + "This is the algorithm for creating the key. There are advantages and disadvantages for the resulting keys, depending on the algorithm.<br>"
            + "As of 2022, we would recommend an RSA key with a key length of 2048 or 4096 bits."
            + "</HTML>"
        },
        {"label.signature", "Signature" },
        {"label.signature.help", "<HTML><strong>Signature</strong><br><br>"
            + "This is the signature algorithm with which the key is signed. It is needed for integrity tests "
            + "of the key itself. This parameter has nothing to do with the signature capabilities of the key - "
            + "for example, you can create SHA-2 signatures with a SHA-1 signed key or vice versa.<br>"
            + "We would recommend a SHA-2 signed key as of 2022."
            + "</HTML>"
        },
        {"label.size", "Size" },
        {"label.size.help", "<HTML><strong>Size</strong><br><br>"
            + "This is the key length of the key. In principle, cryptographic operations with larger key lengths are more secure "
            + "than cryptographic operations with keys of smaller key lengths. However, the disadvantage of large key lengths "
            + "is that cryptographic operations take significantly longer, which can significantly slow down data processing "
            + "depending on the computing power.<br>" 
            + "As of 2022, we would recommend a key with a length of 2048 or 4096 bits."
            + "</HTML>"
        },        
        {"label.commonname", "Common name" },
        {"label.commonname.help", "<HTML><strong>Common Name</strong><br><br>"
            + "This is the name of your domain as it corresponds to the DNS record. This "
            + "parameter is important for the handshake of a TLS connection. "
            + "It is possible (but not recommended!) to enter an IP address here. It is also "
            + "possible to create a wildcard certificate if you replace parts of "
            + "the domain with * here. But this is not recommended either, because not "
            + "all partners accept such keys.<br>"
            + "If you want to use this key as TLS key and this entry points to a non-existent domain "
            + "or does not match your domain, most systems should abort incoming TLS connections."
            + "</HTML>"
        },
        {"label.commonname.hint", "(Domain name of the server)" },
        {"label.organisationunit", "Organisation unit" },
        {"label.organisationname", "Organisation name" },
        {"label.locality", "Locality" },
        {"label.locality.hint", "(City)" },
        {"label.state", "State" },
        {"label.countrycode", "Country code" },
        {"label.countrycode.hint", "(2 digits, ISO 3166)" },
        {"label.mailaddress", "Mail address" },
        {"label.mailaddress.help", "<HTML><strong>Mail address</strong><br><br>"
            + "This is the mail address associated with the key. Technically, this parameter is unimportant. "
            + "However, if you want to have the key trusted, this mail address is usually used for "
            + "communication with the CA. Furthermore, the mail address should also be on the server''s "
            + "domain and correspond to something like webmaster@domain or similar, because most CAs "
            + "thus check whether you are in possession of the associated domain."
            + "</HTML>"
        },
        {"label.validity", "Validity in days" },
        {"label.validity.help", "<HTML><strong>Validity in days</strong><br><br>"
            + "This value is only interesting for self signed keys. In case of a trust process the CA will "
            + "overwrite this value."
            + "</HTML>"
        },
        {"label.purpose", "Key purpose / Additional key usage" },
        {"label.purpose.encsign", "Encryption and signature" },
        {"label.purpose.ssl", "TLS/SSL" },        
        {"label.subjectalternativenames", "Subject alternative names" },        
        {"warning.mail.in.domain", "The mail address is not part of the domain \"{0}\" (e.g. myname@{0}).\nThis might be a problem if you would like to trust the key later."},
        {"warning.nonexisting.domain", "The domain \"{0}\" seems not to exist." },
        {"warning.invalid.mail", "The mail address \"{0}\" is invalid." },
        {"button.reedit", "Edit settings" },
        {"button.ignore", "Ignore warnings" },
        {"warning.title", "Possible key settings problem" },
        {"view.expert", "Expert view" },
        {"view.basic", "Basic view" },
        {"label.namedeccurve", "Curve" },
        {"label.namedeccurve.help", "<HTML><strong>Curve</strong><br><br>"
            + "Selects the name of the EC curve to be used for the generation of the key. The desired key "
            + "length is usually part of the curve name, for example the key of the curve \"BrainpoolP256r1\" has a "
            + "length of 256bit. The most widely used curve as of 2022 (about 75% of all EC certificates "
            + "on the Internet use it) is NIST P-256, which you can find here under the name \"Prime256v1\". "
            + "It is the standard curve of OpenSSL as of 2022."
            + "</HTML>" },
    };
    
}