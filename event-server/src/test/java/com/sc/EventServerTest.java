package com.sc;

import com.sc.config.AppConfigReader;
import com.sc.constants.ServerMode;
import com.sc.io.IEventServer;
import com.sc.model.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Slf4j
public class EventServerTest {

    @Test
    public void startEventServer() {
        try {
            AppConfig appConfig = AppConfigReader.fetchAppConfig();
            IEventServer eventServer = IEventServer.builder()
                    .mode(ServerMode.valueOf(appConfig.getMode()))
                    .threads(appConfig.getThreads())
                    .port(appConfig.getPort())
                    .build();

            log.info("Starting Event Server in Mode [{}] - Threads [{}] - Port [{}]", appConfig.getMode(), appConfig.getThreads(), appConfig.getPort());

            eventServer.startServer();
        } catch (IOException e) {
            log.error("Unable to start server - [{}]", e.getMessage(), e);
        }
    }
}
