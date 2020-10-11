package com.sc.messaging.file.consumer.processor;

import com.sc.messaging.model.Record;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

@Slf4j
public class EventFileProcessor {

    public boolean process(Path filePath, Function function) {

        try (BufferedReader reader = Files.newBufferedReader(filePath, Charset.forName("UTF-8"))) {

            String recordLine;
            while ((recordLine = reader.readLine()) != null) {
                log.trace("Record [{}] from File [{}]", recordLine, filePath.getFileName());

                Record<Long, String> record = Record.<Long, String>builder()
                        .key(Long.parseLong(recordLine.split("\\|")[0]))
                        .value(recordLine).build();

                function.apply(record);
            }
            return true;

        } catch (IOException ex) {
            log.error("Error while processing File [{}] - [{}]", filePath.getFileName(), ex.getMessage(), ex);
        }

        return false;
    }
}
