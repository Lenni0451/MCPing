package net.lenni0451.mcping.pings.impl;

import com.google.gson.JsonObject;
import net.lenni0451.mcping.ServerAddress;
import net.lenni0451.mcping.pings.ATCPPing;
import net.lenni0451.mcping.pings.IStatusListener;
import net.lenni0451.mcping.responses.SocketPingResponse;
import net.lenni0451.mcping.stream.MCInputStream;
import net.lenni0451.mcping.stream.MCOutputStream;

import java.net.Socket;

public class SocketPing extends ATCPPing {

    public SocketPing(final int connectTimeout) {
        super(connectTimeout, 10_000, 0);
    }

    @Override
    public int getDefaultPort() {
        return 25565;
    }

    @Override
    public void ping(ServerAddress serverAddress, IStatusListener statusListener) {
        long connectTime = System.currentTimeMillis();
        try (Socket ignored = this.connect(serverAddress, 25565)) {
            connectTime = System.currentTimeMillis() - connectTime;
            JsonObject ping = new JsonObject();
            this.prepareResponse(serverAddress, ping);
            ping.addProperty("latency", connectTime);

            SocketPingResponse pingResponse = this.gson.fromJson(ping, SocketPingResponse.class);
            statusListener.onResponse(pingResponse);
            statusListener.onPing(pingResponse, connectTime);
        } catch (Throwable t) {
            statusListener.onError(t);
        }
    }

    @Override
    protected void writePacket(MCOutputStream os, int packetId, PacketWriter packetWriter) {
    }

    @Override
    protected void readPacket(MCInputStream is, int packetId, PacketReader packetReader) {
    }

}
