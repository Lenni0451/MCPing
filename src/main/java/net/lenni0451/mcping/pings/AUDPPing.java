package net.lenni0451.mcping.pings;

import net.lenni0451.mcping.ServerAddress;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

/**
 * The abstract class used to implement UDP based pings.
 */
public abstract class AUDPPing extends APing {

    private final int readTimeout;

    public AUDPPing(final int readTimeout) {
        this.readTimeout = readTimeout;
    }


    /**
     * Create a new datagram socket.
     *
     * @return The created socket
     * @throws IOException If an I/O error occurs
     */
    protected final DatagramSocket connect() throws IOException {
        DatagramSocket s = new DatagramSocket();
        s.setSoTimeout(this.readTimeout);
        return s;
    }

    /**
     * Write a packet and send it to the given server address.
     *
     * @param s             The UDP socket
     * @param serverAddress The server address
     * @param packetWriter  The packet writer
     * @throws IOException If an I/O error occurs
     */
    protected void writePacket(final DatagramSocket s, final ServerAddress serverAddress, final PacketWriter packetWriter) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);

        packetWriter.write(os);

        DatagramPacket packet = new DatagramPacket(baos.toByteArray(), baos.size(), serverAddress.toInetSocketAddress());
        s.send(packet);
    }

    /**
     * Read a packet from the given socket.
     *
     * @param s            The UDP socket
     * @param bufferSize   The buffer size
     * @param packetReader The packet reader
     * @throws IOException If an I/O error occurs
     */
    protected void readPacket(final DatagramSocket s, final int bufferSize, PacketReader packetReader) throws IOException {
        DatagramPacket packet = new DatagramPacket(new byte[bufferSize], bufferSize);
        s.receive(packet);
        DataInputStream is = new DataInputStream(new ByteArrayInputStream(Arrays.copyOfRange(packet.getData(), 0, packet.getLength())));

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
