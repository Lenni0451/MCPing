package net.lenni0451.mcping.responses;

import lombok.ToString;
import net.lenni0451.mcping.pings.impl.QueryPing;

/**
 * The response of a {@link QueryPing}.
 */
@ToString
public class QueryPingResponse implements IPingResponse {

    public String description;
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

}
