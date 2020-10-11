package com.sc.messaging.kafka.constant;

public enum SCKafkaStream {

    SOURCE("Source"),
    PROCESSOR("Process"),
    SINK("Sink");

    private String value;

    SCKafkaStream(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static SCKafkaStream fromValue(String val) {
        for (SCKafkaStream b : SCKafkaStream.values()) {
            if (val.equals(b.value)) {
                return b;
            }
        }

        return null;
    }
}
