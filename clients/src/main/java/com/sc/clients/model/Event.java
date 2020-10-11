package com.sc.clients.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Event {

    private int sequence;
    private String payload;
    private EventType eventType;
    private int fromUserId;
    private int toUserId;

}
