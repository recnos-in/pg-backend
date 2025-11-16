package org.recnos.pg.service.storage;

import lombok.RequiredArgsConstructor;
import org.recnos.pg.config.FileUploadProperties;
import org.recnos.pg.exception.FileStorageException;
import org.recnos.pg.model.dto.request.file.ImageUploadRequest;
import org.recnos.pg.model.dto.request.file.PresignedUrlRequest;
import org.recnos.pg.model.dto.request.file.UploadConfirmRequest;
import org.recnos.pg.model.dto.response.file.ImageUploadResponse;
import org.recnos.pg.model.dto.response.file.MultiImageUploadResponse;
import org.recnos.pg.model.dto.response.file.PresignedUrlResponse;
import org.recnos.pg.util.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final S3Service s3Service;
    private final ImageProcessingService imageProcessingService;
    private final FileUploadProperties fileUploadProperties;

    private static final int THUMBNAIL_SIZE = 400; // 400x400 thumbnail

    /**
     * Upload a single image (direct upload method)
     */
    public ImageUploadResponse uploadImage(MultipartFile file, ImageUploadRequest request) {
        // Generate unique filename
        String originalFileName = FileUtil.getOriginalFileName(file);
        String uniqueFileName = FileUtil.generateUniqueFileName(originalFileName);
        String extension = FileUtil.extractFileExtension(uniqueFileName);
        String contentType = FileUtil.getContentType(extension);

        // Generate S3 key
        String s3Key = FileUtil.generateS3Key(request.getFolder(), uniqueFileName);

        // Process image (resize if needed)
        ImageProcessingService.ProcessedImage processedImage = imageProcessingService.processImage(file);

        // Upload processed image to S3
        String imageUrl = s3Service.uploadBytes(processedImage.getImageBytes(), s3Key, contentType);

        // Generate and upload thumbnail
        String thumbnailUrl = null;
        try {
            byte[] thumbnailBytes = imageProcessingService.generateThumbnailFromBytes(
                    processedImage.getImageBytes(),
                    THUMBNAIL_SIZE
            );

            String thumbnailKey = FileUtil.generateS3Key(
                    request.getFolder() + "/thumbnails",
                    uniqueFileName
            );

            thumbnailUrl = s3Service.uploadBytes(thumbnailBytes, thumbnailKey, "image/jpeg");

        } catch (Exception e) {
            // Thumbnail generation failed, but main image succeeded - log and continue
            System.err.println("Failed to generate thumbnail: " + e.getMessage());
        }

        return ImageUploadResponse.builder()
                .imageUrl(imageUrl)
                .thumbnailUrl(thumbnailUrl)
                .s3Key(s3Key)
                .fileName(uniqueFileName)
                .fileSize(processedImage.getFileSize())
                .fileSizeFormatted(FileUtil.formatFileSize(processedImage.getFileSize()))
                .width(processedImage.getWidth())
                .height(processedImage.getHeight())
                .contentType(contentType)
                .uploadedAt(Instant.now().toString())
                .build();
    }

    /**
     * Upload multiple images (batch upload)
     */
    public MultiImageUploadResponse uploadImages(MultipartFile[] files, ImageUploadRequest request) {
        List<ImageUploadResponse> successfulUploads = new ArrayList<>();
        List<MultiImageUploadResponse.UploadError> failedUploads = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                ImageUploadResponse response = uploadImage(file, request);
                successfulUploads.add(response);
            } catch (Exception e) {
                failedUploads.add(MultiImageUploadResponse.UploadError.builder()
                        .fileName(FileUtil.getOriginalFileName(file))
                        .error(e.getMessage())
                        .errorCode(e.getClass().getSimpleName())
                        .build());
            }
        }

        return MultiImageUploadResponse.builder()
                .totalUploaded(successfulUploads.size())
                .totalFailed(failedUploads.size())
                .successfulUploads(successfulUploads)
                .failedUploads(failedUploads)
                .build();
    }

    /**
     * Generate presigned URL for client-side upload
     */
    public PresignedUrlResponse generatePresignedUrl(PresignedUrlRequest request) {
        // Generate unique filename
        String uniqueFileName = FileUtil.generateUniqueFileName(request.getFileName());

        // Generate S3 key
        String s3Key = FileUtil.generateS3Key(request.getFolder(), uniqueFileName);

        // Generate presigned URL
        PresignedPutObjectRequest presignedRequest = s3Service.generatePresignedUploadUrl(
                s3Key,
                request.getContentType()
        );

        Instant expiresAt = Instant.now().plusSeconds(
                fileUploadProperties.getPresignedUrlExpiration() * 60L
        );

        return PresignedUrlResponse.builder()
                .uploadUrl(presignedRequest.url().toString())
                .s3Key(s3Key)
                .fileName(uniqueFileName)
                .expiresAt(expiresAt)
                .expiresInMinutes(fileUploadProperties.getPresignedUrlExpiration())
                .method("PUT")
                .contentType(request.getContentType())
                .build();
    }

    /**
     * Confirm presigned URL upload (after client completes upload)
     */
    public ImageUploadResponse confirmUpload(UploadConfirmRequest request) {
        // Verify the file exists in S3
        if (!s3Service.fileExists(request.getS3Key())) {
            throw new FileStorageException("File not found in S3. Upload may have failed.");
        }

        // Get the file size from S3
        Long fileSize = s3Service.getFileSize(request.getS3Key());

        // Verify file size matches (optional, for integrity)
        if (request.getFileSize() != null && !request.getFileSize().equals(fileSize)) {
            throw new FileStorageException("File size mismatch. Expected: " + request.getFileSize() + ", Actual: " + fileSize);
        }

        // Get public URL
        String imageUrl = s3Service.getPublicUrl(request.getS3Key());

        // Note: For presigned uploads, we can't easily generate thumbnails
        // unless the client also uploads the original file for processing
        // or we download it from S3, process it, and re-upload

        return ImageUploadResponse.builder()
                .imageUrl(imageUrl)
                .thumbnailUrl(null) // No thumbnail for presigned uploads
                .s3Key(request.getS3Key())
                .fileName(request.getFileName())
                .fileSize(fileSize)
                .fileSizeFormatted(FileUtil.formatFileSize(fileSize))
                .width(null) // Unknown for presigned uploads
                .height(null) // Unknown for presigned uploads
                .contentType(request.getContentType())
                .uploadedAt(Instant.now().toString())
                .build();
    }

    /**
     * Delete an uploaded image
     */
    public void deleteImage(String s3Key) {
        s3Service.deleteFile(s3Key);

        // Also try to delete thumbnail if it exists
        try {
            String thumbnailKey = s3Key.replace("/", "/thumbnails/");
            if (s3Service.fileExists(thumbnailKey)) {
                s3Service.deleteFile(thumbnailKey);
            }
        } catch (Exception e) {
            // Thumbnail deletion failed, but main file was deleted - log and continue
            System.err.println("Failed to delete thumbnail: " + e.getMessage());
        }
    }

    /**
     * Delete multiple images
     */
    public void deleteImages(List<String> s3Keys) {
        if (s3Keys == null || s3Keys.isEmpty()) {
            return;
        }

        // Delete main images
        s3Service.deleteFiles(s3Keys);

        // Try to delete thumbnails
        try {
            List<String> thumbnailKeys = s3Keys.stream()
                    .map(key -> key.replace("/", "/thumbnails/"))
                    .filter(s3Service::fileExists)
                    .toList();

            if (!thumbnailKeys.isEmpty()) {
                s3Service.deleteFiles(thumbnailKeys);
            }
        } catch (Exception e) {
            System.err.println("Failed to delete thumbnails: " + e.getMessage());
        }
    }

    /**
     * Get image URL from S3 key
     */
    public String getImageUrl(String s3Key) {
        if (!s3Service.fileExists(s3Key)) {
            throw new FileStorageException("Image not found with key: " + s3Key);
        }
        return s3Service.getPublicUrl(s3Key);
    }

    /**
     * Check if image exists
     */
    public boolean imageExists(String s3Key) {
        return s3Service.fileExists(s3Key);
    }
}
