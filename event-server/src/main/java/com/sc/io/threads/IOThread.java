package com.sc.io.threads;

import com.sc.messaging.IProducer;
import com.sc.messaging.model.Record;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;

/**
 * Input/Output Thread for incoming connection
 */
@Slf4j
public class IOThread implements Runnable {

    private Socket socket;
    private IProducer<Long, String> producer;
    private int request;

    public IOThread(Socket socket, IProducer<Long, String> producer, int request) {
        this.socket = socket;
        this.producer = producer;
        this.request = request;
    }

    @Override
    public void run() {
        log.info("IO Request : {}", this.request);

        try (BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            String clientMessage;
            BigInteger count = BigInteger.ONE;
            do {
                clientMessage = inputStream.readLine();
                log.debug(clientMessage);

                Record<Long, String> record = Record.<Long, String>builder()
                        .key(Long.parseLong(clientMessage.split("\\|")[0]))
                        .value(clientMessage).build();

                // Message Pushed to Messaging Producer
                this.producer.produce(record);
                log.debug("{} <:-:-:> {}", record.getKey(), record.getValue());

                count = count.add(BigInteger.ONE);

                // Print incremental message update
                if (count.longValue() % 10000 == 0) {
                    log.info("Message Published So Far ::: {}", count.longValue());
                }

            } while (!clientMessage.equals("quit"));

            writer.write("Time to say Bye Bye, forwarded all your messages to their appropriate destination!!");
            this.socket.close();
        } catch (IOException e) {
            log.error("Error while executing request - msg [{}]", e.getMessage(), e);
        }

    }
}
