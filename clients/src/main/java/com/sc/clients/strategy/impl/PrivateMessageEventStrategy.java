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
import java.util.Objects;

/**
 * Actions :
 *
 * <ul>
 * <li>Only the <code>toUserId</code> should be notified</li>
 * </ul>
 */
@Slf4j
public class PrivateMessageEventStrategy implements IEventStrategy<String, Event> {

    @Override
    public Event parse(String msg) {
        String[] msgArr = msg.split("\\|");

        return Event.builder()
                .sequence(Integer.parseInt(msgArr[0]))
                .eventType(EventType.PRIVATE_MESSAGE)
                .fromUserId(Integer.parseInt(msgArr[2]))
                .toUserId(Integer.parseInt(msgArr[3]))
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

                // Step 1 - Notify toUserId
                log.debug("REQUEST PRIVATE_MESSAGE :: FromUserId [{}] to ToUserID [{}]  :::: {}", event.getFromUserId(), event.getToUserId(), msg);

                List<IOClientThread> toUserConnection = ClientRegistry.locateClientThreadUsingId(event.getToUserId());

                toUserConnection.stream()
                        .filter(fConn -> fConn != null)
                        .peek(fConn -> log.debug("PRIVATE_MSG :: Valid Connection for ToUserID [{}]", event.getToUserId()))
                        .forEach(fConn -> fConn.addMessage(msg));

            }
        } catch (Exception e) {
            throw new EventProcessingException("PrivateMessageEventStrategy : msg - [" + msg + "] -- processing error " + e.getMessage(), e);
        }
    }
}
