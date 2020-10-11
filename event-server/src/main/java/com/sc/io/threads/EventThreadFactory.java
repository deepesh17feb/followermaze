package com.sc.io.threads;

import java.util.concurrent.ThreadFactory;

public class EventThreadFactory implements ThreadFactory {
    private int counter = 1;

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, "event" + "-thread-" + counter++);
    }
}
