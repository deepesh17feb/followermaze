package com.sc.messaging.file.container.impl;

import com.sc.messaging.file.config.SCFileConfig;
import com.sc.messaging.file.container.IEventContainer;
import com.sc.messaging.file.producer.EventBatcher;
import com.sc.messaging.model.Record;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

/**
 * Thread Safe Event Container - it contains all transient events and flush batching capability
 */
@Slf4j
public class EventContainer implements IEventContainer<Long, String> {

    public static final int IDLE_TIME = 60000;

    private Instant lastUpdatedBatchProcessingTime = Instant.now();
    private List<Record<Long, String>> records;
    private EventBatcher eventBatcher;
    private SCFileConfig scFileConfig;

    public EventContainer(EventBatcher eventBatcher, SCFileConfig scFileConfig) {
        this.eventBatcher = eventBatcher;
        this.scFileConfig = scFileConfig;
        this.records = new LinkedList<>();
    }

    /**
     * Thread Safe add to Record
     *
     * @param record
     */
    public void add(Record<Long, String> record) {
        synchronized (this) {
            this.records.add(record);
        }
    }

    public List<Record<Long, String>> getRecords() {
        return records;
    }

    /**
     * Thread Safe Batching Operation
     *
     * @return
     */
    public boolean batch() {
        synchronized (this) {
            if (isBatchReady()) {
                lastUpdatedBatchProcessingTime = Instant.now();

                try {
                    eventBatcher.processBatch(this.records, this.scFileConfig);
                } catch (IOException e) {
                    log.error("Event Batching Interrupted with Error - {}", e.getMessage(), e);
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Batch is Ready in below conditions :
     * 1) If container records crossed batch size
     * 2) If last batch run time crossed idle time (60 sec)
     *
     * @return
     */
    public boolean isBatchReady() {
        return (this.records.size() >= this.scFileConfig.getBatchSize())
                ||
                (
                        (System.currentTimeMillis() - lastUpdatedBatchProcessingTime.toEpochMilli() > IDLE_TIME)
                                &&
                                this.records.size() > 0
                );
    }

    /**
     * Thread Safe Clear Operation
     */
    public void clear() {
        synchronized (this) {
            this.records.clear();
            this.records = null;
        }

    }
}
