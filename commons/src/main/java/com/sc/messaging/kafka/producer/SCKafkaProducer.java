package com.sc.messaging.kafka.producer;

import com.sc.messaging.IProducer;
import com.sc.messaging.model.Record;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.time.Duration;

@Slf4j
public class SCKafkaProducer implements IProducer<Long, String> {

    private Producer<Long, String> producer;
    private String topic;

    public SCKafkaProducer(Producer<Long, String> producer, String topic) {
        this.producer = producer;
        this.topic = topic;
    }

    @Override
    public void produce(Record<Long, String> record) throws IOException {
        ProducerRecord<Long, String> producerRecord = new ProducerRecord<>(this.topic, record.getKey(), record.getValue());

        long time = System.currentTimeMillis();
        this.producer.send(producerRecord, (metadata, exception) ->
        {
            if (metadata != null) {
                log.trace("Sent Record(key={} value={}) <--> Meta(partition={}, offset={}) time={}",
                        producerRecord.key(), producerRecord.value(), metadata.partition(), metadata.offset(), System.currentTimeMillis() - time);
            } else {
                log.error("Error while sending Record(key={} value={}) - msg [{}]",
                        producerRecord.key(), producerRecord.value(), exception.getMessage(), exception);
            }
        });
    }

    @Override
    public void stop() throws IOException {

        // Add shutdown hook to stop the Kafka Producer threads.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.producer.flush();
            this.producer.close(Duration.ofSeconds(5));
            log.debug("Kafka Consumer Closed Successfully subscribed to Topic :: {}", this.topic);
        }));
    }
}
