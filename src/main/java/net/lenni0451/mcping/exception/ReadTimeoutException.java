package net.lenni0451.mcping.exception;

import java.io.IOException;

public class ReadTimeoutException extends IOException {

    private final int readTimeout;

    public ReadTimeoutException(final int readTimeout) {
        super("Read timed out (" + readTimeout + " ms)");
        this.readTimeout = readTimeout;
    }

    public int getReadTimeout() {
        return this.readTimeout;
    }

}
