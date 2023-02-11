package net.lenni0451.mcping.stream;

import net.lenni0451.mcping.pings.impl.ClassicPing;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * A data output stream which can write minecraft specific data types.<br>
 * Implemented types:
 * - var int
 * - var string
 * - legacy string
 * - classic string
 */
public class MCOutputStream extends DataOutputStream {

    public MCOutputStream(final OutputStream os) {
        super(os);
    }

    /**
     * Write a var int to the stream.<br>
     * This is the integer format used by the modern ping protocol ({@literal >}= 1.7).
     *
     * @param value The value to write
     * @throws IOException If an I/O error occurs
     */
    public void writeVarInt(int value) throws IOException {
        while ((value & -128) != 0) {
            this.writeByte(value & 127 | 128);
            value >>>= 7;
        }

        this.writeByte(value);
    }

    /**
     * Write a var string to the stream.<br>
     * This is the string format used by the modern ping protocol ({@literal >}= 1.7).
     *
     * @param string The string to write
     * @throws IOException If an I/O error occurs
     */
    public void writeVarString(String string) throws IOException {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        this.writeVarInt(bytes.length);
        this.write(bytes);
    }

    /**
     * Write a legacy string to the stream.<br>
     * This is the string format used by the legacy ping protocol ({@literal <}= 1.6).
     *
     * @param s The string to write
     * @throws IOException If an I/O error occurs
     */
    public void writeLegacyString(final String s) throws IOException {
        this.writeShort(s.length());
        for (char c : s.toCharArray()) this.writeChar(c);
    }

    /**
     * Write a classic string to the stream.<br>
     * This is the string format used by the classic protocol.
     *
     * @param s The string to write
     * @throws IOException If an I/O error occurs
     */
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
