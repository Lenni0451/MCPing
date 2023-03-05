package net.lenni0451.mcping.exception;

import net.lenni0451.mcping.ServerAddress;

import java.io.IOException;

public class ConnectionRefusedException extends IOException {

    private final ServerAddress serverAddress;

    public ConnectionRefusedException(final ServerAddress serverAddress) {
        super("Connection to " + serverAddress.getHost() + ":" + serverAddress.getPort() + " was refused");
        this.serverAddress = serverAddress;
    }

    public ServerAddress getServerAddress() {
        return this.serverAddress;
    }

}
