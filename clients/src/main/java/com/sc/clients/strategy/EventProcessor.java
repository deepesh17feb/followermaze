package com.sc.clients.strategy;

import com.sc.clients.model.Event;
import com.sc.clients.model.EventType;
import com.sc.messaging.model.Record;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.Function;

/**
 * Lambda Function to execute strategy
 */
@Slf4j
public class EventProcessor implements Function<Record<Long, String>, Boolean> {

    @Override
    public Boolean apply(Record<Long, String> record) {

        EventType eventType = EventType.fromValue(record.getValue());

        Optional<IEventStrategy<String, Event>> wrappedEventStrategy = EventStrategyFinder.findStrategy(eventType);

        if (!wrappedEventStrategy.isPresent()) {
            log.error("No suitable Event Strategy found for Record :: {}", record.getValue());
            return false;
        }

        IEventStrategy<String, Event> eventStrategy = wrappedEventStrategy.get();
        eventStrategy.process(record.getValue());
        log.trace("EventId Processed Successfully :: {}", record.getValue());

        return true;
    }
}
