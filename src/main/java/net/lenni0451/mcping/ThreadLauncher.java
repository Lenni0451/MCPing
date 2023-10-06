package net.lenni0451.mcping;

import java.lang.invoke.*;
import java.util.function.Function;

/**
 * Util class to launch threads.<br>
 * Virtual threads are used if available, otherwise platform threads.
 */
class ThreadLauncher {

    private static Function<Runnable, Thread> launcher;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle startVirtualThread = lookup.findStatic(Thread.class, "startVirtualThread", MethodType.methodType(Thread.class, Runnable.class));

            CallSite factory = LambdaMetafactory.metafactory(lookup,
                    "apply",
                    MethodType.methodType(Function.class),
                    MethodType.methodType(Object.class, Object.class),
                    startVirtualThread,
                    startVirtualThread.type());
            launcher = (Function<Runnable, Thread>) factory.getTarget().invokeExact();
        } catch (Throwable ignored) {
        }
    }

    /**
     * Start a virtual thread if available, otherwise a platform thread.<br>
     * The thread will be started as a daemon thread.
     *
     * @param runnable The runnable to run
     * @param name     The name of the thread (only used for platform threads)
     * @return The started thread
     */
    static Thread startThread(final Runnable runnable, final String name) {
        Thread thread;
        if (launcher == null) {
            thread = new Thread(runnable, name);
            thread.setDaemon(true);
            thread.start();
        } else {
            thread = launcher.apply(runnable);
        }
        return thread;
    }

}
