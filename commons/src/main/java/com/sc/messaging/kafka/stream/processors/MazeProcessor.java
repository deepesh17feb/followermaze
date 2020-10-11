package com.sc.messaging.kafka.stream.processors;

import com.sc.messaging.kafka.config.SCKafkaConfig;
import com.sc.messaging.model.Record;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Processor to sort the incoming data on basis of its key
 *
 * @more https://kafka.apache.org/10/documentation/streams/developer-guide/processor-api.html#streams-developer-guide-processor-api
 */
@Slf4j
public class MazeProcessor implements Processor<Long, String> {

    private ProcessorContext context;
    private SCKafkaConfig scKafkaConfig;
    private List<Record<Long, String>> windowDataList = new ArrayList<>();

    public MazeProcessor(SCKafkaConfig scKafkaConfig) {
        this.scKafkaConfig = scKafkaConfig;
    }

    /**
     * @param context {@inheritDoc}
     * @more https://kafka.apache.org/10/documentation/streams/developer-guide/processor-api#defining-a-stream-processor
     */
    @Override
    public void init(ProcessorContext context) {
        this.context = context;

        /** schedule a punctuate() method every n millis based on stream-time
         * @more https://kafka.apache.org/20/documentation/streams/core-concepts#streams_time
         */
        this.context.schedule(Duration.ofMillis(this.scKafkaConfig.getPunctuateIntervalInMillis()),
                PunctuationType.WALL_CLOCK_TIME,
                timestamp -> {

                    // Apply Sorting
                    try {
                        Collections.sort(this.windowDataList, (kv1, kv2) -> {

                            if (kv1.getKey() == kv2.getKey()) {
                                return 0;
                            }

                            return kv1.getKey() > kv2.getKey() ? 1 : -1;
                        });
                    } catch (Exception e) {
                        log.error("Error while sorting stream events before pushing to sink");
                    }

                    int n = this.windowDataList.size();
                    log.info("Stream Window - {} :::: {} -> {}",
                            n,
                            n > 0 ? this.windowDataList.get(0).getKey() : "-",
                            n > 0 ? this.windowDataList.get(n - 1).getKey() : "-"
                    );

                    // Push to Sink
                    long count = 0;
                    for (Record<Long, String> entry : this.windowDataList) {
                        context.forward(entry.getKey(), entry.getValue());

                        if (count == 1) {
                            log.debug("Stream Record :: (Key={} Value={})", entry.getKey(), entry.getValue());
                        }

                    }

                    this.windowDataList.clear();

                    // commit the current processing progress
                    this.context.commit();
                });
    }

    @Override
    public void process(Long key, String value) {

        log.debug("Stream Record :: (Key={} Value={})", key, value);
        this.windowDataList.add(Record.<Long, String>builder().key(key).value(value).build());
    }

    /**
     * Note: Do not close any StateStores as these are managed by the library
     */
    @Override
    public void close() {
        this.windowDataList = null;
    }
}
