package com.sc.io;

import com.sc.constants.ServerMode;
import com.sc.exceptions.InvalidServerModeException;
import com.sc.io.impl.BlockingEventServer;
import com.sc.io.impl.NonBlockingEventServer;

import java.io.IOException;

/**
 * Interface Defining methods required to implement Event Server with following Configuration
 * <ul>
 *     <li>ServerMode - BLOCKING/NON_BLOCKING</li>
 *     <li>Threads - number of server threads</li>
 *     <li>Host/Port - address of server</li>
 * </ul>
 */
public interface IEventServer {

    /**
     * start server
     *
     * @throws IOException
     */
    void startServer() throws IOException;

    /**
     * stop server
     */
    void stopServer();

    static EventServerBuilder builder() {
        return new EventServerBuilder();
    }

    class EventServerBuilder {
        private ServerMode serverMode;
        private int threads;
        private String host;
        private int port;

        public EventServerBuilder mode(ServerMode serverMode) {
            this.serverMode = serverMode;
            return this;
        }

        public EventServerBuilder host(String host) {
            this.host = host;
            return this;
        }

        public EventServerBuilder port(int port) {
            this.port = port;
            return this;
        }

        public EventServerBuilder threads(int threads) {
            this.threads = threads;
            return this;
        }

        public IEventServer build() throws IOException {
            switch (this.serverMode) {
                case BLOCKING:
                    return new BlockingEventServer(this.host, this.port, this.threads);
                case NON_BLOCKING:
                    return new NonBlockingEventServer(this.host, this.port, this.threads);
                default:
                    throw new InvalidServerModeException("Invalid Server Mode : " + this.serverMode);
            }
        }
    }
}
