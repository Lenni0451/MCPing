package net.lenni0451.mcping.responses;

import lombok.ToString;
import net.lenni0451.mcping.pings.impl.BedrockPing;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The response of a {@link BedrockPing}.
 */
@ToString
public class BedrockPingResponse implements IPingResponse {

    public String descriptionLine1;
    public String descriptionLine2;
    public String gameType;
    public String gameId;
    public String serverId;
    public boolean nintendoLimited = false;
    public Server server;
    public Version version;
    public Players players;

    @ToString
    public static class Server {
        public String ip;
        public int port = -1;
        public int protocol = -1;
        public long ping = -1;
        public int ipv4Port = -1;
        public int ipv6Port = -1;
    }

    @ToString
    public static class Version {
        public String name;
        public int protocol = -1;
    }

    @ToString
    public static class Players {
        public int max = -1;
        public int online = -1;
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
        if (this.descriptionLine1 == null && this.descriptionLine2 == null) return "";
        if (this.descriptionLine1 == null) return this.descriptionLine2;
        if (this.descriptionLine2 == null) return this.descriptionLine1;
        return this.descriptionLine1 + "\n" + this.descriptionLine2;
    }

    @Override
    public long getPing() {
        return Optional.ofNullable(this.server).map(s -> s.ping).orElse(-1L);
    }

    @Override
    public int getOnlinePlayers() {
        return Optional.ofNullable(this.players).map(p -> p.online).orElse(-1);
    }

    @Override
    public int getMaxPlayers() {
        return Optional.ofNullable(this.players).map(p -> p.max).orElse(-1);
    }

    @Nonnull
    @Override
    public String getVersionName() {
        return Optional.ofNullable(this.version).map(v -> v.name).orElse("Unknown");
    }

    @Override
    public int getProtocolId() {
        return Optional.ofNullable(this.version).map(v -> v.protocol).orElse(-1);
    }

    @Nonnull
    @Override
    public List<String> getSample() {
        List<String> sample = new ArrayList<>();
        if (this.server != null) {
            if (this.server.ipv4Port >= 0) sample.add("IPv4 Port: " + this.server.ipv4Port);
            if (this.server.ipv6Port >= 0) sample.add("IPv6 Port: " + this.server.ipv6Port);
        }
        if (this.gameType != null) sample.add("Game Type: " + this.gameType);
        if (this.version != null) {
            sample.add("Version: " + this.version.name);
            sample.add("Protocol: " + this.version.protocol);
        }
        if (this.nintendoLimited) sample.add("Nintendo Limited");
        return sample;
    }

}
