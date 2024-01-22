//$Header: /as2/de/mendelson/comm/as2/server/AnonymousProcessingAS2.java 6     1-11-16 12:19p Heller $
package de.mendelson.comm.as2.server;

import de.mendelson.comm.as2.clientserver.message.IncomingMessageRequest;
import de.mendelson.util.clientserver.about.ServerInfoRequest;
import de.mendelson.util.clientserver.AnonymousProcessing;
import de.mendelson.util.clientserver.messages.ClientServerMessage;
import org.apache.mina.core.session.IoSession;

/**
 * Contains all request messages that could be processed without a login
 *
 * @author S.Heller
 * @version $Revision: 6 $
 * @since build 68
 */
public class AnonymousProcessingAS2 implements AnonymousProcessing {

    @Override
    public boolean processMessageWithoutLogin(IoSession session, ClientServerMessage message) {
        if (message instanceof IncomingMessageRequest ) {
            return (true);
        }
        if (message instanceof ServerInfoRequest) {
            return (true);
        }        
        return (false);
    }
}
