package com.sc.clients.strategy.impl;

import com.sc.clients.constants.AppConstants;
import com.sc.clients.exceptions.EventProcessingException;
import com.sc.clients.manager.ClientFollowerManager;
import com.sc.clients.manager.InstanceManager;
import com.sc.clients.model.Event;
import com.sc.clients.model.EventType;
import com.sc.clients.strategy.IEventStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Actions :
 *
 * <ul>
 * <li>Remove <code>toUserId</code> in the follower group of <code>fromUserId</code></li>
 * <li>No clients should be notified</li>
 * </ul>
 */
@Slf4j
public class UnFollowEventStrategy implements IEventStrategy<String, Event> {

    @Override
    public Event parse(String msg) {
        String[] msgArr = msg.split("\\|");

        return Event.builder()
                .sequence(Integer.parseInt(msgArr[0]))
                .eventType(EventType.UN_FOLLOW)
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

                // Step 1 - Add toUserId in the follower group of fromUserId
                boolean status = followerManager.unFollow(event.getFromUserId(), event.getToUserId());
                log.debug("REQUEST UN_FOLLOW :: FromUserId [{}] to ToUserID [{}] - [{}]  :::: {}", event.getFromUserId(), event.getToUserId(),
                        status ? "SUCCESS" : "FAILED", msg);

            }
        } catch (Exception e) {
            throw new EventProcessingException("UnFollowEventStrategy : msg - [" + msg + "] -- processing error " + e.getMessage(), e);
        }
    }
}
