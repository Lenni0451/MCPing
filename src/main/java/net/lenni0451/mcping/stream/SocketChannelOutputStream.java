package net.lenni0451.mcping.stream;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

@ParametersAreNonnullByDefault
public class SocketChannelOutputStream extends OutputStream {

    private final SocketChannel socketChannel;

    public SocketChannelOutputStream(final SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    @Override
    public void write(int b) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put((byte) b);
        buffer.flip();
        this.socketChannel.write(buffer);
    }

    @Override
    public void write(byte[] b) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(b);
        this.socketChannel.write(buffer);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(b, off, len);
        this.socketChannel.write(buffer);
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.socketChannel.close();
    }

}
