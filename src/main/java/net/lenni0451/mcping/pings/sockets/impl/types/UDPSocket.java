package net.lenni0451.mcping.pings.sockets.impl.types;

import net.lenni0451.mcping.ServerAddress;
import net.lenni0451.mcping.exception.ReadTimeoutException;
import net.lenni0451.mcping.pings.sockets.types.IUDPSocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.Arrays;

/**
 * The default {@link DatagramSocket} implementation for the {@link IUDPSocket} interface.
 */
public class UDPSocket implements IUDPSocket {

    private final ServerAddress serverAddress;
    private final int readTimeout;
    private DatagramSocket socket;

    public UDPSocket(final ServerAddress serverAddress, final int readTimeout) {
        this.serverAddress = serverAddress;
        this.readTimeout = readTimeout;
    }

    @Override
    public void connect() throws IOException {
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(this.readTimeout);
    }

    @Override
    public void send(byte[] data) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, data.length, this.serverAddress.getSocketAddress());
        this.socket.send(packet);
    }

    @Override
    public byte[] receive(int bufferSize) throws IOException {
        DatagramPacket packet = new DatagramPacket(new byte[bufferSize], bufferSize);
        try {
            this.socket.receive(packet);
        } catch (SocketTimeoutException e) {
            throw new ReadTimeoutException(this.readTimeout);
        }
        return Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
    }

    @Override
    public void close() throws IOException {
        this.socket.close();
    }

}
