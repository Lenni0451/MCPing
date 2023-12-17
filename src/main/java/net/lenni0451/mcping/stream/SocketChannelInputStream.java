package net.lenni0451.mcping.stream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SocketChannelInputStream extends InputStream {

    private final SocketChannel socketChannel;
    private final int readTimeout;
    private final ByteBuffer buffer;
    private final Selector selector;

    public SocketChannelInputStream(final SocketChannel socketChannel, final int readTimeout) throws IOException {
        this.socketChannel = socketChannel;
        this.readTimeout = readTimeout;
        this.buffer = ByteBuffer.allocateDirect(1);
        this.selector = Selector.open();
        this.socketChannel.register(this.selector, SelectionKey.OP_READ);
    }

    private void removeKey() {
        Iterator<SelectionKey> it = this.selector.selectedKeys().iterator();
        if (it.hasNext()) {
            it.next();
            it.remove();
        }
    }

    @Override
    public int read() throws IOException {
        this.buffer.clear();
        while (this.buffer.position() == 0) {
            int numKeys = this.selector.select(this.readTimeout);
            if (numKeys == 0) throw new IOException("Read timeout");
            this.removeKey();
            this.socketChannel.read(this.buffer);
        }
        this.buffer.flip();
        return this.buffer.get() & 0xFF;
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.selector.close();
        this.socketChannel.close();
    }

}
