//$Header: /as2/de/mendelson/util/security/csr/ResourceBundleCSRUtil_de.java 3     4/06/18 1:35p Heller $
package de.mendelson.util.security.csr;
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
 * @version $Revision: 3 $
 */
public class ResourceBundleCSRUtil_de extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {                
        {"verification.failed", "Operation fehlgeschlagen - Die Überprüfung des erstellten Certificate Sign Requests (CSR) ist fehlgeschlagen." },
        {"no.certificates.in.reply", "Operation fehlgeschlagen - Der Schlüssel konnte nicht gepatcht werden, es wurden in der CA Antwort keine Zertifikate gefunden." },
        {"missing.cert.in.trustchain", "Operation fehlgeschlagen - Es fehlen für diese Operation Zertifikate im System.\nBitte importieren Sie zunächst das Zertifikat mit den Eckdaten (issuer)\n{0}." },
        {"response.chain.incomplete", "Operation fehlgeschlagen - Der Trust Chain der CSR Antwort ist unvollständig." },
        {"response.verification.failed", "Operation fehlgeschlagen - Der Trust Chain der CSR Antwort konnte nicht verifiziert werden: {0}" },
        {"response.public.key.does.not.match", "Operation fehlgeschlagen - Diese Antwort der CA passt nicht zu diesem Schlüssel." },
    };


    

}