package net.lenni0451.mcping.stream;

import net.lenni0451.mcping.pings.impl.ClassicPing;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MCOutputStream extends DataOutputStream {

    public MCOutputStream(final OutputStream os) {
        super(os);
    }

    public void writeVarInt(int value) throws IOException {
        while ((value & -128) != 0) {
            this.writeByte(value & 127 | 128);
            value >>>= 7;
        }

        this.writeByte(value);
    }

    public void writeString(String string) throws IOException {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        this.writeVarInt(bytes.length);
        this.write(bytes);
    }

    public void writeLegacyString(final String s) throws IOException {
        this.writeShort(s.length());
        for (char c : s.toCharArray()) this.writeChar(c);
    }

    public void writeClassicString(final String s) throws IOException {
        final byte[] bytes = new byte[64];
        final byte[] stringBytes = new byte[s.length()];
        for (int i = 0; i < s.toCharArray().length; i++) {
            for (int ci = 0; ci < ClassicPing.CP437.length; ci++) {
                if (ClassicPing.CP437[ci] == s.charAt(i)) {
                    stringBytes[i] = (byte) ci;
                    break;
                }
            }
        }

        Arrays.fill(bytes, (byte) 32);
        System.arraycopy(stringBytes, 0, bytes, 0, Math.min(bytes.length, stringBytes.length));

        this.write(bytes);
    }

}
