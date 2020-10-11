package com.sc.messaging.file.config;

import com.sc.messaging.file.constants.SCFileConstants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SCFileConfigFactory {

    public static SCFileConfig fetchFileConfig() throws IOException {
        try (InputStream input = SCFileConfigFactory.class.getClassLoader().getResourceAsStream("file.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            return SCFileConfig.builder()
                    .filepath(prop.getProperty(SCFileConstants.BATCH_FILE_PATH, "/tmp"))
                    .batchSize(Integer.parseInt(prop.getProperty(SCFileConstants.BATCH_SIZE, "10000")))
                    .batchPoll(Integer.parseInt(prop.getProperty(SCFileConstants.BATCH_POLL, "5000")))
                    .retry(Integer.parseInt(prop.getProperty(SCFileConstants.MAX_RETRY, "1")))
                    .build();
        }
    }
}
