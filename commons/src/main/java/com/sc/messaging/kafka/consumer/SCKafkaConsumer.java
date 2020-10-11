package com.sc.messaging.kafka.consumer;

import com.sc.messaging.IConsumer;
import com.sc.messaging.model.Record;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Function;

@Slf4j
public class SCKafkaConsumer implements IConsumer<Record<Long, String>, Boolean> {

    private Consumer<Long, String> consumer;
    private String topic;
    private boolean pollStatus = true;

    public SCKafkaConsumer(Consumer<Long, String> consumer, String topic) {
        this.consumer = consumer;
        this.topic = topic;
    }

    @Override
    public void consume(Function<Record<Long, String>, Boolean> function) throws IOException {

        while (pollStatus) {

            final ConsumerRecords<Long, String> consumerRecords = this.consumer.poll(Duration.ofSeconds(5));

            if (consumerRecords.count() == 0) {
                log.debug("No Records Received from Topic [{}], retrying again", topic);
                continue;
            }
            log.debug("Consumer Records Returned :: {}", consumerRecords.count());

            consumerRecords.forEach(consumerRecord -> {
                log.trace("Consumer Record (key={} value={} partition={}, offset={}) from Topic [{}]",
                        consumerRecord.key(), consumerRecord.value(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.topic());

                Record record = Record.builder()
                        .key(consumerRecord.key())
                        .value(consumerRecord.value())
                        .build();

                function.apply(record);
            });

            //consumer.commitSync(Duration.ofSeconds(5));
            // Commit Offset Async
            consumer.commitAsync((offsets, exception) -> {
                if (!offsets.isEmpty()) {
                    log.trace("Kafka Offsets Committed - {}", offsets);
                } else {
                    log.error("Error while committing offsets {} - msg [{}]",
                            exception.getMessage(), exception);
                }
            });
        }


    }

    @Override
    public void stop() throws IOException {

        // Add shutdown hook to stop the Kafka Consumer threads.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.pollStatus = false;
            this.consumer.close(Duration.ofSeconds(5));
            log.debug("Kafka Consumer Closed Successfully subscribed to Topic :: {}", this.topic);
        }));
    }
}
