package org.recnos.pg.util;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.recnos.pg.config.FileUploadProperties;
import org.recnos.pg.exception.FileSizeExceededException;
import org.recnos.pg.exception.InvalidFileTypeException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class FileValidator {

    private final FileUploadProperties fileUploadProperties;
    private final Tika tika = new Tika();

    /**
     * Validate a single file for upload
     */
    public void validateImage(MultipartFile file) {
        if (FileUtil.isEmpty(file)) {
            throw new InvalidFileTypeException("File is empty");
        }

        validateFileSize(file);
        validateFileExtension(file);
        validateMimeType(file);
        validateImageContent(file);
    }

    /**
     * Validate file size against configured limit
     */
    private void validateFileSize(MultipartFile file) {
        long maxSize = fileUploadProperties.getMaxFileSizeBytes();

        if (file.getSize() > maxSize) {
            throw new FileSizeExceededException(
                    String.format("File size %s exceeds maximum allowed size of %s",
                            FileUtil.formatFileSize(file.getSize()),
                            FileUtil.formatFileSize(maxSize))
            );
        }

        if (file.getSize() == 0) {
            throw new InvalidFileTypeException("File is empty (0 bytes)");
        }
    }

    /**
     * Validate file extension against allowed list
     */
    private void validateFileExtension(MultipartFile file) {
        String originalFileName = FileUtil.getOriginalFileName(file);
        String extension = FileUtil.extractFileExtension(originalFileName);

        if (extension.isEmpty()) {
            throw new InvalidFileTypeException("File has no extension");
        }

        if (!FileUtil.isValidImageExtension(extension, fileUploadProperties.getAllowedExtensions())) {
            throw new InvalidFileTypeException(
                    String.format("File type '.%s' is not allowed. Allowed types: %s",
                            extension,
                            String.join(", ", fileUploadProperties.getAllowedExtensions()))
            );
        }
    }

    /**
     * Validate MIME type using Apache Tika
     */
    private void validateMimeType(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            String detectedType = tika.detect(inputStream);

            // Check if detected type is an image
            if (detectedType == null || !detectedType.startsWith("image/")) {
                throw new InvalidFileTypeException(
                        String.format("File is not a valid image. Detected type: %s", detectedType)
                );
            }

            // Verify the MIME type matches the extension
            String extension = FileUtil.extractFileExtension(FileUtil.getOriginalFileName(file));
            String expectedContentType = FileUtil.getContentType(extension);

            // Some tolerance for JPEG variations
            if (!detectedType.equals(expectedContentType)) {
                if (!(detectedType.equals("image/jpeg") && expectedContentType.equals("image/jpeg"))) {
                    // Log warning but don't fail for minor MIME type mismatches
                    System.out.println(String.format("MIME type mismatch: detected %s but expected %s for extension .%s",
                            detectedType, expectedContentType, extension));
                }
            }

        } catch (IOException e) {
            throw new InvalidFileTypeException("Failed to detect file type: " + e.getMessage(), e);
        }
    }

    /**
     * Validate that the file is actually a readable image
     */
    private void validateImageContent(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);

            if (image == null) {
                throw new InvalidFileTypeException("File is not a valid image or format is not supported");
            }

            // Optionally validate image dimensions
            int width = image.getWidth();
            int height = image.getHeight();

            if (width <= 0 || height <= 0) {
                throw new InvalidFileTypeException("Image has invalid dimensions");
            }

            // Reasonable dimension limits (e.g., not larger than 10000x10000)
            if (width > 10000 || height > 10000) {
                throw new InvalidFileTypeException(
                        String.format("Image dimensions %dx%d exceed maximum allowed size of 10000x10000",
                                width, height)
                );
            }

        } catch (IOException e) {
            throw new InvalidFileTypeException("Failed to read image content: " + e.getMessage(), e);
        }
    }

    /**
     * Validate batch of files
     */
    public void validateBatch(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new InvalidFileTypeException("No files provided");
        }

        if (files.length > fileUploadProperties.getMaxImagesPerRequest()) {
            throw new InvalidFileTypeException(
                    String.format("Too many files. Maximum %d files allowed per request",
                            fileUploadProperties.getMaxImagesPerRequest())
            );
        }

        long totalSize = 0;
        for (MultipartFile file : files) {
            validateImage(file);
            totalSize += file.getSize();
        }

        if (totalSize > fileUploadProperties.getMaxRequestSizeBytes()) {
            throw new FileSizeExceededException(
                    String.format("Total request size %s exceeds maximum allowed size of %s",
                            FileUtil.formatFileSize(totalSize),
                            FileUtil.formatFileSize(fileUploadProperties.getMaxRequestSizeBytes()))
            );
        }
    }
}
