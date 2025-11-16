package org.recnos.pg.model.dto.response.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadResponse {

    private String imageUrl; // Public URL of the uploaded image
    private String thumbnailUrl; // Public URL of thumbnail (if generated)
    private String s3Key; // S3 object key for deletion
    private String fileName; // Original or generated filename
    private Long fileSize; // File size in bytes
    private String fileSizeFormatted; // Human-readable file size
    private Integer width; // Image width in pixels
    private Integer height; // Image height in pixels
    private String contentType; // MIME type
    private String uploadedAt; // Timestamp of upload
}
