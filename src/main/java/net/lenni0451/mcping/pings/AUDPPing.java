package net.lenni0451.mcping.pings;

import net.lenni0451.mcping.ServerAddress;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public abstract class AUDPPing extends APing {

    private final int readTimeout;

    public AUDPPing(final int readTimeout) {
        this.readTimeout = readTimeout;
    }


    protected final DatagramSocket connect() throws IOException {
        DatagramSocket s = new DatagramSocket();
        s.setSoTimeout(this.readTimeout);
        return s;
    }

    protected void writePacket(final DatagramSocket s, final ServerAddress serverAddress, final int defaultPort, final PacketWriter packetWriter) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);

        packetWriter.write(os);

        DatagramPacket packet = new DatagramPacket(baos.toByteArray(), baos.size(), serverAddress.toInetSocketAddress(defaultPort));
        s.send(packet);
    }

    protected void readPacket(final DatagramSocket s, final int bufferSize, PacketReader packetReader) throws IOException {
        DatagramPacket packet = new DatagramPacket(new byte[bufferSize], bufferSize);
        s.receive(packet);
        DataInputStream is = new DataInputStream(new ByteArrayInputStream(Arrays.copyOfRange(packet.getData(), 0, packet.getLength())));

        packetReader.read(is);
    }


    protected interface PacketWriter {
        void write(final DataOutputStream os) throws IOException;
    }

    protected interface PacketReader {
        void read(final DataInputStream is) throws IOException;
    }

}
