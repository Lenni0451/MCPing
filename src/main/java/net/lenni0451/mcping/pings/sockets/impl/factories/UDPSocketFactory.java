package net.lenni0451.mcping.pings.sockets.impl.factories;

import net.lenni0451.mcping.ServerAddress;
import net.lenni0451.mcping.pings.sockets.factories.IUDPSocketFactory;
import net.lenni0451.mcping.pings.sockets.impl.types.UDPSocket;
import net.lenni0451.mcping.pings.sockets.types.IUDPSocket;

import java.io.IOException;

/**
 * The default {@link IUDPSocketFactory} implementation.
 */
public class UDPSocketFactory implements IUDPSocketFactory {

    @Override
    public IUDPSocket create(ServerAddress serverAddress, int readTimeout) throws IOException {
        return new UDPSocket(serverAddress, readTimeout);
    }

}
