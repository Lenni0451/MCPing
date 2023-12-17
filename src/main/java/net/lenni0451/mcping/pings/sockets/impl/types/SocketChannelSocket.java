package net.lenni0451.mcping.pings.sockets.impl.types;

import net.lenni0451.mcping.ServerAddress;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;

public class SocketChannelSocket extends TCPSocket {

    public SocketChannelSocket(final ServerAddress serverAddress, final int connectTimeout, final int readTimeout) {
        super(serverAddress, connectTimeout, readTimeout);
    }

    @Override
    protected Socket newSocket() throws IOException {
        return SocketChannel.open().socket();
    }

}
