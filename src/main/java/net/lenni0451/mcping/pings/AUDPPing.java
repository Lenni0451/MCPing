package net.lenni0451.mcping.pings;

import net.lenni0451.mcping.ServerAddress;
import net.lenni0451.mcping.pings.sockets.factories.IUDPSocketFactory;
import net.lenni0451.mcping.pings.sockets.types.IUDPSocket;

import java.io.*;

/**
 * The abstract class used to implement UDP based pings.
 */
public abstract class AUDPPing extends APing {

    private IUDPSocketFactory socketFactory;
    private final int readTimeout;

    public AUDPPing(final IUDPSocketFactory socketFactory, final int readTimeout) {
        this.socketFactory = socketFactory;
        this.readTimeout = readTimeout;
    }

    /**
     * Create a new datagram socket.
     *
     * @param serverAddress The server address
     * @return The created socket
     * @throws IOException If an I/O error occurs
     */
    protected final IUDPSocket connect(final ServerAddress serverAddress) throws IOException {
        return this.socketFactory.create(serverAddress, this.readTimeout);
    }

    /**
     * Write a packet and send it to the given server address.
     *
     * @param s            The UDP socket
     * @param packetWriter The packet writer
     * @throws IOException If an I/O error occurs
     */
    protected void writePacket(final IUDPSocket s, final PacketWriter packetWriter) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);
        packetWriter.write(os);
        s.send(baos.toByteArray());
    }

    /**
     * Read a packet from the given socket.
     *
     * @param s            The UDP socket
     * @param bufferSize   The buffer size
     * @param packetReader The packet reader
     * @throws IOException If an I/O error occurs
     */
    protected void readPacket(final IUDPSocket s, final int bufferSize, PacketReader packetReader) throws IOException {
        byte[] data = s.receive(bufferSize);
        DataInputStream is = new DataInputStream(new ByteArrayInputStream(data));
        packetReader.read(is);
    }


    @FunctionalInterface
    protected interface PacketWriter {
        void write(final DataOutputStream os) throws IOException;
    }

    @FunctionalInterface
    protected interface PacketReader {
        void read(final DataInputStream is) throws IOException;
    }

}
