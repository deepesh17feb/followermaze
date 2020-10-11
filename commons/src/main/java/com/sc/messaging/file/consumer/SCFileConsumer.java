package com.sc.messaging.file.consumer;

import com.sc.messaging.IConsumer;
import com.sc.messaging.file.config.SCFileConfig;
import com.sc.messaging.file.consumer.processor.EventFileProcessor;
import com.sc.messaging.file.util.FileUtility;
import com.sc.messaging.model.Record;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.function.Function;

/**
 * Consumer Implementation based on File
 */
@Slf4j
public class SCFileConsumer implements IConsumer<Record<Long, String>, Boolean> {

    public static final String PROCESSED = "PROCESSED";
    public static final String EVENT_FILE_FORMAT = "*.event";
    public static final String GZIP_EXTENSION = ".gz";

    private String location;
    private int poll;
    private EventFileProcessor eventFileProcessor;
    private boolean pollStatus = true;

    public SCFileConsumer(SCFileConfig fileConfig) {
        this.location = fileConfig.getFilepath();
        this.poll = fileConfig.getBatchPoll();
        this.eventFileProcessor = new EventFileProcessor();
    }

    /**
     * Steps to process events :
     * <ul>
     *     <li>1. Poll for new events after n secs</li>
     *     <li>2. Read folders datewise searching for new events</li>
     *     <li>3. Process all files ending with <code>.event</code></li>
     *     <li>4. Once Processed Successfully move it to <code>PROCESSED</code> folder</li>
     * </ul>
     *
     * @param function
     * @throws IOException
     */
    @Override
    public void consume(Function<Record<Long, String>, Boolean> function) throws IOException {
        while (pollStatus) {

            // 1. Poll for new events after n secs
            poll(Duration.ofMillis(this.poll));

            // 2. Read all datewise directories only using maxDepth=1
            List<Path> dateWisePaths = FileUtility.listFolders(this.location, 1);
            log.info("Total Available DateWise Directories :: {}", dateWisePaths);

            if (!dateWisePaths.isEmpty()) {

                for (Path filePath : dateWisePaths) {

                    //3. Stream all files with name ending .event
                    try (DirectoryStream<Path> eventFiles = Files.newDirectoryStream(filePath, EVENT_FILE_FORMAT)) {

                        for (Path eventFilePath : eventFiles) {
                            log.info("Start Processing for EventBatch -(*)-> [{}] - [{}]", eventFilePath.getParent().getFileName(), eventFilePath.getFileName());

                            // Process Events Batch
                            boolean status = eventFileProcessor.process(eventFilePath, function);

                            // 4. Move to PROCESSED Folder
                            if (status) {
                                log.debug("Event Batch :: Processed Successfully - [{}]", filePath.getFileName());

                                Path processedPath = Paths.get(eventFilePath.getParent().toString(), PROCESSED);

                                // 4.1 Check for PROCESSED directory
                                FileUtility.createDirectory(processedPath);
                                log.info("PROCESSED path available - [{}]", processedPath.toString());

                                // 4.2 GZIP Compress/Delete & Move file to PROCESSED directory

                                // GZIP Compress
                                File compressedFile = new File(eventFilePath.toAbsolutePath() + GZIP_EXTENSION);
                                FileUtility.compressGZIP(eventFilePath.toFile(), compressedFile);
                                FileUtility.deleteFile(eventFilePath);

                                // Move
                                Path destinationPath = Paths.get(processedPath.toString(), compressedFile.getName());
                                FileUtility.moveFile(Paths.get(compressedFile.getAbsolutePath()), destinationPath);


                            } else {
                                log.error("Event Batch :: Failed during Processing - [{}]", filePath.getFileName());
                            }
                        }

                    }
                }
            }
        }
    }

    private void poll(Duration duration) {
        log.info("Waiting for new events for {} seconds", duration.getSeconds());

        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            log.error("Error while polling [{}]", e.getMessage(), e);
        }
    }

    @Override
    public void stop() throws IOException {

        // Add shutdown hook to stop the File Consumer threads.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.pollStatus = false;
            log.debug("File Consumer Closed Successfully subscribed polling to location :: {}", this.location);
        }));
    }
}
