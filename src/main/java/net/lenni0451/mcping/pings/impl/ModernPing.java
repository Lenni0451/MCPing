package net.lenni0451.mcping.pings.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.lenni0451.mcping.ServerAddress;
import net.lenni0451.mcping.pings.ATCPPing;
import net.lenni0451.mcping.pings.IStatusListener;
import net.lenni0451.mcping.pings.PingReference;
import net.lenni0451.mcping.responses.MCPingResponse;
import net.lenni0451.mcping.stream.MCInputStream;
import net.lenni0451.mcping.stream.MCOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ModernPing extends ATCPPing {

    public ModernPing(final int connectTimeout, final int readTimeout, final int protocolVersion) {
        super(connectTimeout, readTimeout, protocolVersion);
    }

    @Override
    public int getDefaultPort() {
        return 25565;
    }

    @Override
    public void ping(ServerAddress serverAddress, IStatusListener statusListener) {
        try (Socket s = this.connect(serverAddress, this.getDefaultPort())) {
            MCInputStream is = new MCInputStream(s.getInputStream());
            MCOutputStream os = new MCOutputStream(s.getOutputStream());

            MCPingResponse[] pingResponse = new MCPingResponse[1];
            this.writePacket(os, 0, packetOs -> {
                packetOs.writeVarInt(this.protocolVersion);
                packetOs.writeString(serverAddress.getIp());
                packetOs.writeShort(serverAddress.getPort());
                packetOs.writeVarInt(1);
            });
            this.writePacket(os, 0, packetOs -> {
            });
            this.readPacket(is, 0, packetIs -> {
                String rawResponse = packetIs.readString(32767);
                JsonObject parsedResponse = JsonParser.parseString(rawResponse).getAsJsonObject();
                this.prepareResponse(serverAddress, parsedResponse, this.getDefaultPort());
                pingResponse[0] = this.gson.fromJson(parsedResponse, MCPingResponse.class);
                statusListener.onResponse(pingResponse[0]);
            });
            PingReference pingReference = new PingReference();
            this.writePacket(os, 1, packetOs -> {
                packetOs.writeLong(pingReference.startAndGet());
            });
            this.readPacket(is, 1, packetIs -> {
                pingReference.stop();

                pingResponse[0].server.ping = pingReference.get();
                statusListener.onPing(pingResponse[0], pingReference.get());
            });
        } catch (Throwable t) {
            statusListener.onError(t);
        }
    }

    protected void writePacket(final MCOutputStream os, final int packetId, final PacketWriter packetWriter) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MCOutputStream packetOs = new MCOutputStream(baos);

        packetOs.writeVarInt(packetId);
        packetWriter.write(packetOs);

        os.writeVarInt(baos.size());
        os.write(baos.toByteArray());
        os.flush();
    }

    protected void readPacket(final MCInputStream is, final int packetId, final PacketReader packetReader) throws IOException {
        int packetLength = is.readVarInt();
        byte[] packetData = new byte[packetLength];
        is.readFully(packetData);
        MCInputStream packetIs = new MCInputStream(new ByteArrayInputStream(packetData));

        int packetPacketId = packetIs.readVarInt();
        if (packetPacketId != packetId) throw new IOException("Expected packet id " + packetId + ", got " + packetPacketId);
        packetReader.read(packetIs);
    }

}
