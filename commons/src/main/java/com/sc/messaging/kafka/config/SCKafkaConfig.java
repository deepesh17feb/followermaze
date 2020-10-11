package com.sc.messaging.kafka.config;

import com.sc.messaging.IConfig;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SCKafkaConfig implements IConfig {

    private String bootstrapServers;

    private String zookeeperServers;

    private String consumerGroupId;

    private int partitions;

    private int replication;

    private int pollRecords;

    // kafka Stream
    private String sourceTopic;

    private String sinkTopic;

    private String applicationId;

    private String stateStoreName;

    private String stateStoreLocation;

    private Long commitIntervalInMillis;

    private Long punctuateIntervalInMillis;

    @Builder.Default
    private int retry = 1;
}
