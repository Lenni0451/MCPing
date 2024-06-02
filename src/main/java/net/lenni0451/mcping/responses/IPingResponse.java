package net.lenni0451.mcping.responses;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Marker interface for all possible ping responses.<br>
 * Some methods exist for common values, but not all responses have all values.
 */
public interface IPingResponse {

    /**
     * Get the address of the server.<br>
     * <br>
     * Implemented: {@link MCPingResponse}, {@link ClassicPingResponse}, {@link BedrockPingResponse}, {@link QueryPingResponse}, {@link SocketPingResponse}<br>
     * Default: {@code "Unknown"}
     *
     * @return The address
     */
    @Nonnull
    default String getAddress() {
        return "Unknown";
    }

    /**
     * Get the port of the server.<br>
     * <br>
     * Implemented: {@link MCPingResponse}, {@link ClassicPingResponse}, {@link BedrockPingResponse}, {@link QueryPingResponse}, {@link SocketPingResponse}<br>
     * Default: {@code -1}
     *
     * @return The port
     */
    default int getPort() {
        return -1;
    }

    /**
     * Get the MOTD of the server.<br>
     * The MOTD <u>can</u> be a text component but is not guaranteed to be one.<br>
     * <br>
     * Implemented: {@link MCPingResponse}, {@link ClassicPingResponse}, {@link BedrockPingResponse}, {@link QueryPingResponse}<br>
     * Default: {@code "Unknown"}
     *
     * @return The MOTD
     */
    @Nonnull
    default String getMotd() {
        return "Unknown";
    }

    /**
     * Get the favicon of the server.<br>
     * The favicon is a base64 encoded png image. The server can send any string here, so it is not guaranteed to be a valid image.<br>
     * <br>
     * Implemented: {@link MCPingResponse}<br>
     * Default: {@code null}
     *
     * @return The favicon
     */
    @Nullable
    default String getFavicon() {
        return null;
    }

    /**
     * Get the ping to the server in milliseconds.<br>
     * <br>
     * Implemented: {@link MCPingResponse}, {@link ClassicPingResponse}, {@link BedrockPingResponse}, {@link QueryPingResponse}, {@link SocketPingResponse}<br>
     * Default: {@code -1}
     *
     * @return The ping
     */
    default long getPing() {
        return -1;
    }

    /**
     * Get the amount of players currently online on the server.<br>
     * <br>
     * Implemented: {@link MCPingResponse}, {@link BedrockPingResponse}, {@link QueryPingResponse}<br>
     * Default: {@code -1}
     *
     * @return The amount of players
     */
    default int getOnlinePlayers() {
        return -1;
    }

    /**
     * Get the maximum amount of players that can be online on the server.<br>
     * <br>
     * Implemented: {@link MCPingResponse}, {@link BedrockPingResponse}, {@link QueryPingResponse}<br>
     * Default: {@code -1}
     *
     * @return The maximum amount of players
     */
    default int getMaxPlayers() {
        return -1;
    }

    /**
     * Get the version name of the server.<br>
     * <br>
     * Implemented: {@link MCPingResponse}, {@link BedrockPingResponse}, {@link QueryPingResponse}<br>
     * Default: {@code "Unknown"}
     *
     * @return The version name
     */
    @Nonnull
    default String getVersionName() {
        return "Unknown";
    }

    /**
     * Get the protocol id of the server.<br>
     * <br>
     * Implemented: {@link MCPingResponse}, {@link ClassicPingResponse}, {@link BedrockPingResponse}, {@link QueryPingResponse}<br>
     * Default: {@code -1}
     *
     * @return The protocol id
     */
    default int getProtocolId() {
        return -1;
    }

    /**
     * Get a sample of players on the server.<br>
     * The server can send any list of strings here, so it is not guaranteed to be valid player names.<br>
     * Some implementations may return some general information about the server here (e.g. {@link BedrockPingResponse}).<br>
     * The list can be unmodifiable.<br>
     * <br>
     * Implemented: {@link MCPingResponse}, {@link BedrockPingResponse}, {@link QueryPingResponse}<br>
     *
     * @return The sample players
     */
    @Nonnull
    default List<String> getSample() {
        return Collections.emptyList();
    }

}
