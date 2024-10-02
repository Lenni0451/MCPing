package net.lenni0451.mcping.responses;

import lombok.ToString;
import net.lenni0451.mcping.pings.impl.QueryPing;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * The response of a {@link QueryPing}.
 */
@ToString
public class QueryPingResponse implements IPingResponse {

    public String description;
    public DescriptionType descriptionType;
    public String gameType;
    public String map;
    public String gameId;
    public String version;
    public Server server;
    public Players players;
    public Plugins plugins;

    @ToString
    public static class Server {
        public String ip;
        public int port = -1;
        public int protocol = -1;
        public long ping = -1;
        public String hostIp;
        public int hostPort;
    }

    @ToString
    public static class Players {
        public int max;
        public int online;
        public String[] sample = new String[0];
    }

    @ToString
    public static class Plugins {
        public String base;
        public String[] sample = new String[0];
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
        return Optional.ofNullable(this.description).orElse("Unknown");
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
        return Optional.ofNullable(this.version).orElse("Unknown");
    }

    @Override
    public int getProtocolId() {
        return Optional.ofNullable(this.server).map(s -> s.protocol).orElse(-1);
    }

    @Nonnull
    @Override
    public List<String> getSample() {
        return Arrays.asList(Optional.ofNullable(this.players).map(p -> p.sample).orElse(new String[0]));
    }

}
