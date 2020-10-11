package com.sc.messaging;

import com.sc.messaging.model.Record;

import java.io.IOException;

/**
 * Methods necessary to implement a producer
 *
 * @param <S>
 * @param <U>
 */
public interface IProducer<S, U> extends IMessaging {

    void produce(Record<S, U> record) throws IOException;

    void stop() throws IOException;
}
