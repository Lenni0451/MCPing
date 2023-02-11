package net.lenni0451.mcping.responses;

public class SocketPingResponse implements IPingResponse {

    public int latency;
    public Server server;

    @Override
    public String toString() {
        return "SocketPingResponse{" +
                "latency=" + latency +
                ", server=" + server +
                '}';
    }

    public static class Server {
        public String ip;
        public int port = -1;

        @Override
        public String toString() {
            return "Server{" +
                    "ip='" + ip + '\'' +
                    ", port=" + port +
                    '}';
        }
    }

}
