package net.lenni0451.mcping.responses;

import net.lenni0451.mcping.pings.impl.QueryPing;

import java.util.Arrays;

/**
 * The response of a {@link QueryPing}.
 */
public class QueryPingResponse implements IPingResponse {

    public String description;
    public String gameType;
    public String map;
    public String gameId;
    public String version;
    public Server server;
    public Players players;
    public Plugins plugins;

    @Override
    public String toString() {
        return "QueryPingResponse{" +
                "description='" + description + '\'' +
                ", gameType='" + gameType + '\'' +
                ", map='" + map + '\'' +
                ", gameId='" + gameId + '\'' +
                ", version='" + version + '\'' +
                ", server=" + server +
                ", players=" + players +
                ", plugins=" + plugins +
                '}';
    }

    public static class Server {
        public String ip;
        public int port = -1;
        public int protocol = -1;
        public long ping = -1;
        public String hostIp;
        public int hostPort;

        @Override
        public String toString() {
            return "Server{" +
                    "ip='" + ip + '\'' +
                    ", port=" + port +
                    ", protocol=" + protocol +
                    ", ping=" + ping +
                    ", hostIp='" + hostIp + '\'' +
                    ", hostPort=" + hostPort +
                    '}';
        }
    }

    public static class Players {
        public int max;
        public int online;
        public String[] sample = new String[0];

        @Override
        public String toString() {
            return "Players{" +
                    "max=" + max +
                    ", online=" + online +
                    ", sample=" + Arrays.toString(sample) +
                    '}';
        }
    }

    public static class Plugins {
        public String base;
        public String[] sample = new String[0];

        @Override
        public String toString() {
            return "Plugins{" +
                    "base='" + base + '\'' +
                    ", sample=" + Arrays.toString(sample) +
                    '}';
        }
    }

}
