//$Header: /as2/de/mendelson/util/clientserver/codec/ClientServerCodecFactory.java 4     11/02/22 13:14 Heller $
package de.mendelson.util.clientserver.codec;

import de.mendelson.util.clientserver.ClientSessionHandlerCallback;
import java.util.logging.Logger;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Factory that handles encoding/decoding of the requests
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class ClientServerCodecFactory implements ProtocolCodecFactory {

    private final ProtocolEncoder encoder;
    private final ProtocolDecoder decoder;

    /**
     * 
     * @param logger
     * @param clientCallback This may be null if there is no callback or this is not a client instance
     */
    public ClientServerCodecFactory( Logger logger, ClientSessionHandlerCallback clientCallback) {
        this.encoder = new ClientServerEncoder();
        this.decoder = new ClientServerDecoder(logger, clientCallback);
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession is) throws Exception {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession is) throws Exception {
        return decoder;
    }
}
