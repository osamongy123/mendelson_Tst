//$Header: /as2/de/mendelson/comm/as2/message/postprocessingevent/IProcessingExecution.java 2     10.09.20 12:57 Heller $
package de.mendelson.comm.as2.message.postprocessingevent;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Interface for all execution classes that execute postprocess processes
 *
 * @author S.Heller
 * @version $Revision: 2 $
 */
public interface IProcessingExecution{

    public void executeProcess(ProcessingEvent event) throws Exception;
    
}
