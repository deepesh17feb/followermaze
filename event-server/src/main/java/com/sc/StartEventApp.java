package com.sc;

import com.sc.config.AppConfigReader;
import com.sc.constants.AppConstants;
import com.sc.constants.ServerMode;
import com.sc.exceptions.EventServerException;
import com.sc.io.IEventServer;
import com.sc.model.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Event App Application start point
 */
@Slf4j
public class StartEventApp {

    final static Logger logger = LoggerFactory.getLogger(AppConstants.UI_THREAD);

    public static void main(String[] args) {
        try {
            AppConfig appConfig = AppConfigReader.fetchAppConfig();
            IEventServer eventServer = IEventServer.builder()
                    .mode(ServerMode.valueOf(appConfig.getMode()))
                    .threads(appConfig.getThreads())
                    .host(appConfig.getHost())
                    .port(appConfig.getPort())
                    .build();

            logger.info("Starting Event Server in Mode [{}] - Threads [{}] - Port [{}]", appConfig.getMode(), appConfig.getThreads(), appConfig.getPort());
            log.info("Starting Event Server in Mode [{}] - Threads [{}] - Port [{}]", appConfig.getMode(), appConfig.getThreads(), appConfig.getPort());

            eventServer.startServer();
        } catch (IOException e) {
            log.error("Unable to start server - [{}]", e.getMessage(), e);
            throw new EventServerException("Server Error : " + e.getMessage());
        }
    }
}
