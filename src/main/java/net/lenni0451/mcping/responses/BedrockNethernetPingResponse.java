package net.lenni0451.mcping.responses;

import lombok.ToString;
import net.lenni0451.mcping.pings.impl.BedrockNethernetPing;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The response of a {@link BedrockNethernetPing}.
 */
@ToString
public class BedrockNethernetPingResponse implements IPingResponse {

    public String name;
    public int protocol = -1;
    public String version;
    public String level;
    public int players = -1;
    public int maxPlayers = -1;
    public int gameType = -1;
    public int dataVersion = -1;
    public boolean editor;
    public boolean hardcore;
    public String nonce;
    public boolean onlineAuth;
    public boolean selfSignedAuth;
    public int transportLayer = -1;
    public int connection = -1;
    public Server server;

    @ToString
    public static class Server {
        public String ip;
        public int port = -1;
        public int protocol = -1;
        public long ping = -1;
    }


    @Nonnull
    @Override
    public String getAddress() {
        return Optional.ofNullable(this.server).map(s -> s.ip).orElse("Unknown");
    }

    @Override
    public int getPort() {
        return Optional.ofNullable(this.server).map(s -> s.port).orElse(-1);
    }

    @Nonnull
    @Override
    public String getMotd() {
        return this.name != null ? this.name : "";
    }

    @Override
    public long getPing() {
        return Optional.ofNullable(this.server).map(s -> s.ping).orElse(-1L);
    }

    @Override
    public int getOnlinePlayers() {
        return this.players;
    }

    @Override
    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    @Nonnull
    @Override
    public String getVersionName() {
        return this.version != null ? this.version : "Unknown";
    }

    @Override
    public int getProtocolId() {
        return this.protocol;
    }

    @Nonnull
    @Override
    public List<String> getSample() {
        List<String> sample = new ArrayList<>();
        if (this.level != null) sample.add("Level: " + this.level);
        if (this.gameType >= 0) sample.add("Game Type: " + this.gameType);
        if (this.version != null) sample.add("Version: " + this.version);
        if (this.protocol >= 0) sample.add("Protocol: " + this.protocol);
        if (this.dataVersion >= 0) sample.add("Data Version: " + this.dataVersion);
        if (this.nonce != null) sample.add("Nonce: " + this.nonce);
        if (this.transportLayer >= 0) sample.add("Transport Layer: " + this.transportLayer);
        if (this.connection >= 0) sample.add("Connection: " + this.connection);
        if (this.editor) sample.add("Editor");
        if (this.hardcore) sample.add("Hardcore");
        if (this.onlineAuth) sample.add("Online Auth");
        if (this.selfSignedAuth) sample.add("Self Signed Auth");
        return sample;
    }

}
