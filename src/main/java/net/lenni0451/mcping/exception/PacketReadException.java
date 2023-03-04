package net.lenni0451.mcping.exception;

import java.io.IOException;

public class PacketReadException extends IOException {

    public PacketReadException(final String message) {
        super(message);
    }

}
