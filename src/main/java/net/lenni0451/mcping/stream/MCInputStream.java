package net.lenni0451.mcping.stream;

import net.lenni0451.mcping.pings.impl.ClassicPing;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MCInputStream extends DataInputStream {

    public MCInputStream(final InputStream is) {
        super(is);
    }

    public int readVarInt() throws IOException {
        int i = 0;
        int j = 0;

        byte b;
        do {
            b = this.readByte();
            i |= (b & 127) << j++ * 7;
            if (j > 5) throw new RuntimeException("VarInt too big");
        } while ((b & 128) == 128);

        return i;
    }

    public String readString(final int maxLength) throws IOException {
        int length = this.readVarInt();
        if (length > maxLength * 4) throw new IOException("The received encoded string buffer length is longer than maximum allowed (" + length + " > " + maxLength * 4 + ")");
        if (length < 0) throw new IOException("The received encoded string buffer length is less than zero!");

        byte[] bytes = new byte[length];
        this.readFully(bytes);
        String string = new String(bytes, StandardCharsets.UTF_8);
        if (string.length() > maxLength) throw new IOException("The received string length is longer than maximum allowed (" + length + " > " + maxLength + ")");
        else return string;
    }

    public String readLegacyString() throws IOException {
        int length = this.readShort();
        if (length < 0) throw new IOException("The received encoded string buffer length is less than zero!");

        char[] chars = new char[length];
        for (int i = 0; i < length; i++) chars[i] = this.readChar();
        return new String(chars);
    }

    public String readClassicString() throws IOException {
        byte[] stringBytes = new byte[64];
        char[] chars = new char[stringBytes.length];
        this.readFully(stringBytes);
        for (int i = 0; i < stringBytes.length; i++) chars[i] = ClassicPing.CP437[stringBytes[i] & 0xFF];
        return new String(chars).trim();
    }

}
