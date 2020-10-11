package com.sc.clients.listener;

import com.sc.clients.strategy.EventProcessor;
import com.sc.messaging.IConsumer;
import com.sc.messaging.MessageFactory;
import com.sc.messaging.constant.MessagingComponent;
import com.sc.messaging.constant.MessagingProvider;
import com.sc.messaging.file.config.SCFileConfig;
import com.sc.messaging.file.config.SCFileConfigFactory;
import com.sc.messaging.model.Record;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.function.Function;

/**
 * Event Listener
 */
@Slf4j
public class EventListener extends Thread {

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        try {
            SCFileConfig fileConfig = SCFileConfigFactory.fetchFileConfig();

            IConsumer<Record<Long, String>, Boolean> soundCloudConsumer = (IConsumer<Record<Long, String>, Boolean>) MessageFactory.builder()
                    .config(fileConfig)
                    .provider(MessagingProvider.FILE)
                    .component(MessagingComponent.CONSUMER)
                    .build();

            log.info("STARTED POLLING TO LOCATION :: {}", fileConfig.getFilepath());

            Function<Record<Long, String>, Boolean> processorFunction = new EventProcessor();
            soundCloudConsumer.consume(processorFunction);

        } catch (IOException e) {
            log.error("Error encountered in event listener - {}", e, e.getMessage());
            System.exit(0);
        }
    }
}
