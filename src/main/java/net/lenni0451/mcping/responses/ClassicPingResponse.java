package net.lenni0451.mcping.responses;

import lombok.ToString;
import net.lenni0451.mcping.pings.impl.ClassicPing;

import javax.annotation.Nonnull;
import java.util.Optional;

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
        return Optional.ofNullable(this.motd).orElse("Unknown");
    }

    @Override
    public long getPing() {
        return Optional.ofNullable(this.server).map(s -> s.ping).orElse(-1L);
    }

    @Override
    public int getProtocolId() {
        return Optional.ofNullable(this.server).map(s -> s.protocol).orElse(-1);
    }

}
