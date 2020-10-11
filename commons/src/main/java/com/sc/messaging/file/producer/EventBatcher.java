package com.sc.messaging.file.producer;

import com.sc.messaging.file.config.SCFileConfig;
import com.sc.messaging.file.util.FileUtility;
import com.sc.messaging.model.Record;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class EventBatcher {

    public static final String EVENT_FILE_FORMAT = ".event";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd-MM-yyyy");

    public void processBatch(List<Record<Long, String>> records, SCFileConfig fileConfig) throws IOException {
        long time = System.currentTimeMillis();

        // Apply Sorting
        try {
            Collections.sort(records, Comparator.comparing(Record::getKey));
        } catch (Exception e) {
            log.error("Error while sorting batch records before pushing");
        }

        // Generate Filename
        long totalBatchedRecords = 0;
        if (records.size() > fileConfig.getBatchSize()) {
            List<Record<Long, String>> batchedRecords = new ArrayList<>(fileConfig.getBatchSize());
            batchedRecords.addAll(records.subList(0, fileConfig.getBatchSize()));

            totalBatchedRecords = batchedRecords.size();
            FileUtility.appendToFile(calculateAbsoluteFileName(fileConfig.getFilepath(), time), batchedRecords);

            // removes a range of elements from a list:
            records.subList(0, fileConfig.getBatchSize()).clear();
        } else {
            totalBatchedRecords = records.size();
            FileUtility.appendToFile(calculateAbsoluteFileName(fileConfig.getFilepath(), time), records);
            records.clear();
        }

        log.info("Event Batch :: Records [{}] written to File [{}] in duration :: {}",
                totalBatchedRecords, calculateFileName(time), System.currentTimeMillis() - time);
    }

    private String calculateAbsoluteFileName(String filepath, long time) {
        return filepath + File.separator + SDF.format(new Date(time)) + File.separator + calculateFileName(time);
    }

    private String calculateFileName(long time) {
        return (time + "").concat(EVENT_FILE_FORMAT);
    }

}
