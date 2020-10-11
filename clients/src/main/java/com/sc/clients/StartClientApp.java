package com.sc.clients;

import com.sc.clients.config.AppConfigReader;
import com.sc.clients.constants.AppConstants;
import com.sc.clients.constants.ServerMode;
import com.sc.clients.exceptions.ClientServerException;
import com.sc.clients.io.IClientServer;
import com.sc.clients.listener.EventListener;
import com.sc.clients.model.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client App Application start point in below sequence :
 * <ul>
 *     <li>Source Event Stream</li>
 *     <li>Sorted Event Consumer</li>
 *     <li>Client Server</li>
 * </ul>
 * <p>
 * Key Points to take care :
 * <ul>
 * <li> Events will arrive out of order</li>
 * <li>If there are no user client connected for a user, any notifications for them must be silently ignored</li>
 * <li> user clients expect to be notified of events in the correct order, regardless of the order in which the event source sent them.</li>
 * </ul>
 */
@Slf4j
public class StartClientApp {

    final static Logger logger = LoggerFactory.getLogger(AppConstants.UI_THREAD);

    public static void main(String[] args) {
        try {
            AppConfig appConfig = AppConfigReader.fetchAppConfig();
            IClientServer clientServer = IClientServer.builder()
                    .host(appConfig.getHost())
                    .mode(ServerMode.valueOf(appConfig.getMode()))
                    .threads(appConfig.getThreads())
                    .port(appConfig.getPort())
                    .build();

            // Step 1 - starting event consumer
            EventListener eventListener = new EventListener();
            eventListener.start();

            // Step 2 - starting client server
            logger.info("Starting Client Server in Mode [{}] - Threads [{}] - Port [{}]", appConfig.getMode(), appConfig.getThreads(), appConfig.getPort());
            log.info("Starting Client Server in Mode [{}] - Threads [{}] - Port [{}]", appConfig.getMode(), appConfig.getThreads(), appConfig.getPort());

            clientServer.startServer();
        } catch (Exception e) {
            log.error("Unable to start server - [{}]", e.getMessage(), e);
            throw new ClientServerException("Server Error : " + e.getMessage());
        }
    }
}
