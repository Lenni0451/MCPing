package net.lenni0451.mcping.pings.sockets.types;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An interface used to implement TCP sockets. This allows to use different network libraries in MCPing.
 */
public interface ITCPSocket extends AutoCloseable {

    /**
     * Connect the socket to the server.
     *
     * @throws IOException If an I/O error occurs
     */
    void connect() throws IOException;

    /**
     * @return The input stream to read data from
     */
    InputStream getInputStream();

    /**
     * @return The output stream to write data to
     */
    OutputStream getOutputStream();

}
