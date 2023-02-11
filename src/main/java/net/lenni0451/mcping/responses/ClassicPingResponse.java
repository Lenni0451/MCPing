package net.lenni0451.mcping.responses;

public class ClassicPingResponse implements IPingResponse {

    public String name;
    public String motd;
    public Server server;

    @Override
    public String toString() {
        return "ClassicPingResponse{" +
                "name='" + name + '\'' +
                ", motd='" + motd + '\'' +
                ", server=" + server +
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

}
