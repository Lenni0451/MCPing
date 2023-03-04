package net.lenni0451.mcping.pings.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.lenni0451.mcping.ServerAddress;
import net.lenni0451.mcping.pings.AUDPPing;
import net.lenni0451.mcping.pings.IStatusListener;
import net.lenni0451.mcping.pings.PingReference;
import net.lenni0451.mcping.responses.QueryPingResponse;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * The ping implementation for the query ping protocol.<br>
 * Ping response: {@link QueryPingResponse}
 */
public class QueryPing extends AUDPPing {

    private static final byte[] MAGIC_BYTES = {(byte) 0xFE, (byte) 0xFD};

    private final Random rnd = new Random();
    private final boolean full;

    public QueryPing(final int readTimeout, final boolean full) {
        super(readTimeout);

        this.full = full;
    }

    @Override
    public int getDefaultPort() {
        return 25565;
    }

    @Override
    public void ping(ServerAddress serverAddress, IStatusListener statusListener) {
        try (DatagramSocket s = this.connect()) {
            statusListener.onConnected();
            int sessionId = (this.rnd.nextInt((0x7FFFFFFF - 0x1) + 1) + 0x1) & 0x0F0F0F0F;
            int[] challengeToken = new int[1];

            this.writePacket(s, serverAddress, packetOs -> {
                packetOs.write(MAGIC_BYTES);
                packetOs.write(9);
                packetOs.writeInt(sessionId);
            });
            this.readPacket(s, 32, packetIs -> {
                int id = packetIs.readByte();
                if (id != 9) throw new IOException("Expected packet id 9, got " + id);
                int sessionIdResponse = packetIs.readInt();
                if (sessionIdResponse != sessionId) throw new IllegalStateException("Invalid session id: " + sessionIdResponse);

                byte[] challengeTokenBytes = new byte[packetIs.available()];
                packetIs.readFully(challengeTokenBytes);
                challengeToken[0] = Integer.parseInt(new String(challengeTokenBytes, StandardCharsets.UTF_8).trim());
            });

            if (this.full) this.requestFullQuery(s, serverAddress, statusListener, sessionId, challengeToken[0]);
            else this.requestQuery(s, serverAddress, statusListener, sessionId, challengeToken[0]);
        } catch (Throwable t) {
            statusListener.onError(t);
        }
    }

    private void requestQuery(final DatagramSocket s, final ServerAddress serverAddress, final IStatusListener statusListener, final int sessionId, final int challengeToken) throws IOException {
        PingReference pingReference = new PingReference();
        this.writePacket(s, serverAddress, packetOs -> {
            packetOs.write(MAGIC_BYTES);
            packetOs.write(0);
            packetOs.writeInt(sessionId);
            packetOs.writeInt(challengeToken);

            pingReference.start();
        });
        this.readPacket(s, 1024, packetIs -> {
            pingReference.stop();

            int id = packetIs.readByte();
            if (id != 0) throw new IOException("Expected packet id 0, got " + id);
            int sessionIdResponse = packetIs.readInt();
            if (sessionIdResponse != sessionId) throw new IllegalStateException("Invalid session id: " + sessionIdResponse);

            String motd = this.readNullTerminatedString(packetIs);
            String gameType = this.readNullTerminatedString(packetIs);
            String map = this.readNullTerminatedString(packetIs);
            String numPlayers = this.readNullTerminatedString(packetIs);
            String maxPlayers = this.readNullTerminatedString(packetIs);
            int hostPort = packetIs.readUnsignedByte() | (packetIs.readUnsignedByte() << 8);
            String hostIp = this.readNullTerminatedString(packetIs);

            JsonObject response = new JsonObject();
            this.prepareResponse(serverAddress, response, 0);
            response.getAsJsonObject("server").addProperty("ping", pingReference.get());
            response.getAsJsonObject("server").addProperty("hostIp", hostIp);
            response.getAsJsonObject("server").addProperty("hostPort", hostPort);

            response.addProperty("description", motd);
            response.addProperty("gameType", gameType);
            response.addProperty("map", map);

            JsonObject players = new JsonObject();
            players.addProperty("online", numPlayers);
            players.addProperty("max", maxPlayers);
            response.add("players", players);

            QueryPingResponse pingResponse = this.gson.fromJson(response, QueryPingResponse.class);
            statusListener.onResponse(pingResponse);
            statusListener.onPing(pingResponse, pingReference.get());
        });
    }

    private void requestFullQuery(final DatagramSocket s, final ServerAddress serverAddress, final IStatusListener statusListener, final int sessionId, final int challengeToken) throws IOException {
        PingReference pingReference = new PingReference();
        this.writePacket(s, serverAddress, packetOs -> {
            packetOs.write(MAGIC_BYTES);
            packetOs.write(0);
            packetOs.writeInt(sessionId);
            packetOs.writeInt(challengeToken);
            packetOs.writeInt(0);

            pingReference.start();
        });
        this.readPacket(s, 1024, packetIs -> {
            pingReference.stop();

            int id = packetIs.readByte();
            if (id != 0) throw new IOException("Expected packet id 0, got " + id);
            int sessionIdResponse = packetIs.readInt();
            if (sessionIdResponse != sessionId) throw new IllegalStateException("Invalid session id: " + sessionIdResponse);
            packetIs.skipBytes(11);

            JsonObject response = new JsonObject();
            this.prepareResponse(serverAddress, response, 0);
            response.getAsJsonObject("server").addProperty("ping", pingReference.get());

            JsonObject players = new JsonObject();
            JsonArray sample = new JsonArray();
            response.add("players", players);
            players.add("sample", sample);

            String key;
            String value;
            while (true) {
                key = this.readNullTerminatedString(packetIs);
                if (key.isEmpty()) break;
                value = this.readNullTerminatedString(packetIs);

                if (key.equalsIgnoreCase("hostname")) {
                    response.addProperty("description", value);
                } else if (key.equalsIgnoreCase("gametype")) {
                    response.addProperty("gameType", value);
                } else if (key.equalsIgnoreCase("game_id")) {
                    response.addProperty("gameId", value);
                } else if (key.equalsIgnoreCase("version")) {
                    response.addProperty("version", value);
                } else if (key.equalsIgnoreCase("plugins")) {
                    JsonObject plugins = new JsonObject();
                    JsonArray pluginList = new JsonArray();
                    response.add("plugins", plugins);
                    plugins.add("sample", pluginList);

                    if (value.contains(":")) {
                        String[] split = value.split(":");
                        plugins.addProperty("base", split[0].trim());

                        split = split[1].trim().split(";");
                        for (String plugin : split) pluginList.add(plugin.trim());
                    }
                } else if (key.equalsIgnoreCase("map")) {
                    response.addProperty("map", value);
                } else if (key.equalsIgnoreCase("numplayers")) {
                    players.addProperty("online", value);
                } else if (key.equalsIgnoreCase("maxplayers")) {
                    players.addProperty("max", value);
                } else if (key.equalsIgnoreCase("hostport")) {
                    response.getAsJsonObject("server").addProperty("hostPort", value);
                } else if (key.equalsIgnoreCase("hostip")) {
                    response.getAsJsonObject("server").addProperty("hostIp", value);
                } else {
                    throw new IllegalStateException("Unknown key: " + key);
                }
            }
            packetIs.skipBytes(10);

            while (true) {
                key = this.readNullTerminatedString(packetIs);
                if (key.isEmpty()) break;

                sample.add(key);
            }

            QueryPingResponse pingResponse = this.gson.fromJson(response, QueryPingResponse.class);
            statusListener.onResponse(pingResponse);
            statusListener.onPing(pingResponse, pingReference.get());
        });
    }

    private String readNullTerminatedString(final DataInputStream is) throws IOException {
        byte[] bytes = new byte[is.available()];
        byte b;
        for (int i = 0; (b = is.readByte()) != 0; i++) bytes[i] = b;
        return new String(bytes, StandardCharsets.UTF_8).trim();
    }

}
