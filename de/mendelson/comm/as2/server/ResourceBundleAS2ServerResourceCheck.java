//$Header: /as2/de/mendelson/comm/as2/server/ResourceBundleAS2ServerResourceCheck.java 3     4/06/18 1:35p Heller $
package de.mendelson.comm.as2.server;

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
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class ResourceBundleAS2ServerResourceCheck extends MecResourceBundle {

    public static final long serialVersionUID = 1L;
    
    @Override
    public Object[][] getContents() {
        return contents;
    }
    /**
     * List of messages in the specific language
     */
    static final Object[][] contents = {
        {"port.in.use", "The port {0} is in use by another process."},
        {"warning.few.cpucores", "The system detected only {0} CPU core(s) assigned to the mendelson AS2 server process. With this few number of CPU cores the processing speed will be really slow and some functions may not work properly. Please assign at least 4 CPU cores the the AS2 server process."},
        {"warning.low.maxheap", "The system detected only about {0} available heap memory assigned to the mendelson AS2 server process (Don't worry, this is about 10% less than the value you set up in the start script). Please set the max heap memory to at least 1GB."},};
}
