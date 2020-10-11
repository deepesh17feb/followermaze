package com.sc.clients.io.threads;

import com.sc.clients.constants.AppConstants;
import com.sc.clients.manager.ClientFollowerManager;
import com.sc.clients.manager.ClientRegistry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Input/Output Thread for incoming connection
 */
@Slf4j
public class IOClientThread implements Runnable {

    final static Logger logger = LoggerFactory.getLogger(AppConstants.UI_THREAD);

    private Socket socket;
    private ClientFollowerManager clientsManager;
    private BlockingQueue<String> msgQueue;
    private int clientId;
    private String connectionName;

    public IOClientThread(Socket socket, ClientFollowerManager clientsManager) {
        this.socket = socket;
        this.clientsManager = clientsManager;
        this.msgQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {

        try (BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            this.clientId = Integer.parseInt(inputStream.readLine());
            this.setConnectionName(clientId);

            // Read Message and register to central client repo

            // Adding into Client Registry
            ClientRegistry.registerClientThread(clientId, this);
            this.clientsManager.register(clientId);

            try {
                String queueData;
                while (!(queueData = msgQueue.take()).equals("exit")) {
                    log.debug("Message [{}] received from Queue", queueData);

                    logger.info("client [{}] :::::  Message [{}]", this.clientId, queueData);

                    // Write to client
                    writer.write(queueData);
                }
            } catch (InterruptedException e) {
                log.error("IOClient Thread interrupted while reading from queue - {}", e.getMessage(), e);
            }

            log.info("IOClient Connection [{}] is closing", clientId);
            this.shutdown();
        } catch (IOException e) {
            log.error("Error while executing IoClientThread request - msg [{}]", e.getMessage(), e);
        }

    }

    public void shutdown() {
        try {
            log.info("Closing Client Connection :: ", this.connectionName);
            this.socket.close();
            this.msgQueue = null;
        } catch (IOException e) {
            log.error("IO Exception while closing thread");
        }
    }

    public void addMessages(List<String> messages) {
        messages.forEach(this::addMessage);
    }

    public void addMessage(String message) {
        this.msgQueue.add(message);
    }

    public void setConnectionName(int clientId) {
        this.connectionName = Thread.currentThread().getName() + "-" + clientId;
    }

    public String getConnectionName() {
        return connectionName;
    }
}
