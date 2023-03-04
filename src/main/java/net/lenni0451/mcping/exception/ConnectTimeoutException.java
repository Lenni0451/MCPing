package net.lenni0451.mcping.exception;

import java.io.IOException;

public class ConnectTimeoutException extends IOException {

    public ConnectTimeoutException(final int connectionTimeout) {
        super("Connect timed out (" + connectionTimeout + " ms)");
    }

}
