package net.lenni0451.mcping.pings.impl;

import com.google.gson.JsonObject;
import net.lenni0451.mcping.ServerAddress;
import net.lenni0451.mcping.pings.ATCPPing;
import net.lenni0451.mcping.pings.IStatusListener;
import net.lenni0451.mcping.pings.PingReference;
import net.lenni0451.mcping.responses.MCPingResponse;
import net.lenni0451.mcping.stream.MCInputStream;
import net.lenni0451.mcping.stream.MCOutputStream;

import java.io.IOException;
import java.net.Socket;

public class LegacyPing extends ATCPPing {

    private static final String PING_CHANNEL = "MC|PingHost";

    private final Version version;

    public LegacyPing(final int connectTimeout, final int readTimeout, final Version version, final int protocolVersion) {
        super(connectTimeout, readTimeout, protocolVersion);

        this.version = version;
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

            PingReference pingReference = new PingReference();
            this.writePacket(os, 254, packetOs -> {
                if (this.version.equals(Version.V1_2) || this.version.equals(Version.V1_3)) packetOs.writeByte(1);
                if (this.version.equals(Version.V1_6)) {
                    packetOs.writeByte(250);
                    packetOs.writeLegacyString(PING_CHANNEL);
                    packetOs.writeShort(7 + (2 * serverAddress.getIp().length()));
                    packetOs.writeByte(this.protocolVersion);
                    packetOs.writeLegacyString(serverAddress.getIp());
                    packetOs.writeInt(serverAddress.getPort());
                }

                pingReference.start();
            });
            this.readPacket(is, 255, packetIs -> {
                pingReference.stop();
                JsonObject ping = new JsonObject();
                this.prepareResponse(serverAddress, ping, this.getDefaultPort());
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
        int packetPacketId = is.readUnsignedByte();
        if (packetPacketId != packetId) throw new IOException("Expected packet id " + packetId + ", got " + packetPacketId);
        packetReader.read(is);
    }


    public enum Version {
        V1_2(29),
        V1_3(39),
        V1_5(61),
        V1_6(74);

        private final int defaultId;

        Version(final int defaultId) {
            this.defaultId = defaultId;
        }

        public int getDefaultId() {
            return this.defaultId;
        }
    }

}
