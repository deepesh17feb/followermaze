package com.sc.messaging.kafka.producer;

import com.sc.messaging.IProducer;
import com.sc.messaging.kafka.config.SCKafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * http://cloudurable.com/blog/kafka-tutorial-kafka-producer-advanced-java-examples/index.html
 */
@Slf4j
public class SCKafkaProducerFactory {

    private static Producer<Long, String> createProducer(SCKafkaConfig kafkaConfig) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaConfig.getConsumerGroupId());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        setupBatchingAndCompression(props);

        Producer<Long, String> kafkaProducer = null;
        boolean connected = false;
        int retries = 0;

        do {
            log.info("Initiating Maze KafkaProducer");
            try {
                // Subscribe to the topic.
                kafkaProducer = new KafkaProducer<>(props);
                connected = true;
            } catch (Exception e) {
                log.error("Error during Maze KafkaProducer initialization {} .. retrying", e.getMessage());
                retries++;
            }

        } while (!connected && retries <= kafkaConfig.getRetry()); //retry

        if (!connected) {
            log.warn("Unable to initialize KafkaProducer.. exiting");
            System.exit(0);
        }

        return kafkaProducer;
    }

    private static void setupBatchingAndCompression(final Properties props) {
        //Linger up to 100 ms before sending batch if size not met
        props.put(ProducerConfig.LINGER_MS_CONFIG, 100);

        //Batch up to 64K buffer sizes.
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16_384 * 4);

        //Use Snappy compression for batch compression.
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
    }


    public static IProducer<Long, String> createSoundCloudKafkaProducer(SCKafkaConfig kafkaConfig) {

        IProducer<Long, String> scKafkaProducer = new SCKafkaProducer(createProducer(kafkaConfig), kafkaConfig.getSourceTopic());
        log.info("Maze KafkaProducer initialized successfully for topic [{}]", kafkaConfig.getSourceTopic());

        return scKafkaProducer;
    }
}
