//$Header: /oftp2/de/mendelson/util/clientserver/codec/ClientServerEncoder.java 4     14.09.12 10:03 Heller $
package de.mendelson.util.clientserver.codec;

import de.mendelson.util.clientserver.ClientServerSessionHandler;
import de.mendelson.util.clientserver.messages.LoginState;
import de.mendelson.util.security.BCCryptoHelper;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Encodes a passed object to send it to the client/server. The structure is: 4
 * bytes: 32 bit value that contains the [object length] [object length] bytes:
 * Object that is transmitted
 *
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class ClientServerEncoder implements ProtocolEncoder {

    private BCCryptoHelper helper = new BCCryptoHelper();

    public ClientServerEncoder() {
    }

    /**
     * Returns a 32 bit value as byte array, fixed length 4 bytes
     */
    private byte[] encodeLengthHeader32Bit(int length) {
        BigInteger lengthValue = BigInteger.valueOf(length);
        byte[] lengthHeader = new byte[4];
        Arrays.fill(lengthHeader, (byte) 0);
        byte[] lengthValueArray = lengthValue.toByteArray();
        System.arraycopy(lengthValueArray, 0, lengthHeader, lengthHeader.length - lengthValueArray.length, lengthValueArray.length);
        return (lengthHeader);
    }

    @Override
    public void encode(IoSession ioSession, Object message, ProtocolEncoderOutput encoderOutput) throws Exception {
        ByteArrayOutputStream objectBuffer = new ByteArrayOutputStream();
        ObjectOutput objectOut = new ObjectOutputStream(objectBuffer);
        objectOut.writeObject(message);
        objectOut.flush();
        objectOut.close();
        byte[] objectArray = objectBuffer.toByteArray();        
        byte[] objectHeader = this.encodeLengthHeader32Bit(objectArray.length);
        IoBuffer buffer = IoBuffer.allocate(objectHeader.length + objectArray.length, false);
        buffer.put(objectHeader);
        buffer.put(objectArray);
        buffer.flip();
        encoderOutput.write(buffer);
    }

    @Override
    public void dispose(IoSession session) throws Exception {
        // nothing to dispose
    }
//    //test stuff
//    public static final void main( String[] args ){
//        ClientServerEncoder encoder = new ClientServerEncoder();
//        byte[] test1 = encoder.encodeLengthHeader32Bit(12837);
//        byte[] test2 = encoder.encodeLengthHeader32Bit(Integer.MAX_VALUE);
//        System.out.println(encoder.decodeLengthHeader32Bit(test1));
//        System.out.println(encoder.decodeLengthHeader32Bit(test2));
//    }
}
