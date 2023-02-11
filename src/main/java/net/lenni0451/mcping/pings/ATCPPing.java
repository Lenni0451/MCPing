package net.lenni0451.mcping.pings;

import com.google.gson.JsonObject;
import net.lenni0451.mcping.ServerAddress;
import net.lenni0451.mcping.stream.MCInputStream;
import net.lenni0451.mcping.stream.MCOutputStream;

import java.io.IOException;
import java.net.Socket;

public abstract class ATCPPing extends APing {

    protected final int connectTimeout;
    protected final int readTimeout;
    protected final int protocolVersion;

    public ATCPPing(final int connectTimeout, final int readTimeout, final int protocolVersion) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.protocolVersion = protocolVersion;
    }


    protected abstract void writePacket(final MCOutputStream os, final int packetId, final PacketWriter packetWriter) throws IOException;

    protected abstract void readPacket(final MCInputStream is, final int packetId, final PacketReader packetReader) throws IOException;


    protected final Socket connect(final ServerAddress serverAddress, final int defaultPort) throws IOException {
        Socket s = new Socket();
        s.setSoTimeout(this.readTimeout);
        s.connect(serverAddress.toInetSocketAddress(), this.connectTimeout);
        return s;
    }

    protected final void prepareResponse(final ServerAddress serverAddress, final JsonObject response) {
        this.prepareResponse(serverAddress, response, this.protocolVersion);
    }


    protected interface PacketWriter {
        void write(final MCOutputStream os) throws IOException;
    }

    protected interface PacketReader {
        void read(final MCInputStream is) throws IOException;
    }

}
