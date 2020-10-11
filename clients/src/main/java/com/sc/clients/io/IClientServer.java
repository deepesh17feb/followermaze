package com.sc.clients.io;

import com.sc.clients.constants.ServerMode;
import com.sc.clients.exceptions.InvalidServerModeException;
import com.sc.clients.io.impl.BlockingClientServer;

import java.io.IOException;

/**
 * Interface Defining methods required to implement Client Server with following Configuration
 * <ul>
 *     <li>ServerMode - BLOCKING/NON_BLOCKING</li>
 *     <li>Threads - number of server threads</li>
 *     <li>Host/Port - address of server</li>
 * </ul>
 */
public interface IClientServer {

    void startServer() throws IOException;

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

        public IClientServer build() {
            switch (this.serverMode) {
                case BLOCKING:
                    return new BlockingClientServer(this.host, this.port, this.threads);
                default:
                    throw new InvalidServerModeException("Invalid Server Mode : " + this.serverMode);
            }
        }
    }
}
