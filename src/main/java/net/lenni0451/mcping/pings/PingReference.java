package net.lenni0451.mcping.pings;

public class PingReference {

    private boolean done;
    private long ping = -1;

    public void start() {
        if (this.ping != -1) throw new IllegalStateException("Ping has already started");
        this.ping = System.currentTimeMillis();
    }

    public long startAndGet() {
        this.start();
        return this.ping;
    }

    public void stop() {
        long ping = System.currentTimeMillis() - this.ping; //Calculate this first to minimize code latency
        if (this.done) throw new IllegalStateException("Ping has already stopped");
        this.done = true;
        this.ping = ping;
    }

    public long get() {
        if (!this.done) throw new IllegalStateException("Ping has not finished");
        return this.ping;
    }

}
