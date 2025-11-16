package org.recnos.pg.service.storage;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.recnos.pg.config.FileUploadProperties;
import org.recnos.pg.exception.FileStorageException;
import org.recnos.pg.util.FileValidator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ImageProcessingService {

    private final FileUploadProperties fileUploadProperties;
    private final FileValidator fileValidator;

    /**
     * Process and resize image if needed
     * Returns the processed image as byte array
     */
    public ProcessedImage processImage(MultipartFile file) {
        // Validate the image first
        fileValidator.validateImage(file);

        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage originalImage = ImageIO.read(inputStream);

            if (originalImage == null) {
                throw new FileStorageException("Unable to read image file");
            }

            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            int maxDimension = fileUploadProperties.getMaxDimension();

            // Check if resizing is needed
            boolean needsResize = originalWidth > maxDimension || originalHeight > maxDimension;

            byte[] processedBytes;
            int finalWidth = originalWidth;
            int finalHeight = originalHeight;

            if (needsResize) {
                // Resize maintaining aspect ratio
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                Thumbnails.of(originalImage)
                        .size(maxDimension, maxDimension)
                        .outputFormat("jpg")
                        .outputQuality(0.85) // 85% quality
                        .toOutputStream(outputStream);

                processedBytes = outputStream.toByteArray();

                // Get new dimensions
                BufferedImage resizedImage = ImageIO.read(new ByteArrayInputStream(processedBytes));
                finalWidth = resizedImage.getWidth();
                finalHeight = resizedImage.getHeight();

            } else {
                // No resizing needed, use original
                processedBytes = file.getBytes();
            }

            return ProcessedImage.builder()
                    .imageBytes(processedBytes)
                    .width(finalWidth)
                    .height(finalHeight)
                    .originalWidth(originalWidth)
                    .originalHeight(originalHeight)
                    .wasResized(needsResize)
                    .fileSize((long) processedBytes.length)
                    .build();

        } catch (IOException e) {
            throw new FileStorageException("Failed to process image: " + e.getMessage(), e);
        }
    }

    /**
     * Generate thumbnail from image
     */
    public byte[] generateThumbnail(MultipartFile file, int thumbnailSize) {
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage originalImage = ImageIO.read(inputStream);

            if (originalImage == null) {
                throw new FileStorageException("Unable to read image for thumbnail generation");
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Thumbnails.of(originalImage)
                    .size(thumbnailSize, thumbnailSize)
                    .outputFormat("jpg")
                    .outputQuality(0.75) // Lower quality for thumbnails
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new FileStorageException("Failed to generate thumbnail: " + e.getMessage(), e);
        }
    }

    /**
     * Generate thumbnail from byte array
     */
    public byte[] generateThumbnailFromBytes(byte[] imageBytes, int thumbnailSize) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            BufferedImage originalImage = ImageIO.read(inputStream);

            if (originalImage == null) {
                throw new FileStorageException("Unable to read image bytes for thumbnail generation");
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Thumbnails.of(originalImage)
                    .size(thumbnailSize, thumbnailSize)
                    .outputFormat("jpg")
                    .outputQuality(0.75)
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new FileStorageException("Failed to generate thumbnail from bytes: " + e.getMessage(), e);
        }
    }

    /**
     * Get image dimensions without processing
     */
    public ImageDimensions getImageDimensions(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);

            if (image == null) {
                throw new FileStorageException("Unable to read image dimensions");
            }

            return ImageDimensions.builder()
                    .width(image.getWidth())
                    .height(image.getHeight())
                    .build();

        } catch (IOException e) {
            throw new FileStorageException("Failed to get image dimensions: " + e.getMessage(), e);
        }
    }

    // Inner classes for return types
    @lombok.Data
    @lombok.Builder
    public static class ProcessedImage {
        private byte[] imageBytes;
        private int width;
        private int height;
        private int originalWidth;
        private int originalHeight;
        private boolean wasResized;
        private Long fileSize;
    }

    @lombok.Data
    @lombok.Builder
    public static class ImageDimensions {
        private int width;
        private int height;
    }
}
