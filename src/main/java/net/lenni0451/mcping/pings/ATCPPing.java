package net.lenni0451.mcping.pings;

import com.google.gson.JsonObject;
import net.lenni0451.mcping.ServerAddress;
import net.lenni0451.mcping.exception.ConnectTimeoutException;
import net.lenni0451.mcping.exception.ConnectionRefusedException;
import net.lenni0451.mcping.pings.sockets.factories.ITCPSocketFactory;
import net.lenni0451.mcping.pings.sockets.types.ITCPSocket;
import net.lenni0451.mcping.stream.MCInputStream;
import net.lenni0451.mcping.stream.MCOutputStream;

import java.io.IOException;

/**
 * The abstract class used to implement TCP based pings.
 */
public abstract class ATCPPing extends APing {

    protected final ITCPSocketFactory socketFactory;
    protected final int connectTimeout;
    protected final int readTimeout;
    protected final int protocolVersion;
    private ITCPSocket socket;

    public ATCPPing(final ITCPSocketFactory socketFactory, final int connectTimeout, final int readTimeout, final int protocolVersion) {
        this.socketFactory = socketFactory;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.protocolVersion = protocolVersion;
    }

    /**
     * Connect a socket to the given server address.
     *
     * @param serverAddress The server address
     * @return The connected socket
     * @throws IOException                If an I/O error occurs
     * @throws ConnectTimeoutException    If the connection timed out
     * @throws ConnectionRefusedException If the connection was refused
     */
    protected final ITCPSocket connect(final ServerAddress serverAddress) throws IOException {
        this.socket = this.socketFactory.create(serverAddress, this.connectTimeout, this.readTimeout);
        this.socket.connect();
        return this.socket;
    }

    @Override
    public void close() throws Exception {
        if (this.socket != null) this.socket.close();
    }

    /**
     * Write a packet to the output stream.
     *
     * @param os           The output stream
     * @param packetId     The packet id
     * @param packetWriter The packet writer
     * @throws IOException If an I/O error occurs
     */
    protected abstract void writePacket(final MCOutputStream os, final int packetId, final PacketWriter packetWriter) throws IOException;

    /**
     * Read a packet from the input stream.
     *
     * @param is           The input stream
     * @param packetId     The packet id
     * @param packetReader The packet reader
     * @throws IOException If an I/O error occurs
     */
    protected abstract void readPacket(final MCInputStream is, final int packetId, final PacketReader packetReader) throws IOException;

    /**
     * Prepare the response by adding default server information.<br>
     * See {@link #prepareResponse(ServerAddress, JsonObject, int)} for more information.
     *
     * @param serverAddress The server address
     * @param response      The response
     */
    protected final void prepareResponse(final ServerAddress serverAddress, final JsonObject response) {
        this.prepareResponse(serverAddress, response, this.protocolVersion);
    }


    @FunctionalInterface
    protected interface PacketWriter {
        void write(final MCOutputStream os) throws IOException;
    }

    @FunctionalInterface
    protected interface PacketReader {
        void read(final MCInputStream is) throws IOException;
    }

}
