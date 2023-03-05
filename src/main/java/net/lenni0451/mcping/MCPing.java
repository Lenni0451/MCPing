package net.lenni0451.mcping;

import net.lenni0451.mcping.pings.APing;
import net.lenni0451.mcping.pings.IStatusListener;
import net.lenni0451.mcping.pings.impl.*;
import net.lenni0451.mcping.responses.*;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class MCPing<R extends IPingResponse> {

    /**
     * Ping a server using the modern ping protocol.<br>
     * Defaults to protocol version 47.
     *
     * @return The modern ping builder
     */
    public static MCPing<MCPingResponse> pingModern() {
        return pingModern(47);
    }

    /**
     * Ping a server using the modern ping protocol.
     *
     * @param protocolVersion The protocol version to use
     * @return The modern ping builder
     */
    public static MCPing<MCPingResponse> pingModern(final int protocolVersion) {
        return new MCPing<>(ping -> new ModernPing(ping.connectTimeout, ping.readTimeout, protocolVersion));
    }


    /**
     * Ping a server using the legacy ping protocol.<br>
     * Defaults to the protocol version of the given version ({@link LegacyPing.Version#getDefaultId()}).<br>
     * Versions between b1.8 and 1.2 are not resolving the address by default.
     *
     * @param version The version of the protocol
     * @return The legacy ping builder
     */
    public static MCPing<MCPingResponse> pingLegacy(final LegacyPing.Version version) {
        return pingLegacy(version, version.getDefaultId());
    }

    /**
     * Ping a server using the legacy ping protocol.<br>
     * Versions between b1.8 and 1.2 are not resolving the address by default.
     *
     * @param version         The version of the protocol
     * @param protocolVersion The protocol version to use
     * @return The legacy ping builder
     */
    public static MCPing<MCPingResponse> pingLegacy(final LegacyPing.Version version, final int protocolVersion) {
        MCPing<MCPingResponse> mcPing = new MCPing<>(ping -> new LegacyPing(ping.connectTimeout, ping.readTimeout, version, protocolVersion));
        if (LegacyPing.Version.B1_8.equals(version)) mcPing.noResolve();
        return mcPing;
    }


    /**
     * Ping a server using the classic protocol.<br>
     * Resolving the address is disabled by default.<br>
     * <b>This visibly connects a client to the server causing a disconnect message.</b>
     *
     * @param version The version of the protocol
     * @return The classic ping builder
     */
    public static MCPing<ClassicPingResponse> pingClassic(final ClassicPing.Version version) {
        return new MCPing<ClassicPingResponse>(ping -> new ClassicPing(ping.connectTimeout, ping.readTimeout, version)).noResolve();
    }


    /**
     * Ping a server using the query protocol.<br>
     * Defaults to full query.
     *
     * @return The query ping builder
     */
    public static MCPing<QueryPingResponse> pingQuery() {
        return pingQuery(true);
    }

    /**
     * Ping a server using the query protocol.
     *
     * @param full Whether to use full query or not
     * @return The query ping builder
     */
    public static MCPing<QueryPingResponse> pingQuery(final boolean full) {
        return new MCPing<>(ping -> new QueryPing(ping.readTimeout, full));
    }


    /**
     * Ping a server using the bedrock protocol.<br>
     * Resolving the address is disabled by default.
     *
     * @return The bedrock ping builder
     */
    public static MCPing<BedrockPingResponse> pingBedrock() {
        return new MCPing<BedrockPingResponse>(ping -> new BedrockPing(ping.readTimeout)).noResolve();
    }


    /**
     * Ping a server using a socket.<br>
     * This just connects a socket and measures the time it takes to connect.
     *
     * @return The socket ping builder
     */
    public static MCPing<SocketPingResponse> pingSocket() {
        return new MCPing<>(ping -> new SocketPing(ping.readTimeout));
    }


    private final Function<MCPing<R>, APing> ping;
    private ServerAddress serverAddress;
    private boolean resolve = true;
    private int connectTimeout = 5_000;
    private int readTimeout = 10_000;
    private IStatusListener statusListener;
    private Consumer<Throwable> exceptionHandler;
    private Runnable connectedHandler;
    private Consumer<R> responseHandler;
    private Consumer<R> finishHandler;

    private MCPing(final Function<MCPing<R>, APing> ping) {
        this.ping = ping;
    }

    /**
     * Set the address of the server to ping.<br>
     * See {@link ServerAddress#parse(String, int)} for more information.
     *
     * @param address The string representation of the address and/or port
     * @return This builder
     */
    public MCPing<R> address(final String address) {
        this.serverAddress = ServerAddress.parse(address, this.ping.apply(this).getDefaultPort());
        return this;
    }

    /**
     * Set the address of the server to ping.<br>
     * See {@link ServerAddress#of(String, int, int)} for more information.
     *
     * @param host The host of the server
     * @param port The port of the server
     * @return This builder
     */
    public MCPing<R> address(final String host, final int port) {
        this.serverAddress = ServerAddress.of(host, port, this.ping.apply(this).getDefaultPort());
        return this;
    }

    /**
     * Set the address of the server to ping.
     *
     * @param serverAddress The server address
     * @return This builder
     */
    public MCPing<R> address(final ServerAddress serverAddress) {
        this.serverAddress = serverAddress;
        return this;
    }

    /**
     * Set the address of the server to ping.<br>
     * If the address is an {@link InetSocketAddress} the host and port will be used.<br>
     * Otherwise, the address will be parsed using {@link ServerAddress#parse(String, int)}.
     *
     * @param address The socket address
     * @return This builder
     */
    public MCPing<R> address(final SocketAddress address) {
        if (address instanceof InetSocketAddress) {
            InetSocketAddress socketAddress = (InetSocketAddress) address;
            return this.address(socketAddress.getHostString(), socketAddress.getPort());
        } else {
            return this.address(address.toString());
        }
    }

    /**
     * Set the address of the server to ping.<br>
     * The host will be resolved using {@link InetAddress#getHostAddress()}.
     *
     * @param address The inet address
     * @return This builder
     */
    public MCPing<R> address(final InetAddress address) {
        return this.address(address.getHostAddress());
    }

    /**
     * Set the address to be resolved.
     *
     * @return This builder
     */
    public MCPing<R> resolve() {
        this.resolve = true;
        return this;
    }

    /**
     * Set the address to not be resolved.
     *
     * @return This builder
     */
    public MCPing<R> noResolve() {
        this.resolve = false;
        return this;
    }

    /**
     * Set the connect and read timeout.<br>
     * Not all ping implementations require both timeouts.
     *
     * @param connectTimeout The connect timeout
     * @param readTimeout    The read timeout
     * @return This builder
     */
    public MCPing<R> timeout(final int connectTimeout, final int readTimeout) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        return this;
    }


    /**
     * Set the ping status listener.<br>
     * <b>IMPORTANT!</b> See {@link IStatusListener} for more information about the response special cases.
     *
     * @param statusListener The status listener
     * @return This builder
     */
    public MCPing<R> handler(final IStatusListener statusListener) {
        this.statusListener = statusListener;
        return this;
    }

    /**
     * Set a dedicated exception handler.<br>
     * See {@link IStatusListener#onError(Throwable)} for common exceptions and their cause.<br>
     * <b>IMPORTANT!</b> See {@link IStatusListener} for more information about the response special cases.
     *
     * @param exceptionHandler The exception handler
     * @return This builder
     */
    public MCPing<R> exceptionHandler(final Consumer<Throwable> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    /**
     * Set a dedicated connected handler.<br>
     * This handler is called when the connection to the server was established.<br>
     * <b>IMPORTANT!</b> See {@link IStatusListener} for more information about the response special cases.
     *
     * @param connectedHandler The connected handler
     * @return This builder
     */
    public MCPing<R> connectedHandler(final Runnable connectedHandler) {
        this.connectedHandler = connectedHandler;
        return this;
    }

    /**
     * Set a dedicated response handler.<br>
     * This handler is called when the server responds with a valid status.<br>
     * <b>IMPORTANT!</b> See {@link IStatusListener} for more information about the response special cases.
     *
     * @param responseHandler The response handler
     * @return This builder
     */
    public MCPing<R> responseHandler(final Consumer<R> responseHandler) {
        this.responseHandler = responseHandler;
        return this;
    }

    /**
     * Set a dedicated finish handler.<br>
     * This handler is called when the ping is finished.<br>
     * <b>IMPORTANT!</b> See {@link IStatusListener} for more information about the response special cases.
     *
     * @param finishHandler The finish handler
     * @return This builder
     */
    public MCPing<R> finishHandler(final Consumer<R> finishHandler) {
        this.finishHandler = finishHandler;
        return this;
    }


    /**
     * Get the ping response synchronously.<br>
     * If no status listener or exception handler is set, any exception will be thrown.<br>
     * If a listener is set, null will be returned.
     *
     * @return The ping response
     * @throws RuntimeException If no status listener or exception handler is set and an exception occurs
     */
    public R getSync() {
        CompletableFuture<R> future = new CompletableFuture<>();
        if (this.resolve) this.serverAddress.resolve();
        this.ping.apply(this).ping(this.serverAddress, new StatusListener(future));
        try {
            return future.get();
        } catch (InterruptedException ignored) {
        } catch (ExecutionException e) {
            if (this.statusListener == null && this.exceptionHandler == null) throw new RuntimeException("Unhandled exception during ping", e.getCause());
        }
        return null;
    }

    /**
     * Get a completable future for the ping response.<br>
     * The ping will be executed in a separate thread and started immediately.
     *
     * @return The completable future
     */
    public CompletableFuture<R> getAsync() {
        return new MCPingFuture();
    }

    /**
     * Get a future for the ping response.<br>
     * The ping will be executed in the given executor and started immediately.
     *
     * @param executor The executor
     * @return The future
     */
    public Future<R> getAsync(final ExecutorService executor) {
        return executor.submit(() -> {
            CompletableFuture<R> future = new CompletableFuture<>();
            if (this.resolve) this.serverAddress.resolve();
            this.ping.apply(this).ping(this.serverAddress, new StatusListener(future));
            return future.get();
        });
    }


    private class MCPingFuture extends CompletableFuture<R> {
        private final Thread thread;

        MCPingFuture() {
            this.thread = new Thread(() -> {
                if (MCPing.this.resolve) MCPing.this.serverAddress.resolve();
                MCPing.this.ping.apply(MCPing.this).ping(MCPing.this.serverAddress, new StatusListener(this));
            }, "MCPing Thread");
            this.thread.setDaemon(true);
            this.thread.start();
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            if (mayInterruptIfRunning) this.thread.stop();
            return super.cancel(mayInterruptIfRunning);
        }
    }

    private class StatusListener implements IStatusListener {
        private final CompletableFuture<R> future;

        StatusListener(final CompletableFuture<R> future) {
            this.future = future;
        }

        @Override
        public void onError(Throwable throwable) {
            if (MCPing.this.statusListener != null) MCPing.this.statusListener.onError(throwable);
            if (MCPing.this.exceptionHandler != null) MCPing.this.exceptionHandler.accept(throwable);
            if (this.future != null) this.future.completeExceptionally(throwable);
        }

        @Override
        public void onConnected() {
            if (MCPing.this.connectedHandler != null) MCPing.this.connectedHandler.run();
        }

        @Override
        public void onResponse(IPingResponse pingResponse) {
            if (MCPing.this.statusListener != null) MCPing.this.statusListener.onResponse(pingResponse);
            if (MCPing.this.responseHandler != null) MCPing.this.responseHandler.accept((R) pingResponse);
        }

        @Override
        public void onPing(IPingResponse pingResponse, long ping) {
            if (MCPing.this.statusListener != null) MCPing.this.statusListener.onPing(pingResponse, ping);
            if (MCPing.this.finishHandler != null) MCPing.this.finishHandler.accept((R) pingResponse);
            if (this.future != null) this.future.complete((R) pingResponse);
        }
    }

}
