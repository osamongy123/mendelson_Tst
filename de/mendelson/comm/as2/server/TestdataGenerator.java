//$Header: /as2/de/mendelson/comm/as2/server/TestdataGenerator.java 3     24.09.19 16:05 Heller $
package de.mendelson.comm.as2.server;

import de.mendelson.util.AS2Tools;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Generates a test file that could be sent to a partner for testing purpose
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class TestdataGenerator {

    private TestdataGenerator(){}
    
    public static Path generateTestdata() throws Exception{
        Path testFile = AS2Tools.createTempFile("testdata", ".txt");
        String content = 
                "Dies sind Testdaten, die zum Prüfen der Verbindung geschickt wurden. Bitte ignorieren Sie sie in Ihrer Verarbeitung.\n\n"
                + "This is test data sent to check the connection. Please ignore it in your processing.\n\n"                
                + "Il s'agit de données de test envoyées pour vérifier la connexion. S'il te plaît, ignore-le dans ton traitement.\n\n"
                + "Estos son los datos de prueba enviados para comprobar la conexión. Por favor, ignóralo en tu procesamiento.\n\n"
                + "Si tratta di dati di prova inviati per controllare la connessione. Per favore, ignoralo nel tuo processo di elaborazione.\n\n"                
                + "Dit zijn testgegevens die worden verzonden om de verbinding te controleren. Negeer het alsjeblieft in je verwerking.\n\n"
                + "\u042D\u0442\u043E \u0442\u0435\u0441\u0442\u043E\u0432\u044B\u0435 \u0434\u0430\u043D\u043D\u044B\u0435, \u043E\u0442\u043F\u0440\u0430\u0432\u043B\u044F\u0435\u043C\u044B\u0435 \u0434\u043B\u044F \u043F\u0440\u043E\u0432\u0435\u0440\u043A\u0438 \u0441\u043E\u0435\u0434\u0438\u043D\u0435\u043D\u0438\u044F. \u041F\u043E\u0436\u0430\u043B\u0443\u0439\u0441\u0442\u0430, \u0438\u0433\u043D\u043E\u0440\u0438\u0440\u0443\u0439\u0442\u0435 \u0438\u0445 \u043F\u0440\u0438 \u043E\u0431\u0440\u0430\u0431\u043E\u0442\u043A\u0435.\n\n"
                + "S\u0105 to dane testowe wysy\u0142ane w celu sprawdzenia po\u0142\u0105czenia. Prosz\u0119 zignorowa\u0107 je w procesie przetwarzania.\n\n";
        Files.write(testFile, content.getBytes(StandardCharsets.UTF_8));
        return( testFile );
    }
}
