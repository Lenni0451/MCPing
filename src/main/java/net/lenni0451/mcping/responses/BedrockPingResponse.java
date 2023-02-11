package net.lenni0451.mcping.responses;

import net.lenni0451.mcping.pings.impl.BedrockPing;

/**
 * The response of a {@link BedrockPing}.
 */
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

    @Override
    public String toString() {
        return "BedrockPingResponse{" +
                "descriptionLine1='" + descriptionLine1 + '\'' +
                ", descriptionLine2='" + descriptionLine2 + '\'' +
                ", gameType='" + gameType + '\'' +
                ", gameId='" + gameId + '\'' +
                ", serverId='" + serverId + '\'' +
                ", nintendoLimited=" + nintendoLimited +
                ", server=" + server +
                ", version=" + version +
                ", players=" + players +
                '}';
    }

    public static class Server {
        public String ip;
        public int port = -1;
        public int protocol = -1;
        public long ping = -1;
        public int ipv4Port = -1;
        public int ipv6Port = -1;

        @Override
        public String toString() {
            return "Server{" +
                    "ip='" + ip + '\'' +
                    ", port=" + port +
                    ", protocol=" + protocol +
                    ", ping=" + ping +
                    ", ipv4Port=" + ipv4Port +
                    ", ipv6Port=" + ipv6Port +
                    '}';
        }
    }

    public static class Version {
        public String name;
        public int protocol = -1;

        @Override
        public String toString() {
            return "Version{" +
                    "name='" + name + '\'' +
                    ", protocol=" + protocol +
                    '}';
        }
    }

    public static class Players {
        public int max = -1;
        public int online = -1;

        @Override
        public String toString() {
            return "Players{" +
                    "max=" + max +
                    ", online=" + online +
                    '}';
        }
    }


}
