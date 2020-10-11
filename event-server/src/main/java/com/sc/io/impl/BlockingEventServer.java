package com.sc.io.impl;

import com.sc.io.IEventServer;
import com.sc.io.threads.EventThreadFactory;
import com.sc.io.threads.IOThread;
import com.sc.messaging.IProducer;
import com.sc.messaging.MessageFactory;
import com.sc.messaging.constant.MessagingComponent;
import com.sc.messaging.constant.MessagingProvider;
import com.sc.messaging.file.config.SCFileConfig;
import com.sc.messaging.file.config.SCFileConfigFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Blocking Server implementation using Socket Server
 */
@Slf4j
public class BlockingEventServer implements IEventServer {

    private boolean serverRunningStatus = true;
    private AtomicInteger request;
    private ExecutorService executorService;
    private String host;
    private int port;
    private IProducer<Long, String> producer;

    public BlockingEventServer(String host, int port, int threads) throws IOException {
        this.host = host;
        this.port = port;
        this.request = new AtomicInteger(1);
        this.executorService = Executors.newFixedThreadPool(threads, new EventThreadFactory());

        init();
    }

    @SuppressWarnings("unchecked")
    private void init() throws IOException {
        SCFileConfig fileConfig = SCFileConfigFactory.fetchFileConfig();
        this.producer = (IProducer<Long, String>) MessageFactory.builder()
                .config(fileConfig)
                .provider(MessagingProvider.FILE)
                .component(MessagingComponent.PRODUCER)
                .build();
    }

    @Override
    public void startServer() throws IOException {

        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(this.host, this.port));

            while (serverRunningStatus) {
                log.info("STARTED LISTENING ON PORT :: {}", this.port);

                Socket socket = serverSocket.accept();

                IOThread ioThread = new IOThread(socket, this.producer, request.getAndIncrement());
                executorService.submit(ioThread);
            }
        }
    }

    @Override
    public void stopServer() {
        // Add shutdown hook to stop the Kafka Producer threads.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.serverRunningStatus = false;
            log.info("Shutting Down Event Server");
        }));
    }
}
