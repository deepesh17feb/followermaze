package com.sc.clients.strategy.impl;

import com.sc.clients.constants.AppConstants;
import com.sc.clients.exceptions.EventProcessingException;
import com.sc.clients.manager.ClientFollowerManager;
import com.sc.clients.manager.ClientRegistry;
import com.sc.clients.manager.InstanceManager;
import com.sc.clients.model.Event;
import com.sc.clients.model.EventType;
import com.sc.clients.strategy.IEventStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Actions :
 *
 * <ul>
 * <li>All current followers of the <code>fromUserId</code> should be notified</li>
 * </ul>
 */
@Slf4j
public class StatusUpdateEventStrategy implements IEventStrategy<String, Event> {

    @Override
    public Event parse(String msg) {
        String[] msgArr = msg.split("\\|");

        return Event.builder()
                .sequence(Integer.parseInt(msgArr[0]))
                .eventType(EventType.STATUS_UPDATE)
                .fromUserId(Integer.parseInt(msgArr[2]))
                .toUserId(-1)
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
                Set<Integer> followers = followerManager.followers(event.getFromUserId());
                log.debug("REQUEST STATUS_UPDATE :: FromUserId [{}] to FOLLOWERS [{}]  :::: {}", event.getFromUserId(), followers.size(), msg);

                followers.stream()
                        .map(followerId -> ClientRegistry.locateClientThreadUsingId(event.getFromUserId()))
                        .filter(fConn -> fConn != null && !fConn.isEmpty())
                        .flatMap(List::stream)
                        .filter(fConn -> fConn != null)
                        .peek(fConn -> log.debug("STATUS_UPDATE :: Valid Connection for ToUserID [{}]", event.getToUserId()))
                        .forEach(fConn -> fConn.addMessage(msg));

            }
        } catch (Exception e) {
            throw new EventProcessingException("StatusUpdateEventStrategy : msg - [" + msg + "] -- processing error " + e.getMessage(), e);
        }
    }
}
