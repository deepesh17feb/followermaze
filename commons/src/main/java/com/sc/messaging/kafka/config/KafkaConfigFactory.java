package com.sc.messaging.kafka.config;

import com.sc.messaging.kafka.constant.KafkaConstants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KafkaConfigFactory {

    public static SCKafkaConfig fetchKafkaConfig() throws IOException {
        try (InputStream input = KafkaConfigFactory.class.getClassLoader().getResourceAsStream("kafka.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            return SCKafkaConfig.builder()
                    .bootstrapServers(prop.getProperty(KafkaConstants.KAFKA_BROKERS, "localhost:9092"))
                    .zookeeperServers(prop.getProperty(KafkaConstants.ZOOKEEPER_CONNECT, "localhost:2181"))
                    .consumerGroupId(prop.getProperty(KafkaConstants.GROUP_ID_CONFIG, "EventKafkaProducer"))
                    .partitions(Integer.parseInt(prop.getProperty(KafkaConstants.MAX_PARTITIONS, "1")))
                    .pollRecords(Integer.parseInt(prop.getProperty(KafkaConstants.MAX_POLL_RECORDS, "10")))
                    .replication(Integer.parseInt(prop.getProperty(KafkaConstants.MAX_REPLICATIONS, "1")))
                    .applicationId(prop.getProperty(KafkaConstants.STREAM_APP_ID, "KafkaStreamApp"))
                    .sourceTopic(prop.getProperty(KafkaConstants.SOURCE_TOPIC, "demo-source"))
                    .sinkTopic(prop.getProperty(KafkaConstants.SINK_TOPIC, "demo-sink"))
                    .retry(Integer.parseInt(prop.getProperty(KafkaConstants.MAX_RETRY, "1")))
                    .commitIntervalInMillis(Long.parseLong(prop.getProperty(KafkaConstants.STREAM_COMMIT_INTERVAL_MILLIS, "1000")))
                    .punctuateIntervalInMillis(Long.parseLong(prop.getProperty(KafkaConstants.STREAM_PUNCTUATE_INTERVAL_MILLIS, "3000")))
                    .stateStoreName(prop.getProperty(KafkaConstants.STREAM_STATE_STORE_NAME, "state-store"))
                    .stateStoreLocation(prop.getProperty(KafkaConstants.STREAM_STATE_STORE_LOCATION, "/tmp"))
                    .build();

        }
    }
}
