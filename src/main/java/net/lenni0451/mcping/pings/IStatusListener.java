package net.lenni0451.mcping.pings;

import net.lenni0451.mcping.exception.*;
import net.lenni0451.mcping.responses.IPingResponse;

import java.io.EOFException;
import java.io.IOException;
import java.net.UnknownHostException;

/**
 * The interface used to listen for the status of a ping.<br>
 * <br>
 * <b>Important:</b><br>
 * Some servers don't respond with a ping which causes the {@link #onPing(IPingResponse, long)} method to not be called.<br>
 * Make sure to also implement {@link #onResponse(IPingResponse)} if you need the actual response.<br>
 * If the server does not correctly respond with a ping the {@link #onError(Throwable)} method will be called together with the {@link #onResponse(IPingResponse)} method.<br>
 * Only if the {@link #onPing(IPingResponse, long)} method is called the ping will be treated successful.
 */
public interface IStatusListener {

    /**
     * Called when the ping failed with an exception.<br>
     * Common exceptions:<br>
     * {@link ConnectionRefusedException} - The connection was refused by the server (unbound port, firewall, etc.)<br>
     * {@link UnknownHostException} - The server address is invalid or the server is offline<br>
     * {@link ConnectTimeoutException} - The connect timed out (server is offline or the connection is too slow)<br>
     * {@link DataReadException} - The server responded with invalid data<br>
     * {@link PacketReadException} - The server responded with invalid packets<br>
     * {@link ReadTimeoutException} - The server did not respond in time<br>
     * {@link EOFException} - The stream was closed before the server responded<br>
     * {@link IOException} - An unknown I/O error occurred
     *
     * @param throwable The thrown exception
     */
    default void onError(final Throwable throwable) {
    }

    /**
     * Called when the connection to the server was established.
     */
    default void onConnected() {
    }

    /**
     * Called when the server responded with a status message.
     *
     * @param pingResponse The response
     */
    default void onResponse(final IPingResponse pingResponse) {
    }

    /**
     * Called when the ping was successful.
     *
     * @param pingResponse The response
     * @param ping         The ping in ms
     */
    default void onPing(final IPingResponse pingResponse, final long ping) {
    }

}
