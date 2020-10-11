package com.sc.clients.strategy.impl;

import com.sc.clients.constants.AppConstants;
import com.sc.clients.exceptions.EventProcessingException;
import com.sc.clients.io.threads.IOClientThread;
import com.sc.clients.manager.ClientFollowerManager;
import com.sc.clients.manager.ClientRegistry;
import com.sc.clients.manager.InstanceManager;
import com.sc.clients.model.Event;
import com.sc.clients.model.EventType;
import com.sc.clients.strategy.IEventStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Actions :
 *
 * <ul>
 * <li>All connected user clients should be notified</li>
 * </ul>
 */
@Slf4j
public class BroadcastEventStrategy implements IEventStrategy<String, Event> {

    @Override
    public Event parse(String msg) {
        String[] msgArr = msg.split("\\|");

        return Event.builder()
                .sequence(Integer.parseInt(msgArr[0]))
                .eventType(EventType.BROADCAST)
                .payload(msg)
                .build();
    }

    @Override
    public void process(String msg) throws EventProcessingException {
        try {
            Event event = parse(msg);
            log.trace("Event :: {}", event);

            ClientFollowerManager followerManager = (ClientFollowerManager) InstanceManager.getInstanceUsingName(AppConstants.CLIENT_FOLLOWER_MANAGER);

            if (Objects.nonNull(followerManager)) {

                // Step 1 - Notify All Connected Client
                final Map<Integer, List<IOClientThread>> clientThreadMap = ClientRegistry.getAllConnectedClient();
                log.info("Total BROADCAST Clients : {}", clientThreadMap.size());

                if (clientThreadMap != null) {

                    clientThreadMap.keySet().stream()
                            .filter(clientKey -> !clientThreadMap.get(clientKey).isEmpty())
                            .forEach(clientKey ->

                                    clientThreadMap.get(clientKey)
                                            .stream()
                                            .filter(fConn -> fConn != null)
                                            .peek(fConn -> log.debug("BROADCAST :: Valid Connection for ToUserID [{}]", clientKey))
                                            .forEach(fConn -> fConn.addMessage(msg))
                            );


                }
            }
        } catch (Exception e) {
            throw new EventProcessingException("BroadcastEventStrategy : msg - [" + msg + "] -- processing error " + e.getMessage(), e);
        }
    }
}
