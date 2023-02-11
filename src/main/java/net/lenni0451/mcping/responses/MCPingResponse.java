package net.lenni0451.mcping.responses;

import java.util.Arrays;

public class MCPingResponse implements IPingResponse {

    public String description;
    public Server server;
    public Boolean previewsChat;
    public Version version;
    public Players players;
    public ModInfo modInfo;
    public ForgeData forgeData;

    @Override
    public String toString() {
        return "MCPingResponse{" +
                "description='" + description + '\'' +
                ", server=" + server +
                ", previewsChat=" + previewsChat +
                ", version=" + version +
                ", players=" + players +
                ", modInfo=" + modInfo +
                ", forgeData=" + forgeData +
                '}';
    }

    public static class Server {
        public String ip;
        public int port = -1;
        public int protocol = -1;
        public long ping = -1;

        @Override
        public String toString() {
            return "Server{" +
                    "ip='" + ip + '\'' +
                    ", port=" + port +
                    ", protocol=" + protocol +
                    ", ping=" + ping +
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
        public Player[] sample = new Player[0];

        @Override
        public String toString() {
            return "Players{" +
                    "max=" + max +
                    ", online=" + online +
                    ", sample=" + Arrays.toString(sample) +
                    '}';
        }

        public static class Player {
            public String name;
            public String id;

            @Override
            public String toString() {
                return "Player{" +
                        "name='" + name + '\'' +
                        ", id='" + id + '\'' +
                        '}';
            }
        }
    }

    public static class ModInfo {
        public String type;
        public Mod[] modList = new Mod[0];

        @Override
        public String toString() {
            return "ModInfo{" +
                    "type='" + type + '\'' +
                    ", modList=" + Arrays.toString(modList) +
                    '}';
        }

        public static class Mod {
            public String modid;
            public String version;

            @Override
            public String toString() {
                return "Mod{" +
                        "modid='" + modid + '\'' +
                        ", version='" + version + '\'' +
                        '}';
            }
        }
    }

    public static class ForgeData {
        public int fmlNetworkVersion = -1;
        public boolean truncated = false;
        public Mod[] mods = new Mod[0];
        public Channel[] channels = new Channel[0];

        @Override
        public String toString() {
            return "ForgeData{" +
                    "fmlNetworkVersion=" + fmlNetworkVersion +
                    ", truncated=" + truncated +
                    ", mods=" + Arrays.toString(mods) +
                    ", channels=" + Arrays.toString(channels) +
                    '}';
        }

        public static class Mod {
            public String modId;
            public String modmarker;

            @Override
            public String toString() {
                return "Mod{" +
                        "modId='" + modId + '\'' +
                        ", modmarker='" + modmarker + '\'' +
                        '}';
            }
        }

        public static class Channel {
            public String res;
            public String version;
            public boolean required = false;

            @Override
            public String toString() {
                return "Channel{" +
                        "res='" + res + '\'' +
                        ", version='" + version + '\'' +
                        ", required=" + required +
                        '}';
            }
        }
    }

}
