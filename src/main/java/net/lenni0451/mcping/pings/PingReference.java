package net.lenni0451.mcping.pings;

/**
 * The ping reference is used to calculate the ping of a server.<br>
 * The reference has to be started and stopped to calculate the ping.
 */
public class PingReference {

    private boolean done;
    private long ping = -1;

    /**
     * Start the ping calculation.
     *
     * @throws IllegalStateException If the ping has already started
     */
    public void start() {
        if (this.ping != -1) throw new IllegalStateException("Ping has already started");
        this.ping = System.currentTimeMillis();
    }

    /**
     * Start the ping calculation and return the start time.
     *
     * @return The start time
     * @throws IllegalStateException If the ping has already started
     */
    public long startAndGet() {
        this.start();
        return this.ping;
    }

    /**
     * Stop the ping calculation.
     *
     * @throws IllegalStateException If the ping has already stopped
     */
    public void stop() {
        long ping = System.currentTimeMillis() - this.ping; //Calculate this first to minimize code latency
        if (this.done) throw new IllegalStateException("Ping has already stopped");
        this.done = true;
        this.ping = ping;
    }

    /**
     * Get the ping.
     *
     * @return The ping
     * @throws IllegalStateException If the ping has not finished
     */
    public long get() {
        if (!this.done) throw new IllegalStateException("Ping has not finished");
        return this.ping;
    }

}
