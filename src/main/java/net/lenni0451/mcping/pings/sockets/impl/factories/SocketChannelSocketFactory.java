package net.lenni0451.mcping.pings.sockets.impl.factories;

import net.lenni0451.mcping.ServerAddress;
import net.lenni0451.mcping.pings.sockets.factories.ITCPSocketFactory;
import net.lenni0451.mcping.pings.sockets.impl.types.SocketChannelSocket;
import net.lenni0451.mcping.pings.sockets.types.ITCPSocket;

public class SocketChannelSocketFactory implements ITCPSocketFactory {

    @Override
    public ITCPSocket create(ServerAddress serverAddress, int connectTimeout, int readTimeout) {
        return new SocketChannelSocket(serverAddress, connectTimeout, readTimeout);
    }

}
