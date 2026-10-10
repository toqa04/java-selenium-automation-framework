package framework.utils;

import framework.exceptions.FrameworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * File utility functions for the framework.
 * 
 * Provides file operations such as:
 * - Directory creation
 * - File existence checks
 * - File deletion
 * - Timestamp-based file naming
 * 
 * @author Automation Team
 * @version 1.0.0
 */
public class FileUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * Private constructor to prevent instantiation.
     */
    private FileUtils() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    /**
     * Creates a directory if it doesn't exist.
     *
     * @param directoryPath the directory path
     * @throws FrameworkException if directory creation fails
     */
    public static void createDirectory(String directoryPath) {
        try {
            Path path = Paths.get(directoryPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                logger.info("Directory created: {}", directoryPath);
            }
        } catch (IOException e) {
            logger.error("Failed to create directory: {}", directoryPath, e);
            throw new FrameworkException("Failed to create directory: " + directoryPath, e);
        }
    }

    /**
     * Checks if a directory exists.
     *
     * @param directoryPath the directory path
     * @return true if directory exists, false otherwise
     */
    public static boolean directoryExists(String directoryPath) {
        Path path = Paths.get(directoryPath);
        return Files.exists(path) && Files.isDirectory(path);
    }

    /**
     * Checks if a file exists.
     *
     * @param filePath the file path
     * @return true if file exists, false otherwise
     */
    public static boolean fileExists(String filePath) {
        Path path = Paths.get(filePath);
        return Files.exists(path) && Files.isRegularFile(path);
    }

    /**
     * Deletes a file.
     *
     * @param filePath the file path
     * @return true if deletion was successful, false otherwise
     */
    public static boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            boolean deleted = Files.deleteIfExists(path);
            if (deleted) {
                logger.info("File deleted: {}", filePath);
            }
            return deleted;
        } catch (IOException e) {
            logger.warn("Failed to delete file: {}", filePath, e);
            return false;
        }
    }

    /**
     * Deletes a directory and all its contents.
     *
     * @param directoryPath the directory path
     * @throws FrameworkException if deletion fails
     */
    public static void deleteDirectory(String directoryPath) {
        try {
            Path path = Paths.get(directoryPath);
            if (Files.exists(path)) {
                Files.walk(path)
                    .sorted((p1, p2) -> p2.compareTo(p1))
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException e) {
                            logger.warn("Failed to delete: {}", p);
                        }
                    });
                logger.info("Directory deleted: {}", directoryPath);
            }
        } catch (IOException e) {
            logger.error("Failed to delete directory: {}", directoryPath, e);
            throw new FrameworkException("Failed to delete directory: " + directoryPath, e);
        }
    }

    /**
     * Gets the file extension (including the dot).
     *
     * @param fileName the file name
     * @return the extension (e.g., ".txt"), or empty string if no extension
     */
    public static String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1 || lastDot == 0) {
            return "";
        }
        return fileName.substring(lastDot);
    }

    /**
     * Gets the file name without extension.
     *
     * @param fileName the file name
     * @return the file name without extension
     */
    public static String getFileNameWithoutExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1 || lastDot == 0) {
            return fileName;
        }
        return fileName.substring(0, lastDot);
    }

    /**
     * Generates a timestamp-based file name.
     * Format: fileName_yyyyMMdd_HHmmss
     *
     * @param fileName the base file name (without extension)
     * @param extension the file extension (with dot, e.g., ".png")
     * @return the timestamped file name
     */
    public static String generateTimestampedFileName(String fileName, String extension) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = now.format(formatter);
        return fileName + "_" + timestamp + extension;
    }

    /**
     * Generates a file path with timestamp.
     *
     * @param directory the directory path
     * @param fileName the base file name (without extension)
     * @param extension the file extension (with dot, e.g., ".png")
     * @return the full file path with timestamp
     */
    public static String generateTimestampedFilePath(String directory, String fileName, String extension) {
        createDirectory(directory);
        String timestampedFileName = generateTimestampedFileName(fileName, extension);
        return Paths.get(directory, timestampedFileName).toString();
    }

    /**
     * Gets the size of a file in bytes.
     *
     * @param filePath the file path
     * @return the file size in bytes
     * @throws FrameworkException if file doesn't exist
     */
    public static long getFileSize(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.size(path);
        } catch (IOException e) {
            logger.error("Failed to get file size: {}", filePath, e);
            throw new FrameworkException("Failed to get file size: " + filePath, e);
        }
    }

    /**
     * Reads entire file content as a string.
     *
     * @param filePath the file path
     * @return the file content
     * @throws FrameworkException if file cannot be read
     */
    public static String readFileContent(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.readString(path);
        } catch (IOException e) {
            logger.error("Failed to read file: {}", filePath, e);
            throw new FrameworkException("Failed to read file: " + filePath, e);
        }
    }

    /**
     * Writes content to a file.
     *
     * @param filePath the file path
     * @param content the content to write
     * @throws FrameworkException if write fails
     */
    public static void writeFileContent(String filePath, String content) {
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            Files.writeString(path, content);
            logger.info("File written: {}", filePath);
        } catch (IOException e) {
            logger.error("Failed to write file: {}", filePath, e);
            throw new FrameworkException("Failed to write file: " + filePath, e);
        }
    }
}
