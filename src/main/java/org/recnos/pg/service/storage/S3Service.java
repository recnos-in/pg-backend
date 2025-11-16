package org.recnos.pg.service.storage;

import lombok.RequiredArgsConstructor;
import org.recnos.pg.config.FileUploadProperties;
import org.recnos.pg.config.S3Config;
import org.recnos.pg.exception.FileStorageException;
import org.recnos.pg.util.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3Config s3Config;
    private final FileUploadProperties fileUploadProperties;

    /**
     * Upload a file to S3
     */
    public String uploadFile(MultipartFile file, String s3Key, String contentType) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(s3Key)
                    .contentType(contentType)
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return getPublicUrl(s3Key);

        } catch (S3Exception e) {
            throw new FileStorageException("Failed to upload file to S3: " + e.awsErrorDetails().errorMessage(), e);
        } catch (IOException e) {
            throw new FileStorageException("Failed to read file for upload: " + e.getMessage(), e);
        }
    }

    /**
     * Upload byte array to S3 (for processed images)
     */
    public String uploadBytes(byte[] bytes, String s3Key, String contentType) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(s3Key)
                    .contentType(contentType)
                    .contentLength((long) bytes.length)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

            return getPublicUrl(s3Key);

        } catch (S3Exception e) {
            throw new FileStorageException("Failed to upload bytes to S3: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    /**
     * Generate presigned URL for client-side upload
     */
    public PresignedPutObjectRequest generatePresignedUploadUrl(String s3Key, String contentType) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(s3Key)
                    .contentType(contentType)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(fileUploadProperties.getPresignedUrlExpiration()))
                    .putObjectRequest(putObjectRequest)
                    .build();

            return s3Presigner.presignPutObject(presignRequest);

        } catch (S3Exception e) {
            throw new FileStorageException("Failed to generate presigned URL: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    /**
     * Delete a file from S3
     */
    public void deleteFile(String s3Key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

        } catch (S3Exception e) {
            throw new FileStorageException("Failed to delete file from S3: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    /**
     * Delete multiple files from S3
     */
    public void deleteFiles(java.util.List<String> s3Keys) {
        if (s3Keys == null || s3Keys.isEmpty()) {
            return;
        }

        try {
            java.util.List<ObjectIdentifier> objectIdentifiers = s3Keys.stream()
                    .map(key -> ObjectIdentifier.builder().key(key).build())
                    .collect(java.util.stream.Collectors.toList());

            Delete delete = Delete.builder()
                    .objects(objectIdentifiers)
                    .build();

            DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .delete(delete)
                    .build();

            s3Client.deleteObjects(deleteObjectsRequest);

        } catch (S3Exception e) {
            throw new FileStorageException("Failed to delete files from S3: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    /**
     * Check if a file exists in S3
     */
    public boolean fileExists(String s3Key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(s3Key)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;

        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            throw new FileStorageException("Failed to check if file exists: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    /**
     * Get file size from S3
     */
    public Long getFileSize(String s3Key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(s3Config.getBucketName())
                    .key(s3Key)
                    .build();

            HeadObjectResponse response = s3Client.headObject(headObjectRequest);
            return response.contentLength();

        } catch (S3Exception e) {
            throw new FileStorageException("Failed to get file size: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    /**
     * Get public URL for a file
     */
    public String getPublicUrl(String s3Key) {
        // If CloudFront domain is configured, use it; otherwise use S3 direct URL
        if (s3Config.getCloudFrontDomain() != null && !s3Config.getCloudFrontDomain().isEmpty()) {
            return "https://" + s3Config.getCloudFrontDomain() + "/" + s3Key;
        }

        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                s3Config.getBucketName(),
                s3Config.getRegion(),
                s3Key);
    }

    /**
     * Get bucket name
     */
    public String getBucketName() {
        return s3Config.getBucketName();
    }
}