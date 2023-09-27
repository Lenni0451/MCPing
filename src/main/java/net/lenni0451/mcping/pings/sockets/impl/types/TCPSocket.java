package net.lenni0451.mcping.pings.sockets.impl.types;

import net.lenni0451.mcping.ServerAddress;
import net.lenni0451.mcping.exception.ConnectTimeoutException;
import net.lenni0451.mcping.exception.ConnectionRefusedException;
import net.lenni0451.mcping.pings.sockets.types.ITCPSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * The default {@link Socket} implementation for the {@link ITCPSocket} interface.
 */
public class TCPSocket implements ITCPSocket {

    private final ServerAddress serverAddress;
    private final int connectTimeout;
    private final int readTimeout;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public TCPSocket(final ServerAddress serverAddress, final int connectTimeout, final int readTimeout) {
        this.serverAddress = serverAddress;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    @Override
    public void connect() throws IOException {
        try {
            this.socket = new Socket();
            this.socket.setSoTimeout(readTimeout);
            this.socket.connect(this.serverAddress.toInetSocketAddress(), this.connectTimeout);

            this.inputStream = this.socket.getInputStream();
            this.outputStream = this.socket.getOutputStream();
        } catch (ConnectException e) {
            throw new ConnectionRefusedException(serverAddress);
        } catch (SocketTimeoutException e) {
            throw new ConnectTimeoutException(connectTimeout);
        }
    }

    @Override
    public InputStream getInputStream() {
        return this.inputStream;
    }

    @Override
    public OutputStream getOutputStream() {
        return this.outputStream;
    }

    @Override
    public void close() throws IOException {
        this.socket.close();
    }

}
