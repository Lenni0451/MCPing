package net.lenni0451.mcping.exception;

import java.io.IOException;

public class PacketReadException extends IOException {

    public static PacketReadException wrongPacketId(final int expected, final int actual) {
        return new PacketReadException("Expected packet id " + expected + " but got " + actual);
    }


    public PacketReadException(final String message) {
        super(message);
    }

}
