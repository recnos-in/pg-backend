package org.recnos.pg.util;

import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileUtil {

    private static final Map<String, String> CONTENT_TYPE_MAP = new HashMap<>();

    static {
        CONTENT_TYPE_MAP.put("jpg", "image/jpeg");
        CONTENT_TYPE_MAP.put("jpeg", "image/jpeg");
        CONTENT_TYPE_MAP.put("png", "image/png");
        CONTENT_TYPE_MAP.put("webp", "image/webp");
        CONTENT_TYPE_MAP.put("heic", "image/heic");
        CONTENT_TYPE_MAP.put("gif", "image/gif");
    }

    /**
     * Generate a unique filename using UUID while preserving the extension
     */
    public static String generateUniqueFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isEmpty()) {
            return UUID.randomUUID().toString();
        }

        String extension = extractFileExtension(originalFileName);
        String uuid = UUID.randomUUID().toString();

        return extension.isEmpty() ? uuid : uuid + "." + extension;
    }

    /**
     * Safely extract file extension from filename
     */
    public static String extractFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        // Remove any path components
        fileName = fileName.replaceAll(".*[/\\\\]", "");

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * Validate if the file extension is in the allowed list
     */
    public static boolean isValidImageExtension(String extension, java.util.List<String> allowedExtensions) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }

        return allowedExtensions.stream()
                .anyMatch(allowed -> allowed.equalsIgnoreCase(extension));
    }

    /**
     * Format file size in bytes to human-readable format
     */
    public static String formatFileSize(long bytes) {
        if (bytes <= 0) return "0 B";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(bytes / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * Get content type (MIME type) based on file extension
     */
    public static String getContentType(String extension) {
        if (extension == null || extension.isEmpty()) {
            return "application/octet-stream";
        }

        return CONTENT_TYPE_MAP.getOrDefault(extension.toLowerCase(), "application/octet-stream");
    }

    /**
     * Sanitize filename to prevent path traversal attacks
     */
    public static String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return UUID.randomUUID().toString();
        }

        // Remove any path components
        fileName = fileName.replaceAll(".*[/\\\\]", "");

        // Remove potentially dangerous characters
        fileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");

        // Limit length
        if (fileName.length() > 255) {
            String extension = extractFileExtension(fileName);
            String nameWithoutExt = fileName.substring(0, 255 - extension.length() - 1);
            fileName = nameWithoutExt + "." + extension;
        }

        return fileName;
    }

    /**
     * Generate S3 object key with folder structure
     */
    public static String generateS3Key(String folder, String fileName) {
        String sanitized = sanitizeFileName(fileName);

        if (folder == null || folder.isEmpty()) {
            return sanitized;
        }

        // Remove leading/trailing slashes from folder
        folder = folder.replaceAll("^/+|/+$", "");

        return folder + "/" + sanitized;
    }

    /**
     * Extract original filename from MultipartFile
     */
    public static String getOriginalFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        return originalFilename != null ? originalFilename : "unknown";
    }

    /**
     * Check if file is empty
     */
    public static boolean isEmpty(MultipartFile file) {
        return file == null || file.isEmpty() || file.getSize() == 0;
    }
}