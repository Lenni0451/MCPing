package net.lenni0451.mcping.pings.sockets.factories;

import net.lenni0451.mcping.ServerAddress;
import net.lenni0451.mcping.pings.sockets.types.IUDPSocket;

import java.io.IOException;

/**
 * A factory interface to create a new {@link IUDPSocket} instance.
 */
public interface IUDPSocketFactory {

    /**
     * Create a new {@link IUDPSocket} instance.
     *
     * @param serverAddress The server address to connect to
     * @param readTimeout   The read timeout
     * @return The connected socket
     * @throws IOException If an I/O error occurs
     */
    IUDPSocket create(final ServerAddress serverAddress, final int readTimeout) throws IOException;

}
