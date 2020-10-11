package com.sc.messaging;

import java.io.IOException;

/**
 * Methods necessary to implement a stream
 */
public interface IStream extends IMessaging {

    void start() throws IOException;

    void stop() throws IOException;
}
