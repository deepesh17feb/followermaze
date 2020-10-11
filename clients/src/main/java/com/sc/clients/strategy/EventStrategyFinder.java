package com.sc.clients.strategy;

import com.sc.clients.model.Event;
import com.sc.clients.model.EventType;
import com.sc.clients.strategy.impl.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Locate Appropriate Strategy on basis of event
 */
public class EventStrategyFinder {

    private static Map<EventType, IEventStrategy<String, Event>> eventStrategyMap = new HashMap<>();

    static {

        eventStrategyMap.put(EventType.FOLLOW, new FollowEventStrategy());
        eventStrategyMap.put(EventType.UN_FOLLOW, new UnFollowEventStrategy());
        eventStrategyMap.put(EventType.BROADCAST, new BroadcastEventStrategy());
        eventStrategyMap.put(EventType.PRIVATE_MESSAGE, new PrivateMessageEventStrategy());
        eventStrategyMap.put(EventType.STATUS_UPDATE, new StatusUpdateEventStrategy());
    }

    public static Optional<IEventStrategy<String, Event>> findStrategy(EventType eventType) {
        return Optional.ofNullable(eventStrategyMap.get(eventType));
    }
}
