package net.lenni0451.mcping.responses;

import lombok.ToString;
import net.lenni0451.mcping.pings.impl.BedrockPing;

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

}
