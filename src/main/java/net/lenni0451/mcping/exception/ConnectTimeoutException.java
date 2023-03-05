package net.lenni0451.mcping.exception;

import java.io.IOException;

public class ConnectTimeoutException extends IOException {

    private final int connectionTimeout;

    public ConnectTimeoutException(final int connectionTimeout) {
        super("Connect timed out (> " + connectionTimeout + " ms)");
        this.connectionTimeout = connectionTimeout;
    }

    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

}
