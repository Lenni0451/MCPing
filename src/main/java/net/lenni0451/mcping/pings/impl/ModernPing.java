package net.lenni0451.mcping.pings.impl;

import com.google.gson.JsonArray;
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

    private final boolean skipPing;

    public ModernPing(final ITCPSocketFactory socketFactory, final int connectTimeout, final int readTimeout, final int protocolVersion, final boolean skipPing) {
        super(socketFactory, connectTimeout, readTimeout, protocolVersion);
        this.skipPing = skipPing;
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

            PingReference pingReference = new PingReference();
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
                if (this.skipPing) pingReference.start();
            });
            this.readPacket(is, 0, packetIs -> {
                if (this.skipPing) pingReference.stop();
                String rawResponse = packetIs.readVarString(32767);
                JsonObject parsedResponse = JsonParser.parseString(rawResponse).getAsJsonObject();
                this.prepareResponse(serverAddress, parsedResponse);
                this.parseEncodedForgeData(parsedResponse);
                pingResponse[0] = this.gson.fromJson(parsedResponse, MCPingResponse.class);
                statusListener.onResponse(pingResponse[0]);
                if (this.skipPing) {
                    pingResponse[0].server.ping = pingReference.get();
                    statusListener.onPing(pingResponse[0], pingReference.get());
                }
            });
            if (!this.skipPing) {
                this.writePacket(os, 1, packetOs -> {
                    packetOs.writeLong(pingReference.startAndGet());
                });
                this.readPacket(is, 1, packetIs -> {
                    pingReference.stop();

                    pingResponse[0].server.ping = pingReference.get();
                    statusListener.onPing(pingResponse[0], pingReference.get());
                });
            }
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

    private void parseEncodedForgeData(final JsonObject object) throws IOException {
        if (!object.has("forgeData")) return;
        JsonObject forgeData = object.getAsJsonObject("forgeData");
        if (!forgeData.has("d") || !forgeData.get("d").isJsonPrimitive()) return;

        String d = forgeData.get("d").getAsString();
        int size = d.charAt(0) | (d.charAt(1) << 15);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
        int buffer = 0;
        int bufferBits = 0;
        for (int i = 2; i < d.length(); i++) {
            while (bufferBits >= 8) {
                baos.write(buffer & 0xFF);
                buffer >>>= 8;
                bufferBits -= 8;
            }
            char c = d.charAt(i);
            buffer |= (c & 0x7FFF) << bufferBits;
            bufferBits += 15;
        }
        while (baos.size() < size) {
            baos.write(buffer & 0xFF);
            buffer >>>= 8;
            bufferBits -= 8;
        }

        JsonArray newMods = new JsonArray();
        JsonArray newChannels = new JsonArray();
        MCInputStream dis = new MCInputStream(new ByteArrayInputStream(baos.toByteArray()));
        forgeData.addProperty("truncated", dis.readBoolean());
        forgeData.add("mods", newMods);
        forgeData.add("channels", newChannels);
        int modCount = dis.readUnsignedShort();
        for (int i = 0; i < modCount; i++) {
            int channelCountAndVersionFlag = dis.readVarInt();
            int channelCount = channelCountAndVersionFlag >>> 1;
            boolean isIgnoreServerOnly = (channelCountAndVersionFlag & 1) != 0;
            String modId = dis.readVarString(Short.MAX_VALUE);
            String modVersion = isIgnoreServerOnly ? "SERVER_ONLY" : dis.readVarString(Short.MAX_VALUE);
            for (int j = 0; j < channelCount; j++) {
                JsonObject channel = new JsonObject();
                channel.addProperty("res", modId + ":" + dis.readVarString(Short.MAX_VALUE));
                channel.addProperty("version", dis.readVarString(Short.MAX_VALUE));
                channel.addProperty("required", dis.readBoolean());
                newChannels.add(channel);
            }
            JsonObject mod = new JsonObject();
            mod.addProperty("modId", modId);
            mod.addProperty("modmarker", modVersion);
            newMods.add(mod);
        }
        int channelCount = dis.readVarInt();
        for (int i = 0; i < channelCount; i++) {
            JsonObject channel = new JsonObject();
            channel.addProperty("res", dis.readVarString(Short.MAX_VALUE));
            channel.addProperty("version", dis.readVarString(Short.MAX_VALUE));
            channel.addProperty("required", dis.readBoolean());
            newChannels.add(channel);
        }
    }

}
