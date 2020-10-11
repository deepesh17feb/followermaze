package com.sc.clients.io.impl;

import com.sc.clients.constants.AppConstants;
import com.sc.clients.io.IClientServer;
import com.sc.clients.io.threads.ClientThreadFactory;
import com.sc.clients.io.threads.IOClientThread;
import com.sc.clients.manager.ClientFollowerManager;
import com.sc.clients.manager.InstanceManager;
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
public class BlockingClientServer implements IClientServer {

    private boolean serverRunningStatus = true;
    private AtomicInteger request;
    private String host;
    private int port;
    private ExecutorService executorService;
    private ClientFollowerManager clientsManager;

    public BlockingClientServer(String host, int port, int threads) {
        this.host = host;
        this.port = port;
        this.request = new AtomicInteger(1);
        this.executorService = Executors.newFixedThreadPool(threads, new ClientThreadFactory());
        this.clientsManager = (ClientFollowerManager) InstanceManager.getInstanceUsingName(AppConstants.CLIENT_FOLLOWER_MANAGER);
    }

    @Override
    public void startServer() throws IOException {

        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(this.host, this.port));

            log.info("STARTED LISTENING ON PORT :: {}", this.port);

            // Started Listening for receiving connection from clients
            while (serverRunningStatus) {

                Socket socket = serverSocket.accept();
                log.debug("New Connection Request Received - {}", request.getAndIncrement());

                IOClientThread ioThread = new IOClientThread(socket, clientsManager);
                executorService.submit(ioThread);

            }
        }

    }

    @Override
    public void stopServer() {
        // Add shutdown hook to stop the Kafka Producer threads.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.serverRunningStatus = false;
            log.info("Shutting Down Clients Server");
        }));
    }
}
