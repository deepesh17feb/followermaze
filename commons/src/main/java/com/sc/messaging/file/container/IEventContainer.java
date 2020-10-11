package com.sc.messaging.file.container;

import com.sc.messaging.model.Record;

import java.util.List;

/**
 * Interface Defining Container Capabilities
 *
 * @param <S>
 * @param <U>
 */
public interface IEventContainer<S, U> {

    /**
     * Add Records to Container
     *
     * @param record
     */
    void add(Record<S, U> record);

    /**
     * Fetch All Current Records in Container
     *
     * @return
     */
    List<Record<S, U>> getRecords();

    /**
     * Batching Operation
     *
     * @return
     */
    boolean batch();


    /**
     * Clear Operation
     */
    void clear();
}
