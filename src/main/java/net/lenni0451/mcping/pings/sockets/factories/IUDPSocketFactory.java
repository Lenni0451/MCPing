package net.lenni0451.mcping.pings.sockets.factories;

import net.lenni0451.mcping.ServerAddress;
import net.lenni0451.mcping.pings.sockets.types.IUDPSocket;

/**
 * A factory interface to create a new {@link IUDPSocket} instance.
 */
public interface IUDPSocketFactory {

    /**
     * Create a new {@link IUDPSocket} instance.<br>
     * The socket should not be connected yet.
     *
     * @param serverAddress The server address to connect to
     * @param readTimeout   The read timeout
     * @return The connected socket
     */
    IUDPSocket create(final ServerAddress serverAddress, final int readTimeout);

}
