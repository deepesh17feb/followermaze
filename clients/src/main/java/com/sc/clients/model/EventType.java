package com.sc.clients.model;

public enum EventType {

    FOLLOW("F"),
    UN_FOLLOW("U"),
    BROADCAST("B"),
    PRIVATE_MESSAGE("P"),
    STATUS_UPDATE("S");

    private String value;

    EventType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static EventType fromValue(String text) {
        for (EventType b : EventType.values()) {
            if (text.toUpperCase().contains(b.value)) {
                return b;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }
}
