package com.sc.messaging.file.config;

import com.sc.messaging.IConfig;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SCFileConfig implements IConfig {

    @Builder.Default
    private int batchSize = 10000;

    private int batchPoll;

    @Builder.Default
    private String filepath = "/tmp";

    @Builder.Default
    private int retry = 1;
}
