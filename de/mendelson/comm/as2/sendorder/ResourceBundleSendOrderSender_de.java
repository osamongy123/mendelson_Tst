//$Header: /as2/de/mendelson/comm/as2/sendorder/ResourceBundleSendOrderSender_de.java 5     6.12.18 16:26 Heller $
package de.mendelson.comm.as2.sendorder;
import de.mendelson.util.MecResourceBundle;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * ResourceBundle to localize a mendelson product
 * @author S.Heller
 * @version $Revision: 5 $
 */
public class ResourceBundleSendOrderSender_de extends MecResourceBundle{
    
    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }
    
    /**List of messages in the specific language*/
    static final Object[][] CONTENTS = {
        {"message.packed", "Ausgehende AS2 Nachricht aus \"{0}\" für den Empfänger \"{1}\" erstellt in {3}, Rohdatengrösse: {2}, benutzerdefinierte id: \"{4}\"" },
        {"sendoder.sendfailed", "Es trat ein Problem beim Verarbeiten eines Sendeauftrags auf: [{0}] \"{1}\" - die Daten wurden nicht an den Partner übermittelt." },
    };
    
}