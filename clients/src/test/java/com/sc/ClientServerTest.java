package com.sc;

import com.sc.clients.config.AppConfigReader;
import com.sc.clients.constants.ServerMode;
import com.sc.clients.io.IClientServer;
import com.sc.clients.model.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Slf4j
public class ClientServerTest {

    @Test
    public void startClientServer() {
        try {
            AppConfig appConfig = AppConfigReader.fetchAppConfig();
            IClientServer clientServer = IClientServer.builder()
                    .mode(ServerMode.valueOf(appConfig.getMode()))
                    .threads(appConfig.getThreads())
                    .port(appConfig.getPort())
                    .build();

            log.info("Starting Client Server in Mode [{}] - Threads [{}] - Port [{}]", appConfig.getMode(), appConfig.getThreads(), appConfig.getPort());
            clientServer.startServer();
        } catch (IOException e) {
            log.error("Error Starting Client Server - {}", e.getMessage());
        }
    }
}
