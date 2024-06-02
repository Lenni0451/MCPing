package net.lenni0451.mcping.responses;

import lombok.ToString;
import net.lenni0451.mcping.pings.impl.SocketPing;

/**
 * The response of a {@link SocketPing}.
 */
@ToString
public class SocketPingResponse implements IPingResponse {

    public int latency;
    public Server server;

    @ToString
    public static class Server {
        public String ip;
        public int port = -1;
    }

}
