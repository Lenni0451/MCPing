package net.lenni0451.mcping.pings.impl;

import com.google.gson.JsonObject;
import net.lenni0451.mcping.ServerAddress;
import net.lenni0451.mcping.exception.PacketReadException;
import net.lenni0451.mcping.pings.AUDPPing;
import net.lenni0451.mcping.pings.IStatusListener;
import net.lenni0451.mcping.pings.PingReference;
import net.lenni0451.mcping.responses.BedrockPingResponse;

import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

/**
 * The ping implementation for the bedrock edition.<br>
 * Ping response: {@link BedrockPingResponse}
 */
public class BedrockPing extends AUDPPing {

    private static final byte[] RAKNET_UNCONNECTED_MAGIC = new byte[]{0, -1, -1, 0, -2, -2, -2, -2, -3, -3, -3, -3, 18, 52, 86, 120};

    private final Random rnd = new Random();

    public BedrockPing(final int readTimeout) {
        super(readTimeout);
    }

    @Override
    public int getDefaultPort() {
        return 19132;
    }

    @Override
    public void ping(ServerAddress serverAddress, IStatusListener statusListener) {
        try (DatagramSocket s = this.connect()) {
            statusListener.onConnected();
            long sessionId = this.rnd.nextLong();

            PingReference pingReference = new PingReference();
            this.writePacket(s, serverAddress, packetOs -> {
                packetOs.writeByte(1);
                packetOs.writeLong(System.currentTimeMillis());
                packetOs.write(RAKNET_UNCONNECTED_MAGIC);
                packetOs.writeLong(sessionId);

                pingReference.start();
            });
            this.readPacket(s, 32767, packetIs -> {
                pingReference.stop();

                int packetId = packetIs.readUnsignedByte();
                if (packetId != 28) throw PacketReadException.wrongPacketId(28, packetId);
                byte[] readMagic = new byte[RAKNET_UNCONNECTED_MAGIC.length];

                packetIs.readLong();
                packetIs.readLong();
                packetIs.readFully(readMagic);
                if (!Arrays.equals(readMagic, RAKNET_UNCONNECTED_MAGIC)) throw new PacketReadException("Invalid raknet magic");

                byte[] userData;
                if (packetIs.available() <= 0) throw new PacketReadException("No user data");
                userData = new byte[packetIs.readUnsignedShort()];
                packetIs.readFully(userData);

                JsonObject response = new JsonObject();
                this.prepareResponse(serverAddress, response, 0);
                response.getAsJsonObject("server").addProperty("ping", pingReference.get());

                this.parseResponse(response, new String(userData, StandardCharsets.UTF_8));

                BedrockPingResponse pingResponse = this.gson.fromJson(response, BedrockPingResponse.class);
                statusListener.onResponse(pingResponse);
                statusListener.onPing(pingResponse, pingReference.get());
            });
        } catch (Throwable t) {
            statusListener.onError(t);
        }
    }

    private void parseResponse(final JsonObject response, final String data) {
        JsonObject version = new JsonObject();
        JsonObject players = new JsonObject();
        response.add("version", version);
        response.add("players", players);

        String[] infos = data.split(";");
        for (int i = 0; i < infos.length; i++) {
            String info = infos[i];

            if (i == 0) response.addProperty("gameId", info);
            else if (i == 1) response.addProperty("descriptionLine1", info);
            else if (i == 2) version.addProperty("protocol", info);
            else if (i == 3) version.addProperty("name", info);
            else if (i == 4) players.addProperty("online", info);
            else if (i == 5) players.addProperty("max", info);
            else if (i == 6) response.addProperty("serverId", info);
            else if (i == 7) response.addProperty("descriptionLine2", info);
            else if (i == 8) response.addProperty("gameType", info);
            else if (i == 9) response.addProperty("nintendoLimited", !info.equalsIgnoreCase("1"));
            else if (i == 10) response.getAsJsonObject("server").addProperty("ipv4Port", info);
            else if (i == 11) response.getAsJsonObject("server").addProperty("ipv6Port", info);
        }
    }

}
