package com.sc.messaging.kafka.stream.impl;

import com.sc.messaging.kafka.config.SCKafkaConfig;
import com.sc.messaging.kafka.constant.SCKafkaStream;
import com.sc.messaging.kafka.stream.ISCKafkaStream;
import com.sc.messaging.kafka.stream.processors.MazeProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;

import java.io.IOException;
import java.time.Duration;

/**
 * Maze Kafka Stream perform following tasks -
 * <ul>
 *     <li>Reads from source topic</li>
 *     <li>Sort Records in a stream window before pushing using record key</li>
 *     <li>Push the data to sink topic</li>
 * </ul>
 *
 * @more https://kafka.apache.org/10/documentation/streams/developer-guide/write-streams
 * @more https://cwiki.apache.org/confluence/display/KAFKA/Punctuate+Use+Cases
 */
@Slf4j
public class MazeKafkaStream implements ISCKafkaStream<Long, String> {

    private SCKafkaConfig scKafkaConfig;
    private KafkaStreams streams;

    public MazeKafkaStream(SCKafkaConfig scKafkaConfig) {
        this.scKafkaConfig = scKafkaConfig;
    }

    @Override
    public void start() {

        boolean connected = false;
        int retries = 0;
        do {
            log.info("Initiating Maze Kafka Streams");
            try {
                this.streams = new KafkaStreams(getTopology(), getProperties(scKafkaConfig));
                connected = true;
            } catch (Exception e) {
                log.error("Error during Maze Kafka Stream initialization {} .. retrying", e.getMessage());
                retries++;
            }

        } while (!connected && retries <= scKafkaConfig.getRetry()); //retry

        if (!connected) {
            log.warn("Unable to initialize Kafka Streams.. exiting");
            System.exit(0);
        }

        streams.setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> {
            log.error("Uncaught exception in Thread {} - {}", thread.getName(), throwable.getMessage());
        });

        // Start the Kafka Streams threads
        streams.start();
    }

    /**
     * @return
     * @inheritdoc
     */
    @Override
    public Topology getTopology() {
        Topology builder = new Topology();

        builder.addSource(SCKafkaStream.SOURCE.toString(), this.scKafkaConfig.getSourceTopic())

                // add the MazeProcessor node which takes the source processor as its upstream processor
                .addProcessor(SCKafkaStream.PROCESSOR.toString(), () -> new MazeProcessor(this.scKafkaConfig), SCKafkaStream.SOURCE.toString())

                // add the maze store associated with the MazeProcessor processor
//                .addStateStore(storeBuilder(), SCKafkaStream.PROCESSOR.toString())

                // add the sink processor node that takes Kafka topic as output
                // and the MazeProcessor node as its upstream processor
                .addSink(SCKafkaStream.SINK.toString(), this.scKafkaConfig.getSinkTopic(), SCKafkaStream.PROCESSOR.toString());

        return builder;
    }

    /**
     * @return
     * @inheritdoc
     */
    @Override
    public StoreBuilder<KeyValueStore<Long, String>> storeBuilder() {

        return Stores.keyValueStoreBuilder(
                Stores.inMemoryKeyValueStore(this.scKafkaConfig.getStateStoreName())
                , Serdes.Long()
                , Serdes.String()
        );

    }

    @Override
    public void stop() throws IOException {
        // Add shutdown hook to stop the Kafka Streams threads.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.streams.cleanUp();
            this.streams.close(Duration.ofSeconds(5));
            log.debug("Kafka Stream Closed Successfully - {} ~~ (*) ~~~> {}",
                    this.scKafkaConfig.getSourceTopic(), this.scKafkaConfig.getSinkTopic());
        }));
    }
}
