package net.lenni0451.mcping.pings.impl;

import com.google.gson.JsonObject;
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

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * The ping implementation for the legacy edition.<br>
 * Ping response: {@link MCPingResponse}
 */
public class LegacyPing extends ATCPPing {

    private static final String PING_CHANNEL = "MC|PingHost";

    private final Version version;

    public LegacyPing(final ITCPSocketFactory socketFactory, final int connectTimeout, final int readTimeout, final Version version, final int protocolVersion) {
        super(socketFactory, connectTimeout, readTimeout, protocolVersion);

        this.version = version;
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
            this.writePacket(os, 254, packetOs -> {
                if (this.version.equals(Version.V1_5) || this.version.equals(Version.V1_6)) packetOs.writeByte(1);
                if (this.version.equals(Version.V1_6)) {
                    packetOs.writeByte(250);
                    packetOs.writeLegacyString(PING_CHANNEL);
                    packetOs.writeShort(7 + (2 * serverAddress.getHost().length()));
                    packetOs.writeByte(this.protocolVersion);
                    packetOs.writeLegacyString(serverAddress.getHost());
                    packetOs.writeInt(serverAddress.getPort());
                }

                pingReference.start();
            });
            this.readPacket(is, 255, packetIs -> {
                pingReference.stop();
                JsonObject ping = new JsonObject();
                this.prepareResponse(serverAddress, ping);
                ping.getAsJsonObject("server").addProperty("ping", pingReference.get());

                if (this.version.equals(Version.V1_5) || this.version.equals(Version.V1_6)) {
                    String[] parts = packetIs.readLegacyString().split("\0");
                    JsonObject players = new JsonObject();
                    JsonObject version = new JsonObject();

                    version.addProperty("protocol", Integer.parseInt(parts[1]));
                    version.addProperty("name", parts[2]);
                    ping.add("version", version);

                    ping.addProperty("description", parts[3]);

                    players.addProperty("online", Integer.parseInt(parts[4]));
                    players.addProperty("max", Integer.parseInt(parts[5]));
                    ping.add("players", players);
                } else {
                    String[] parts = packetIs.readLegacyString().split("§");
                    JsonObject players = new JsonObject();
                    JsonObject version = new JsonObject();

                    ping.addProperty("description", parts[0]);

                    players.addProperty("online", Integer.parseInt(parts[1]));
                    players.addProperty("max", Integer.parseInt(parts[2]));
                    ping.add("players", players);

                    version.addProperty("name", "Unknown (Legacy)");
                    version.addProperty("protocol", this.protocolVersion);
                    ping.add("version", version);
                }

                MCPingResponse pingResponse = this.gson.fromJson(ping, MCPingResponse.class);
                statusListener.onResponse(pingResponse);
                statusListener.onPing(pingResponse, pingReference.get());
            });
        } catch (Throwable t) {
            statusListener.onError(t);
        }
    }

    @Override
    protected void writePacket(MCOutputStream os, int packetId, PacketWriter packetWriter) throws IOException {
        os.writeByte(packetId);
        packetWriter.write(os);
    }

    @Override
    protected void readPacket(MCInputStream is, int packetId, PacketReader packetReader) throws IOException {
        try {
            int packetPacketId = is.readUnsignedByte();
            if (packetPacketId != packetId) throw PacketReadException.wrongPacketId(packetId, packetPacketId);
            packetReader.read(is);
        } catch (SocketTimeoutException e) {
            throw new ReadTimeoutException(this.readTimeout);
        }
    }


    /**
     * The version of the legacy ping protocol.
     */
    public enum Version {
        B1_8(17),
        V1_3(39),
        V1_5(60),
        V1_6(73);

        private final int defaultId;

        Version(final int defaultId) {
            this.defaultId = defaultId;
        }

        /**
         * @return The default protocol version for this version
         */
        public int getDefaultId() {
            return this.defaultId;
        }
    }

}
