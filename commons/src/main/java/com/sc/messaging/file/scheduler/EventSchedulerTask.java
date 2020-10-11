package com.sc.messaging.file.scheduler;

import com.sc.messaging.file.container.IEventContainer;
import lombok.extern.slf4j.Slf4j;

import java.util.TimerTask;

/**
 * Scheduler Task for Event Preservation
 */
@Slf4j
public class EventSchedulerTask extends TimerTask {

    private IEventContainer<Long, String> eventContainer;

    public EventSchedulerTask(IEventContainer<Long, String> eventContainer) {
        this.eventContainer = eventContainer;
    }

    @Override
    public void run() {
        log.debug("EVENT_SCHEDULER EXECUTION STARTED");

        long startTime = System.currentTimeMillis();

        if (eventContainer.batch()) {
            log.debug("EVENT_SCHEDULER EXECUTION COMPLETED");
        } else {
            log.debug("EVENT_SCHEDULER EXECUTION INTERRUPTED WITH ERROR");
        }

        log.info("EVENT_SCHEDULER EXECUTION RUNTIME :: {}", System.currentTimeMillis() - startTime);
    }

}
