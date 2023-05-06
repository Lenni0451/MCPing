package net.lenni0451.mcping.pings.sockets.types;

import java.io.Closeable;
import java.io.IOException;

/**
 * An interface used to implement UDP sockets. This allows to use different network libraries in MCPing.
 */
public interface IUDPSocket extends Closeable {

    /**
     * Send the given data to the target address.
     *
     * @param data The data to send
     * @throws IOException If an I/O error occurs
     */
    void send(final byte[] data) throws IOException;

    /**
     * Receive data from the socket.
     *
     * @param bufferSize The buffer size
     * @return The received data
     * @throws IOException If an I/O error occurs
     */
    byte[] receive(final int bufferSize) throws IOException;

}
