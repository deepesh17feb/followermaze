package com.sc.io.impl;

import com.sc.io.IEventServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Non Blocking Server implementation using ServerSocketChannel
 */
@Slf4j
public class NonBlockingEventServer implements IEventServer {

    private boolean serverRunningStatus = true;
    private AtomicInteger request;
    private ExecutorService executorService;
    private String host;
    private int port;

    public NonBlockingEventServer(String host, int port, int threads) {
        this.host = host;
        this.port = port;
        this.request = new AtomicInteger(1);
        this.executorService = Executors.newFixedThreadPool(threads);
    }

    @Override
    public void startServer() throws IOException {
        throw new IllegalArgumentException("Not Implemented!!");
    }

    @Override
    public void stopServer() {
        throw new IllegalArgumentException("Not Implemented!!");
    }
}
