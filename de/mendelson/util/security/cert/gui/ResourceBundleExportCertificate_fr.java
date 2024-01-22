//$Header: /oftp2/de/mendelson/util/security/cert/gui/ResourceBundleExportCertificate_fr.java 8     7/12/22 16:31 Heller $
package de.mendelson.util.security.cert.gui;
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
 * @author E.Pailleau
 * @version $Revision: 8 $
 */
public class ResourceBundleExportCertificate_fr extends MecResourceBundle{

    public static final long serialVersionUID = 1L;
    
    @Override
  public Object[][] getContents() {
    return CONTENTS;
  }

  /**List of messages in the specific language*/
  static final Object[][] CONTENTS = {
        
    {"button.ok", "Valider" },
    {"button.cancel", "Annuler" },
    {"button.browse", "Parcourir..." },            
    {"title", "Exporter un certificat X.509" },
    {"label.exportfile", "Nom du fichier" },
    {"label.exportfile.hint", "Fichier de certificat qui est généré"},
    {"label.alias", "Alias" },        
    {"label.exportformat", "Format" },
    {"filechooser.certificate.export", "Merci de sélectionner le fichier d''export du certificat." },
    {"certificate.export.error.title", "L''export du certificat a échoué" },
    {"certificate.export.error.message", "L''export du certificat suivant a échoué:\n{0}" },
    {"certificate.export.success.title", "Succès" },
    {"certificate.export.success.message", "Le certificat a été exporté avec succès a\n\"{0}\"" }, 
    {JDialogExportCertificate.PEM, "Format texte (PEM. *.cer)" },
    {JDialogExportCertificate.DER, "Format binaire (DER, *.cer)" },
    {JDialogExportCertificate.PKCS7, "Avec chaîne de confiance (PKCS#7, *.p7b)" },  
    {JDialogExportCertificate.SSH2, "Format SSH2 (clé publique, *.pub)"},
  };		
  
}
