package com.sc.messaging.file.producer;

import com.sc.messaging.IProducer;
import com.sc.messaging.file.config.SCFileConfig;
import com.sc.messaging.file.container.IEventContainer;
import com.sc.messaging.file.container.impl.EventContainer;
import com.sc.messaging.file.scheduler.EventSchedulerTask;
import com.sc.messaging.model.Record;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Timer;

/**
 * File based producer to generate events
 */
@Slf4j
public class SCFileProducer implements IProducer<Long, String> {

    private SCFileConfig scFileConfig;
    private EventBatcher eventBatcher;
    private Timer timer;
    private IEventContainer<Long, String> eventContainer;

    public SCFileProducer(SCFileConfig scFileConfig) {
        this.scFileConfig = scFileConfig;
        this.eventBatcher = new EventBatcher();
        this.eventContainer = new EventContainer(this.eventBatcher, this.scFileConfig);

        // Register Event Batcher scheduler
        registerScheduler();

    }

    private void registerScheduler() {

        // Timer
        this.timer = new Timer("EventBatcher");

        //Task
        EventSchedulerTask eventScheduler = new EventSchedulerTask(this.eventContainer);

        // Schedule Timer
        this.timer.schedule(eventScheduler, scFileConfig.getBatchPoll(), scFileConfig.getBatchPoll());
    }

    /**
     * 1. Append new records to list
     * 2. Sort and flush to disk periodically
     * 3. Handle delta out of series events
     *
     * @param record
     * @throws IOException
     */
    @Override
    public void produce(Record<Long, String> record) throws IOException {
        long time = System.currentTimeMillis();

        this.eventContainer.add(record);
        log.trace("Sent Record(key={} value={}) time={}", record.getKey(), record.getValue(), System.currentTimeMillis() - time);
    }

    @Override
    public void stop() throws IOException {
        // Add shutdown hook to stop the File Producer threads.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            this.eventContainer.batch();
            this.eventContainer.clear();
            log.debug("file producer closed successfully");

        }));
    }

}
