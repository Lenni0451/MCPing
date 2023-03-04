package net.lenni0451.mcping.exception;

import java.io.IOException;

public class ReadTimeoutException extends IOException {

    public ReadTimeoutException(final int readTimeout) {
        super("Read timed out (" + readTimeout + " ms)");
    }

}
