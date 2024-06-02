package net.lenni0451.mcping.responses;

import lombok.ToString;
import net.lenni0451.mcping.pings.impl.LegacyPing;
import net.lenni0451.mcping.pings.impl.ModernPing;

/**
 * The response of a {@link ModernPing} and {@link LegacyPing}.
 */
@ToString
public class MCPingResponse implements IPingResponse {

    public String description;
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

}
