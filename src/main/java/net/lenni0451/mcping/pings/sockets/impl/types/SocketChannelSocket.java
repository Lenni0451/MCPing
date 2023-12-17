package net.lenni0451.mcping.pings.sockets.impl.types;

import net.lenni0451.mcping.ServerAddress;
import net.lenni0451.mcping.exception.ConnectTimeoutException;
import net.lenni0451.mcping.exception.ConnectionRefusedException;
import net.lenni0451.mcping.pings.sockets.types.ITCPSocket;
import net.lenni0451.mcping.stream.SocketChannelInputStream;
import net.lenni0451.mcping.stream.SocketChannelOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.concurrent.*;

public class SocketChannelSocket implements ITCPSocket {

    private final ServerAddress serverAddress;
    private final int connectTimeout;
    private final int readTimeout;
    private SocketChannel socketChannel;
    private InputStream inputStream;
    private OutputStream outputStream;

    public SocketChannelSocket(final ServerAddress serverAddress, final int connectTimeout, final int readTimeout) {
        this.serverAddress = serverAddress;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    @Override
    public void connect() throws IOException {
        CompletableFuture<SocketChannel> channelFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return SocketChannel.open(this.serverAddress.getSocketAddress());
            } catch (Throwable t) {
                this.throwException(t);
                return null;
            }
        });
        try {
            try {
                this.socketChannel = channelFuture.get(this.connectTimeout, TimeUnit.MILLISECONDS);
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof AsynchronousCloseException) throw new InterruptedException();
                else if (cause instanceof UnresolvedAddressException) throw new ConnectionRefusedException(this.serverAddress);
                else throw cause;
            } catch (TimeoutException e) {
                channelFuture.cancel(true);
                throw new ConnectTimeoutException(this.connectTimeout);
            } catch (CancellationException e) {
                throw new InterruptedException();
            }
        } catch (Throwable t) {
            this.throwException(t);
        }
        this.socketChannel.configureBlocking(false);
        this.inputStream = new SocketChannelInputStream(this.socketChannel, this.readTimeout);
        this.outputStream = new SocketChannelOutputStream(this.socketChannel);
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
    public void close() throws Exception {
        this.socketChannel.close();
        this.inputStream.close();
        this.outputStream.close();
    }

    private <E extends Throwable> void throwException(final Throwable throwable) throws E {
        throw (E) throwable;
    }

}
