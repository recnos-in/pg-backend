package org.recnos.pg.controller.file;

import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.recnos.pg.config.RateLimitConfig;
import org.recnos.pg.exception.RateLimitExceededException;
import org.recnos.pg.model.dto.request.file.ImageUploadRequest;
import org.recnos.pg.model.dto.request.file.PresignedUrlRequest;
import org.recnos.pg.model.dto.request.file.UploadConfirmRequest;
import org.recnos.pg.model.dto.response.file.ImageUploadResponse;
import org.recnos.pg.model.dto.response.file.MultiImageUploadResponse;
import org.recnos.pg.model.dto.response.file.PresignedUrlResponse;
import org.recnos.pg.security.SecurityContextHolder;
import org.recnos.pg.service.storage.FileStorageService;
import org.recnos.pg.util.FileValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("v1/upload")
@RequiredArgsConstructor
@Tag(name = "File Upload", description = "APIs for uploading and managing image files")
@SecurityRequirement(name = "bearerAuth")
public class FileUploadController {

    private final FileStorageService fileStorageService;
    private final FileValidator fileValidator;
    private final RateLimitConfig rateLimitConfig;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload single image", description = "Upload a single image file directly to the server. The image will be validated, resized if needed, and uploaded to S3.")
    public ResponseEntity<ImageUploadResponse> uploadSingleImage(
            @Parameter(description = "Image file to upload")
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Folder path in S3 (e.g., 'pgs', 'profiles')")
            @RequestParam("folder") String folder,
            @Parameter(description = "Context type (pg, profile, document, other)")
            @RequestParam(value = "contextType", required = false, defaultValue = "other") String contextType,
            @Parameter(description = "Optional description")
            @RequestParam(value = "description", required = false) String description) {

        // Rate limiting
        checkRateLimit();

        // Validate file
        fileValidator.validateImage(file);

        // Build request
        ImageUploadRequest request = ImageUploadRequest.builder()
                .folder(folder)
                .contextType(contextType)
                .description(description)
                .build();

        // Upload
        ImageUploadResponse response = fileStorageService.uploadImage(file, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload multiple images", description = "Upload multiple image files in a single request. Maximum 10 images per request.")
    public ResponseEntity<MultiImageUploadResponse> uploadMultipleImages(
            @Parameter(description = "Array of image files to upload")
            @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "Folder path in S3 (e.g., 'pgs', 'profiles')")
            @RequestParam("folder") String folder,
            @Parameter(description = "Context type (pg, profile, document, other)")
            @RequestParam(value = "contextType", required = false, defaultValue = "other") String contextType) {

        // Rate limiting (consumes multiple tokens for batch upload)
        checkRateLimitBatch(files.length);

        // Validate batch
        fileValidator.validateBatch(files);

        // Build request
        ImageUploadRequest request = ImageUploadRequest.builder()
                .folder(folder)
                .contextType(contextType)
                .build();

        // Upload
        MultiImageUploadResponse response = fileStorageService.uploadImages(files, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/presigned-url")
    @Operation(summary = "Generate presigned upload URL",
               description = "Generate a presigned URL for client-side direct upload to S3. The client can then upload the file directly to S3 using the provided URL.")
    public ResponseEntity<PresignedUrlResponse> generatePresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request) {

        // Rate limiting
        checkRateLimit();

        PresignedUrlResponse response = fileStorageService.generatePresignedUrl(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    @Operation(summary = "Confirm presigned upload",
               description = "Confirm that a file was successfully uploaded using a presigned URL. This verifies the file exists in S3.")
    public ResponseEntity<ImageUploadResponse> confirmUpload(
            @Valid @RequestBody UploadConfirmRequest request) {

        ImageUploadResponse response = fileStorageService.confirmUpload(request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/image/{s3Key}")
    @Operation(summary = "Delete an image", description = "Delete an image from S3 using its S3 key")
    public ResponseEntity<Map<String, String>> deleteImage(
            @Parameter(description = "S3 key of the image to delete")
            @PathVariable String s3Key) {

        fileStorageService.deleteImage(s3Key);

        return ResponseEntity.ok(Map.of("message", "Image deleted successfully", "s3Key", s3Key));
    }

    @GetMapping("/image/{s3Key}/url")
    @Operation(summary = "Get image URL", description = "Get the public URL for an image by its S3 key")
    public ResponseEntity<Map<String, String>> getImageUrl(
            @Parameter(description = "S3 key of the image")
            @PathVariable String s3Key) {

        String imageUrl = fileStorageService.getImageUrl(s3Key);

        return ResponseEntity.ok(Map.of("s3Key", s3Key, "imageUrl", imageUrl));
    }

    @GetMapping("/image/{s3Key}/exists")
    @Operation(summary = "Check if image exists", description = "Check if an image exists in S3")
    public ResponseEntity<Map<String, Boolean>> checkImageExists(
            @Parameter(description = "S3 key of the image")
            @PathVariable String s3Key) {

        boolean exists = fileStorageService.imageExists(s3Key);

        return ResponseEntity.ok(Map.of("exists", exists));
    }

    /**
     * Rate limiting helper - check if user can upload
     */
    private void checkRateLimit() {
        UUID userId = SecurityContextHolder.getCurrentUserId();
        Bucket bucket = rateLimitConfig.resolveBucket(userId);

        if (!bucket.tryConsume(1)) {
            long waitTime = bucket.estimateAbilityToConsume(1).getNanosToWaitForRefill();
            throw new RateLimitExceededException(
                    "Upload rate limit exceeded. Please try again later.",
                    waitTime
            );
        }
    }

    /**
     * Rate limiting for batch uploads - consumes multiple tokens
     */
    private void checkRateLimitBatch(int fileCount) {
        UUID userId = SecurityContextHolder.getCurrentUserId();
        Bucket bucket = rateLimitConfig.resolveBucket(userId);

        if (!bucket.tryConsume(fileCount)) {
            long waitTime = bucket.estimateAbilityToConsume(fileCount).getNanosToWaitForRefill();
            throw new RateLimitExceededException(
                    String.format("Upload rate limit exceeded. Attempted to upload %d files. Please try again later.", fileCount),
                    waitTime
            );
        }
    }
}
