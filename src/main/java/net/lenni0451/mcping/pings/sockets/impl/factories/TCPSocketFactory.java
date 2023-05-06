package net.lenni0451.mcping.pings.sockets.impl.factories;

import net.lenni0451.mcping.ServerAddress;
import net.lenni0451.mcping.pings.sockets.factories.ITCPSocketFactory;
import net.lenni0451.mcping.pings.sockets.impl.types.TCPSocket;
import net.lenni0451.mcping.pings.sockets.types.ITCPSocket;

import java.io.IOException;

/**
 * The default {@link ITCPSocketFactory} implementation.
 */
public class TCPSocketFactory implements ITCPSocketFactory {

    @Override
    public ITCPSocket create(ServerAddress serverAddress, int connectTimeout, int readTimeout) throws IOException {
        return new TCPSocket(serverAddress, connectTimeout, readTimeout);
    }

}
