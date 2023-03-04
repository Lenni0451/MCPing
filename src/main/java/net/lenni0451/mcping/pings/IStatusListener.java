package net.lenni0451.mcping.pings;

import net.lenni0451.mcping.responses.IPingResponse;

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
     * Called when the ping failed with an exception.
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
