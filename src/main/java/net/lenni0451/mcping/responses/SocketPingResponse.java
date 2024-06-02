package net.lenni0451.mcping.responses;

import lombok.ToString;
import net.lenni0451.mcping.pings.impl.SocketPing;

import javax.annotation.Nonnull;
import java.util.Optional;

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


    @Nonnull
    @Override
    public String getAddress() {
        return Optional.ofNullable(this.server).map(s -> s.ip).orElse("Unknown");
    }

    @Override
    public int getPort() {
        return Optional.ofNullable(this.server).map(s -> s.port).orElse(-1);
    }

    @Override
    public long getPing() {
        return this.latency;
    }

}
