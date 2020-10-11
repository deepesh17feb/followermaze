package com.sc.messaging.kafka.consumer;

import com.sc.messaging.IConsumer;
import com.sc.messaging.kafka.config.SCKafkaConfig;
import com.sc.messaging.kafka.constant.KafkaConstants;
import com.sc.messaging.model.Record;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

@Slf4j
public class SCKafkaConsumerFactory {

    private static Consumer<Long, String> createConsumer(SCKafkaConfig kafkaConfig) {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        props.put(KafkaConstants.ZOOKEEPER_CONNECT_CONFIG, kafkaConfig.getZookeeperServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.getConsumerGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaConfig.getPollRecords());

        // Create the consumer using props.
        final Consumer<Long, String> consumer = new KafkaConsumer<>(props);

        boolean connected = false;
        int retries = 0;
        do {
            log.info("Initiating Maze KafkaConsumer");
            try {
                // Subscribe to the topic.
                consumer.subscribe(Collections.singletonList(kafkaConfig.getSinkTopic()));
                connected = true;
            } catch (Exception e) {
                log.error("Error during Maze KafkaConsumer initialization {} .. retrying", e.getMessage());
                retries++;
            }

        } while (!connected && retries <= kafkaConfig.getRetry()); //retry

        if (!connected) {
            log.warn("Unable to initialize Maze KafkaConsumer.. exiting");
            System.exit(0);
        }

        return consumer;
    }

    public static IConsumer<Record<Long, String>, Boolean> createSoundCloudKafkaConsumer(SCKafkaConfig kafkaConfig) {

        IConsumer<Record<Long, String>, Boolean> scKafkaConsumer = new SCKafkaConsumer(createConsumer(kafkaConfig), kafkaConfig.getSinkTopic());
        log.info("Maze KafkaConsumer initialized successfully for topic [{}]", kafkaConfig.getSinkTopic());

        return scKafkaConsumer;
    }
}
