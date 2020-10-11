package com.sc.messaging.kafka.stream;

import com.sc.messaging.IStream;
import com.sc.messaging.kafka.config.SCKafkaConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;

import java.util.Properties;

/**
 * Template methods defining Kafka Streams
 *
 * @param <S>
 * @param <U>
 * @more https://www.confluent.io/blog/enabling-exactly-once-kafka-streams/
 */
public interface ISCKafkaStream<S, U> extends IStream {

    /**
     * @param kafkaConfig
     * @return
     * @more https://kafkaConfig.apache.org/10/documentation/streams/developer-guide/config-streams
     */
    default Properties getProperties(SCKafkaConfig kafkaConfig) {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaConfig.getApplicationId());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Long().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

//        props.put(StreamsConfig.STATE_DIR_CONFIG, kafkaConfig.getStateStoreLocation());
//        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, kafkaConfig.getCommitIntervalInMillis());
//        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 2);
//        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);

        return props;
    }

    /**
     * Define Stream Topology using the Processor API
     *
     * @return
     */
    Topology getTopology();

    /**
     * Define Store builder for stream processing
     *
     * @return
     */
    StoreBuilder<KeyValueStore<S, U>> storeBuilder();
}
