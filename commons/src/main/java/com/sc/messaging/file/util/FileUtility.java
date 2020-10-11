package com.sc.messaging.file.util;

import com.sc.messaging.model.Record;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class to handle File & Folder related operations in Java
 */
@Slf4j
public class FileUtility {

    /**
     * Append records to a file
     *
     * @param fileLocation
     * @param lines
     * @throws IOException
     */
    public static void appendToFile(String fileLocation, List<Record<Long, String>> lines) throws IOException {
        Path absoluteFilePath = Paths.get(fileLocation);

        // create folder if not exist
        createDirectory(absoluteFilePath.getParent());

        // create file if not exist
        createFile(absoluteFilePath);

        try (BufferedWriter writer = Files.newBufferedWriter(absoluteFilePath, Charset.forName("UTF-8"), StandardOpenOption.APPEND)) {

            for (Record line : lines) {
                writer.write(line.getValue().toString());
                writer.newLine();
                log.debug("Data [{}] appended to fileLocation [{}]", line, fileLocation);
            }
        }

    }

    /**
     * List all directories at a path
     *
     * @param location
     * @param maxDepth
     * @return
     */
    public static List<Path> listFolders(String location, int maxDepth) throws IOException {

        maxDepth = maxDepth <= 0 ? Integer.MAX_VALUE : maxDepth;
        Path searchableLocation = Paths.get(location);
        try (Stream<Path> paths = Files.walk(searchableLocation, maxDepth)) {
            return paths
                    .filter(Files::isDirectory)
                    .filter(iPath -> !iPath.toString().equals(searchableLocation.toString()))
                    .collect(Collectors.toList());
        }
    }

    /**
     * List all files at a path
     *
     * @param location
     * @return
     */
    public static List<Path> listFiles(String location) throws IOException {

        try (Stream<Path> paths = Files.walk(Paths.get(location))) {
            return paths
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }
    }

    public static void moveFile(String srcLocation, String destLocation) throws IOException {
        moveFile(Paths.get(srcLocation), Paths.get(destLocation));
    }

    /**
     * Move file from location to another
     *
     * @param srcLocation
     * @param destLocation
     * @return
     */
    public static void moveFile(Path srcLocation, Path destLocation) throws IOException {
        Files.move(srcLocation, destLocation, StandardCopyOption.REPLACE_EXISTING);
        log.debug("File [{}] moved successfully to destination [{}]", srcLocation, destLocation);
    }

    /**
     * Create Directory if doesn't exists and also take of creation of parent directories if needed
     *
     * @param path
     * @return
     */
    public static void createDirectory(Path path) throws IOException {

        if (!Files.exists(path) && !Files.isDirectory(path)) {

            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxr--r--");
            FileAttribute<Set<PosixFilePermission>> fileAttributes = PosixFilePermissions.asFileAttribute(permissions);

            Path directory = Files.createDirectories(path, fileAttributes);
            log.debug("Directory Created Successfully - [{}]", directory.toAbsolutePath());
        }
    }

    /**
     * Create File if doesn't exists
     *
     * @param path
     * @return
     */
    public static void createFile(Path path) throws IOException {

        if (!Files.exists(path)) {
            File file = path.toFile();
            if (file.createNewFile()) {
                log.debug("file created [{}]", path.toAbsolutePath());
            } else {
                log.debug("error in file creation [{}]", path.toAbsolutePath());
            }
        }

    }

    /**
     * Delete File
     *
     * @param path
     * @throws IOException
     */
    public static void deleteFile(Path path) throws IOException {
        Files.delete(path);
        log.debug("File [{}] deleted successfully ]", path);
    }

    /**
     * GZIP Compression
     *
     * @param input
     * @param output
     * @throws IOException
     */
    public static void compressGZIP(File input, File output) throws IOException {
        try (GzipCompressorOutputStream out = new GzipCompressorOutputStream(new FileOutputStream(output))) {
            IOUtils.copy(new FileInputStream(input), out);
        }
    }
}
