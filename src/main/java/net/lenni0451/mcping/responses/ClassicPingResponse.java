package net.lenni0451.mcping.responses;

import lombok.ToString;
import net.lenni0451.mcping.pings.impl.ClassicPing;

/**
 * The response of a {@link ClassicPing}.
 */
@ToString
public class ClassicPingResponse implements IPingResponse {

    public String name;
    public String motd;
    public Server server;

    @ToString
    public static class Server {
        public String ip;
        public int port = -1;
        public int protocol = -1;
        public long ping = -1;
    }

}
