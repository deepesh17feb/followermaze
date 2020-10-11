package com.sc.messaging.kafka.constant;

public final class KafkaConstants {

    public static String KAFKA_BROKERS = "bootstrap.servers";
    public static String ZOOKEEPER_CONNECT = "zookeeper.servers";
    public static String ZOOKEEPER_CONNECT_CONFIG = "zookeeper.connect";

    public static String SOURCE_TOPIC = "events.source.topic";
    public static String SINK_TOPIC = "events.sink.topic";

    public static String MAX_PARTITIONS = "topic.partition";
    public static String MAX_REPLICATIONS = "topic.replication";

    public static String GROUP_ID_CONFIG = "consumer.group.id";
    public static String MAX_POLL_RECORDS = "records.batch";

    public static String MAX_RETRY = "max.retry";

    public static String STREAM_APP_ID = "stream.app.id";
    public static String STREAM_COMMIT_INTERVAL_MILLIS = "stream.commit.interval.ms";
    public static String STREAM_PUNCTUATE_INTERVAL_MILLIS = "stream.punctuate.interval.ms";
    public static String STREAM_STATE_STORE_LOCATION = "stream.state.store.location";
    public static String STREAM_STATE_STORE_NAME = "stream.state.name";

}
