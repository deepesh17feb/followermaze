package com.sc.clients.io.threads;

import java.util.concurrent.ThreadFactory;

public class ClientThreadFactory implements ThreadFactory {
    private int counter = 1;

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, "client" + "-thread-" + counter++);
    }
}
