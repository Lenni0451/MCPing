package net.lenni0451.mcping.responses;

import lombok.ToString;
import net.lenni0451.mcping.pings.impl.LegacyPing;
import net.lenni0451.mcping.pings.impl.ModernPing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The response of a {@link ModernPing} and {@link LegacyPing}.
 */
@ToString
public class MCPingResponse implements IPingResponse {

    public String description;
    public DescriptionType descriptionType;
    public String favicon;
    public Server server;
    public Boolean previewsChat;
    public Version version;
    public Players players;
    public ModInfo modinfo;
    public ForgeData forgeData;

    @ToString
    public static class Server {
        public String ip;
        public int port = -1;
        public int protocol = -1;
        public long ping = -1;
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
        public Player[] sample = new Player[0];

        @ToString
        public static class Player {
            public String name;
            public String id;
        }
    }

    @ToString
    public static class ModInfo {
        public String type;
        public Mod[] modList = new Mod[0];

        @ToString
        public static class Mod {
            public String modid;
            public String version;
        }
    }

    @ToString
    public static class ForgeData {
        public int fmlNetworkVersion = -1;
        public boolean truncated = false;
        public Mod[] mods = new Mod[0];
        public Channel[] channels = new Channel[0];

        @ToString
        public static class Mod {
            public String modId;
            public String modmarker;
        }

        @ToString
        public static class Channel {
            public String res;
            public String version;
            public boolean required = false;
        }
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

    @Nullable
    @Override
    public String getFavicon() {
        return this.favicon;
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
        return Optional.ofNullable(this.players).map(p -> p.sample).map(Arrays::asList).orElse(Collections.emptyList()).stream().map(p -> p.name).collect(Collectors.toList());
    }

}
