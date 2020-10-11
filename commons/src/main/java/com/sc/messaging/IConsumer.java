package com.sc.messaging;

import java.io.IOException;
import java.util.function.Function;

/**
 * Methods necessary to implement a consumer
 *
 * @param <S>
 * @param <U>
 */
public interface IConsumer<S, U> extends IMessaging {

    void consume(Function<S, U> function) throws IOException;

    void stop() throws IOException;
}
