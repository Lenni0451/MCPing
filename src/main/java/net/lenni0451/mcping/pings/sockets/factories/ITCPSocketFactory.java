package net.lenni0451.mcping.pings.sockets.factories;

import net.lenni0451.mcping.ServerAddress;
import net.lenni0451.mcping.pings.sockets.types.ITCPSocket;

import java.io.IOException;

/**
 * A factory interface to create a new {@link ITCPSocket} instance.
 */
public interface ITCPSocketFactory {

    /**
     * Create a new {@link ITCPSocket} instance.<br>
     * The socket should not be connected yet.
     *
     * @param serverAddress  The server address to connect to
     * @param connectTimeout The connect timeout
     * @param readTimeout    The read timeout
     * @return The connected socket
     * @throws IOException If an I/O error occurs
     */
    ITCPSocket create(final ServerAddress serverAddress, final int connectTimeout, final int readTimeout);

}
