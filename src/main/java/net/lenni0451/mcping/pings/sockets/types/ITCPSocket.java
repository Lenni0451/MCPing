package net.lenni0451.mcping.pings.sockets.types;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An interface used to implement TCP sockets. This allows to use different network libraries in MCPing.
 */
public interface ITCPSocket extends Closeable {

    /**
     * @return The input stream to read data from
     */
    InputStream getInputStream();

    /**
     * @return The output stream to write data to
     */
    OutputStream getOutputStream();

}
