package net.lenni0451.mcping.pings.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.lenni0451.mcping.ServerAddress;
import net.lenni0451.mcping.exception.PacketReadException;
import net.lenni0451.mcping.exception.ReadTimeoutException;
import net.lenni0451.mcping.pings.ATCPPing;
import net.lenni0451.mcping.pings.IStatusListener;
import net.lenni0451.mcping.pings.PingReference;
import net.lenni0451.mcping.pings.sockets.factories.ITCPSocketFactory;
import net.lenni0451.mcping.pings.sockets.types.ITCPSocket;
import net.lenni0451.mcping.responses.MCPingResponse;
import net.lenni0451.mcping.stream.MCInputStream;
import net.lenni0451.mcping.stream.MCOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

/**
 * The ping implementation for the modern edition.<br>
 * Ping response: {@link MCPingResponse}
 */
public class ModernPing extends ATCPPing {

    public ModernPing(final ITCPSocketFactory socketFactory, final int connectTimeout, final int readTimeout, final int protocolVersion) {
        super(socketFactory, connectTimeout, readTimeout, protocolVersion);
    }

    @Override
    public int getDefaultPort() {
        return 25565;
    }

    @Override
    public void ping(ServerAddress serverAddress, IStatusListener statusListener) {
        try (ITCPSocket s = this.connect(serverAddress)) {
            MCInputStream is = new MCInputStream(s.getInputStream());
            MCOutputStream os = new MCOutputStream(s.getOutputStream());
            statusListener.onConnected();

            MCPingResponse[] pingResponse = new MCPingResponse[1];
            this.writePacket(os, 0, packetOs -> {
                packetOs.writeVarInt(this.protocolVersion);
                if (this.protocolVersion <= 754) { // <= 1.16.5
                    // Minecraft <= 1.16.5 sends the resolved host and port
                    packetOs.writeVarString(serverAddress.getHost());
                    packetOs.writeShort(serverAddress.getPort());
                } else if (this.protocolVersion == 755) { // 1.17
                    // Minecraft 1.17 sends the unresolved host and port
                    packetOs.writeVarString(serverAddress.getUnresolvedHost());
                    packetOs.writeShort(serverAddress.getUnresolvedPort());
                } else { // >= 1.17.1
                    // Minecraft >= 1.17.1 sends the host and port from the resolved inet socket address
                    SocketAddress socketAddress = serverAddress.getSocketAddress();
                    if (socketAddress instanceof InetSocketAddress) {
                        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
                        packetOs.writeVarString(inetSocketAddress.getHostName());
                        packetOs.writeShort(inetSocketAddress.getPort());
                    } else {
                        //If the socket address is not an InetSocketAddress we just send the host and port
                        //This writes socketAddress.toString() as the host and 0 as the port
                        packetOs.writeVarString(serverAddress.getHost());
                        packetOs.writeShort(serverAddress.getPort());
                    }
                }
                packetOs.writeVarInt(1);
            });
            this.writePacket(os, 0, packetOs -> {
            });
            this.readPacket(is, 0, packetIs -> {
                String rawResponse = packetIs.readVarString(32767);
                JsonObject parsedResponse = JsonParser.parseString(rawResponse).getAsJsonObject();
                this.prepareResponse(serverAddress, parsedResponse);
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
        try {
            int packetLength = is.readVarInt();
            byte[] packetData = new byte[packetLength];
            is.readFully(packetData);
            MCInputStream packetIs = new MCInputStream(new ByteArrayInputStream(packetData));

            int packetPacketId = packetIs.readVarInt();
            if (packetPacketId != packetId) throw PacketReadException.wrongPacketId(packetId, packetPacketId);
            packetReader.read(packetIs);
        } catch (SocketTimeoutException e) {
            throw new ReadTimeoutException(this.readTimeout);
        }
    }

}
