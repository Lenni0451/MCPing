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

    public static MCPing<MCPingResponse> pingModern() {
        return pingModern(47);
    }

    public static MCPing<MCPingResponse> pingModern(final int protocolVersion) {
        return new MCPing<>(ping -> new ModernPing(ping.connectTimeout, ping.readTimeout, protocolVersion));
    }


    public static MCPing<MCPingResponse> pingLegacy(final LegacyPing.Version version) {
        return pingLegacy(version, version.getDefaultId());
    }

    public static MCPing<MCPingResponse> pingLegacy(final LegacyPing.Version version, final int protocolVersion) {
        return new MCPing<>(ping -> new LegacyPing(ping.connectTimeout, ping.readTimeout, version, protocolVersion));
    }


    public static MCPing<ClassicPingResponse> pingClassic(final ClassicPing.Version version) {
        return new MCPing<>(ping -> new ClassicPing(ping.connectTimeout, ping.readTimeout, version));
    }


    public static MCPing<QueryPingResponse> pingQuery() {
        return pingQuery(true);
    }

    public static MCPing<QueryPingResponse> pingQuery(final boolean full) {
        return new MCPing<>(ping -> new QueryPing(ping.readTimeout, full));
    }


    public static MCPing<BedrockPingResponse> pingBedrock() {
        return new MCPing<>(ping -> new BedrockPing(ping.readTimeout));
    }


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
    private Consumer<R> responseHandler;
    private Consumer<R> finishHandler;

    private MCPing(final Function<MCPing<R>, APing> ping) {
        this.ping = ping;
    }

    public MCPing<R> address(final String ip) {
        this.serverAddress = new ServerAddress(ip);
        return this;
    }

    public MCPing<R> address(final String ip, final int port) {
        this.serverAddress = new ServerAddress(ip, port);
        return this;
    }

    public MCPing<R> address(final ServerAddress serverAddress) {
        this.serverAddress = serverAddress;
        return this;
    }

    public MCPing<R> address(final SocketAddress address) {
        if (address instanceof InetSocketAddress) {
            InetSocketAddress socketAddress = (InetSocketAddress) address;
            this.serverAddress = new ServerAddress(socketAddress.getHostString(), socketAddress.getPort());
        } else {
            this.serverAddress = new ServerAddress(address.toString());
        }
        return this;
    }

    public MCPing<R> address(final InetAddress address) {
        this.address(address.getHostAddress());
        return this;
    }

    public MCPing<R> resolve() {
        this.resolve = true;
        return this;
    }

    public MCPing<R> noResolve() {
        this.resolve = false;
        return this;
    }

    public MCPing<R> timeout(final int connectTimeout, final int readTimeout) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        return this;
    }


    public MCPing<R> handler(final IStatusListener statusListener) {
        this.statusListener = statusListener;
        return this;
    }

    public MCPing<R> exceptionHandler(final Consumer<Throwable> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public MCPing<R> responseHandler(final Consumer<R> responseHandler) {
        this.responseHandler = responseHandler;
        return this;
    }

    public MCPing<R> finishHandler(final Consumer<R> finishHandler) {
        this.finishHandler = finishHandler;
        return this;
    }


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

    public CompletableFuture<R> getAsync() {
        return new MCPingFuture();
    }

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

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            if (mayInterruptIfRunning) this.thread.interrupt();
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
            if (MCPing.this.statusListener == null && MCPing.this.exceptionHandler == null) throwable.printStackTrace();
            if (this.future != null) this.future.completeExceptionally(throwable);
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
