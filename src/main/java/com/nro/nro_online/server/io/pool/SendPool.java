package com.nro.nro_online.server.io.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SendPool {
    private static final int MAX_THREAD = 10;
    private static final SendPool INSTANCE = new SendPool();
    private final ExecutorService pool;

    private SendPool() {
        this.pool = Executors.newFixedThreadPool(MAX_THREAD, new ThreadFactory() {
            private final AtomicInteger threadCount = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable run) {
                Thread t = new Thread(run, "SendPool-Thread-" + threadCount.getAndIncrement());
                t.setDaemon(true);
                return t;
            }
        });
    }

    public static SendPool gI() {
        return INSTANCE;
    }

    public void send(SendTask sendTask) {
        if (sendTask != null) {
            this.pool.execute(sendTask);
        }
    }

    public void shutdown() {
        if (!pool.isShutdown()) {
            pool.shutdown();
        }
    }
}